package com.tradingcontroller.ejb;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;

import com.marketdatahandler.ejb.CitiTradeMarketDataHandler;
import com.tradingcontroller.TradeObject;

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
	
	@EJB
	CitiTradeMarketDataHandler marketDataHandler;

	@Resource(mappedName="jms/TCQueue")	
	Queue TCQueue;
	
	private static Logger LOGGER = 
	        Logger.getLogger (AutomatedTradingController.class.getName ());
	
	private static JAXBContext context;
    
    static 
    {
        try
        {
            context = JAXBContext.newInstance (TradeObject.class);
        }
        catch (Exception ex)
        {
        	LOGGER.log (Level.SEVERE, "Couldn't create JAXB context!", ex);
        }
    }
	
	private static TradeObject tradeFromXML(String message) {
		try ( StringReader in = new StringReader (message); )
        {
            Unmarshaller unmarshaller = context.createUnmarshaller ();
            return (TradeObject) unmarshaller.unmarshal (in);
        }
        catch (Exception ex)
        {
            LOGGER.log (Level.WARNING, "Couldn't parse Trade message.", ex);
        }
        
        return null;
	}
	
	private static String tradeToXML(TradeObject trade) {
		try ( StringWriter out = new StringWriter (); )
        {
            Marshaller marshaller = context.createMarshaller ();
            marshaller.marshal (trade, out);
            return out.toString ();
        }
        catch (Exception ex)
        {
            LOGGER.log (Level.WARNING, "Couldn't build Trade message.", ex);
        }
        
        return null;
	}
		
    public void onMessage(Message message) {

		try {
			if (message instanceof TextMessage)
			{
				TextMessage textMessage = (TextMessage)message;
				System.out.println("MessageBean Received:" + textMessage.getText());
				//This object will have only the symbol and the amount
				TradeObject trade = tradeFromXML(textMessage.getText());
				//jmsContext.createProducer().send(brokerQueue, newMessage);
			}
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
    }

}
