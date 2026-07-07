package com.axonivy.connector.imb.mq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditXmlFinancialProfile {
	@JacksonXmlProperty(localName = "MonthlyExpenses")
	private CreditXmlAmount monthlyExpenses;

	@JacksonXmlProperty(localName = "CreditScore")
	private CreditXmlCreditScore creditScore;

	public CreditXmlAmount getMonthlyExpenses() {
		return monthlyExpenses;
	}

	public void setMonthlyExpenses(CreditXmlAmount monthlyExpenses) {
		this.monthlyExpenses = monthlyExpenses;
	}

	public CreditXmlCreditScore getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(CreditXmlCreditScore creditScore) {
		this.creditScore = creditScore;
	}

	@Override
	public String toString() {
		return "CreditXmlFinancialProfile{" + "monthlyExpenses=" + monthlyExpenses + ", creditScore=" + creditScore
				+ '}';
	}
}
