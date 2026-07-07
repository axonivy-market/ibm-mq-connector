package com.axonivy.connector.imb.mq.model;

public class AutoApprovalResult {
	private int totalManualMessages;
	private int totalAutoMessages;
	
	public int getTotalManualMessages() {
		return totalManualMessages;
	}

	public void setTotalManualMessages(int totalManualMessages) {
		this.totalManualMessages = totalManualMessages;
	}

	public int getTotalAutoMessages() {
		return totalAutoMessages;
	}

	public void setTotalAutoMessages(int totalAutoMessages) {
		this.totalAutoMessages = totalAutoMessages;
	}
	
}
