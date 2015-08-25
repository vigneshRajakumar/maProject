package com.portfolio.ejb;

import java.util.ArrayList;

import javax.ejb.Remote;

import com.trade.jpa.Trade;

@Remote
public interface portfolioRequestHandlerRemote {
	public ArrayList<Portfolio> getUserPortfolios(int traderId);
	public Trade createTrade(String type);
	public void createOrder();
}
