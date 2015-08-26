package com.usercontroller.ejb;

import java.util.List;

import javax.ejb.Local;

import com.trade.jpa.Trade;
import com.usercontroller.jpa.User;



@Local
public interface IUserController {

	public User getUserByLogin(String username , String password);
	
	public boolean updateUserInfo(User userObj);
		
	/*
	public boolean setPassword(User userObj , String password);
	public boolean setBalance(User userObj , double balance);
	public boolean setProfit_Lost(User userObj , double profit_lost);
	*/
	
	public List<Trade> getUserTrades(User userObj);

	public User getUserByID(int id);
	public User getUserByUsername(String username);

	boolean registerUser(String name, String passwd, double initBalance,
			String type, String firstname, String lastname, String email);
	
	
}
