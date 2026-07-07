package com.axonivy.connector.imb.mq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditXmlLoanRequest {
	@JacksonXmlProperty(localName = "LoanPurpose")
	private String loanPurpose;

	@JacksonXmlProperty(localName = "RequestedAmount")
	private CreditXmlAmount requestedAmount;

	@JacksonXmlProperty(localName = "RequestedTermMonths")
	private Integer requestedTermMonths;

	public String getLoanPurpose() {
		return loanPurpose;
	}

	public void setLoanPurpose(String loanPurpose) {
		this.loanPurpose = loanPurpose;
	}

	public CreditXmlAmount getRequestedAmount() {
		return requestedAmount;
	}

	public void setRequestedAmount(CreditXmlAmount requestedAmount) {
		this.requestedAmount = requestedAmount;
	}

	public Integer getRequestedTermMonths() {
		return requestedTermMonths;
	}

	public void setRequestedTermMonths(Integer requestedTermMonths) {
		this.requestedTermMonths = requestedTermMonths;
	}

	@Override
	public String toString() {
		return "CreditXmlLoanRequest{" + "loanPurpose='" + loanPurpose + '\'' + ", requestedAmount=" + requestedAmount
				+ ", requestedTermMonths=" + requestedTermMonths + '}';
	}
}
