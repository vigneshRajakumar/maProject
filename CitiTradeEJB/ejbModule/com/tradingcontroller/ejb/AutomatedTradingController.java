package com.tradingcontroller.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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
import com.stocklist.ejb.IStockListController;
import com.stocklist.ejb.StockListController;
import com.stocklist.jpa.StockList;
import com.trade.jpa.Order;
import com.trade.jpa.OrderStatus;
import com.trade.jpa.OrderType;
import com.trade.jpa.Trade;
import com.tradingcontroller.TC_ATObject;
import com.tradingcontroller.TradeObject;
import com.tradingcontroller.TradeObject.Result;
import com.tradingcontroller.mq.TradeMessenger;
import com.tradingcontroller.ejb.TradingController;

/**
 * Session Bean implementation class AutomatedTradingController
 */
class TradeStructureForLogging {
	TradeObject obj;
	OrderStatus status;
	OrderType type;
	int index;

	public TradeStructureForLogging(TradeObject obj, OrderStatus Status,
			OrderType type, int index) {
		this.obj = obj;
		this.status = Status;
		this.index = index;
		this.type = type;
	}
}

@Singleton
@LocalBean
public class AutomatedTradingController {
	@EJB
	MarketDataHandlerLocal marketDataHandler;

	@EJB
	IStockListController stockListController;

	@PersistenceContext(unitName = "ct_projectUnit")
	private EntityManager em;
	private boolean isMonitoring;
	private static ArrayList<Order> beforeEnterOrders = new ArrayList<Order>();
	private static ArrayList<Order> EnteredOrders = new ArrayList<Order>();
	private static ArrayList<bollingerStockWrapper> beforeStockList = new ArrayList<bollingerStockWrapper>();
	private static ArrayList<bollingerStockWrapper> enteredstockList = new ArrayList<bollingerStockWrapper>();

	private HashMap<String, TradeStructureForLogging> tradeMap = new HashMap<String, TradeStructureForLogging>();
	private CalculationHelper calculator = new CalculationHelper();
	private static QueueConnectionFactory orderBrokerMessageFactory;
	private static Context orderBrokerMessageContext;
	private static Queue orderBrokerMessageQueue;
	private static QueueConnection orderBrokerMessageQueueConnection;

	private static Context tradeControllerMessageContext;
	private static QueueConnectionFactory tradeControllerMessageFactory;
	private static Queue tradeControllerMessageQueue;
	private static QueueConnection tradeControllerMessageQueueConnection;

	private static int nextTradeId = 0;

	private final static String QUEUE_OB = "dynamicQueues/OrderBroker";
	private final static String QUEUE_TC = "jms/TCQueue";
	private final static String FACTORY_TC = "jms/TradeConnectionFactory";
	private final static String JMS_ID = "citiTrade";
	private final static String SHORT = "SHORT";
	private final static String LONG = "LONG";
	private final static int TIME_PERIOD = -20;

	private final static int MONITOR_TIME_INTERVAL = 5000;// monitor the data
															// every one minute
	
	private class bollingerStockWrapper {
		private String stockSymbol;
		private int numOfSharesTraded;
		private double enterPrice;
		private double exitPrice;
		private OrderType shortOrLong;
		private double totalAmount;// the total amount to trade
		private double stopLoss;
		private double movingAverage;
		private double percentageProfit;
		private double standardDeviation;
		private final static double BAND_WIDTH = 0.5;
		private boolean isPending = false;

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
			System.out.println("[WRAPPER RUN]");
			// get the historical data
			// calculate sigma
			standardDeviation = getStd(getHistoricalData());
			System.out.println("[AUTO ALGO]: stock " + stockSymbol + " mean: "
					+ movingAverage + "  std: " + standardDeviation);
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
					calReturn.get(Calendar.MONTH) + 1,
					calReturn.get(Calendar.DAY_OF_MONTH),
					today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1,
					today.get(Calendar.DAY_OF_MONTH));

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
			numOfSharesTraded = (int) (totalAmount / price);
			System.out
					.println(String
							.format("[AUTO ALGO - updateEnterInfo] : enterPrice: %f\nNumber of shares: %d",
									enterPrice, numOfSharesTraded));
		}

		public boolean isShortEnter(Stock marketStockInfo) {
			// current bid(sell) price is higher than the average more than 2
			// std
			if ((marketStockInfo.getBid() - movingAverage) > (BAND_WIDTH * standardDeviation)) {
				updateEnterInfo(OrderType.SHORT, marketStockInfo.getBid());
				return true;
			} else
				return false;
		}

		public boolean isLongEnter(Stock marketStockInfo) {
			// current ask(buy) price is lower than the average more than 2 std
			if ((movingAverage - marketStockInfo.getAsk()) > (BAND_WIDTH * standardDeviation)) {
				updateEnterInfo(OrderType.LONG, marketStockInfo.getAsk());
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
				System.out
						.println(String
								.format("[AUTO ALGO- check exit] stock: %s,    percentage P/L: %f,   type: %s",
										stockSymbol, percentagePL, "short"));
				if ((percentagePL <= (-stopLoss))
						|| (percentagePL >= percentageProfit)) {
					exitPrice = ask;
					System.out
							.println(String
									.format("[AUTO ALGO- exit] stock: %s,    percentage P/L: %f,   type: %s,   buy price: %d",
											stockSymbol, percentagePL, "short",
											ask));
					return true;
				}
			} else if (shortOrLong.equals(OrderType.LONG)) {
				percentagePL = checkLossOrGainPercentage(enterPrice, bid);
				System.out
						.println(String
								.format("[AUTO ALGO- check exit] stock: %s,    percentage P/L: %f,   type: %s",
										stockSymbol, percentagePL, "Long"));
				if ((percentagePL <= (-stopLoss))
						|| (percentagePL >= percentageProfit)) {
					exitPrice = bid;
					System.out
							.println(String
									.format("[AUTO ALGO- exit] stock: %s,    percentage P/L: %f,   type: %s,   sell price: %d",
											stockSymbol, percentagePL, "long",
											ask));
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

		public int getNumOfShares() {
			return numOfSharesTraded;
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

		public void setPending(boolean is) {
			isPending = is;
		}

		public boolean getIsPending() {
			return isPending;
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

			orderBrokerMessageContext = new InitialContext(props);
			orderBrokerMessageFactory = (QueueConnectionFactory) orderBrokerMessageContext
					.lookup("ConnectionFactory");
			orderBrokerMessageQueue = (Queue) orderBrokerMessageContext
					.lookup(QUEUE_OB);

			tradeControllerMessageContext = new InitialContext();
			tradeControllerMessageFactory = (QueueConnectionFactory) tradeControllerMessageContext
					.lookup(FACTORY_TC);
			tradeControllerMessageQueue = (Queue) tradeControllerMessageContext
					.lookup(QUEUE_TC);
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
		System.out.println("[AUTO ALGO-start new trade]: stock " + symbol);
		bollingerStockWrapper bollStockTrade = new bollingerStockWrapper(
				symbol, loss, profit, totalAmountLimit);
		bollStockTrade.run();
		beforeStockList.add(bollStockTrade);
		RecordOrder(symbol, OrderType.NONE, totalAmountLimit,
				OrderStatus.BEFORE_ENTERED, loss, profit);
	}

	public void RecordTrade(TradeObject trade) {
		System.out.println("nexttrade: " + nextTradeId);
		if (trade.getResult() == Result.FILLED) {
			// TODO Change open position
			System.out.println("[AUTO ALGO] Result is filled");
			TradeStructureForLogging tradeToBeRecord = tradeMap.get(String
					.valueOf(trade.getId()));

			int index = tradeToBeRecord.index;
			OrderType type = tradeToBeRecord.type;
			OrderStatus status = tradeToBeRecord.status;
			Order affectedOrder = beforeEnterOrders.get(index);
			bollingerStockWrapper affectedBoll = beforeStockList.get(index);
			System.out.println("[AUTO ALGO] record trade: " + type + ' '
					+ status + "Pending:  " + affectedBoll.getIsPending());
			if (affectedBoll.getIsPending())
				if (status == OrderStatus.ENTERED) {
					// update the looping list
					System.out.println("[AUTO ALGO] size: "
							+ beforeEnterOrders.size() + ' '
							+ beforeStockList.size());
					beforeEnterOrders.remove(index);
					beforeStockList.remove(index);
					affectedBoll.setPending(false);
					enteredstockList.add(affectedBoll);
					EnteredOrders.add(affectedOrder);

					// create trade object
					String Tradetype = (type.equals(OrderType.LONG) ? "buy"
							: "sell");
					StockList stock = stockListController
							.getStockListByName(affectedOrder.getStock());
					Trade newTrade = calculator.createTrade(Tradetype,
							affectedOrder.getalgo_id(),
							affectedBoll.getEnterPrice(),
							affectedBoll.getNumOfShares(),
							affectedOrder.getTrader_id(), stock);

					System.out
							.println("!!!!!!!DEBUG MSG!!!!!!!!  Created an new trade with symbol "
									+ affectedOrder.getStock()
									+ " at "
									+ Tradetype);
					// update order object

					affectedOrder.setStatus(status);
					affectedOrder.setType(type);
					affectedOrder.getTrades().add(newTrade);
					System.out
							.println("!!!!!!!DEBUG MSG!!!!!!!!  Update the order status to "
									+ status + " with trade type " + Tradetype);
					// em.persist(newTrade);
					// em.persist(affectedOrder);
					em.merge(affectedOrder);
				} else {// exit statuts
					EnteredOrders.remove(index);
					enteredstockList.remove(index);
					affectedBoll.setPending(false);
					// create trade object
					String Tradetype = (type.equals(OrderType.LONG) ? "sell"
							: "buy");
					StockList stock = stockListController
							.getStockListByName(affectedOrder.getStock());
					Trade newTrade = calculator.createTrade(Tradetype,
							affectedOrder.getalgo_id(),
							affectedBoll.getEnterPrice(),
							affectedBoll.getNumOfShares(),
							affectedOrder.getTrader_id(), stock);

					System.out
							.println("!!!!!!!DEBUG MSG!!!!!!!!  Created an new trade with symbol "
									+ affectedOrder.getStock()
									+ " at "
									+ Tradetype);
					// update order object
					affectedOrder.setStatus(status);
					affectedOrder.setType(type);
					affectedOrder.getTrades().add(newTrade);

					System.out
							.println("!!!!!!!DEBUG MSG!!!!!!!!  Update the order status to "
									+ status + " with trade type " + Tradetype);
					// em.persist(newTrade);
					// em.persist(affectedOrder);
					em.merge(affectedOrder);

					double PL_percentage = calculator.calPercentageChange(
							calculator.calculateCurrentValue(affectedOrder),
							affectedOrder.getTotal_amount());
					// TODO
					sendTCResponse(PL_percentage,
							affectedBoll.numOfSharesTraded,
							affectedBoll.getStockSymbol());

				}

		}// result is fill
			// StartMonitoring();
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

	public void RecordOrder(String symbol, OrderType type, double total,
			OrderStatus status, double lossPer, double proPer) {
		Order newO = new Order();
		newO.setStock(symbol);
		newO.setLossPercentage(lossPer);
		newO.setProfitPercentage(proPer);
		newO.setStatus(status);
		newO.setTotal_amount(total);
		newO.setType(type);
		newO.setTrades(new ArrayList<Trade>());
		newO.setalgo_id(1);
		// TODO hard coded, need to add one file
		newO.setTrader_id(1);
		em.persist(newO);
		beforeEnterOrders.add(newO);// add to the list
		System.out.println("[DEBUG MSG(add beforeenterorder list):       ]"
				+ beforeEnterOrders.size());
	}

	public void StartMonitoring() {
		monitorPriceToEnter();
		monitorPriceToExit();
	}

	private void sendTradeMessage(TradeStructureForLogging trade) {
		try {
			orderBrokerMessageQueueConnection = (QueueConnection) orderBrokerMessageFactory
					.createConnection();
			QueueSession session = orderBrokerMessageQueueConnection
					.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender sender = session.createSender(orderBrokerMessageQueue);
			TextMessage textMsg = session.createTextMessage();
			String text = TradeMessenger.tradeToXML(trade.obj);
			textMsg.setText(text);
			textMsg.setJMSCorrelationID(String.valueOf(trade.obj.getId()));
			sender.send(textMsg);

			tradeMap.put(textMsg.getJMSCorrelationID(), trade);
			orderBrokerMessageQueueConnection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	/*
	 * Check enter condition for every one minute for every order with the
	 * 'BEFORE ENTER' status if the condition hit, 1. send message to order
	 * broker, 2. if trade approved 3.recorder trade, 4. change order status
	 */
	public void monitorPriceToEnter() {
		Timer timer = new Timer();
		// Schedule to run after every 1 minuts(3000 millisecond)
		timer.schedule(new TimerTaskCheckEnter() /*
												 * {
												 * 
												 * @Override public void run() {
												 * System.out.println(
												 * "[AUTO ALGO-monitor exit] size: "
												 * +
												 * beforeEnterOrders.size()+' '+
												 * beforeStockList.size() );
												 * ArrayList<Integer>
												 * pendingList = new
												 * ArrayList<Integer>(); for
												 * (int i = 0; i <
												 * beforeStockList.size(); i++)
												 * { bollingerStockWrapper trade
												 * = beforeStockList.get(i);
												 * Order order =
												 * beforeEnterOrders.get(i);
												 * System.out.println(
												 * "[AUTO ALGO-monitor enter] (loop beforeenterorder symbol):       ]"
												 * +order.getStock()); Stock
												 * newInfo = marketDataHandler.
												 * getStockBySymbol(trade
												 * .getStockSymbol()); if
												 * ((!pendingList.contains(i))
												 * &&
												 * trade.isShortEnter(newInfo))
												 * { // enter short // TODO SEND
												 * MESSAGE, IF SUCESS
												 * TradeObject tradeToMake = new
												 * TradeObject();
												 * tradeToMake.setBuy(false);
												 * tradeToMake
												 * .setId(nextTradeId++);//
												 * check this. Why is
												 * tradeToMake
												 * .setPrice(newInfo.getAsk());
												 * tradeToMake
												 * .setStock(trade.getStockSymbol
												 * ()); tradeToMake.setToNow();
												 * tradeToMake.setSize((int)
												 * trade.numOfSharesTraded);
												 * 
												 * sendTradeMessage(new
												 * TradeStructureForLogging(
												 * tradeToMake,
												 * OrderStatus.ENTERED,
												 * OrderType.SHORT, i));
												 * System.out.println(
												 * "[AUTO ALGO-monitor enter sort- sent msg]"
												 * ); // update order
												 * 
												 * //
												 * trade.updateEnterInfo(OrderType
												 * .SHORT, // newInfo.getAsk());
												 * pendingList.add(i);
												 * 
												 * } else if (
												 * (!pendingList.contains(i))
												 * &&trade.isLongEnter(newInfo))
												 * { // enter long // TODO SEND
												 * MESSAGE, IF SUCESS
												 * TradeObject tradeToMake = new
												 * TradeObject();
												 * tradeToMake.setBuy(true);
												 * tradeToMake
												 * .setId(nextTradeId++);
												 * tradeToMake
												 * .setPrice(newInfo.getBid());
												 * tradeToMake
												 * .setStock(trade.getStockSymbol
												 * ()); tradeToMake.setToNow();
												 * tradeToMake.setSize((int)
												 * trade.numOfSharesTraded);
												 * sendTradeMessage(new
												 * TradeStructureForLogging(
												 * tradeToMake,
												 * OrderStatus.ENTERED,
												 * OrderType.LONG, i));
												 * System.out.println(
												 * "[AUTO ALGO-monitor enter long- sent msg]"
												 * ); //
												 * trade.updateEnterInfo(OrderType
												 * .LONG, // newInfo.getBid());
												 * pendingList.add(i);
												 * 
												 * }
												 * 
												 * } } }
												 */, 0, MONITOR_TIME_INTERVAL);
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
		timer.schedule(new TimerTaskCheckExit() /*
												 * {
												 * 
												 * @Override public void run() {
												 * ArrayList<Integer>
												 * pendingList = new
												 * ArrayList<Integer>();
												 * 
												 * System.out.println(
												 * "[AUTO ALGO-monitor exit] size: "
												 * + EnteredOrders.size() + ' '
												 * + enteredstockList.size());
												 * for (int i = 0; i <
												 * enteredstockList.size(); i++)
												 * { bollingerStockWrapper trade
												 * = enteredstockList.get(i);
												 * Order order =
												 * EnteredOrders.get(i); Stock
												 * newInfo = marketDataHandler.
												 * getStockBySymbol(trade
												 * .getStockSymbol());
												 * 
												 * // SHORT EXIT // SHORT EXIT
												 * if
												 * ((!pendingList.contains(i))
												 * && trade.isExit(newInfo)) {
												 * System
												 * .out.println("[AUTO ALGO EXIT] "
												 * ); TradeObject tradeToMake =
												 * new TradeObject();
												 * tradeToMake
												 * .setBuy(trade.getShortOrLong
												 * () == OrderType.SHORT);
												 * tradeToMake
												 * .setId(nextTradeId++);//
												 * check this. Why is
												 * tradeToMake
												 * .setPrice(trade.getExitPrice
												 * ());
												 * tradeToMake.setStock(trade
												 * .getStockSymbol());
												 * tradeToMake.setToNow();
												 * tradeToMake.setSize((int)
												 * trade.numOfSharesTraded);
												 * TradeStructureForLogging tsfl
												 * = new
												 * TradeStructureForLogging(
												 * tradeToMake,
												 * OrderStatus.EXIT, trade
												 * .getShortOrLong(), i);
												 * sendTradeMessage(tsfl);
												 * pendingList.add(i); } }
												 * 
												 * } }
												 */, 0, MONITOR_TIME_INTERVAL);
	}

	public boolean isMonitoring() {
		return isMonitoring;
	}

	public void setMonitoring(boolean isMonitoring) {
		this.isMonitoring = isMonitoring;
	}

	class TimerTaskCheckExit extends TimerTask {
		@Override
		public void run() {
			ArrayList<Integer> pendingList = new ArrayList<Integer>();

			System.out.println("[AUTO ALGO-monitor exit] size: "
					+ EnteredOrders.size() + ' ' + enteredstockList.size());
			for (int i = 0; i < enteredstockList.size(); i++) {
				bollingerStockWrapper trade = enteredstockList.get(i);
				Order order = EnteredOrders.get(i);
				Stock newInfo = marketDataHandler.getStockBySymbol(trade
						.getStockSymbol());
				System.out.println("[AUTO ALGO-monitor exit] symbol: "
						+ order.getStock() + ' ' + "Pending: "
						+ trade.isPending);

				// SHORT EXIT
				// SHORT EXIT
				if ((!trade.getIsPending()) && trade.isExit(newInfo)) {
					System.out.println("[AUTO ALGO EXIT] ");
					TradeObject tradeToMake = new TradeObject();
					tradeToMake
							.setBuy(trade.getShortOrLong() == OrderType.SHORT);
					tradeToMake.setId(nextTradeId++);// check this. Why is
					tradeToMake.setPrice(trade.getExitPrice());
					tradeToMake.setStock(trade.getStockSymbol());
					tradeToMake.setToNow();
					tradeToMake.setSize((int) trade.numOfSharesTraded);
					TradeStructureForLogging tsfl = new TradeStructureForLogging(
							tradeToMake, OrderStatus.EXIT,
							trade.getShortOrLong(), i);
					sendTradeMessage(tsfl);
					trade.setPending(true);
				}
			}

		}

	}

	class TimerTaskCheckEnter extends TimerTask {

		public TimerTaskCheckEnter() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			System.out.println("[AUTO ALGO-monitor enter] size: "
					+ beforeEnterOrders.size() + ' ' + beforeStockList.size());
			ArrayList<Integer> pendingList = new ArrayList<Integer>();
			for (int i = 0; i < beforeStockList.size(); i++) {
				bollingerStockWrapper trade = beforeStockList.get(i);
				Order order = beforeEnterOrders.get(i);
				System.out
						.println("[AUTO ALGO-monitor enter] (loop  symbol):       ]"
								+ order.getStock()
								+ "Pending: "
								+ trade.isPending);
				Stock newInfo = marketDataHandler.getStockBySymbol(trade
						.getStockSymbol());
				if ((!trade.getIsPending()) && trade.isShortEnter(newInfo)) {
					// enter short
					// TODO SEND MESSAGE, IF SUCESS
					TradeObject tradeToMake = new TradeObject();
					tradeToMake.setBuy(false);
					tradeToMake.setId(nextTradeId++);// check this. Why is
					tradeToMake.setPrice(newInfo.getAsk());
					tradeToMake.setStock(trade.getStockSymbol());
					tradeToMake.setToNow();
					tradeToMake.setSize((int) trade.numOfSharesTraded);

					sendTradeMessage(new TradeStructureForLogging(tradeToMake,
							OrderStatus.ENTERED, OrderType.SHORT, i));
					System.out
							.println("[AUTO ALGO-monitor enter sort- sent msg]");
					// update order

					// trade.updateEnterInfo(OrderType.SHORT,
					// newInfo.getAsk());
					trade.setPending(true);

				} else if ((!trade.getIsPending())
						&& trade.isLongEnter(newInfo)) {
					// enter long
					// TODO SEND MESSAGE, IF SUCESS
					TradeObject tradeToMake = new TradeObject();
					tradeToMake.setBuy(true);
					tradeToMake.setId(nextTradeId++);
					tradeToMake.setPrice(newInfo.getBid());
					tradeToMake.setStock(trade.getStockSymbol());
					tradeToMake.setToNow();
					tradeToMake.setSize((int) trade.numOfSharesTraded);
					sendTradeMessage(new TradeStructureForLogging(tradeToMake,
							OrderStatus.ENTERED, OrderType.LONG, i));
					System.out
							.println("[AUTO ALGO-monitor enter long- sent msg]");
					// trade.updateEnterInfo(OrderType.LONG,
					// newInfo.getBid());
					trade.setPending(true);

				}

			}
		}
	}

}
