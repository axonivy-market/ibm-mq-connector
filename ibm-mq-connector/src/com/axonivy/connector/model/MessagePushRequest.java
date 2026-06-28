package com.axonivy.connector.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessagePushRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String queueName;	
	private List<String> payloads;
	
	public MessagePushRequest() {
		payloads = new ArrayList<>();
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public List<String> getPayloads() {
		return payloads;
	}

	public void setPayloads(List<String> payloads) {
		this.payloads = payloads;
	}
	
}
