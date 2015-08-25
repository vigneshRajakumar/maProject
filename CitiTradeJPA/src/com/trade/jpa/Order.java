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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int order_id;
	
	@OneToOne
	@JoinColumn(name = "stock_id")
	private Stock stock;
	@Column(name = "short_or_sell")
	@Enumerated(EnumType.STRING)
	private OrderType type;
	
	@Column(name = "total_amount")
	private Double total_amount;
	
	@Column(name = "order_status")
	@Enumerated(EnumType.STRING)
	private OrderStatus status;// "BEFORE_ENTER" "ENTERED" "EXIT"
	@Column(name = "loss_percentage")
	private Double lossPercentage;
	@Column(name = "profit_percentage")
	private Double profitPercentage;
	
	private int trader_id;
	private int algo_id;

	// all the trades under this order id
	private ArrayList<Trade> allTrades;

	private static final long serialVersionUID = 1L;

	public Order() {
		super();
	}

	public int getOrder_id() {
		return this.order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
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

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
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
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	public int getTrader_id() {
		return trader_id;
	}

	public void setTrader_id(int trader_id) {
		this.trader_id = trader_id;
	}

	public int getAlgo_id() {
		return algo_id;
	}

	public void setAlgo_id(int algo_id) {
		this.algo_id = algo_id;
	}

}
