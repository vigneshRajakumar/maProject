package com.cititrade.ejb.stockController;

import java.util.List;
import javax.ejb.Local;
import com.cititrade.jpa.stocks.Stock;

@Local
public interface IStockController {
	
	public List<Stock> getAllStock(); 
	public Stock getStockBySymbol(String symbol);
}
