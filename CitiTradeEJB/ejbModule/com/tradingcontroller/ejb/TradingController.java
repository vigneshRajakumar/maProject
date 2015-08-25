package com.tradingcontroller.ejb;

import java.io.StringWriter;
import java.util.logging.Level;

import com.tradingcontroller.TC_ATObject;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.util.logging.Logger;

@Stateless
public class TradingController implements ITradingController {

	
    private static JAXBContext context;
    
    private static Logger LOGGER = Logger.getLogger (TradingController.class.getName ());
    
	@Override
	public void sendInputValues(String symbol, double amt, double profit,
			double loss, String method) {
		
		TC_ATObject newObj = new TC_ATObject(symbol , amt , profit , loss  );
		
		String message = "";
		try {
			message = tradeToXML(newObj);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
			
		try {
			sendMsg(message);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

   
    public static String tradeToXML (TC_ATObject trade) throws JAXBException
    {
    	
    	context = JAXBContext.newInstance (TC_ATObject.class);
    	
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

	
	public void sendMsg(String message) throws NamingException {

		//TradingController tcController = new TradingController();

		InitialContext context = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/TradeConnectionFactory");

		Queue ATQueue = (Queue) context.lookup("jms/ATQueue");
		//Queue queue02 = (Queue) context.lookup("jms/Queue02");

		JMSContext jmsContext = connectionFactory.createContext();
		
		//use jmsContext to create a consumer for message coming to queue02, and message listener will be this instance
		//jmsContext.createConsumer(queue02).setMessageListener(mdbClient);

		//to send message
		JMSProducer jmsProducer = jmsContext.createProducer();
		
		jmsProducer.send(ATQueue, message);
	}    

}
