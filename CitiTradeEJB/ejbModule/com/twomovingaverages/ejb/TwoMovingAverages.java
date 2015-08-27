package com.twomovingaverages.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import com.marketdatahandle.jpa.Stock;
import com.marketdatahandler.ejb.MarketDataHandlerLocal;
import com.tradingcontroller.TC_ATObject;


@Stateful
public class TwoMovingAverages implements ITwoMovingAverages{

	@EJB
	private MarketDataHandlerLocal marketDataHandler;

	ArrayList<TMAObjClass> storeToMonitor = storeToMonitor = new ArrayList<TMAObjClass>();

	private final static int REPEAT_CYCLE_TIME = 2000 ;
	Timer timer = null;

	private class MonitorPrice extends TimerTask {

		public void run() {			

			for (TMAObjClass obj : storeToMonitor){

				System.out.println(obj.getStockSymbol());

				Stock newInfo = marketDataHandler.getStockBySymbol(obj.getStockSymbol());
				double newStockAvg = newInfo.getAsk() + newInfo.getBid();

				obj.setShortCounter(obj.getShortCounter()+1);

				double newShortAvg = (obj.getShortAvg() + newStockAvg)	/ obj.getShortCounter();

				obj.setShortAvg(newShortAvg);

				if (obj.getShortAvg() < obj.getLongAvg()){ //if short average line is below long average  (GOING FOR LONG POSITION)

					if (!obj.isBuy() && obj.getShortAvg() >= obj.getLongAvg()){ //if haven buy and just pass the long avg,quickly buy it

						obj.setBuy(true);;
						double getBuyPrice = newInfo.getAsk();
						obj.setBuyPrice(getBuyPrice);	

					} else if (obj.isBuy()) { //bought already and check for 1 percent profit

						if (obj.getShortAvg() >= (obj.getBuyPrice() * 1.1)){

							obj.setBuy(false); 

						} else if (obj.getShortAvg() <= (obj.getBuyPrice() * 1.1)){ //bought already and check for 1 percent profit loss, exit here	

						}
					}

				} else if (obj.getLongAvg() < obj.getShortAvg()){ //if short average line is higher than long average (Going for SHORT POSITION)

					if (!obj.isSell() && obj.getShortAvg() <= obj.getLongAvg()){ //if it is not sold yet, sell it 

						obj.setSell(true);
						obj.setSellPrice(newInfo.getBid());
					} else if (obj.isSell()) { //sold already and check for 1 percent profit

						if (obj.getShortAvg() <= (obj.getSellPrice() * 1.1)){ //if there is profit

							obj.setSell(false);

						} else if (obj.getShortAvg() >= (obj.getSellPrice() * 1.1)){ //if there is loss, exit here

							obj.setSell(false) ;
							//buy here(or remove from trade) and get the loss 





						}
					}				

				}

			}
		}
	}


	public double monitorPrice(){

		int repeatCycleTime= 5000;

		timer = new Timer();
		timer.schedule(new MonitorPrice(), 0, REPEAT_CYCLE_TIME);
		return 0.0;
	}


	@Override
	public void run(TC_ATObject obj){

		TMAObjClass tmaObj = new TMAObjClass(obj);
		monitorPrice();


		storeToMonitor.add(tmaObj);


	}

}
