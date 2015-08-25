package com.marketdatahandler.client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.naming.InitialContext;

import com.marketdatahandler.ejb.HistoricalData;
import com.marketdatahandler.ejb.MarketDataHandlerRemote;

class Task extends TimerTask {


    int count = 1;

    // run is a abstract method that defines task performed at scheduled time.
    public void run() {
       
    }
}



public class Main {
	private String symbol = "AAPL";
	private double stopLoss;
	private double movingAverage;
	private double percentageProfit;
	private double standardDeviation;
	private final static double BAND_WIDTH = 2;
	private final static int TIME_PERIOD = -20;
	MarketDataHandlerRemote marketDataHandler;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main mainPro = new Main();
		mainPro.test();
	}

	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public Main() {
		super();
	}
	
	
	public void test(){
		try{
			InitialContext context = new InitialContext();
			marketDataHandler = (MarketDataHandlerRemote)context.lookup("com.marketdatahandler.ejb.MarketDataHandlerRemote");
//			System.out.println(handler.getStockBySymbol("aapl").getAsk());
//			ArrayList<HistoricalData> result = handler.getHistoricalDataBySymbol("aapl", 2015, 8, 3, 2015, 8, 20);
//			System.out.println("mean:   "+ getMeanPrice(result));
//			System.out.println("std:   "+ getStd(result));
			standardDeviation = getStd( getHistoricalData());
			System.out.println("mean:   "+ this.movingAverage);
			System.out.println("std:   "+ this.standardDeviation);
			
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	public ArrayList<HistoricalData> getHistoricalData() {
		ArrayList<HistoricalData> data = null;
		Calendar today = Calendar.getInstance();
		Calendar calReturn = Calendar.getInstance();
		calReturn.add(Calendar.DATE, TIME_PERIOD);
//		Date ago = calReturn.getTime();
		System.out.println("date:   "+ calReturn.get(Calendar.YEAR) +calReturn.get(Calendar.MONTH)+calReturn.get(Calendar.DAY_OF_MONTH));
		data = marketDataHandler.getHistoricalDataBySymbol(symbol, calReturn.get(Calendar.YEAR),
				calReturn.get(Calendar.MONTH), calReturn.get(Calendar.DAY_OF_MONTH), today.get(Calendar.YEAR),
				today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
//		data = marketDataHandler.getHistoricalDataBySymbol(symbol, 2015, 8, 4, 2015, 8, 24);

		return data;
	}

	
	public static void MonitorPrice(){
		 Timer timer = new Timer();


	       // Schedule to run after every 3 second(3000 millisecond)
	       timer.schedule( new Task(), 3000);   
	}
	
	
	
	
	
	
	public static double getMeanPrice(ArrayList<HistoricalData> data){
		double sum = 0;
		for(HistoricalData d: data){
			sum += d.getAdj_colse();
		}
		return sum/(data.size());
	}
	
	public static double getStd(ArrayList<HistoricalData> data){
		double mean = getMeanPrice(data);
		double sumDiff = 0;
		for(HistoricalData d: data){
			sumDiff += Math.pow((d.getAdj_colse()-mean),2);
		}
		return Math.sqrt(sumDiff/data.size());
	}
	

}