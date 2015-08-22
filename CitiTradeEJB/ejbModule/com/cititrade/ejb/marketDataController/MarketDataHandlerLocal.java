package com.cititrade.ejb.marketDataController;

import java.util.ArrayList;

import javax.ejb.Local;

import com.cititrade.jpa.MarketDataController.Stock;

@Local
public interface MarketDataHandlerLocal {
	
	/*
	 * This method return an Stock entity of the given symbol with all the information
	 */
	public Stock getStockBySymbol(String symbol);
	
	
	/*
	 * this method return an array of historical data of a stock with the given symbol
	 * from a starting date to an end date
	 */
	public ArrayList<HistoricalData> getHistoricalDataBySymbol(String symbol, int startYear,
			int startMonth, int startDay, int endYear, int endMonth, int endDay);
}
