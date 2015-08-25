package com.tradingcontroller.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.tradingcontroller.TradeObject;
import com.tradingcontroller.mq.TradeMessenger;

/**
 * Message-Driven Bean implementation class for: AutomatedTradingOBMessagebean
 */
@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "OrderBroker_Reply"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
		})
public class AutomatedTradingOBMessagebean implements MessageListener {
	
	@EJB
	AutomatedTradingController automatedTradingController;
    /**
     * Default constructor. 
     */
    public AutomatedTradingOBMessagebean() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        // TODO Auto-generated method stub
    	if(message instanceof TextMessage) {
    		TextMessage textMsg = (TextMessage)message;
    		TradeObject trade = TradeMessenger.parseTradeMessage(textMsg);
    		automatedTradingController.RecordTrade(trade);
    	}
    }

}
