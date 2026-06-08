package com.axonivy.connector.model;

public class MessageRequest {
	private String messageType;
	private String queueName;
	private int maxMessage;

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
