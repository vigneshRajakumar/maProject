package com.tradingcontroller.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.trade.jpa.Trade;


/**
 * Session Bean implementation class Trade
 */
@Stateless
public class TradeController implements ITradeController {

	@PersistenceContext
	private EntityManager em;


	@Override
	public void insertTrade(Trade trade) {
		em.persist(trade);
	}

	@Override
	public List<Trade> getAllTradeByUserID(int userID) {

		Query query = em.createQuery("SELECT trade_a FROM Trade AS trade_a where trade_a.trader_id = :userID");
		query.setParameter("userID", userID);

		// Execute the query, and get a collection of beans back.
		List<Trade> listTrades = query.getResultList();

		for (Trade trade: listTrades) {
			if (trade == null) {
				System.out.print("Team is null");
			} else {
				System.out.println("Return successfully");
			}
		}
		return listTrades;
	}

	@Override
	public Trade getTradeByID(int id) {

		Trade tradeObj = em.find(Trade.class, id);

		if (tradeObj == null){
			System.out.println("No trade obj found");
		} else {
			return tradeObj;
		}
		return tradeObj;
	}

	@Override
	public boolean updateTrade(Trade trade) {
		try {
			em.merge(trade);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public boolean deleteTradeByID(int id) {

		try {
			Trade trade = em.find(Trade.class, id);
			em.remove(trade);
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	@Override
	public boolean deleteTrade(Trade tradeObj) {
	
		try {
			Trade trade = em.find(Trade.class, tradeObj.getTrade_ID());
			em.remove(trade);
			return true;
		} catch (Exception e) {
			return false;
		}
	}



}
