
package com.usercontroller.jpa;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import com.trade.jpa.Trade;

@Entity
@Table(schema="ct_project", name="ct_users")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int user_id; 
	private String uname ; 
	private String passwd ;  
	private double balance ; 
	private double profit_lost ; 
	
	private List<Trade> listTrade;
	private UserType userType;

	public void setUser_ID(int id) {
		this.user_id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getUser_ID() {
		return this.user_id;
	}  

	/*
	public int getUser_Type() {
		return this.user_type;
	}

	public void setUser_Type(int type) {
		this.user_type = type;
	}*/
	
	public String getUname() {
		return this.uname;
	}	

	public void setUname(String name) {
		this.uname = name;
	}
	public String getPasswd() {
		return this.passwd;
	}

	public void setPasswd(String pwd) {
		this.passwd = pwd;
	}

	public double getBalance() {
		return this.balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public double getProfit_Lost() {
		return this.profit_lost;
	}

	public void setProfit_Lost(double profitLoss) {
		this.profit_lost = profitLoss;
	}
	
	//the user can retrieve his trades from database Table - Trade
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id")
	public List<Trade> getUserTrades() {
		return this.listTrade;
	}
	
	public void setUserTrades(List<Trade> listTrade) {
        this.listTrade = listTrade;
    }
	
	//the user can retrieve his trades from database Table - userType
	@OneToOne
	@JoinColumn(name="user_type")
	public UserType getUserTypeString(){
		return this.userType;
	}
		
	public void setUserTypeString(UserType userTypeObj){
		this.userType = userTypeObj;
	}
	
	
	
}

