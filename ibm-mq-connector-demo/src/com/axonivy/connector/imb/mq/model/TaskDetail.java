package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

public class TaskDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private String approvalType;
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

	
	public String getApprovalType() {
		return approvalType;
	}

	public void setApprovalType(String approvalType) {
		this.approvalType = approvalType;
	}

	public LoanApplication getLoanApplication() {
		return loanApplication;
	}

	public void setLoanApplication(LoanApplication loanApplication) {
		this.loanApplication = loanApplication;
	}

	@Override
	public String toString() {
		return "TaskDetail [approvalType=" + approvalType + ", loanApplication=" + loanApplication + "]";
	}

}
