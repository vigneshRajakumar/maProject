package com.tradingcontroller.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.marketdatahandle.jpa.Stock;
import com.marketdatahandler.ejb.HistoricalData;
import com.marketdatahandler.ejb.MarketDataHandlerLocal;
import com.trade.jpa.Order;
import com.trade.jpa.OrderStatus;
import com.trade.jpa.OrderType;
import com.trade.jpa.Trade;
import com.tradingcontroller.TradeObject;
import com.tradingcontroller.TradeObject.Result;
import com.tradingcontroller.mq.TradeMessenger;;

/**
 * Session Bean implementation class AutomatedTradingController
 */

@Singleton
@LocalBean
public class AutomatedTradingController {
	@EJB
	MarketDataHandlerLocal marketDataHandler;

	@PersistenceContext(unitName = "ct_projectUnit")
	private EntityManager em;
	private boolean isMonitoring;
	private ArrayList<Order> beforeEnterOrders = new ArrayList<Order>();
	private ArrayList<Order> EnteredOrders = new ArrayList<Order>();
	private ArrayList<bollingerStockWrapper> beforeStockList = new ArrayList<bollingerStockWrapper>();
	private ArrayList<bollingerStockWrapper> enteredstockList = new ArrayList<bollingerStockWrapper>();
	private HashMap<String,TradeObject> tradeMap = new HashMap<String,TradeObject>();
	
	private static Context ctx;
	private static QueueConnectionFactory factory;
	private static Queue orderBrokerMessageQueue;
	private static QueueConnection orderBrokerMessageQueueConnection;
	private static int nextTradeId = 0;
	
	private final static String QUEUE_OB = "dynamicQueues/OrderBroker";
	private final static String JMS_ID = "citiTrade";
	private final static String SHORT = "SHORT";
	private final static String LONG = "LONG";
	private final static int TIME_PERIOD = -20;
	private final static int MONITOR_TIME_INTERVAL = 60000;// monitor the data
															// every one minute
	
	private class bollingerStockWrapper {
		private String stockSymbol;
		private long numOfSharesTraded;
		private double enterPrice;
		private double exitPrice;
		private OrderType shortOrLong;
		private double totalAmount;// the total amount to trade
		private double stopLoss;
		private double movingAverage;
		private double percentageProfit;
		private double standardDeviation;
		private final static double BAND_WIDTH = 2;

		// constructor
		public bollingerStockWrapper(String symbol, double loss, double profit,
				double totalAmountLimit) {
			stockSymbol = symbol;
			stopLoss = loss;
			percentageProfit = profit;
			totalAmount = totalAmountLimit;
			numOfSharesTraded = 0;// defult

		}

		public void run() {
			// get the historical data
			// calculate sigma
			standardDeviation = getStd(getHistoricalData());
			// loop to find entry condition

		}

		/*
		 * get the last time_period of market data, and return a list of
		 * historical data
		 */
		private ArrayList<HistoricalData> getHistoricalData() {
			ArrayList<HistoricalData> data = null;
			Calendar today = Calendar.getInstance();
			Calendar calReturn = Calendar.getInstance();
			calReturn.add(Calendar.DATE, TIME_PERIOD);
			data = marketDataHandler.getHistoricalDataBySymbol(stockSymbol,
					calReturn.get(Calendar.YEAR),
					calReturn.get(Calendar.MONTH),
					calReturn.get(Calendar.DAY_OF_MONTH),
					today.get(Calendar.YEAR), today.get(Calendar.YEAR),
					today.get(Calendar.YEAR));

			return data;
		}

		private double getMeanPrice(ArrayList<HistoricalData> data) {
			double sum = 0;
			for (HistoricalData d : data) {
				sum += d.getAdj_colse();
			}
			return sum / (data.size());
		}

		private double getStd(ArrayList<HistoricalData> data) {
			movingAverage = getMeanPrice(data);
			double sumDiff = 0;
			for (HistoricalData d : data) {
				sumDiff += Math.pow((d.getAdj_colse() - movingAverage), 2);
			}
			return Math.sqrt(sumDiff / data.size());
		}

		public void updateEnterInfo(OrderType type, double price) {
			enterPrice = price;
			shortOrLong = type;

			numOfSharesTraded = (long) (totalAmount / price);
		}

		public boolean isShortEnter(Stock marketStockInfo) {
			if ((movingAverage - marketStockInfo.getAsk()) > (BAND_WIDTH * standardDeviation)) {
				updateEnterInfo(OrderType.SHORT, marketStockInfo.getAsk());
				return true;
			} else
				return false;
		}

		public boolean isLongEnter(Stock marketStockInfo) {
			if ((marketStockInfo.getBid() - movingAverage) > (BAND_WIDTH * standardDeviation)) {
				updateEnterInfo(OrderType.LONG, marketStockInfo.getBid());
				return true;
			}
			return false;
		}

		public boolean isExit(Stock marketStockInfo) {
			double ask = marketStockInfo.getAsk();
			double bid = marketStockInfo.getBid();
			double percentagePL;
			if (shortOrLong.equals(OrderType.SHORT)) {
				percentagePL = checkLossOrGainPercentage(ask, enterPrice);
				if ((percentagePL < (-stopLoss))
						|| (percentagePL > percentageProfit)) {
					exitPrice = ask;
					return true;
				}
			} else if (shortOrLong.equals(OrderType.LONG)) {
				percentagePL = checkLossOrGainPercentage(enterPrice, bid);
				if ((percentagePL < (-stopLoss))
						|| (percentagePL > percentageProfit)) {
					exitPrice = bid;
					return true;
				}
			}
			return false;
		}

		// get the percentage of gain or loss
		private double checkLossOrGainPercentage(double buyPrice,
				double sellPrice) {
			return ((sellPrice - buyPrice) * numOfSharesTraded) / totalAmount;
		}

		public String getStockSymbol() {
			return stockSymbol;
		}

		public double getEnterPrice() {
			return enterPrice;
		}

		public double getExitPrice() {
			return exitPrice;
		}

		public OrderType getShortOrLong() {
			return shortOrLong;
		}

		public double getTotalAmount() {
			return totalAmount;
		}

		public double getStopLoss() {
			return stopLoss;
		}

		public double getProfitPercentage() {
			return percentageProfit;
		}

		public void setEnterPrice(double enterPrice) {
			this.enterPrice = enterPrice;
		}

	}// end of wrapper
		// list to store all the

	/**
	 * Default constructor.
	 */
	public AutomatedTradingController() {
		// TODO Auto-generated constructor stub
		try {
			isMonitoring = false;
			Properties props = new Properties();
			props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
					"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

			ctx = new InitialContext(props);
			factory = (QueueConnectionFactory) ctx.lookup("ConnectionFactory");
			orderBrokerMessageQueue = (Queue) ctx.lookup(QUEUE_OB);
			
			
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * called by the message bean one there is new trade entered the new trade
	 * is added into the list of ongoing orders and being monitored
	 */
	public void startNewTrade(String symbol, double loss, double profit,
			double totalAmountLimit) {
		bollingerStockWrapper bollStockTrade = new bollingerStockWrapper(
				symbol, loss, profit, totalAmountLimit);
		bollStockTrade.run();
		beforeStockList.add(bollStockTrade);
		RecordOrder(OrderType.NONE, totalAmountLimit,
				OrderStatus.BEFORE_ENTERED, loss, profit);
	}

	public void RecordTrade(TradeObject trade) {
		if(trade.getResult()==Result.FILLED) {
			//TODO Change open position
		}
		//TODO persist trade to db
	}

	public void RecordOrder(OrderType type, double total, OrderStatus status,
			double lossPer, double proPer) {
		Order newO = new Order();
		newO.setLossPercentage(lossPer);
		newO.setProfitPercentage(proPer);
		newO.setStatus(status);
		newO.setTotal_amount(total);
		newO.setType(type);
		beforeEnterOrders.add(newO);// add to the list
		em.persist(newO);
	}

	public void StartMonitoring() {
		monitorPriceToEnter();
	}

	private void sendTradeMessage(TradeObject trade) {
		try {
			orderBrokerMessageQueueConnection = (QueueConnection) factory
					.createConnection();
			QueueSession session = orderBrokerMessageQueueConnection
					.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session.createSender(orderBrokerMessageQueue);
			TextMessage textMsg = session.createTextMessage();
			String text = TradeMessenger.tradeToXML(trade);
			textMsg.setText(text);
			textMsg.setJMSCorrelationID(String.valueOf(trade.getId()));
			sender.send(textMsg);
			tradeMap.put(textMsg.getJMSCorrelationID(), trade); 
			orderBrokerMessageQueueConnection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Check enter condition for every one minute for every order with the
	 * 'BEFORE ENTER' status if the condition hit, 1. send message to order
	 * broker, 2. if trade approved 3.recorder trade, 4. change order status
	 */
	public void monitorPriceToEnter() {
		Timer timer = new Timer();
		// Schedule to run after every 1 minuts(3000 millisecond)
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ArrayList<Integer> changedStatusIndex = new ArrayList<Integer>();
				for (int i = 0; i < beforeStockList.size(); i++) {
					bollingerStockWrapper trade = beforeStockList.get(i);
					Order order = beforeEnterOrders.get(i);
					Stock newInfo = marketDataHandler.getStockBySymbol(trade
							.getStockSymbol());
					if (trade.isShortEnter(newInfo)) {
						// enter short
						// TODO SEND MESSAGE, IF SUCESS
						TradeObject tradeToMake = new TradeObject();
						tradeToMake.setBuy(false);
						tradeToMake.setId(nextTradeId++);//check this. Why is it needed
						tradeToMake.setPrice(newInfo.getAsk());
						tradeToMake.setStock(trade.getStockSymbol());
						tradeToMake.setToNow();
						tradeToMake.setSize((int) trade.numOfSharesTraded);
						sendTradeMessage(tradeToMake);
						// update order
						order.setType(OrderType.SHORT);
						order.setStatus(OrderStatus.ENTERED);
						em.persist(order);
						//trade.updateEnterInfo(OrderType.SHORT, newInfo.getAsk());
						changedStatusIndex.add(i);

					} else if (trade.isLongEnter(newInfo)) {
						// enter long
						// TODO SEND MESSAGE, IF SUCESS
						TradeObject tradeToMake = new TradeObject();
						tradeToMake.setBuy(true);
						tradeToMake.setId(nextTradeId++);
						tradeToMake.setPrice(newInfo.getBid());
						tradeToMake.setStock(trade.getStockSymbol());
						tradeToMake.setToNow();
						tradeToMake.setSize((int) trade.numOfSharesTraded);
						sendTradeMessage(tradeToMake);
						order.setType(OrderType.LONG);
						order.setStatus(OrderStatus.ENTERED);
						em.persist(order);
						//trade.updateEnterInfo(OrderType.LONG, newInfo.getBid());
						changedStatusIndex.add(i);

					}
					// TODO
					// remove the change status from the list
				}
			}
		}, MONITOR_TIME_INTERVAL);
	}

	/*
	 * Check exit condition for every one minute for every order with the
	 * entered status if the condition hit, 1. send message to order broker, 2.
	 * recorder trade, 3. change order status
	 */
	public void monitorPriceToExit() {
		// will there be any data lost???????????????
		// numOfSharesTraded = (long) (totalAmount / enterPrice);

		Timer timer = new Timer();
		// Schedule to run after every 1 minute
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ArrayList<Integer> changedStatusIndex = new ArrayList<Integer>();
				for (int i = 0; i < enteredstockList.size(); i++) {
					bollingerStockWrapper trade = enteredstockList.get(i);
					Order order = EnteredOrders.get(i);
					Stock newInfo = marketDataHandler.getStockBySymbol(trade
							.getStockSymbol());

					// SHORT EXIT
					double shortPL;
					double longPL;
					// SHORT EXIT
					if (trade.isExit(newInfo)) {
						// TODO SEND MESSAGE
						// TODO ADD TRADE
						// TODO UPDATE DATABASE
					}
				}

			}
		}, MONITOR_TIME_INTERVAL);
	}

	public boolean isMonitoring() {
		return isMonitoring;
	}

	public void setMonitoring(boolean isMonitoring) {
		this.isMonitoring = isMonitoring;
	}

	/*
	 * public ArrayList<bollingerStockWrapper> getStockList() { return
	 * stockList; }
	 * 
	 * public void setStockList(ArrayList<bollingerStockWrapper> stockList) {
	 * this.stockList = stockList; }
	 */
}
