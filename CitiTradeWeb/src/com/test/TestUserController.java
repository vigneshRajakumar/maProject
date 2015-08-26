
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
import com.usercontroller.ejb.IUserController;
import com.usercontroller.jpa.User;
import com.usercontroller.jpa.UserType;

@WebServlet("/TestUserController")

public class TestUserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	private IUserController userController;

	public User testGetUserByLogin(String username, String password){

		User userObj = userController.getUserByLogin(username, password);

		return userObj;
	}

	public void testUpdateUserInfo(User userObj){

		try {
			userController.updateUserInfo(userObj);
		} catch (Exception e){
			e.printStackTrace();
		}
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		//boolean isgood = userController.registerUser("lol", "lol", 0.0, "trader" , "userFirstname" , "userLastname" , "useremail");
		//System.out.println(isgood);
		//User userObj = testGetUserByLogin("trader" , "trader");
		//System.out.println(userObj.getUserType().getUser_Type());
		
		User userObj = userController.getUserByUsername("trader");
		System.out.println(userObj.getUser_ID());
		
		User userObj2 = userController.getUserByID(1);
		System.out.println(userObj2.getUname());
		
		//userObj.setPasswd("changePassword");
		//testUpdateUserInfo(userObj) ;
		
		//userObj.setBalance(99);
		//testUpdateUserInfo(userObj) ;
		
		//userObj.setProfit_Lost(-99);
		//testUpdateUserInfo(userObj) ; 
		
		//List<Trade> tradeList = userObj.getUserTrades();
		
		//for (Trade e : tradeList){
			//System.out.println(e.getNum_Shares() +" "+e.getPrice_PerShare());
		//}
		
		//UserType u = userObj.getUserTypeString();
		//System.out.println(u.getUser_Type());
		
		
	}


	/*
	public void testSetBalance(){

		userController.setBalance(99.00);

		System.out.println("The balance is "+userController.getBalance());

	}

	public void testSetProfit_Lost(){

		userController.setProfit_Lost(200);

		System.out.println("The profit is "+userController.getProfit_Lost());

	}

	public void testSetPassword(){

		try {
		userController.setPassword("changePassword");
		System.out.println("Password successfully changed");
		} catch (Exception e){
			System.out.println("Change password failed");
		}
	}
	 */

}
