package com.axonivy.connector.model;

import java.io.Serializable;

public class MessageFetchRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String messageType;
	private String queueName;
	private int maxMessage = 10;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public int getMaxMessage() {
		return maxMessage;
	}

	public void setMaxMessage(int maxMessage) {
		this.maxMessage = maxMessage;
	}

	@Override
	public String toString() {
		return "MessageRequest [messageType=" + messageType + ", queueName=" + queueName + ", maxMessage=" + maxMessage
				+ "]";
	}
	
}
