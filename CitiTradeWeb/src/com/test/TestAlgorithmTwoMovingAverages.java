package com.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marketdatahandler.ejb.HistoricalData;
import com.marketdatahandler.ejb.MarketDataHandlerLocal;
import com.tradingcontroller.TC_ATObject;
import com.twomovingaverages.ejb.ITwoMovingAverages;

/**
 * Servlet implementation class TestAlgo
 */
@WebServlet("/TestAlgo")
public class TestAlgorithmTwoMovingAverages extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private MarketDataHandlerLocal marketDataHandler;

	@EJB ITwoMovingAverages twoMovingAvgController;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		TC_ATObject obj = new TC_ATObject("AAPL", 33000, 55000 , 0, "Bollinger Bands");

		twoMovingAvgController.run(obj);
		
		TC_ATObject obj2 = new TC_ATObject("BBBB", 33000, 55000 , 0, "Bollinger Bands");

		twoMovingAvgController.run(obj2);


//		ArrayList<HistoricalData> historicalDataThirtyDays = marketDataHandler.getHistoricalDataBySymbol("AAPL", year, month, dayOfMonth-5, year, month, dayOfMonth);

		
		//String symbol, int startYear,	int startMonth, int startDay, int endYear, int endMonth, int endDay);
//		ArrayList<HistoricalData> historicalDataThirtyDays = marketDataHandler.getHistoricalDataBySymbol("AAPL", year, month, dayOfMonth-1, year, month, dayOfMonth);

		
	}


}

