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
	
		/*

		try {
			if (message instanceof TextMessage)
			{
				TextMessage textMessage = (TextMessage)message;
				System.out.println("MessageBean Received:" + textMessage.getText());
				//This object will have only the symbol and the amount
				TradeObject trade = tradeFromXML(textMessage.getText());
				//jmsContext.createProducer().send(brokerQueue, newMessage);
			}
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	}
	public ArrayList<bollingerStockWrapper> getStockList() {
		return stockList;
	}

	public void setStockList(ArrayList<bollingerStockWrapper> stockList) {
		this.stockList = stockList;
	}
}
