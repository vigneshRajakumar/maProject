package com.portfolios.ejb;

import java.util.ArrayList;

import javax.ejb.Local;

@Local
public interface portfolioRequestHandlerLocal {
	
	public ArrayList<Portfolio> getUserPortfolios(int traderId);
		
	
}
