package com.marketdatahandler.ejb;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.marketdatahandle.jpa.Stock;






/**
 * Session Bean implementation class MarketDataHandler
 */

@Stateless
@LocalBean
public class CitiTradeMarketDataHandler implements MarketDataHandlerRemote, MarketDataHandlerLocal {

    /**
     * Default constructor. 
     */
	@PersistenceContext(unitName = "ct_projectUnit")
	private EntityManager em;
	
    public CitiTradeMarketDataHandler() {
        // TODO Auto-generated constructor stub
    }
    


	@Override
	public Stock getStockBySymbol(String symbol) {
		Stock result = null;
		StockInfoHelper helper = new StockInfoHelper();
		
		TypedQuery<Stock> query = em.createQuery("SELECT s FROM Stock AS s where s.symbol = :sym",Stock.class);
		query.setParameter("sym", symbol);
		try{
			Stock sto = (Stock) query.getSingleResult();
			result = helper.getStock(symbol, sto);
			em.persist(result);
		}catch(javax.persistence.NoResultException e){
			//if the stock is not inside the database
			result = helper.getStock(symbol, null);
			em.persist(result);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}



	@Override
	public ArrayList<HistoricalData> getHistoricalDataBySymbol(String symbol,
			int startYear, int startMonth, int startDay, int endYear,
			int endMonth, int endDay) {
		
		HistoricalInfoHelper helper = new HistoricalInfoHelper();
		return helper.getStockHistoricalData(symbol, startYear, startMonth, startDay, endYear, endMonth, endDay);
	}
	
	
	
	
	
	
	

	


}
