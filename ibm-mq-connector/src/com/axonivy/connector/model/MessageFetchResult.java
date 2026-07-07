package com.axonivy.connector.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageFetchResult implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String notification;
	private String error;
	private String messageType;
	private List<MessageDetail> messageDetails;

	public MessageFetchResult() {
		messageDetails = new ArrayList<>();
	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public List<MessageDetail> getMessageDetails() {
		return messageDetails;
	}

	public void setMessageDetails(List<MessageDetail> messageDetails) {
		this.messageDetails = messageDetails;
	}

	@Override
	public String toString() {
		return "MessageFetchResult [notification=" + notification + ", error=" + error + ", messageType=" + messageType
				+ ", messageDetails=" + messageDetails + "]";
	}	
}
