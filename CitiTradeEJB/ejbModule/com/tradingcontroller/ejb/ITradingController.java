package com.tradingcontroller.ejb;

import javax.ejb.Local;

import com.tradingcontroller.TC_ATObject;

@Local
public interface ITradingController {

	//void sendMsg();
	void sendInputValues(String symbol , double amt , double profit , double loss, String method);
	String setRecordStatus(TC_ATObject obj);

}
