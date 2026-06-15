package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

public class TaskDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean isAutoApproval;
	private LoanApplication loanApplication;

	public TaskDetail() {
	}

	public String getCustomerId() {
		try {
			return loanApplication.getApplicant().getCustomerId();
		} catch (Exception e) {
			return "";
		}
	}	
	
	public boolean isAutoApproval() {
		return isAutoApproval;
	}

	public void setAutoApproval(boolean isAutoApproval) {
		this.isAutoApproval = isAutoApproval;
	}

	public LoanApplication getLoanApplication() {
		return loanApplication;
	}

	public void setLoanApplication(LoanApplication loanApplication) {
		this.loanApplication = loanApplication;
	}

	@Override
	public String toString() {
		return "TaskDetail [isAutoApproval=" + isAutoApproval + ", loanApplication=" + loanApplication + "]";
	}	

}
