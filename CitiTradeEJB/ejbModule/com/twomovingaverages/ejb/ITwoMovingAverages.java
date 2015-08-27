package com.twomovingaverages.ejb;

import javax.ejb.Local;

import com.tradingcontroller.TC_ATObject;

@Local
public interface ITwoMovingAverages {

	public void run(TC_ATObject obj);
}
