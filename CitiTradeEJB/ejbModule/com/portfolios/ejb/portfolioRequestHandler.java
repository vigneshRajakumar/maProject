package com.portfolios.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.eclipse.persistence.config.QueryType;

import com.trade.jpa.Order;
import com.trade.jpa.OrderStatus;
import com.trade.jpa.Trade;

/**
 * Session Bean implementation class portfolioRequestHandler
 */
@Stateless
public class portfolioRequestHandler implements portfolioRequestHandlerRemote,
		portfolioRequestHandlerLocal {

	@PersistenceContext(name = "ct_projectUnit")
	private EntityManager em;

	public ArrayList<Portfolio> userPortfolios;

	/**
	 * Default constructor.
	 */
	public portfolioRequestHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Portfolio> getUserPortfolios(int traderId) {
		ArrayList<Portfolio> userPortfolio = new ArrayList<Portfolio>();
		Query query = em
				.createQuery("SELECT o FROM Order WHERE o.trader_id = :trader");
		query.setParameter("trader", traderId);

		List<Order> result = query.getResultList();
		for (Order order : result) {
			String symbol = order.getStock().getSymbol();
			int algo = order.getAlgo_id();
			double amount = order.getTotal_amount();
			int status;
			double currentValue ;
			double percentageChange;
			switch (order.getStatus()) {
			case BEFORE_ENTERED:
				
				status = 0;
				currentValue = amount;
				percentageChange = 0;
				userPortfolio.add(new Portfolio(symbol, status, algo, amount, currentValue, percentageChange));
				break;
			case ENTERED:
				status = 1;				
				currentValue = calculateCurrentValue(order);
				percentageChange = 0;
				userPortfolio.add(new Portfolio(symbol, status, algo, amount, currentValue, percentageChange));
				break;
			case EXIT:
				break;
			default:
				System.out
						.println("ERROR IN GETTING STATUS!!!!!!!!!!!!!!!!!!!");
			}
		}
		return null;
	}
	
	private double calculateCurrentValue(Order order){
		double amountLeft;
		List<Trade> trades = order.getAllTrades();
		switch(order.getStatus()){
		case ENTERED:
			break;
		case EXIT:
			break;
		}
		
		
	}
}
