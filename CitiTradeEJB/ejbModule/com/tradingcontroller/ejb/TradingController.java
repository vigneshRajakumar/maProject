package com.tradingcontroller.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Message-Driven Bean implementation class for: TradingController
 */
@MessageDriven(
		activationConfig = { @ActivationConfigProperty(
				propertyName = "destination", propertyValue = "TCQueue"), @ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue")
		}, 
		mappedName = "TCQueue")


public class TradingController implements MessageListener {
    	
	public void sendMsg() throws NamingException{

		TradingController tcController = new TradingController();

		InitialContext context = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/TradeConnectionFactory");

		Queue ATQueue = (Queue) context.lookup("jms/ATQueue");

		JMSContext jmsContext = connectionFactory.createContext();

		JMSProducer jmsProducer = jmsContext.createProducer();

		String messageToSend = "testetest";
		
		jmsProducer.send(ATQueue, messageToSend);
	}
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        // TODO Auto-generated method stub
        
    }

}
