package com.tradingcontroller.ejb;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;

/**
 * Message-Driven Bean implementation class for: AutoTraderMDB
 */
@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "ATQueue"), 
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
		},mappedName = "ATQueue")

public class AutomatedTradingController implements MessageListener {

   
	@Inject 
	JMSContext jmsContext;

	@Resource(mappedName="jms/TCQueue")	
	Queue TCQueue;
	
		
    public void onMessage(Message message) {

		try {
			if (message instanceof TextMessage)
			{
				TextMessage textMessage = (TextMessage)message;
				System.out.println("MessageBean Received:" + textMessage.getText());
				
				//jmsContext.createProducer().send(brokerQueue, newMessage);
			}
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
    }

}
