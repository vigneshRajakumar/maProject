package com.tradingcontroller;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.EJB;

import com.marketdatahandle.jpa.Stock;
import com.marketdatahandler.ejb.HistoricalData;
import com.marketdatahandler.ejb.MarketDataHandlerLocal;


public class BolingerThread extends Thread {
	private String symbol;
	private long numOfSharesTraded;
	private double enterPrice;
	private double exitPrice;
	private String shortOrLong;
	private double totalAmount;// the total amount to trade
	private double stopLoss;
	private double movingAverage;
	private double percentageProfit;
	private double standardDeviation;
	private final static String SHORT = "short";
	private final static String LONG = "long";
	private final static double BAND_WIDTH = 2;
	private final static int TIME_PERIOD = -20;
	private final static int MONITOR_TIME_INTERVAL = 60000;// monitor the data
															// every one minute
	@EJB
	private MarketDataHandlerLocal marketDataHandler;

	public BolingerThread(String symbol, double loss, double profit,
			double totalAmountLimit) {
		this.symbol = symbol;
		stopLoss = loss;
		percentageProfit = profit;
		totalAmount = totalAmountLimit;
		numOfSharesTraded = 0;// defult

	}

	public void run() {
		// get the historical data
		// calculate sigma
		standardDeviation = getStd(getHistoricalData());
		// get the context and queue
		// loop to find entry condition
		monitorPriceToEnter();
		// make the trades
		// send messages
		// check the exit condition
		// exit the loop
		// exit thread
	}

	// onmessage listner

	/*
	 * get the last time_period of market data, and return a list of historical
	 * data
	 */
	public ArrayList<HistoricalData> getHistoricalData() {
		ArrayList<HistoricalData> data = null;
		Calendar today = Calendar.getInstance();
		Calendar calReturn = Calendar.getInstance();
		calReturn.add(Calendar.DATE, TIME_PERIOD);
		data = marketDataHandler.getHistoricalDataBySymbol(symbol,
				calReturn.get(Calendar.YEAR), calReturn.get(Calendar.MONTH),
				calReturn.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR),
				today.get(Calendar.YEAR), today.get(Calendar.YEAR));

		return data;
	}

	public double getMeanPrice(ArrayList<HistoricalData> data) {
		double sum = 0;
		for (HistoricalData d : data) {
			sum += d.getAdj_colse();
		}
		return sum / (data.size());
	}

	public double getStd(ArrayList<HistoricalData> data) {
		movingAverage = getMeanPrice(data);
		double sumDiff = 0;
		for (HistoricalData d : data) {
			sumDiff += Math.pow((d.getAdj_colse() - movingAverage), 2);
		}
		return Math.sqrt(sumDiff / data.size());
	}

	public void monitorPriceToEnter() {
		Timer timer = new Timer();
		// Schedule to run after every 1 minuts(3000 millisecond)
		timer.schedule(new TimerTask() {
			// run is a abstract method that defines task performed at scheduled
			// time.
			@Override
			public void run() {
				Stock newInfo = marketDataHandler.getStockBySymbol(symbol);
				double ask = newInfo.getAsk();
				double bid = newInfo.getBid();
				double mid_price = (ask + bid) / 2.0;
				// overvalued, go short
				if ((mid_price - movingAverage) > (BAND_WIDTH * standardDeviation)) {
					enterPrice = bid;// set the trade price
					shortOrLong = SHORT;
					// TODO VIGNESH
					// add the send message to short
					// if success, create a new trade and persist into database
				}
				// undervalued. go long
				else if ((mid_price - movingAverage) < (BAND_WIDTH * standardDeviation)) {
					enterPrice = ask; // set the trade price
					shortOrLong = LONG;
					// TODO VIGNESH
					// add the send message to long
				}

			}
		}, MONITOR_TIME_INTERVAL);
	}

	public void monitorPriceToExit() {
		// will there be any data lost???????????????
		numOfSharesTraded = (long) (totalAmount / enterPrice);
		

		Timer timer = new Timer();
		// Schedule to run after every 1 minute
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Stock newInfo = marketDataHandler.getStockBySymbol(symbol);
				double ask = newInfo.getAsk();
				double bid = newInfo.getBid();

				// SHORT EXIT
				double shortPL;
				double longPL;
				if ((shortPL = checkLossOrGainPercentage(ask)) > stopLoss
						|| shortPL > percentageProfit) {
					exitPrice = ask;
					// TODO add trade

				}
				// LONG EXIT
				else if ((longPL = checkLossOrGainPercentage(bid)) > stopLoss
						|| longPL > percentageProfit) {
					exitPrice = bid;
					// TODO add trade
				}

			}

			// get the percentage of gain or loss
			public double checkLossOrGainPercentage(double marketPrice) {
				return ((marketPrice - enterPrice) * numOfSharesTraded)
						/ totalAmount;
			}
		}, MONITOR_TIME_INTERVAL);
	}
}
