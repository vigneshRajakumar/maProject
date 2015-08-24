package com.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.stocklist.jpa.StockList;
import com.stocklistcontroller.ejb.IStockListController;


@WebServlet("/TestStockListController")
public class TestStockListController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private IStockListController stockListController;

	public void testGetAllStockList(){

		List<StockList> stockList = stockListController.getAllStockList();
		for (StockList t: stockList) {
			System.out.println("The available symbol are "+t.getSymbol()) ;
		}
	}

	public StockList testGetStockListByName(){ 

		StockList stockListObj = stockListController.getStockListByName("XXII");
		System.out.println("The id for AACC is "+stockListObj.getStock_ID());
		return stockListObj;
	}
	
	public void printLine(){
		
		BufferedReader br = null;

		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader("sample.txt"));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void runFile(StockList obj) {

		String csvFile = "C:\\Users\\Lenovo\\Documents\\GitHub\\maProject\\AMEXcompanylist.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		int counter = 2;

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				try {
			        // use comma as separator
				String[] stockList = line.split(cvsSplitBy);

				stockList[0] = stockList[0].replaceAll("\"", "");
				stockList[1] = stockList[1].replaceAll("\"", "");
				obj.setStock_ID(counter);
				obj.setSymbol(stockList[0]);
				obj.setSName(stockList[1]);
				
				stockListController.insertStockList(obj);
				
				//System.out.println("[Symbol= " + stockList[0] 
	                                // + " , Symbol Name=" + stockList[1] + "]");

				counter++; 
				}catch (Exception e){
					System.out.println("999999999999999999999999999999999999999999999999999999"+obj.getSymbol());
					continue;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
	  }
		

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		StockList obj = testGetStockListByName();
		runFile(obj);
		
		
		/*
		for (int i = 0 ; )
		obj.setStock_ID(2);
		obj.setSymbol("BBCC");
		stockListController.insertStockList(obj);
				*/

	}


}
