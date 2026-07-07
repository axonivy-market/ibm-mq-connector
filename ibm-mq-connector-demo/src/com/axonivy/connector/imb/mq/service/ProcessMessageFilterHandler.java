package com.axonivy.connector.imb.mq.service;

import com.axonivy.connector.service.MessageHandler;

public class ProcessMessageFilterHandler implements MessageHandler {
	private String messageType;

	public ProcessMessageFilterHandler(String messageType) {
		this.messageType = messageType;
	}

	@Override
	public void handleText(String payload) {
		ProcessMessageService.getInstance().processFilter(payload, messageType);
	}

}
