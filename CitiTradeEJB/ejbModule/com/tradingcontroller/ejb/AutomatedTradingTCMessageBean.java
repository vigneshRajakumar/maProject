package com.tradingcontroller.ejb;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;

import com.tradingcontroller.mq.TradeMessenger;
import com.tradingcontroller.ejb.TradingController;
import com.tradingcontroller.TC_ATObject;
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
	AutomatedTradingController autoTradingController;
	
	private static Logger LOGGER = 
	        Logger.getLogger (AutomatedTradingTCMessageBean.class.getName ());
	
	private static JAXBContext context;
    private static TradeMessenger tradeMessenger;
    private static final String ALGO_BB = "Bollinger Bands";
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
				TextMessage textMessage = (TextMessage)message;
				System.out.println("ATMessageBean Received:" + textMessage.getText());
				//This object will have only the symbol and the amount
				TC_ATObject obj = TradingController.tradeFromXML(textMessage.getText());
				if(obj.getAlgo().equals(ALGO_BB)) {
					if(!autoTradingController.isMonitoring()) {
						autoTradingController.StartMonitoring();
						autoTradingController.setMonitoring(true);
					}
					autoTradingController.startNewTrade(obj.getSymbol(), obj.getLoss(), obj.getProfit(), obj.getAmtToTrade());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        
    }

}