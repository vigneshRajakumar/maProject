package com.stocklist.ejb;

import java.util.List;

import com.stocklist.jpa.StockList;

public interface IStockListController {

	public List<StockList> getAllStockList(); 
	public StockList getStockListByName(String symbol);
	void insertStockList(StockList stockList);
}
