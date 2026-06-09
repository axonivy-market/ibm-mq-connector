package com.axonivy.connector.model;

public class MessagePush {
	private String type;
	private String payload;
	
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
		return "MessagePush [type=" + type + ", payload=" + payload + "]";
	}	
	
}
