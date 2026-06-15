package com.axonivy.connector.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessagePushRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String queueName;	
	private List<MessageDetail> messageDetails;
	
	public MessagePushRequest() {
		messageDetails = new ArrayList<>();
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public List<MessageDetail> getMessageDetails() {
		return messageDetails;
	}

	public void setMessageDetails(List<MessageDetail> messageDetails) {
		this.messageDetails = messageDetails;
	}

	@Override
	public String toString() {
		return "MessagePushRequest [queueName=" + queueName + ", messageDetails=" + messageDetails + "]";
	}	
	
}
