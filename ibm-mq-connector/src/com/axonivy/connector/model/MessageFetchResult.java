package com.axonivy.connector.model;

import java.util.ArrayList;
import java.util.List;

public class MessageFetchResult {
	private String error;
	private String messageType;
	private List<MessageDetail> messageDetails;

	public MessageFetchResult() {
		messageDetails = new ArrayList<>();
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
		return "MessageFetchResult [error=" + error + ", messageType=" + messageType + ", messageDetails="
				+ messageDetails + "]";
	}	
}
