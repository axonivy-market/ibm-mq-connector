package com.axonivy.connector.imb.mq.model;

import java.util.ArrayList;
import java.util.List;

import com.axonivy.connector.model.MessageDetail;

public class AutoApprovalResult {	
	private List<MessageDetail> autoApprovalMessages;
	private List<MessageDetail> manualMessages;
	
	public AutoApprovalResult() {
		autoApprovalMessages = new ArrayList<>();
		manualMessages = new ArrayList<>();		
	}

	public List<MessageDetail> getAutoApprovalMessages() {
		return autoApprovalMessages;
	}

	public void setAutoApprovalMessages(List<MessageDetail> autoApprovalMessages) {
		this.autoApprovalMessages = autoApprovalMessages;
	}

	public List<MessageDetail> getManualMessages() {
		return manualMessages;
	}

	public void setManualMessages(List<MessageDetail> manualMessages) {
		this.manualMessages = manualMessages;
	}
	
}
