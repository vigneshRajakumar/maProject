package com.tradingcontroller.ejb;

import javax.ejb.Local;

@Local
public interface ITradingController {

	void sendMsg();
}
