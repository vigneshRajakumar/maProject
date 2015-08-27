package com.twomovingaverages.ejb;

import javax.ejb.Local;

import com.tradingcontroller.TC_ATObject;

@Local
public interface ITwoMovingAverages {

	public boolean isMonitoring();
	public void startNewTrade(String symbol, double loss, double profit,double totalAmountLimit) ;
	public void run(TC_ATObject obj);
	public void startMonitoring();
}
