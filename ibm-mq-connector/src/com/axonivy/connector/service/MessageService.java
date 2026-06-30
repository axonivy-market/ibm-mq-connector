package com.axonivy.connector.service;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.model.MessagePushRequest;

public class MessageService {
	private static final MessageService INSTANCE = new MessageService();
	private static final MQueueProducer mQueueProducer = new MQueueProducer();

	private MessageService() {
	}

	public static MessageService getInstance() {
		return INSTANCE;
	}

	public void send(MessagePushRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("MessagePushRequest is required.");
		}
		if (StringUtils.isBlank(request.getQueueName())) {
			throw new IllegalArgumentException("QueueName is required.");
		}
		if (request.getPayloads() == null) {
			return;
		}

		mQueueProducer.sendMessages(request.getQueueName(), request.getPayloads());
	}

}