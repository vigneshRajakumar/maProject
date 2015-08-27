package com.restful.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.json.JSONException;

import com.marketdatahandler.ejb.MarketDataHandlerLocal;
import com.portfolio.ejb.Portfolio;
import com.portfolio.ejb.portfolioRequestHandlerLocal;
import com.stocklist.ejb.IStockListController;
import com.stocklist.jpa.StockList;
import com.trade.jpa.Trade;
import com.tradingcontroller.ejb.ITradeController;
import com.tradingcontroller.ejb.ITradingController;
import com.usercontroller.ejb.IUserController;
import com.usercontroller.jpa.User;



@Path("/rest")
public class CitiRestful {
	
    @SuppressWarnings("unused")
    @Context
    private UriInfo context;
    
    @EJB
    private ITradeController tradeController;
    
    @EJB
    private ITradingController tradingController;
    
    @EJB
    private IUserController userController;
    
    @EJB
    private IStockListController stockListController;
    
    @EJB
    private portfolioRequestHandlerLocal portfolioController;
    
    @EJB
    private MarketDataHandlerLocal marketDataHandler;
    
    @Context
	private HttpServletResponse response;

    /**
     * Default constructor. 
     */
    public CitiRestful() {
        // TODO Auto-generated constructor stub
    }
    
    @POST
    @Path("/login")
    @Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public void login(
			@FormParam("username") String username,
			@FormParam("password") String password) throws IOException 
    {
    	System.out.println("Entered login ");
    	User currentUser = userController.getUserByLogin(username, password);
    	
    	if(currentUser != null) {
    		System.out.println("Inside login : " + currentUser.getemail());
    		response.sendRedirect(response.encodeRedirectURL("../../auth.html?username=" + username + "&userid=" + currentUser.getUser_ID()));
    	} else {
    		System.out.println("Inside login : no user");
    		response.sendRedirect(response.encodeRedirectURL("../../index.html?error=wronginfo"));
    	}
	}
    
    @POST
    @Path("/register")
    @Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public void register(
			@FormParam("first_name") String first_name,
			@FormParam("last_name") String last_name,
			@FormParam("user_name") String user_name,
			@FormParam("email") String email,
			@FormParam("password") String password) throws IOException 
    {
    	boolean isSuccessful = userController.registerUser(user_name, password, 0.0, "trader", first_name, last_name, email);
    	
    	if(isSuccessful) {
    		int userid = userController.getUserByUsername(user_name).getUser_ID();
    		response.sendRedirect(response.encodeRedirectURL("../../auth.html?username=" + user_name + "&userid=" + userid));
    	} else {
    		response.sendRedirect(response.encodeRedirectURL("../../index.html?error=wronginfo"));
    	}
	}
    
    @GET
    @Path("/allTrades")
    @Produces("text/plain")
	public String getAllTrades(@CookieParam("userid") String userid) throws JSONException {
    	List<Trade> trades = tradeController.getAllTradeByUserID(Integer.parseInt(userid));
    	String json = "[";
    	
    	System.out.println("***************** inside getAllTrades ***********************");
    	for(Trade t : trades ) {
    		String temp = "{";
    		temp += "\"record\":{";
    		temp += "\"SName\":\"" + t.getStockList().getSName() + "\",";
    		temp += "\"SSymbol\":\"" + t.getStockList().getSymbol() + "\",";
    		temp += "\"trade_Type\":\"" + t.getTrade_Type() + "\",";
    		temp += "\"algo_ID\":\"" + t.getAlgo_ID() + "\",";
    		temp += "\"num_Shares\":\"" + t.getNum_Shares() + "\",";
    		temp += "\"price_PerShare\":\"" + t.getPrice_PerShare() + "\"}},";
    		json += temp;
    	}
    	
    	json = json.substring(0, json.length() - 1);
    	json += "]";
    	
    	return json;
	}
    
    @GET
    @Path("/portfolio")
    @Produces("text/plain")
	public String getPortfolio(@CookieParam("userid") String userid) {
    	ArrayList<Portfolio> orders = portfolioController.getUserPortfolios(Integer.parseInt(userid));
    	String json = "[";
    	
    	System.out.println("***************** inside getPortfolio ***********************");    	
    	for( Portfolio p : orders ) {
    		String temp = "{";
    		temp += "\"order\":{";
    		temp += "\"symbol\":\"" + p.getSymbol() + "\",";
    		temp += "\"status\":\"" + p.getProcessStatus() + "\",";
    		temp += "\"initialAmt\":\"" + p.getTotalAmount() + "\",";
    		temp += "\"currentAmt\":\"" + p.getCurrentValue() + "\",";
    		temp += "\"percentChange\":\"" + p.getPercentageChange() + "\"}},";
    		json += temp;
    	}
    	
    	json = json.substring(0, json.length() - 1);
    	json += "]";
    	
    	return json;
	}
    
    @GET
    @Path("/stocklist")
    @Produces("application/json")
	public List<StockList> getStockList() {
    	List<StockList> stockList = stockListController.getAllStockList();
		return stockList;
	}
        
    @GET
    @Path("/userinfo")
    @Produces("application/json")
	public User getUserProfile(@CookieParam("username") String username) {
    	//User user = userController.getUserByID(Integer.parseInt(userid));
    	User user = userController.getUserByUsername(username);
    	System.out.println("User details: " + user.getemail());
		return user;
	}
    
    @GET
    @Path("/marketdata/{symbol}")
    @Produces("application/json")
    public void getMarketData (@PathParam("symbol") String symbol) {
    	//marketDataHandler.getHistoricalDataBySymbol(symbol, startYear, startMonth, startDay, endYear, endMonth, endDay)
    }
        
    @POST
    @Path("/userinfoUpdate")
    @Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
    public void updateUserProfile(
    		@CookieParam("userid") String userid,
    		@FormParam("first_name") String first_name,
			@FormParam("last_name") String last_name,
			@FormParam("email") String email) {
    	User user = userController.getUserByID(Integer.parseInt(userid));
    	
    	user.setfirstname(first_name);
    	user.setlastname(last_name);
    	user.setemail(email);
    	
    	boolean result = userController.updateUserInfo(user);
    }
    
    @POST
    @Path("/trade")
    @Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public void doTrade(
			@FormParam("stock") String stock,
			@FormParam("amount") String amount,
			@FormParam("percentProfit") String percentProfit,
			@FormParam("percentLoss") String percentLoss,
			@FormParam("method") String method) throws IOException 
    {
    	System.out.println("Inside doTrade function *********");
    	double outAmount = Double.parseDouble(amount);
    	double outPercentProfit = Double.parseDouble(percentProfit)/100.0;
    	double outPercentLoss = Double.parseDouble(percentLoss)/100.0;
    	
    	method = "Bollinger Bands";
    	    	
    	tradingController.sendInputValues(stock, outAmount, outPercentProfit, outPercentLoss, method);
	}
    
    @GET
    @Path("/message")
    @Produces("text/plain")
    public String getMessage (@CookieParam("userid") String userid) {
    	System.out.println("Inside getMessage function *********");
    	return tradingController.getMsgFromQueue();
    }
}