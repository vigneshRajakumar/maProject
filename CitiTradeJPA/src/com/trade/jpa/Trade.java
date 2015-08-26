package com.trade.jpa;

import java.io.Serializable;

import javax.persistence.*;

import com.stocklist.jpa.StockList;


@Entity
@Table(schema="ct_project", name="ct_trades")
public class Trade implements Serializable {

	private static final long serialVersionUID = 1L;

	private int trade_id; 
	private int trader_id; 
	private StockList stockList;
	private int algo_id ;  
	private String trade_type ; 
	private int num_shares ;
	private double price_pershare ; 
	private Order order;
	

	public void setTrade_ID(int id) {
		this.trade_id = id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public int getTrade_ID() {
		return this.trade_id;
	}  

	public int getTrader_id() {
		return this.trader_id;
	}

	public void setTrader_id(int index) {
		this.trader_id = index;
	}
	
	@OneToOne
	@JoinColumn(name="stock_id")
	public StockList getStockList(){
		return this.stockList;
	}
	
	public void setStockList(StockList stocklist){
		this.stockList = stocklist;
	}
	
	/*
	public int getStock_id() {
		return this.stock_id;
	}

	public void setStock_id(int id) {
		this.stock_id = id;
	}
*/
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

	@ManyToOne
	@JoinColumn(name ="order_id")
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
}
