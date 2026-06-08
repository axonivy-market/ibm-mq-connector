package com.axonivy.connector.model;

import java.io.Serializable;

public class LoanResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String error;
	private String messageType;

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

	@Override
	public String toString() {
		return "LoanResult [error=" + error + ", messageType=" + messageType + "]";
	}

	
	
}
