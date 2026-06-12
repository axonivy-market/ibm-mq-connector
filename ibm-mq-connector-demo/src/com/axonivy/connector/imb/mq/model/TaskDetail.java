package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

public class TaskDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String approvalType;
	private LoanApplication loanApplication;

	public TaskDetail() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return "TaskDetail [id=" + id + ", approvalType=" + approvalType + ", loanApplication=" + loanApplication + "]";
	}

}
