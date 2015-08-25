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
		
		InitialContext context = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/TradeConnectionFactory");

		Queue ATQueue = (Queue) context.lookup("jms/ATQueue");
		//Queue queue02 = (Queue) context.lookup("jms/Queue02");

		JMSContext jmsContext = connectionFactory.createContext();

		//use jmsContext to create a consumer for message coming to queue02, and message listener will be this instance
		//jmsContext.createConsumer(queue02).setMessageListener(mdbClient);

		//to send message
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
