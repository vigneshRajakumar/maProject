package com.tradingcontroller.ejb;

import java.util.List;

import javax.ejb.Local;

import com.trade.jpa.Trade;



@Local
public interface ITradeController {

	public boolean deleteTradeByID(int id);
	public boolean deleteTrade(Trade tradeObj);
	public void insertTrade(Trade trade);
	public Trade getTradeByID(int id);
	public boolean updateTrade(Trade trade);
	public List<Trade> getAllTradeByUserID(int userID) ;
	
}
