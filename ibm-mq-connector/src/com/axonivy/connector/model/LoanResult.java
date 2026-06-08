package com.axonivy.connector.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoanResult implements Serializable {
	private static final long serialVersionUID = 1L;	
	private String error;
	private String message;
	private String messageType;
	private int autoApprovedCount;
	private int manualReviewCount;
	
	private List<CreditXmlMessage> xmlMessages; 
	private List<LoanJsonMessage> jsonMessages;
	
	public LoanResult() {
		this.xmlMessages = new ArrayList<>();
		this.jsonMessages = new ArrayList<>();
	}
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public List<CreditXmlMessage> getXmlMessages() {
		return xmlMessages;
	}
	public void setXmlMessages(List<CreditXmlMessage> xmlMessages) {
		this.xmlMessages = xmlMessages;
	}
	public List<LoanJsonMessage> getJsonMessages() {
		return jsonMessages;
	}
	public void setJsonMessages(List<LoanJsonMessage> jsonMessages) {
		this.jsonMessages = jsonMessages;
	}
	public int getAutoApprovedCount() {
		return autoApprovedCount;
	}
	public void setAutoApprovedCount(int autoApprovedCount) {
		this.autoApprovedCount = autoApprovedCount;
	}
	public int getManualReviewCount() {
		return manualReviewCount;
	}

	public void setManualReviewCount(int manualReviewCount) {
		this.manualReviewCount = manualReviewCount;
	}

	@Override
	public String toString() {
		return "LoanResult [error=" + error + ", message=" + message + ", messageType=" + messageType
				+ ", autoApprovedCount=" + autoApprovedCount + ", manualReviewCount=" + manualReviewCount
				+ ", xmlMessages=" + xmlMessages + ", jsonMessages=" + jsonMessages + "]";
	}

}
