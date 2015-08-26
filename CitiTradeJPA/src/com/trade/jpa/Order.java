package com.trade.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

import com.marketdatahandle.jpa.Stock;

@Entity
@Table(schema = "ct_project", name = "ct_orders")
public class Order implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="order_id")
	private int order_id;
	
/*	@OneToOne*/
	@Column(name = "stock")
	private String stock;
	@Column(name = "short_or_long")
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

	@Column(name = "trader_id")
	private int trader_id;
	
	@Column(name = "algo_id")
	private int algo_id;
	// all the trades under this order id
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<Trade> trades;

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

	
	public List<Trade> getTrades() {
		return trades;
	}

	public void setTrades(List<Trade> allTrades) {
		this.trades = allTrades;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
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

	public int getalgo_id() {
		return algo_id;
	}

	public void setalgo_id(int algo_id) {
		this.algo_id = algo_id;
	}

}
