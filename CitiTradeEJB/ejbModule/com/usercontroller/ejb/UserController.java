
package com.usercontroller.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.trade.jpa.Trade;
import com.usercontroller.jpa.User;



@Stateless
public class UserController implements IUserController {
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
    public User getUserByLogin(String username , String password) {

    	TypedQuery<User> query = em.createQuery("SELECT user_a FROM User AS user_a WHERE user_a.uname = :username and user_a.passwd =:password", User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);

        // Execute the query, and get a collection of beans back.
        User userObj = null;
        try {
            userObj = query.getSingleResult();
            
        } catch (EntityNotFoundException ex) {
            System.out.println("User not found");
        } 
        
        return userObj;
    }
		
	//use this when u does not want to saved to database
	@Override
	public boolean updateUserInfo(User userObj){
		try {
			em.merge(userObj);
			return true;
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
	}
	
	public List<Trade> getUserTrades(User userObj){
		
		List<Trade> tradeList = null;
		tradeList = userObj.getUserTrades();
        
        return tradeList;
	}
	

	/*
	@Override
	public boolean setPassword(User userObj, String password){
		
		userObj.setPasswd(password);
		try {
			em.merge(userObj);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void setBalance(User userObj , double newBalance){
		userObj.setBalance(newBalance);
		em.merge(userObj);
	}
	

	@Override
	public void setProfit_Lost(User userObj , double profit_Lost){
		userObj.setProfit_Lost(profit_Lost);
		em.merge(userObj);
	}
	*/
	

}
