package com.tradingcontroller.ejb;

import javax.ejb.Local;
import javax.naming.NamingException;

@Local
public interface ITradingController {

	void sendMsg() throws NamingException;
}
