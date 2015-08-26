package com.portfolio.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.eclipse.persistence.config.QueryType;

import com.marketdatahandle.jpa.Stock;
import com.marketdatahandler.ejb.MarketDataHandlerLocal;
import com.trade.jpa.Order;
import com.trade.jpa.OrderStatus;
import com.trade.jpa.OrderType;
import com.trade.jpa.Trade;

/**
 * Session Bean implementation class portfolioRequestHandler
 */
@Stateless
public class portfolioRequestHandler implements portfolioRequestHandlerRemote,
		portfolioRequestHandlerLocal {

	@EJB
	MarketDataHandlerLocal market;

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
				.createQuery("SELECT o FROM Order AS o WHERE o.trader_id = :trader");
		query.setParameter("trader", traderId);

		List<Order> result = query.getResultList();
		System.out.print("no. of results "+result.size());
		for (Order order : result) {
			String symbol = order.getStock();
			int algo = order.getAlgo_id();
			double amount = order.getTotal_amount();
			int status;
			double currentValue;
			double percentageChange;
			switch (order.getStatus()) {
			case BEFORE_ENTERED:

				status = 0;
				currentValue = amount;
				percentageChange = 0;
				userPortfolio.add(new Portfolio(symbol, status, algo, amount,
						currentValue, percentageChange));
				break;
			case ENTERED:
				status = 1;
				currentValue = calculateCurrentValue(order);
				percentageChange = calPercentageChange(currentValue,
						order.getTotal_amount());
				userPortfolio.add(new Portfolio(symbol, status, algo, amount,
						currentValue, percentageChange));
				break;
			case EXIT:
				status = 2;
				currentValue = calculateCurrentValue(order);
				percentageChange = calPercentageChange(currentValue,
						order.getTotal_amount());
				userPortfolio.add(new Portfolio(symbol, status, algo, amount,
						currentValue, percentageChange));
				break;
			default:
				System.out
						.println("ERROR IN GETTING STATUS!!!!!!!!!!!!!!!!!!!");
			}
		}
		return userPortfolio;
	}

	private double calculateCurrentValue(Order order) {
		double amountLeft = 0;
		List<Trade> trades =  order.getTrades();
		switch (order.getStatus()) {
		case ENTERED:
			if (order.getType().equals(OrderType.SHORT)) {
				double buyPrice = market.getStockBySymbol(order.getStock())
						.getAsk();
				double marketValue = (buyPrice * trades.get(0).getNum_Shares());
				double enterPrice = trades.get(0).getPrice_PerShare();
				double numOfShares = trades.get(0).getNum_Shares();
				return amountLeft + marketValue;
			} else {
				double sellPrice = market.getStockBySymbol(order.getStock())
						.getBid();
				double marketValue = (sellPrice * trades.get(0).getNum_Shares());
				amountLeft = order.getTotal_amount()
						- (trades.get(0).getPrice_PerShare() * trades.get(0)
								.getNum_Shares());
				return amountLeft + marketValue;
			}

		case EXIT:

			double sellPrice = getTradeWithType(trades, "sell")
					.getPrice_PerShare();
			double buyPrice = getTradeWithType(trades, "buy")
					.getPrice_PerShare();
			return order.getTotal_amount()
					+ getEarning(sellPrice, buyPrice, trades.get(0)
							.getNum_Shares());

		}
		return amountLeft;

	}

	public double calPercentageChange(double end, double origi) {
		return (end - origi) / origi;
	}

	public Trade getTradeWithType(List<Trade> allT, String buyorsell) {
		for (Trade t : allT) {
			if (t.getTrade_Type().equals(buyorsell))
				return t;
		}

		return null;
	}

	public double getEarning(double sellPrice, double buyPrice, int numOfShares) {
		return (sellPrice - buyPrice) * numOfShares;
	}

	public double getAmountLeft(double enterPrice, double numOfShares,
			double total) {
		return total - (enterPrice * numOfShares);
	}

	public void createOrderTest() {
		// TODO Auto-generated method stub
		Order o = new Order();
		o.setAlgo_id(1);

	}

	public Trade createNewTrade() {
		Trade trade = new Trade();
		return trade;
	}

	public void createOrder() {

		Order o = new Order();
		o.setTrader_id(1);
		o.setStatus(OrderStatus.EXIT);
		o.setAlgo_id(1);
		o.setLossPercentage(0.01);
		o.setProfitPercentage(0.05);
		o.setTotal_amount(1000000.0);
		o.setType(OrderType.LONG);
		o.setStock("aapl");
		em.persist(o);
//		List<Trade> allTrades = o.getTrades();
		List<Trade> allTrades = new ArrayList<Trade>();
		allTrades.add(createTrade("sell"));
		allTrades.add(createTrade("buy"));
		o.setTrades(allTrades);
		em.persist(o);
	/*	System.out.println("isnul!!!!!!!!!??? "+(allTrades == null));
		if (allTrades != null) {
			allTrades.add(createTrade("sell"));
			allTrades.add(createTrade("buy"));
		}*/

	}

	public Trade createTrade(String type) {
		Trade trade = new Trade();
		trade.setAlgo_ID(1);
		// trade.setOrder(order);
		trade.setTrade_Type(type);
		trade.setTrader_id(1);
		
		trade.setPrice_PerShare(100.0);
		trade.setNum_Shares(10000);
		// em.persist(trade);
		return trade;
	}
}
