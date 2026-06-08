package com.axonivy.connector.model;

import java.util.ArrayList;
import java.util.List;

public class MessageResult {
	private String error;
	private String messagetypeRequest;
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

	public String getMessagetypeRequest() {
		return messagetypeRequest;
	}

	public void setMessagetypeRequest(String messagetypeRequest) {
		this.messagetypeRequest = messagetypeRequest;
	}

	public List<MessageDetail> getMessageDetails() {
		return messageDetails;
	}

	public void setMessageDetails(List<MessageDetail> messageDetails) {
		this.messageDetails = messageDetails;
	}

}
