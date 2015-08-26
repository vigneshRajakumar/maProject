package com.tradingcontroller.ejb;

import java.io.StringReader;
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
import javax.xml.bind.Unmarshaller;

import java.util.logging.Logger;

@Stateless
public class TradingController implements ITradingController {
	
    private static JAXBContext context;
    
    private static Logger LOGGER = Logger.getLogger (TradingController.class.getName ());
    
	@Override
	public void sendInputValues(String symbol, double amt, double profit,
			double loss, String method) {
		
		TC_ATObject newObj = new TC_ATObject(symbol , amt , profit , loss , method );
		
		String message = "";
		message = tradeToXML(newObj);

		try {
			sendMsg(message);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

   
    public static String tradeToXML (TC_ATObject trade)
    {
        try ( StringWriter out = new StringWriter (); )
        {
        	context = JAXBContext.newInstance(TC_ATObject.class);
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

	public static TC_ATObject tradeFromXML(String trade) {
		try ( StringReader in = new StringReader (trade); ) {
			context = JAXBContext.newInstance(TC_ATObject.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (TC_ATObject) unmarshaller.unmarshal(in);
		}
		catch (Exception ex) {
			LOGGER.log (Level.WARNING, "Couldn't parse Trade message.", ex);
		}
		return null;
	}
	
	public void sendMsg(String message) throws NamingException {

		//TradingController tcController = new TradingController();

		InitialContext context = new InitialContext();
		ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("jms/TradeConnectionFactory");

		Queue ATQueue = (Queue) context.lookup("jms/ATQueue");

		JMSContext jmsContext = connectionFactory.createContext();

		JMSProducer jmsProducer = jmsContext.createProducer();
		
		jmsProducer.send(ATQueue, message);
	}
	
	
	public String setRecordStatus(TC_ATObject obj  ){
		
		/*
		String
		String symbol = obj.getSymbol();
		double amt = obj.getAmtToTrade();
		double loss = obj.getLoss();
		double profit = obj.getProfit();
		
		
		System.out.println("Symbol : ")
				*/
		
		return "";
	}

}
