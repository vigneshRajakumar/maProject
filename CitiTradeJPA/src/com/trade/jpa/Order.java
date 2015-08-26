package com.trade.jpa;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.*;

import com.marketdatahandle.jpa.Stock;

/**
 * Entity implementation class for Entity: Order
 *
 */

@Entity
@Table(schema = "ct_project", name = "ct_orders")
public class Order implements Serializable {

	private static final long serialVersionUID = 1L;
		
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int order_id;
	
	@Column(name = "short_or_sell")
	@Enumerated(EnumType.STRING)
	private OrderType type;
	
	@Column(name = "total_amount")
	private Double total_amount;
	
	@Column(name = "order_status")
	@Enumerated(EnumType.STRING)
	private OrderStatus status;// "BEFORE ENTER" "ENTERED" "EXIT"
	
	@Column(name = "loss_percentage")
	private Double lossPercentage;
	
	@Column(name = "profit_percentage")
	private Double profitPercentage;
		
	private int trader_id ;
	
	private int strategy_id;
	/*
	// all the trades under this order id
	private ArrayList<Trade> allTrades;
		
	@OneToOne
	@JoinColumn(name = "stock_id")
	private Stock stock;
	*/
	
	public int getOrder_id() {
		return this.order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}
	
	public Double getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(Double total_amount) {
		this.total_amount = total_amount;
	}
	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Double getLossPercentage() {
		return lossPercentage;
	}

	public void setLossPercentage(Double lossPercentage) {
		this.lossPercentage = lossPercentage;
	}

	public Double getProfitPercentage() {
		return profitPercentage;
	}

	public void setProfitPercentage(Double profitPercentage) {
		this.profitPercentage = profitPercentage;
	}
		
	public int gettrader_id() {
		return trader_id;
	}

	public void settrader_id(int trader_id) {
		this.trader_id = trader_id;
	}

	public int getstrategy_id() {
		return strategy_id;
	}

	public void setstrategy_id(int strategy_id) {
		this.strategy_id = strategy_id;
	}

	/*
	public ArrayList<Trade> getAllTrades() {
		return allTrades;
	}
	public void setAllTrades(ArrayList<Trade> allTrades) {
		this.allTrades = allTrades;
	}
	public Stock getStock() {
		return stock;
	}
	public void setStock(Stock stock) {
		this.stock = stock;
	}*/
	
}
