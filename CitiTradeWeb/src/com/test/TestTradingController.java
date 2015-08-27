package com.test;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.tradingcontroller.TC_ATObject;
import com.tradingcontroller.ejb.ITradingController;


@WebServlet("/TestTradingController")
public class TestTradingController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	@EJB
	private ITradingController tradingController;
	
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		//TC_ATObject tradeResponseObj = new TC_ATObject("AAPL", 33000, 55000 , 0, "Bollinger Bands");
		
		//tradingController.setRecordStatus(tradeResponseObj);
		
		tradingController.sendInputValues("CHL", 1000, 0.01, 0.01,"Bollinger Bands");
		//tradingController.sendMsgToQueue("Hello Kitty");
		
		try {
		    Thread.sleep(3000);                 
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		
		
		//System.out.println("Now getting from queue !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		//tradingController.getMsgFromQueue();
		
	}
	
	

}
