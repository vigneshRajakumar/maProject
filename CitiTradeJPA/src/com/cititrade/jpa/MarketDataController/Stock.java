package com.cititrade.jpa.MarketDataController;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Stock
 *
 */
@Entity
@Table(name = "ct_stocks")
public class Stock implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="stock_id")
	private Long id;
	
	@Column(name="symbol")
	private String symbol;
	@Transient
	private double averageDailyVolume;
	@Column(name="bid")
	private double bid;
	@Column(name="ask")
	private double ask;
	@Column(name="day_hight")
	private double dailyHigh;
	@Column(name="day_low")
	private double yearlyLow;
	
	@Transient
	private double open;
	@Transient
	private double bookValue;
	@Transient
	private double changePercent;
	@Transient
	private double change;
	@Transient
	private double dividendShare;
	@Transient
//	@Temporal(TemporalType.DATE)
	private Date lastTradeDate;
	@Transient
	private double earningsShare;
	@Transient
	private double epsEstimateCurrentYear;
	@Transient
	private double epsEstimateNextYear;
	@Transient
	private double epsEstimateNextQuarter;
	@Transient
	private double dailyLow;
	@Transient
	private double yearlyHigh;
	@Transient
	private long marketCapitalization;
	@Transient
	private double ebitda;
	@Transient
	private double changeFromYearLow;
	@Transient
	private double percentChangeFromYearLow;
	@Transient
	private double changeFromYearHigh;
	@Transient
	private double percentChangeFromYearHigh;
	@Transient
	private double lastTradePrice;
	@Transient
	private double fiftyDayMovingAverage;
	@Transient
	private double twoHunderedDayMovingAverage;
	@Transient
	private double changeFromTwoHundredDayMovingAverage;
	@Transient
	private double percentChangeFromFiftyDayMovingAverage;
	@Transient
	private String currency;
	@Transient
	private double previousClose;
	@Transient
	private double changeInPercent;
	@Transient
	private double priceSales;
	@Transient
	private double priceBook;
	@Transient
//	@Temporal(TemporalType.DATE)
	private Date exDividendDate;
	@Transient
	private double pegRatio;
	@Transient
	private double priceEpsEstimateCurrentYear;
	@Transient
	private double priceEpsEstimateNextYear;
	@Transient
	private double shortRatio;
	@Transient
	private double oneYearPriceTarget;
	@Transient
	private double dividendYield;
	@Transient
//	@Temporal(TemporalType.DATE)
	private Date dividendPayDate;
	@Transient
	private double percentChangeFromTwoHundredDayMovingAverage;
	@Transient
	private double peRatio;
	@Transient
	private double volume;
	@Transient
	private String stockExchange;
	@Transient
//	@Temporal(TemporalType.DATE)
	private Date lastUpdate;
	

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getAverageDailyVolume() {
		return averageDailyVolume;
	}

	public void setAverageDailyVolume(double averageDailyVolume) {
		this.averageDailyVolume = averageDailyVolume;
	}

	public double getBid() {
		return bid;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}

	public double getAsk() {
		return ask;
	}

	public void setAsk(double ask) {
		this.ask = ask;
	}

	public double getBookValue() {
		return bookValue;
	}

	public void setBookValue(double bookValue) {
		this.bookValue = bookValue;
	}

	public double getChangePercent() {
		return changePercent;
	}

	public void setChangePercent(double changePercent) {
		this.changePercent = changePercent;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	public double getDividendShare() {
		return dividendShare;
	}

	public void setDividendShare(double dividendShare) {
		this.dividendShare = dividendShare;
	}

	public Date getLastTradeDate() {
		return lastTradeDate;
	}

	public void setLastTradeDate(Date lastTradeDate) {
		this.lastTradeDate = lastTradeDate;
	}

	public double getEarningsShare() {
		return earningsShare;
	}

	public void setEarningsShare(double earningsShare) {
		this.earningsShare = earningsShare;
	}

	public double getEpsEstimateCurrentYear() {
		return epsEstimateCurrentYear;
	}

	public void setEpsEstimateCurrentYear(double epsEstimateCurrentYear) {
		this.epsEstimateCurrentYear = epsEstimateCurrentYear;
	}

	public double getEpsEstimateNextYear() {
		return epsEstimateNextYear;
	}

	public void setEpsEstimateNextYear(double epsEstimateNextYear) {
		this.epsEstimateNextYear = epsEstimateNextYear;
	}

	public double getEpsEstimateNextQuarter() {
		return epsEstimateNextQuarter;
	}

	public void setEpsEstimateNextQuarter(double epsEstimateNextQuarter) {
		this.epsEstimateNextQuarter = epsEstimateNextQuarter;
	}

	public double getDailyLow() {
		return dailyLow;
	}

	public void setDailyLow(double dailyLow) {
		this.dailyLow = dailyLow;
	}

	public double getDailyHigh() {
		return dailyHigh;
	}

	public void setDailyHigh(double dailyHigh) {
		this.dailyHigh = dailyHigh;
	}

	public double getYearlyLow() {
		return yearlyLow;
	}

	public void setYearlyLow(double yearlyLow) {
		this.yearlyLow = yearlyLow;
	}

	public double getYearlyHigh() {
		return yearlyHigh;
	}

	public void setYearlyHigh(double yearlyHigh) {
		this.yearlyHigh = yearlyHigh;
	}

	public long getMarketCapitalization() {
		return marketCapitalization;
	}

	public void setMarketCapitalization(long marketCapitalization) {
		this.marketCapitalization = marketCapitalization;
	}

	public double getEbitda() {
		return ebitda;
	}

	public void setEbitda(double ebitda) {
		this.ebitda = ebitda;
	}

	public double getChangeFromYearLow() {
		return changeFromYearLow;
	}

	public void setChangeFromYearLow(double changeFromYearLow) {
		this.changeFromYearLow = changeFromYearLow;
	}

	public double getPercentChangeFromYearLow() {
		return percentChangeFromYearLow;
	}

	public void setPercentChangeFromYearLow(double percentChangeFromYearLow) {
		this.percentChangeFromYearLow = percentChangeFromYearLow;
	}

	public double getChangeFromYearHigh() {
		return changeFromYearHigh;
	}

	public void setChangeFromYearHigh(double changeFromYearHigh) {
		this.changeFromYearHigh = changeFromYearHigh;
	}

	public double getPercentChangeFromYearHigh() {
		return percentChangeFromYearHigh;
	}

	public void setPercentChangeFromYearHigh(double percentChangeFromYearHigh) {
		this.percentChangeFromYearHigh = percentChangeFromYearHigh;
	}

	public double getLastTradePrice() {
		return lastTradePrice;
	}

	public void setLastTradePrice(double lastTradePrice) {
		this.lastTradePrice = lastTradePrice;
	}

	public double getFiftyDayMovingAverage() {
		return fiftyDayMovingAverage;
	}

	public void setFiftyDayMovingAverage(double fiftyDayMovingAverage) {
		this.fiftyDayMovingAverage = fiftyDayMovingAverage;
	}

	public double getTwoHunderedDayMovingAverage() {
		return twoHunderedDayMovingAverage;
	}

	public void setTwoHunderedDayMovingAverage(
			double twoHunderedDayMovingAverage) {
		this.twoHunderedDayMovingAverage = twoHunderedDayMovingAverage;
	}

	public double getChangeFromTwoHundredDayMovingAverage() {
		return changeFromTwoHundredDayMovingAverage;
	}

	public void setChangeFromTwoHundredDayMovingAverage(
			double changeFromTwoHundredDayMovingAverage) {
		this.changeFromTwoHundredDayMovingAverage = changeFromTwoHundredDayMovingAverage;
	}

	public double getPercentChangeFromFiftyDayMovingAverage() {
		return percentChangeFromFiftyDayMovingAverage;
	}

	public void setPercentChangeFromFiftyDayMovingAverage(
			double percentChangeFromFiftyDayMovingAverage) {
		this.percentChangeFromFiftyDayMovingAverage = percentChangeFromFiftyDayMovingAverage;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getStockExchange() {
		return stockExchange;
	}

	public void setStockExchange(String stockExchange) {
		this.stockExchange = stockExchange;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getPeRatio() {
		return peRatio;
	}

	public void setPeRatio(double peRatio) {
		this.peRatio = peRatio;
	}

	public double getPercentChangeFromTwoHundredDayMovingAverage() {
		return percentChangeFromTwoHundredDayMovingAverage;
	}

	public void setPercentChangeFromTwoHundredDayMovingAverage(
			double percentChangeFromTwoHundredDayMovingAverage) {
		this.percentChangeFromTwoHundredDayMovingAverage = percentChangeFromTwoHundredDayMovingAverage;
	}

	public Date getDividendPayDate() {
		return dividendPayDate;
	}

	public void setDividendPayDate(Date dividendPayDate) {
		this.dividendPayDate = dividendPayDate;
	}

	public double getDividendYield() {
		return dividendYield;
	}

	public void setDividendYield(double dividendYield) {
		this.dividendYield = dividendYield;
	}

	public double getOneYearPriceTarget() {
		return oneYearPriceTarget;
	}

	public void setOneYearPriceTarget(double oneYearPriceTarget) {
		this.oneYearPriceTarget = oneYearPriceTarget;
	}

	public double getShortRatio() {
		return shortRatio;
	}

	public void setShortRatio(double shortRatio) {
		this.shortRatio = shortRatio;
	}

	public double getPriceEpsEstimateNextYear() {
		return priceEpsEstimateNextYear;
	}

	public void setPriceEpsEstimateNextYear(double priceEpsEstimateNextYear) {
		this.priceEpsEstimateNextYear = priceEpsEstimateNextYear;
	}

	public double getPriceEpsEstimateCurrentYear() {
		return priceEpsEstimateCurrentYear;
	}

	public void setPriceEpsEstimateCurrentYear(
			double priceEpsEstimateCurrentYear) {
		this.priceEpsEstimateCurrentYear = priceEpsEstimateCurrentYear;
	}

	public double getPegRatio() {
		return pegRatio;
	}

	public void setPegRatio(double pegRatio) {
		this.pegRatio = pegRatio;
	}

	public Date getExDividendDate() {
		return exDividendDate;
	}

	public void setExDividendDate(Date exDividendDate) {
		this.exDividendDate = exDividendDate;
	}

	public double getPriceBook() {
		return priceBook;
	}

	public void setPriceBook(double priceBook) {
		this.priceBook = priceBook;
	}

	public double getPriceSales() {
		return priceSales;
	}

	public void setPriceSales(double priceSales) {
		this.priceSales = priceSales;
	}

	public double getChangeInPercent() {
		return changeInPercent;
	}

	public void setChangeInPercent(double changeInPercent) {
		this.changeInPercent = changeInPercent;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String name) {
		this.currency = name;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getPreviousClose() {
		return previousClose;
	}

	public void setPreviousClose(double previousClose) {
		this.previousClose = previousClose;
	}

}
