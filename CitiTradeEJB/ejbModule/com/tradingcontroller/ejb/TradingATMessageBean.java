package com.tradingcontroller.ejb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.tradingcontroller.TC_ATObject;
import com.tradingcontroller.ejb.TradingController;
/**
 * Message-Driven Bean implementation class for: TradingATMessageBean
 */
@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "TCQueue"),
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
		})
public class TradingATMessageBean implements MessageListener {

	@EJB
	ITradingController tradingController;
    /**
     * Default constructor. 
     */
    public TradingATMessageBean() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        // TODO Auto-generated method stub
    	if(message instanceof TextMessage) {
    		
    		try {
    			TextMessage textMessage = (TextMessage)message;
				String text = textMessage.getText();
				TC_ATObject order = TradingController.tradeFromXML(text);
				tradingController.setRecordStatus(order);
    		} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        
    }

}
