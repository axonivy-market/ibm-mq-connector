package com.axonivy.connector.model;

import java.io.Serializable;

public class MessageDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private String type;
	private String payload;

	public MessageDetail() {
	}

	public MessageDetail(String type, String payload) {
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

	@Override
	public String toString() {
		return "MessageDetail [type=" + type + ", payload=" + payload + "]";
	}

}
