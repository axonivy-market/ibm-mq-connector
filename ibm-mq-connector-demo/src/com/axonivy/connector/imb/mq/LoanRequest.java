package com.axonivy.connector.imb.mq;

import java.io.Serializable;

public class LoanRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String messageType;
	private String error;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}	
	
}
