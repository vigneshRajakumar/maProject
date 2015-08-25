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
import com.tradingcontroller.mq.TradeMessenger;

/**
 * Message-Driven Bean implementation class for: AutoTraderMDB
 */
@MessageDriven(
		activationConfig = { 
				@ActivationConfigProperty(propertyName = "destination", propertyValue = "ATQueue"), 
				@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
		},mappedName = "ATQueue")

public class AutomatedTradingTCMessageBean implements MessageListener {

   
	@Inject 
	JMSContext jmsContext;
	
	@EJB
	CitiTradeMarketDataHandler marketDataHandler;

	@Resource(mappedName="jms/TCQueue")	
	Queue TCQueue;
	
	private static Logger LOGGER = 
	        Logger.getLogger (AutomatedTradingTCMessageBean.class.getName ());
	
	private static JAXBContext context;
    private static TradeMessenger tradeMessenger;
    static 
    {
        try
        {
            tradeMessenger = new TradeMessenger();
        }
        catch (Exception ex)
        {
        	LOGGER.log (Level.SEVERE, "Couldn't create TradeMessenger!", ex);
        }
    }
    
    public void onMessage(Message message) {

		try {
			if (message instanceof TextMessage)
			{
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
    }

}