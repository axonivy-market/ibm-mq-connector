package com.axonivy.connector.imb.mq.model;

import com.axonivy.connector.model.Applicant;
import com.axonivy.connector.model.CreditScore;
import com.axonivy.connector.model.LoanRequest;

public class LoanApplication {
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
