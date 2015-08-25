package com.tradingcontroller.ejb;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import com.trade.jpa.Trade;

/**
 * Session Bean implementation class AutomatedTradingController
 */
@Stateful
@LocalBean
public class AutomatedTradingController {
	private class bollingerStockWrapper {
		private String stockSymbol;

		public String getStockSymbol() {
			return stockSymbol;
		}

		public void setStockSymbol(String stockSymbol) {
			this.stockSymbol = stockSymbol;
		}
		
	}

	private ArrayList<bollingerStockWrapper> stockList;
    /**
     * Default constructor. 
     */
    public AutomatedTradingController() {
        // TODO Auto-generated constructor stub
    	
    }
    
    public void AddStockToTrade(String stock) {
    	
    }

	public void RemoveStockToTrade(String stock) {
		
	}
	
	public void RecordTrade(Trade trade) {
		
	}


	public ArrayList<bollingerStockWrapper> getStockList() {
		return stockList;
	}

	public void setStockList(ArrayList<bollingerStockWrapper> stockList) {
		this.stockList = stockList;
	}
}
