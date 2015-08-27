package com.twomovingaverages.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.marketdatahandle.jpa.Stock;
import com.marketdatahandler.ejb.MarketDataHandlerLocal;
import com.stocklist.ejb.IStockListController;
import com.stocklist.jpa.StockList;
import com.trade.jpa.Order;
import com.trade.jpa.OrderStatus;
import com.trade.jpa.OrderType;
import com.trade.jpa.Trade;
import com.tradingcontroller.TC_ATObject;
import com.tradingcontroller.TradeObject;
import com.tradingcontroller.TradeObject.Result;
import com.tradingcontroller.ejb.CalculationHelper;
import com.tradingcontroller.ejb.TradingController;
import com.tradingcontroller.mq.TradeMessenger;


class TwoMovingAverageWrapper{
	TMAObjClass tmaObject;
	Order relatedOrder;
	
	public TwoMovingAverageWrapper(TMAObjClass tmaObject, Order relatedOrder) {
		// TODO Auto-generated constructor stub
		this.tmaObject = tmaObject;
		this.relatedOrder = relatedOrder;
	}
}

@Singleton
@Stateful
public class TwoMovingAverages implements ITwoMovingAverages{

	@PersistenceContext(unitName = "ct_projectUnit")
	private EntityManager em;
	
	@EJB
	private MarketDataHandlerLocal marketDataHandler;
	
	@EJB
	private IStockListController stockListController;

	ArrayList<TwoMovingAverageWrapper> storeToMonitor = new ArrayList<TwoMovingAverageWrapper>();
	private boolean isMonitoring = false;
	
	private static QueueConnectionFactory orderBrokerMessageFactory;
	private static Context orderBrokerMessageContext;
	private static Queue orderBrokerMessageQueue;
	private static QueueConnection orderBrokerMessageQueueConnection;

	private static Context tradeControllerMessageContext;
	private static QueueConnectionFactory tradeControllerMessageFactory;
	private static Queue tradeControllerMessageQueue;
	private static QueueConnection tradeControllerMessageQueueConnection;

	private static int nextTradeId = 0;
	private HashMap<Integer, TwoMovingAverageWrapper> pendingList = new HashMap<Integer, TwoMovingAverageWrapper>();

	private final static String QUEUE_OB = "dynamicQueues/OrderBroker";
	private final static String QUEUE_TC = "jms/TCQueue";
	private final static String FACTORY_TC = "jms/TradeConnectionFactory";
	private final static String JMS_ID = "citiTrade";
	
	private final static int REPEAT_CYCLE_TIME = 2000 ;
	Timer timer = null;

	private class MonitorPrice extends TimerTask {

		public void run() {			
			
			for (TwoMovingAverageWrapper wrapperObj : storeToMonitor){
								
				TMAObjClass obj = wrapperObj.tmaObject;
				
				
				System.out.println("[TWO ALGO: Monitoring] "+obj.getStockSymbol());
				
				Stock newInfo = marketDataHandler.getStockBySymbol(obj.getStockSymbol());
				double newStockAvg = newInfo.getAsk() + newInfo.getBid();
				
				obj.setShortCounter(obj.getShortCounter()+1);
				
				double newShortAvg = (obj.getShortAvg() + newStockAvg)	/ obj.getShortCounter();
				
				obj.setShortAvg(newShortAvg);
				
				if ( (!obj.isSend) && obj.getShortAvg() < obj.getLongAvg()){ //if short average line is below long average  (GOING FOR LONG POSITION)
				
					if (!obj.isBuy() && obj.getShortAvg() >= obj.getLongAvg()){ //if haven buy and just pass the long avg,quickly buy it
						
						obj.setBuy(true);
						double getBuyPrice = newInfo.getAsk();
						obj.setBuyPrice(getBuyPrice);					
						obj.setNumOfTrade(obj.getObj().getAmtToTrade() / obj.getBuyPrice());
						
						obj.setSend(true);
						obj.setId(nextTradeId++);
						pendingList.put(obj.getId(), wrapperObj);
						recordAndSendMessageToOB(true, getBuyPrice, obj.getStockSymbol(), obj.getNumOfTrade());
						
						
						
						
						
					} else if (obj.isBuy()) { //bought already and check for 1 percent profit

						
						if ((obj.getShortAvg() >= (obj.getBuyPrice() * 1.01))  || (obj.getShortAvg() <= (obj.getBuyPrice() * 1.01))){
							
							obj.setSell(true);
//							obj.setBuy(false); 
							//TODO
							//sell here(or remove from trade) and get the profit 
							
							double getSellPrice = newInfo.getBid();
							obj.setSellPrice(getSellPrice);					
							obj.setNumOfTrade(obj.getObj().getAmtToTrade() / obj.getBuyPrice());
							
							obj.setSend(true);
							obj.setId(nextTradeId++);
							pendingList.put(obj.getId(), wrapperObj);
							recordAndSendMessageToOB(false, getSellPrice, obj.getStockSymbol(), obj.getNumOfTrade());
						
							
							
							
						} else if (obj.getShortAvg() <= (obj.getBuyPrice() * 1.01)){ //bought already and check for 1 percent profit loss, exit here
							
							//sell here(or remove from trade) and make a loss

						}
					}
					
				} else if ((!obj.isSend) && obj.getLongAvg() < obj.getShortAvg()){ //if short average line is higher than long average (Going for SHORT POSITION)
					
					if (!obj.isSell() && obj.getShortAvg() <= obj.getLongAvg()){ //if it is not sold yet, sell it (ENTER TO SELL)
						
						obj.setShort(true);
						obj.setSell(true);
						obj.setSellPrice(newInfo.getBid());

						//vignesh insert into new trade here, (SHORT POSITION)
						obj.setNumOfTrade(obj.getObj().getAmtToTrade() / obj.getSellPrice());
						
						obj.setSend(true);
						obj.setId(nextTradeId++);
						pendingList.put(obj.getId(), wrapperObj);
						recordAndSendMessageToOB(false, obj.getSellPrice(), obj.getStockSymbol(), obj.getNumOfTrade());
						
						
						
						

					} else if (obj.isSell()) { //sold already and check for 1 percent profit (EXIT TO BUY)

						if( (obj.getShortAvg() <= (obj.getSellPrice() * 1.1)) || (obj.getShortAvg() >= (obj.getSellPrice() * 1.1))){ //if there is profit
							
							obj.setBuy(true);
							//buy here(or remove from trade) and get the profit 
				
							obj.setBuyPrice(newInfo.getAsk());

							//vignesh insert into new trade here, (SHORT POSITION)
							obj.setNumOfTrade(obj.getObj().getAmtToTrade() / obj.getBuyPrice());
							
							obj.setSend(true);
							obj.setId(nextTradeId++);
							pendingList.put(obj.getId(), wrapperObj);
							recordAndSendMessageToOB(true, obj.getBuyPrice(), obj.getStockSymbol(), obj.getNumOfTrade());
							
						
						
						} else if (obj.getShortAvg() >= (obj.getSellPrice() * 1.1)){ //if there is loss, exit here
													
							
							//buy here(or remove from trade) and get the loss 

						}
					}				
					
				}
				
			}
		}
	}


	public double monitorPrice(){

		int repeatCycleTime= 5000;

		timer = new Timer();
		timer.schedule(new MonitorPrice(), 0, REPEAT_CYCLE_TIME);
		return 0.0;
	}

	
	public boolean isMonitoring(){
		return isMonitoring;
	}
	
	/*
	 * called by the message bean one there is new trade entered the new trade
	 * is added into the list of ongoing orders and being monitored
	 */
	public void startNewTrade(String symbol, double loss, double profit,double totalAmountLimit) {
		System.out.println("[TWO  ALGO -start new trade]: stock " + symbol);
		TC_ATObject obj = new TC_ATObject(symbol, totalAmountLimit, profit, loss, "Two Moving Averages");
		TMAObjClass tmaObj = new TMAObjClass(obj);
		Order order = RecordOrder(symbol, OrderType.NONE, totalAmountLimit,
				OrderStatus.BEFORE_ENTERED, loss, profit);
		TwoMovingAverageWrapper wrapperObj = new TwoMovingAverageWrapper(tmaObj, order);
		storeToMonitor.add(wrapperObj);
	}
	
	public void startMonitoring(){
		isMonitoring = true;
		monitorPrice();
		
	}
	
	//RECORD TRADE AFTER RECEIVING MSG
	public void RecordTrade(TradeObject trade) {
		if (trade.getResult() == Result.FILLED) {
			int trader = 1;
			System.out.println("[TWO ALGO] Result is filled");
			TMAObjClass obj = pendingList.get(trade.getId()).tmaObject;
			Order affectedOrder = pendingList.get(trade.getId()).relatedOrder;
			CalculationHelper calculator = new CalculationHelper();
			StockList stock = stockListController.getStockListByName(affectedOrder.getStock());
			if((!obj.isShort()) && obj.isBuy() && (!obj.isSell())){//LONG ENTERED
				System.out.println("[TWO ALGO] LONG ENTER RECORD TRADE");
				Trade newTrade = calculator.createTrade("buy", 2, obj.getBuyPrice(), obj.getNumOfTrade(), trader, stock);
				affectedOrder.setStatus(OrderStatus.ENTERED);
				affectedOrder.setType(OrderType.LONG);
				affectedOrder.getTrades().add(newTrade);
				
			}
			else if( (!obj.isShort()) && obj.isBuy() && obj.isSell()){
				System.out.println("[TWO ALGO] LONG EXIT RECORD TRADE");
				Trade newTrade = calculator.createTrade("sell", 2, obj.getSellPrice(), obj.getNumOfTrade(), trader, stock);
				affectedOrder.setStatus(OrderStatus.EXIT);
				affectedOrder.setType(OrderType.LONG);
				affectedOrder.getTrades().add(newTrade);
				
				double PL_percentage = calculator.calPercentageChange(
						calculator.calculateCurrentValue(affectedOrder),
						affectedOrder.getTotal_amount());
				// TODO
				sendTCResponse(PL_percentage, obj.getNumOfTrade(), obj.getStockSymbol());
			}
			else if((obj.isShort()) && (!obj.isBuy()) && (obj.isSell())){
				System.out.println("[TWO ALGO] SHORT ENTER RECORD TRADE");
				Trade newTrade = calculator.createTrade("sell", 2, obj.getSellPrice(), obj.getNumOfTrade(), trader, stock);
				affectedOrder.setStatus(OrderStatus.ENTERED);
				affectedOrder.setType(OrderType.SHORT);
				affectedOrder.getTrades().add(newTrade);
			}
			else if((obj.isShort()) && (obj.isBuy()) && (obj.isSell())){
				System.out.println("[TWO ALGO] SHORT EXIT RECORD TRADE");
				Trade newTrade = calculator.createTrade("buy", 2, obj.getBuyPrice(), obj.getNumOfTrade(), trader, stock);
				affectedOrder.setStatus(OrderStatus.EXIT);
				affectedOrder.setType(OrderType.SHORT);
				affectedOrder.getTrades().add(newTrade);
				
				double PL_percentage = calculator.calPercentageChange(
						calculator.calculateCurrentValue(affectedOrder),
						affectedOrder.getTotal_amount());
				// TODO
				sendTCResponse(PL_percentage, obj.getNumOfTrade(), obj.getStockSymbol());
			}
			
			em.merge(affectedOrder);

		
			
			
		}
	}
	
	//record order to databas	
	private Order RecordOrder(String symbol, OrderType type, double total,
			OrderStatus status, double lossPer, double proPer) {
		Order newO = new Order();
		newO.setStock(symbol);
		newO.setLossPercentage(lossPer);
		newO.setProfitPercentage(proPer);
		newO.setStatus(status);
		newO.setTotal_amount(total);
		newO.setType(type);
		newO.setTrades(new ArrayList<Trade>());
		newO.setalgo_id(2);
		// TODO hard coded, need to add one file
		newO.setTrader_id(1);
		em.persist(newO);
		return newO;
	}
	
	
	
	private void recordAndSendMessageToOB(boolean isBuy, double price, String symbol, int numOfTrade){
		TradeObject tradeToMake = new TradeObject();
		tradeToMake.setBuy(isBuy);
		tradeToMake.setId(nextTradeId++);// check this. Why is
		tradeToMake.setPrice(price);
		tradeToMake.setStock(symbol);
		tradeToMake.setToNow();
		tradeToMake.setSize(numOfTrade);

		sendTradeMessage(tradeToMake);
		System.out.println("[TWO ALGO - SEND]");
	}
	
	private void sendTradeMessage(TradeObject trade) {
		try {
			orderBrokerMessageQueueConnection = (QueueConnection) orderBrokerMessageFactory
					.createConnection();
			QueueSession session = orderBrokerMessageQueueConnection
					.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session.createSender(orderBrokerMessageQueue);
			TextMessage textMsg = session.createTextMessage();
			String text = TradeMessenger.tradeToXML(trade);
			textMsg.setText(text);
			textMsg.setJMSCorrelationID(String.valueOf(trade.getId()));
			sender.send(textMsg);

//			tradeMap.put(textMsg.getJMSCorrelationID(), trade);
			orderBrokerMessageQueueConnection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void sendTCResponse(double pL_percentage, int numOfSharesTraded,
			String symbol) {
		// TODO Auto-generated method stub
		TC_ATObject obj = new TC_ATObject();
		obj.setAlgo("Bollinger Bands");
		obj.setAmtToTrade(numOfSharesTraded);
		if (pL_percentage > 0) {
			obj.setLoss(0);
			obj.setProfit(pL_percentage);
		} else {
			obj.setProfit(0);
			obj.setLoss(pL_percentage);
		}
		obj.setSymbol(symbol);

		sendResponseMessage(obj);
	}
	
	private void sendResponseMessage(TC_ATObject trade) {
		try {
			tradeControllerMessageQueueConnection = (QueueConnection) tradeControllerMessageFactory
					.createConnection();
			QueueSession session = tradeControllerMessageQueueConnection
					.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session
					.createSender(tradeControllerMessageQueue);
			TextMessage textMsg = session.createTextMessage();
			String text = TradingController.tradeToXML(trade);
			textMsg.setText(text);
			sender.send(textMsg);
			tradeControllerMessageQueueConnection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run(TC_ATObject obj) {
		// TODO Auto-generated method stub
		
	}



}
