package com.tradingcontroller;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="trade")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class TC_ATObject {

	private String symbol;
	private double profit;
	private double loss;
	private double amtToTrade ;
	private String algo;
	
	public TC_ATObject(String symbol, double amtToTrade, double profit , double loss, String method ){
		
		this.symbol = symbol;
		this.profit = profit;
		this.loss = loss;
		this.amtToTrade = amtToTrade;
		this.algo = method;
		
	}
	
	public TC_ATObject(){
		
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getProfit() {
		return profit;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	public double getLoss() {
		return loss;
	}
	public void setLoss(double loss) {
		this.loss = loss;
	}
	public double getAmtToTrade() {
		return amtToTrade;
	}
	public void setAmtToTrade(double amtToTrade) {
		this.amtToTrade = amtToTrade;
	}

	public String getAlgo() {
		return algo;
	}

	public void setAlgo(String algo) {
		this.algo = algo;
	}
}
