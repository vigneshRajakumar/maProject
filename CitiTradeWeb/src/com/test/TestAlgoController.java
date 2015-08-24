package com.test ;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.algorithm.jpa.Algo;
import com.algorithmscontroller.ejb.IAlgoController;


@WebServlet("/TestAlgoController")
public class TestAlgoController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@EJB
    private IAlgoController algoController;
	
	public void testGetAllAlgo(){
		
	    List<Algo> algoList = algoController.getAllAlgo();
        for (Algo t: algoList) {
        	System.out.println("The available algorithms are "+t.getAlgo_Name()) ;
        }
	}
		
	public void testGetAlgoByName(){ 
		
	   Algo algoObj = algoController.getAlgoByName("Bollinger Bands");
       System.out.println("The id for Bollinger Bands is "+algoObj.getAlgo_id());
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		testGetAllAlgo();
		testGetAlgoByName();
		 
    }

}