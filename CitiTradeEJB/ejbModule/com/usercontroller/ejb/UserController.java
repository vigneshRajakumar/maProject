package com.usercontroller.ejb;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.trade.jpa.Trade;
import com.usercontroller.jpa.User;
import com.usercontroller.jpa.UserType;

@Stateless
public class UserController implements IUserController {

	@PersistenceContext
	private EntityManager em;

	@Override
	public boolean registerUser(String name, String passwd, double initBalance,
			String type) {
		TypedQuery<User> qu = em.createQuery(
				"SELECT u FROM User AS u WHERE u.uname = :name", User.class);
		qu.setParameter("name", name);
		Collection<User> u = qu.getResultList();
		if (u.size() > 0)
			return false;
		
		String pwd;
		UserType uType;
		if ((pwd = cryptWithMD5(passwd)) != null && (uType = getUserType(type)) != null ) {
			User newU = new User();
			newU.setBalance(initBalance);
			newU.setUname(name);
			newU.setPasswd(pwd);
			newU.setUserTypeString(uType);
			em.persist(newU);
			
			return true;
		} else{
			System.out.println("error in creating user");
			return false;
		}
	}

	@Override
	public User getUserByLogin(String username, String password) {

		// use the encrypted password
		String encryptedPwd = cryptWithMD5(password);
		
		System.out.println(encryptedPwd);
		TypedQuery<User> query = em
				.createQuery(
						"SELECT user_a FROM User AS user_a WHERE user_a.uname = :username and user_a.passwd =:password",
						User.class);
		query.setParameter("username", username);
		query.setParameter("password", encryptedPwd);

		// Execute the query, and get a collection of beans back.
		User userObj = null;
		try {
			userObj = query.getSingleResult();

		} catch (EntityNotFoundException ex) {
			System.out.println("User not found");
		}

		return userObj;
	}

	// use this when u does not want to saved to database
	@Override
	public boolean updateUserInfo(User userObj) {
		try {
			em.merge(userObj);
			return true;
		} catch (Exception e) {
			System.out.println(e.toString());
			return false;
		}
	}

	public List<Trade> getUserTrades(User userObj) {

		List<Trade> tradeList = null;
		tradeList = userObj.getUserTrades();

		return tradeList;
	}

	/*
	 * @Override public boolean setPassword(User userObj, String password){
	 * 
	 * userObj.setPasswd(password); try { em.merge(userObj); return true; }
	 * catch (Exception e) { e.printStackTrace(); return false; } }
	 * 
	 * @Override public void setBalance(User userObj , double newBalance){
	 * userObj.setBalance(newBalance); em.merge(userObj); }
	 * 
	 * 
	 * @Override public void setProfit_Lost(User userObj , double profit_Lost){
	 * userObj.setProfit_Lost(profit_Lost); em.merge(userObj); }
	 */

	private UserType getUserType(String _type) {
		UserType uType = null;
		TypedQuery<UserType> query = em.createQuery(
				"SELECT u FROM UserType AS u WHERE u.user_type = :type",
				UserType.class);
		query.setParameter("type", _type);
		try {
			uType = query.getSingleResult();
		} catch (EntityNotFoundException ex) {
			System.out.println("Type not found");
		}catch (NonUniqueResultException ex) {
			System.out.println("More than one type selected");
		}
		return uType;

		//
	}

	private static String cryptWithMD5(String pass) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] passBytes = pass.getBytes();
			md.reset();
			byte[] digested = md.digest(passBytes);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digested.length; i++) {
				sb.append(Integer.toHexString(0xff & digested[i]));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			/*
			 * Logger.getLogger(CryptWithMD5.class.getName()).log(Level.SEVERE,
			 * null, ex);
			 */
			System.out.println(ex.getMessage());
		}
		return null;

	}

}
