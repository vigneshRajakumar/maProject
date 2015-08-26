package com.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marketdatahandler.ejb.HistoricalData;
import com.marketdatahandler.ejb.MarketDataHandlerLocal;
import com.tradingcontroller.TC_ATObject;

/**
 * Servlet implementation class TestAlgo
 */
@WebServlet("/TestAlgo")
public class TestAlgorithmTwoMovingAverages extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@EJB
	private MarketDataHandlerLocal marketDataHandler;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		Calendar calReturn = Calendar.getInstance();	
		int dayOfMonth = calReturn.get(Calendar.DAY_OF_MONTH);
		int year = calReturn.get(Calendar.YEAR);
		int month = calReturn.get(Calendar.MONTH);
		/*
		TC_ATObject obj = new TC_ATObject("AAPL", 33000, 55000 , 0, "Bollinger Bands");
				*/

		ArrayList<HistoricalData> historicalDataThirtyDays = marketDataHandler.getHistoricalDataBySymbol("AAPL", year, month, dayOfMonth-5, year, month, dayOfMonth);

		
		//String symbol, int startYear,	int startMonth, int startDay, int endYear, int endMonth, int endDay);
//		ArrayList<HistoricalData> historicalDataThirtyDays = marketDataHandler.getHistoricalDataBySymbol("AAPL", year, month, dayOfMonth-1, year, month, dayOfMonth);

		
		for (HistoricalData s : historicalDataThirtyDays){
			
			System.out.println("Symbol"+s.getSymbol()+"\n");
			System.out.println("Open"+s.getOpen()+"\n");
			System.out.println("Close"+s.getClose()+"\n");
			System.out.println("High"+s.getHigh()+"\n");
			System.out.println("Low"+s.getLow()+"\n");
			System.out.println("volume"+s.getVolume()+"\n");
			System.out.println("Adj_colse"+s.getAdj_colse()+"\n");
			System.out.println("\n");
			
		}
	}

	
}
