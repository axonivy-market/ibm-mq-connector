package com.axonivy.connector.imb.mq.service;

import com.axonivy.connector.service.MQueueListener;

public class ProcessButtonHandler {
	private static ProcessButtonHandler INSTANCE = new ProcessButtonHandler();
	
	public static ProcessButtonHandler getInstance() {
		return INSTANCE;
	}

	public void process(String messageType, String queueName) {	
		MQueueListener mqListener = new MQueueListener(queueName, new ProcessMessageFilterHandler(messageType));
		mqListener.start();
		mqListener.receiveNoWait();		
		mqListener.stop();
	}
	
	
}
