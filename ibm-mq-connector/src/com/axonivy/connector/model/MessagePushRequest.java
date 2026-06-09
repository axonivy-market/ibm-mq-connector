package com.axonivy.connector.model;

import java.util.List;

public class MessagePushRequest {	
	private String queueName;	
	private List<MessagePush> messagePushs;

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public List<MessagePush> getMessagePushs() {
		return messagePushs;
	}

	public void setMessagePushs(List<MessagePush> messagePushs) {
		this.messagePushs = messagePushs;
	}

	@Override
	public String toString() {
		return "MessagePushRequest [queueName=" + queueName + ", messagePushs=" + messagePushs + "]";
	}	
	
}
