package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String purpose;
	private Double amount;
	private Integer termMonths;

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getTermMonths() {
		return termMonths;
	}

	public void setTermMonths(Integer termMonths) {
		this.termMonths = termMonths;
	}

	@Override
	public String toString() {
		return "LoanRequest{" +
				"purpose='" + purpose + '\'' +
				", amount=" + amount +
				", termMonths=" + termMonths +
				'}';
	}
}
