package com.axonivy.connector.model;

import java.io.Serializable;

public class LoanResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	
}
