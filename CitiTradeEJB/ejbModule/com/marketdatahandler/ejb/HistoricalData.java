package com.marketdatahandler.ejb;

public class HistoricalData {
	private String symbol;
	private double open;
	private double close;
	private double high;
	private double low;
	private long volume;
	private double adj_colse;
	
	
	public HistoricalData(String s, double o, double c, double h, double l, long v, double a){
		symbol = s;
		open = o;
		close = c;
		high = h;
		low = l;
		volume = v;
		adj_colse = a;
	}
	
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public double getAdj_colse() {
		return adj_colse;
	}
	public void setAdj_colse(double adj_colse) {
		this.adj_colse = adj_colse;
	}
}
