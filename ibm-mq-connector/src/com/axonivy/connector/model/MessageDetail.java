package com.axonivy.connector.model;

public class MessageDetail {
	private String type;
	private Object payload;

	public MessageDetail() {
	}

	public MessageDetail(String type, Object payload) {
		this.type = type;
		this.payload = payload;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "MessageDetail [type=" + type + ", payload=" + payload + "]";
	}

}
