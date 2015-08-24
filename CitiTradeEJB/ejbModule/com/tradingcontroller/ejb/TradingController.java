package com.tradingcontroller.ejb;

import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Stateless
public class TradingController implements ITradingController {
    	
	public void sendMsg() throws NamingException{
		
		TradingController tcController = new TradingController();
		InitialContext context = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/TradeConnectionFactory");

		Queue ATQueue = (Queue) context.lookup("jms/ATQueue");
		Queue TCQueue = (Queue) context.lookup("jms/TCQueue");

		JMSContext jmsContext = connectionFactory.createContext();

		//use jmsContext to create a consumer for message coming to queue02, and message listener will be this instance
		//jmsContext.createConsumer(TCQueue).setMessageListener(tcController);

		//to send message
		JMSProducer jmsProducer = jmsContext.createProducer();

		String messageToSend = "testetest";
		
		jmsProducer.send(ATQueue, messageToSend);
	}
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
    	try
		{
			if (message instanceof TextMessage)
			{
				TextMessage textMessage = (TextMessage)message;
				System.out.println("Received: " + textMessage.getText());
			}
		}
		catch (JMSException ex)
		{
			System.err.println("JMSException occurred: " + ex);
		}
		catch (Exception ex)
		{
			System.err.println("Some other exception occurred: " + ex);
		}
    }

}
