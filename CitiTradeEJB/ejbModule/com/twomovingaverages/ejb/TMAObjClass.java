package com.twomovingaverages.ejb;

import java.util.ArrayList;
import java.util.Calendar;

import com.tradingcontroller.TC_ATObject;

public class TMAObjClass {
	
	String stockSymbol = "";
	int longCounter = 0;
	int shortCounter = 0;
	double shortAvg = 0;
	double longAvg = 0;

	boolean buy = false;
	boolean sell = false ;
	
	TC_ATObject obj = null;

	double buyPrice = 0;
	double sellPrice = 0;
	
	public TMAObjClass(TC_ATObject obj){
		
		stockSymbol = obj.getSymbol();
		setLongAvg(obj.getSymbol());
		setShortAvg(obj.getSymbol());	
	}	

	public int getLongCounter() {
		return longCounter;
	}

	public void setLongCounter(int longCounter) {
		this.longCounter = longCounter;
	}

	public int getShortCounter() {
		return shortCounter;
	}

	public void setShortCounter(int shortCounter) {
		this.shortCounter = shortCounter;
	}

	
	public String getStockSymbol() {
		return stockSymbol;
	}
	public TC_ATObject getObj() {
		return obj;
	}
	public void setObj(TC_ATObject obj) {
		this.obj = obj;
	}
	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}
	
	public double getShortAvg() {
		return shortAvg;
	}
	public void setShortAvg(double shortAvg) {
		this.shortAvg = shortAvg;
	}
	public double getLongAvg() {
		return longAvg;
	}
	public void setLongAvg(double longAvg) {
		this.longAvg = longAvg;
	}
	public boolean isBuy() {
		return buy;
	}
	public void setBuy(boolean buy) {
		this.buy = buy;
	}
	public boolean isSell() {
		return sell;
	}
	public void setSell(boolean sell) {
		this.sell = sell;
	}
	public double getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}
	public double getSellPrice() {
		return sellPrice;
	}
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}
	public void setLongAvg(String symbol){

		Calendar calReturn = Calendar.getInstance();	
		int endDay = calReturn.get(Calendar.DAY_OF_MONTH);
		int startDay = endDay - 30;
		int year = calReturn.get(Calendar.YEAR);
		int month = calReturn.get(Calendar.MONTH);

		Calendar startDate = Calendar.getInstance();

		//Set startDate
		startDate.set(Calendar.YEAR, year);
		startDate.set(Calendar.MONTH,month);
		startDate.set(Calendar.DATE, startDay);

		//End Date
		Calendar endDate = Calendar.getInstance();
		endDate.set(Calendar.YEAR, year);
		endDate.set(Calendar.MONTH, month);
		endDate.set(Calendar.DATE, endDay);

		YahooFinanceConstructURL urlObj = new YahooFinanceConstructURL(startDate, endDate, symbol); 
		GetHistoricalData historyObj = new GetHistoricalData(urlObj.constructURL()); 

		ArrayList<Double> adjCloseArray = historyObj.getAdjClose();

		int avg = 0;
		int total = 0;
		for (double s : adjCloseArray){
			total += s;
		}

		avg = total / adjCloseArray.size();
		
		this.longCounter = 0;
		
		this.longAvg = avg ;

	}
	
	public void setShortAvg(String symbol){


		Calendar calReturn = Calendar.getInstance();	
		int endDay = calReturn.get(Calendar.DAY_OF_MONTH);
		int startDay = endDay - 5;
		int year = calReturn.get(Calendar.YEAR);
		int month = calReturn.get(Calendar.MONTH);

		Calendar startDate = Calendar.getInstance();

		//Set startDate
		startDate.set(Calendar.YEAR, year);
		startDate.set(Calendar.MONTH,month);
		startDate.set(Calendar.DATE, startDay);

		//End Date
		Calendar endDate = Calendar.getInstance();
		endDate.set(Calendar.YEAR, year);
		endDate.set(Calendar.MONTH, month);
		endDate.set(Calendar.DATE, endDay);

		YahooFinanceConstructURL urlObj = new YahooFinanceConstructURL(startDate, endDate, symbol); 
		GetHistoricalData historyObj = new GetHistoricalData(urlObj.constructURL()); 

		ArrayList<Double> adjCloseArray = historyObj.getAdjClose();

		int avg = 0;
		int total = 0;
		for (double s : adjCloseArray){
			total += s;
		}

		avg = total / adjCloseArray.size();

		this.shortCounter = adjCloseArray.size();
		shortAvg = avg;
	}

	
}
