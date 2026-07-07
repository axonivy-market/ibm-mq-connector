package com.axonivy.connector.imb.mq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmploymentDetails {
	@JacksonXmlProperty(localName = "Status")
	private String status;

	@JacksonXmlProperty(localName = "Employer")
	private String employer;

	@JacksonXmlProperty(localName = "JobTitle")
	private String jobTitle;

	@JacksonXmlProperty(localName = "MonthlyNetIncome")
	private CreditXmlAmount monthlyNetIncome;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public CreditXmlAmount getMonthlyNetIncome() {
		return monthlyNetIncome;
	}

	public void setMonthlyNetIncome(CreditXmlAmount monthlyNetIncome) {
		this.monthlyNetIncome = monthlyNetIncome;
	}

	@Override
	public String toString() {
		return "EmploymentDetails{" + "status='" + status + '\'' + ", employer='" + employer + '\'' + ", jobTitle='"
				+ jobTitle + '\'' + ", monthlyNetIncome=" + monthlyNetIncome + '}';
	}
}
