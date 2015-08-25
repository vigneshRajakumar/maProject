package com.tradingcontroller.ejb;

import javax.ejb.Local;

@Local
public interface ITradingController {

	//void sendMsg();
	void sendInputValues(String symbol , double amt , double profit , double loss, String method);
}
