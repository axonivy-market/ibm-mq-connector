package com.axonivy.connector.model;

import java.util.ArrayList;
import java.util.List;

public class MessageResult {
	private String error;
	private String messageTypeRequest;
	private List<MessageDetail> messageDetails;

	public MessageResult() {
		messageDetails = new ArrayList<>();
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}	

	public String getMessageTypeRequest() {
		return messageTypeRequest;
	}

	public void setMessageTypeRequest(String messageTypeRequest) {
		this.messageTypeRequest = messageTypeRequest;
	}

	public List<MessageDetail> getMessageDetails() {
		return messageDetails;
	}

	public void setMessageDetails(List<MessageDetail> messageDetails) {
		this.messageDetails = messageDetails;
	}

}
