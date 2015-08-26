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
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.internal.helper.Helper;

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
			System.out.println("[PARSING!!!!!!!!!!!!!!!!]: " +out.toString());
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

	@Override
	public void setRecordStatus(TC_ATObject obj ){

		String symbol = obj.getSymbol();
		double profit = obj.getProfit();
		double amt = obj.getAmtToTrade();
		double loss = obj.getLoss();
		String algo = obj.getAlgo();	

		StringBuilder sb = new StringBuilder();
		sb.append("New Trade Status Received:\n\n" );
		sb.append("Symbol: "+symbol+"\n");

		if (profit > loss)
		{
			sb.append("Profit: "+profit+"\n");
		} else {

			sb.append("Loss: "+loss+"\n");
		}
		
		sendMsgToQueue(sb.toString());		
	}
	
	@Override
	public void sendMsgToQueue(String msgToSend){

		QueueConnection queueConnection = null;

		try {

			InitialContext context = new InitialContext();
			QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup("jms/MyQueueConnectionFactory");
			Queue queue = (Queue) context.lookup("jms/MyQueue");	
			queueConnection = queueConnectionFactory.createQueueConnection();
			QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender queueSender = queueSession.createSender(queue);
			TextMessage message = queueSession.createTextMessage();

			message.setText(msgToSend);
			queueSender.send(message);
			

			// Send an empty message, to signify end of transmission.
			queueSender.send(queueSession.createMessage());

		} catch (JMSException ex) {
			System.out.println("JMSException occurred: " + ex);

		} catch (NamingException ex) {
			System.out.println("NamingException occurred: " + ex);

		} catch (Exception ex) {
			System.out.println("General Exception occurred: " + ex);

		} finally {
			if (queueConnection != null) {
				try {
					queueConnection.close();
				} catch (JMSException e) {
				}
			}
		}
	}

	@Override
	public void getMsgFromQueue(){

		QueueConnection queueConnection = null;

		try {

			InitialContext context = new InitialContext();
			QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) context.lookup("jms/MyQueueConnectionFactory");
			Queue queue = (Queue) context.lookup("jms/MyQueue");	
			queueConnection = queueConnectionFactory.createQueueConnection();
			QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueReceiver queueReceiver = queueSession.createReceiver(queue);
			queueConnection.start();

			while (true) {
				Message m = queueReceiver.receive(1);
				if (m != null) {
					if (m instanceof TextMessage) {
						TextMessage message = (TextMessage) m;
						System.out.println(message.getText());
					} else {
						
						break;
					}
				} else {
				}
			}


		} catch (JMSException ex) {
			System.out.println("JMSException occurred: " + ex);

		} catch (NamingException ex) {
			System.out.println("NamingException occurred: " + ex);

		} catch (Exception ex) {
			System.out.println("General Exception occurred: " + ex);

		} finally {
			if (queueConnection != null) {
				try {
					queueConnection.close();
				} catch (JMSException e) {
				}
			}
		}
	}



}
