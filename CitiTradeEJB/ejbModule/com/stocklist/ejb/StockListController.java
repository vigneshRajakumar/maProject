package com.stocklist.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.stocklist.jpa.StockList;


@Stateless
public class StockListController implements IStockListController{
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<StockList> getAllStockList(){

		Query query = em.createQuery("SELECT stocklist_a FROM StockList AS stocklist_a order by stocklist_a.symbol asc"); 
			
		// Execute the query, and get a collection of beans back.
		List<StockList> stockList = query.getResultList();
		for (StockList s : stockList) {
			if (s == null) {
				System.out.print("stocklist is null");
			} 
		}
		return stockList;
	}	
	
	@Override
    public StockList getStockListByName(String symbol) {

    	TypedQuery<StockList> query = em.createQuery("SELECT stocklist_b FROM StockList AS stocklist_b WHERE stocklist_b.symbol = :symbol", StockList.class);
        query.setParameter("symbol", symbol);

        // Execute the query, and get a collection of beans back.
        StockList stockListObj = null;
        try {
            stockListObj = query.getSingleResult();

        } catch (EntityNotFoundException ex) {
            System.out.println("StockList not found: " + stockListObj);

        } catch (NonUniqueResultException ex) {
            System.out.println("More than one stocklist named: " + stockListObj);

        }
          
        return stockListObj;
    }
	
}
