package com.axonivy.connector.model;

import java.io.Serializable;

public class MessageDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type;
	private String payload;
	private boolean isAutoApproval;

	public MessageDetail() {
	}

	public MessageDetail(boolean isAutoApproval, String type, String payload) {
		this.isAutoApproval = isAutoApproval;
		this.type = type;
		this.payload = payload;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public boolean isAutoApproval() {
		return isAutoApproval;
	}

	public void setAutoApproval(boolean isAutoApproval) {
		this.isAutoApproval = isAutoApproval;
	}

	@Override
	public String toString() {
		return "MessageDetail [type=" + type + ", payload=" + payload + ", isAutoApproval=" + isAutoApproval + "]";
	}	

}
