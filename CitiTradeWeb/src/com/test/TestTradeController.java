package com.test;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trade.jpa.Trade;
import com.tradingcontroller.ejb.ITradeController;



@WebServlet("/TestTradeController")
public class TestTradeController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@EJB
	private ITradeController tradeController;

	public void testGetAllTrade(){

		List<Trade> tradeList = tradeController.getAllTradeByUserID(1);
		for (Trade t: tradeList) {
			System.out.println("The available Trade are "+t.getStockList().getSName() +" "+t.getStockList().getSymbol()+" "+t.getTrade_ID()+" "+t.getNum_Shares()+" "+t.getPrice_PerShare()) ;
		}
	}

	public void testUpdateTrade(Trade trade){
		tradeController.updateTrade(trade);
	}

	public Trade testGetTradeByID(){
		Trade tradeObj = tradeController.getTradeByID(1);
		return tradeObj;	
	}
	
	public void testDeleteTrade(Trade tradeObj){
		tradeController.deleteTrade(tradeObj);
	}
	
	public void testDeleteTradeByID(){
		tradeController.deleteTradeByID(1);
	}
	
	public void testInsertNewTrade(Trade tradeObj){
		tradeController.insertTrade(tradeObj);
	}
	

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		testGetAllTrade();
		//Trade tradeObj = testGetTradeByID() ;
		
		//System.out.println(tradeObj.getTrade_ID());
		//tradeObj.setTrade_ID(9999);
		//testInsertNewTrade(tradeObj);
		
		//testDeleteTrade(tradeObj);
		
		//tradeObj.setNum_Shares(99999);
		//testUpdateTrade(tradeObj);
		
		
		
	}


}
