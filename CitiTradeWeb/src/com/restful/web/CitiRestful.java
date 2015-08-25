package com.restful.web;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.stocklist.ejb.IStockListController;
import com.stocklist.jpa.StockList;
import com.trade.jpa.Trade;
import com.tradingcontroller.ejb.ITradeController;
import com.usercontroller.ejb.IUserController;
import com.usercontroller.jpa.User;

@Path("/rest")
public class CitiRestful {
	
	User newUser = null;
	
    @SuppressWarnings("unused")
    @Context
    private UriInfo context;
    
    @EJB
    private ITradeController tradeController;
    
    @EJB
    private IUserController userController;
    
    @EJB
    private IStockListController stockListController;
    
    @Context
	private HttpServletResponse response;

    /**
     * Default constructor. 
     */
    public CitiRestful() {
        // TODO Auto-generated constructor stub
    }
    
    @Path("/login")
    @POST
    @Consumes("application/x-www-form-urlencoded")
	@Produces("text/plain")
	public void login(
			@FormParam("username") String username,
			@FormParam("password") String password) throws IOException 
    {
    	newUser = userController.getUserByLogin(username, password);
    	
    	if(newUser != null) {
    		response.sendRedirect(response.encodeRedirectURL("../../main.html"));
    	} else {
    		response.sendRedirect(response.encodeRedirectURL("../../index.html?error=wronginfo"));
    	}
	}
    
    @Path("/logout")
    @GET
    @Produces("text/plain")
    public void logout() throws IOException {
    	newUser = null;
    }
    
    @Path("/verification")
    @GET
    @Produces("text/plain")
	public boolean verifyUserStatus() {
    	if (newUser == null) {
    		return false;
    	} else {
    		System.out.println(newUser.getUname());
    		return true;
    	}
	}
    
    @Path("/allTrades")
    @GET
    @Produces("application/json")
	public List<Trade> getAllTrades() {
    	int a = 1;
    	return tradeController.getAllTradeByUserID(a);
	}
    
    @Path("/stocklist")
    @GET
    @Produces("application/json")
	public List<StockList> getStockList() {
    	List<StockList> stockList = stockListController.getAllStockList();
		return stockList;
	}
}