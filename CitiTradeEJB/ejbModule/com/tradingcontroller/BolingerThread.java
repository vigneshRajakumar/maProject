package com.tradingcontroller;

import javax.jms.Message;
import javax.jms.MessageListener;
import com.marketdatahandler.ejb.CitiTradeMarketDataHandler;
import com.marketdatahandler.ejb.HistoricalData;


public class BolingerThread extends Thread implements MessageListener{

	private double stopLoss;
	private double movingAverage;
	private double percentageProfit;
	private double standardDeviation;
	private final static double BAND_WIDTH = 2;
	private final static int TIME_PERIOD = 20;
	public BolingerThread() {
		// TODO Auto-generated constructor stub
		
	}
	
	public void run() {
		//calculate sigma
		//get the context and queue
		//loop to find entry condition
			//make the trades
				//send messages
			//check the exit condition
			//exit the loop
		//exit thread
	}

	@Override
	public void onMessage(Message arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//onmessage listner

}
