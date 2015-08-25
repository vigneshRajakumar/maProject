package com.test;

import java.io.IOException;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tradingcontroller.ejb.ITradingController;


@WebServlet("/TestTradingController")
public class TestTradingController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
	@EJB
	private ITradingController tradingController;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			tradingController.sendMsg();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
