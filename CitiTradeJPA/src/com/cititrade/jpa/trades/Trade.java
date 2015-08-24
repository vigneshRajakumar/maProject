package com.cititrade.jpa.trades;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Trades
 *
 */

@Entity
@Table(schema="ct_project", name="ct_trades")
public class Trade implements Serializable {

	private static final long serialVersionUID = 1L;

	private int trade_id; 
	private int user_id ; 
	private int stock_id ; 
	private int algo_id ;  
	private String trade_type ; 
	private int num_shares ;
	private double price_pershare ; 

	public void setTrade_ID(int id) {
		this.trade_id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getTrade_ID() {
		return this.trade_id;
	}  

	public int getUser_ID() {
		return this.user_id;
	}

	public void setUser_ID(int index) {
		this.user_id = index;
	}
	public int getStock_ID() {
		return this.stock_id;
	}

	public void setStock_ID(int index) {
		this.stock_id = index;
	}

	public int getAlgo_ID() {
		return this.algo_id;
	}

	public void setAlgo_ID(int index) {
		this.algo_id = index;
	}
	
	public String getTrade_Type() {
		return this.trade_type;
	}

	public void setTrade_Type(String type) {
		this.trade_type = type;
	}
	
	public int getNum_Shares() {
		return this.num_shares;
	}

	public void setNum_Shares(int numberOfShares) {
		this.num_shares = numberOfShares;
	}

	public double getPrice_PerShare() {
		return this.price_pershare;
	}

	public void setPrice_PerShare(double pricePerShare) {
		this.price_pershare = pricePerShare;
	}
}
