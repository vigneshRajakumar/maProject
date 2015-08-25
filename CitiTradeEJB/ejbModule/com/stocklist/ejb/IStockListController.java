package com.stocklist.ejb;

import java.util.List;

import javax.ejb.Local;

import com.stocklist.jpa.StockList;

@Local
public interface IStockListController {

	public List<StockList> getAllStockList(); 
	public StockList getStockListByName(String symbol);
}
