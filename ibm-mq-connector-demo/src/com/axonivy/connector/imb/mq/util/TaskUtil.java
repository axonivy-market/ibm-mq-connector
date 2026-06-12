package com.axonivy.connector.imb.mq.util;

import com.axonivy.connector.imb.mq.model.LoanApplication;
import com.axonivy.connector.model.Applicant;
import com.axonivy.connector.model.LoanJsonMessage.Name;
import com.axonivy.connector.model.CreditXmlMessage;
import com.axonivy.connector.model.CreditXmlMessage.CreditApplicationRequest;
import com.axonivy.connector.model.LoanJsonMessage;

public final class TaskUtil {
	private TaskUtil() {
	}

	public static LoanApplication convertFromLoanJsonMessage(LoanJsonMessage jsonMessage) {
		if (jsonMessage == null) {
			return null;
		}
		LoanApplication loanApplication = new LoanApplication();
		loanApplication.setApplicant(jsonMessage.getApplicant());
		loanApplication.setCreditScore(jsonMessage.getCreditScore());
		loanApplication.setLoanRequest(jsonMessage.getLoanRequest());

		return loanApplication;
	}

	public static LoanApplication convertFromCreditXmlMessage(CreditXmlMessage xmlMessage) {
		if (xmlMessage == null) {
			return null;
		}
		LoanApplication loanApplication = new LoanApplication();
		CreditApplicationRequest reqeuest = xmlMessage.getCreditApplicationRequest();
		if (reqeuest == null) {
			return null;
		}		
		if (reqeuest.getApplicant() != null) {
			Applicant applicant = new Applicant();
			Name name = new Name();
			name.setFirst(reqeuest.getApplicant().getFirstName());
			name.setLast(reqeuest.getApplicant().getLastName());
			
			applicant.setName(name);
			applicant.setCustomerId(reqeuest.getApplicant().getCustomerId());
			applicant.setDateOfBirth(reqeuest.getApplicant().getDateOfBirth());
//			applicant.setMonthlyNetIncome(reqeuest.getApplicant().getEmploymentDetails().getMonthlyNetIncome().);
			loanApplication.setApplicant(applicant);
		}

		

//		loanApplication.setScore(xmlMessage.getScore());
//		loanApplication.setIncome(xmlMessage.getIncome());
		return loanApplication;
	}
}
