package com.axonivy.connector.imb.mq.util;

import com.axonivy.connector.imb.mq.model.LoanApplication;
import com.axonivy.connector.model.Applicant;
import com.axonivy.connector.model.CreditScore;
import com.axonivy.connector.model.LoanRequest;
import com.axonivy.connector.model.CreditXmlMessage;
import com.axonivy.connector.model.CreditXmlMessage.CreditApplicationRequest;
import com.axonivy.connector.model.LoanJsonMessage;
import com.axonivy.connector.model.LoanJsonMessage.Name;

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
		CreditApplicationRequest Request = xmlMessage.getCreditApplicationRequest();
		if (Request == null) {
			return null;
		}
		loanApplication.setApplicant(getApplicantXml(Request));
		loanApplication.setCreditScore(getCreditScoreXml(Request));
		loanApplication.setLoanRequest(getLoanReuestXml(Request));

		return loanApplication;
	}

	private static CreditScore getCreditScoreXml(CreditApplicationRequest request) {
		if (request.getFinancialProfile() == null || request.getFinancialProfile().getCreditScore() == null) {
			return null;
		}
		CreditScore creditScore = new CreditScore();
		creditScore.setProvider(request.getFinancialProfile().getCreditScore().getProvider());
		creditScore.setScore(request.getFinancialProfile().getCreditScore().getScore());
		creditScore.setScoreClass(request.getFinancialProfile().getCreditScore().getScoreClass());

		return creditScore;
	}

	private static LoanRequest getLoanReuestXml(CreditApplicationRequest request) {
		if (request.getLoanRequest() == null) {
			return null;
		}
		LoanRequest loanRequest = new LoanRequest();
		loanRequest.setPurpose(request.getLoanRequest().getLoanPurpose());
		loanRequest.setAmount(Double.parseDouble(request.getLoanRequest().getRequestedAmount().getValue()));
		loanRequest.setTermMonths(request.getLoanRequest().getRequestedTermMonths());

		return loanRequest;
	}

	private static Applicant getApplicantXml(CreditApplicationRequest request) {
		if (request.getApplicant() == null) {
			return null;
		}

		Applicant applicant = new Applicant();
		Name name = new Name();
		name.setFirst(request.getApplicant().getFirstName());
		name.setLast(request.getApplicant().getLastName());

		applicant.setName(name);
		applicant.setCustomerId(request.getApplicant().getCustomerId());
		applicant.setDateOfBirth(request.getApplicant().getDateOfBirth());

		if (request.getApplicant().getEmploymentDetails() != null
				&& request.getApplicant().getEmploymentDetails().getMonthlyNetIncome() != null) {
			applicant.setMonthlyNetIncome(
					Double.parseDouble(request.getApplicant().getEmploymentDetails().getMonthlyNetIncome().getValue()));
		} else {
			applicant.setMonthlyNetIncome(0.0);
		}

		return applicant;
	}
}
