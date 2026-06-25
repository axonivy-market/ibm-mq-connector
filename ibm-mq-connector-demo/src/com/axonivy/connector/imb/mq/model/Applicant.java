package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

import com.axonivy.connector.imb.mq.model.LoanJsonMessage.Name;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Applicant implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String customerId;
	private Name name;
	private String dateOfBirth;
	private Double monthlyNetIncome;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Double getMonthlyNetIncome() {
		return monthlyNetIncome;
	}

	public void setMonthlyNetIncome(Double monthlyNetIncome) {
		this.monthlyNetIncome = monthlyNetIncome;
	}

	@Override
	public String toString() {
		return "Applicant{" +
				"customerId='" + customerId + '\'' +
				", name=" + name +
				", dateOfBirth='" + dateOfBirth + '\'' +
				", monthlyNetIncome=" + monthlyNetIncome +
				'}';
	}
}
