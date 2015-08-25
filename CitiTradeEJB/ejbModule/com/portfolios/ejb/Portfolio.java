package com.portfolios.ejb;

public class Portfolio {
	private String symbol;
	private int processStatus;
	private int algo;
	private double totalAmount;
	private double currentValue;
	private double percentageChange;
	
	public Portfolio(String symbol,int process,int algo,double totalAmount,double currentValue, double percentageChange){
		this.symbol = symbol;
		this.processStatus = process;
		this.algo = algo;
		this.totalAmount = totalAmount;
		this.currentValue = currentValue;
		this.percentageChange = percentageChange;
		
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(int process) {
		this.processStatus = process;
	}
	public int getAlgo() {
		return algo;
	}
	public void setAlgo(int algo) {
		this.algo = algo;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}
	public double getPercentageChange() {
		return percentageChange;
	}
	public void setPercentageChange(double percentageChange) {
		this.percentageChange = percentageChange;
	}

}
