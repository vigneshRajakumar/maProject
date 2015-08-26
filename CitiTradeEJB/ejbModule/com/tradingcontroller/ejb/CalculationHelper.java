package com.tradingcontroller.ejb;

import java.util.List;

import com.portfolio.ejb.Portfolio;
import com.stocklist.jpa.StockList;
import com.trade.jpa.Order;
import com.trade.jpa.OrderType;
import com.trade.jpa.Trade;

public class CalculationHelper {

	public double calculateCurrentValue(Order order) {
		List<Trade> trades = order.getTrades();
		double sellPrice = getTradeWithType(trades, "sell").getPrice_PerShare();
		double buyPrice = getTradeWithType(trades, "buy").getPrice_PerShare();
		return order.getTotal_amount()
				+ getEarning(sellPrice, buyPrice, trades.get(0).getNum_Shares());

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
	
	
	public Trade createTrade(String type, int algo, double price, int num_of_shares, int trader, StockList stock) {
		Trade trade = new Trade();
		trade.setAlgo_ID(algo);
		// trade.setOrder(order);
		trade.setTrade_Type(type);
		trade.setTrader_id(trader);
		
		trade.setPrice_PerShare(price);
		trade.setNum_Shares(num_of_shares);
		trade.setStockList(stock);
		// em.persist(trade);
		return trade;
	}
}
