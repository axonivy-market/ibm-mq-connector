package com.axonivy.connector.imb.mq.model;

import java.util.ArrayList;
import java.util.List;

import com.axonivy.connector.model.MessageDetail;

public class AutoApprovalResult {	
	private List<MessageDetail> autoApprovalMessages;
	private int totalManualMessages;
	
	public AutoApprovalResult() {
		autoApprovalMessages = new ArrayList<>();				
	}

	public List<MessageDetail> getAutoApprovalMessages() {
		return autoApprovalMessages;
	}

	public void setAutoApprovalMessages(List<MessageDetail> autoApprovalMessages) {
		this.autoApprovalMessages = autoApprovalMessages;
	}

	public int getTotalManualMessages() {
		return totalManualMessages;
	}

	public void setTotalManualMessages(int totalManualMessages) {
		this.totalManualMessages = totalManualMessages;
	}
	
}
