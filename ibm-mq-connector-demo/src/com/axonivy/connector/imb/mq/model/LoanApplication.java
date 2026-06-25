package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

public class LoanApplication implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Applicant applicant;
	private LoanRequest loanRequest;
	private CreditScore creditScore;

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public LoanRequest getLoanRequest() {
		return loanRequest;
	}

	public void setLoanRequest(LoanRequest loanRequest) {
		this.loanRequest = loanRequest;
	}

	public CreditScore getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(CreditScore creditScore) {
		this.creditScore = creditScore;
	}

	@Override
	public String toString() {
		return "LoanApplication [applicant=" + applicant + ", loanRequest=" + loanRequest + ", creditScore="
				+ creditScore + "]";
	}

}