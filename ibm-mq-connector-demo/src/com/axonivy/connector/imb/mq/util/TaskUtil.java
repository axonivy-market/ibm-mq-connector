package com.axonivy.connector.imb.mq.util;

import com.axonivy.connector.imb.mq.model.Applicant;
import com.axonivy.connector.imb.mq.model.CreditApplicationRequest;
import com.axonivy.connector.imb.mq.model.CreditScore;
import com.axonivy.connector.imb.mq.model.CreditXmlApplicant;
import com.axonivy.connector.imb.mq.model.CreditXmlCreditScore;
import com.axonivy.connector.imb.mq.model.CreditXmlFinancialProfile;
import com.axonivy.connector.imb.mq.model.CreditXmlLoanRequest;
import com.axonivy.connector.imb.mq.model.CreditXmlMessage;
import com.axonivy.connector.imb.mq.model.LoanApplication;
import com.axonivy.connector.imb.mq.model.LoanJsonMessage;
import com.axonivy.connector.imb.mq.model.LoanJsonMessage.Name;
import com.axonivy.connector.imb.mq.model.LoanRequest;

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
		CreditApplicationRequest request = xmlMessage.getCreditApplicationRequest();
		if (request == null) {
			return null;
		}
		loanApplication.setApplicant(getApplicantXml(request));
		loanApplication.setCreditScore(getCreditScoreXml(request));
		loanApplication.setLoanRequest(getLoanRequestXml(request));

		return loanApplication;
	}

	private static CreditScore getCreditScoreXml(CreditApplicationRequest request) {
		CreditXmlFinancialProfile financialProfile = request.getFinancialProfile();
		CreditXmlCreditScore creditScoreData = financialProfile == null ? null : financialProfile.getCreditScore();
		if (creditScoreData == null) {
			return null;
		}
		CreditScore creditScore = new CreditScore();
		creditScore.setProvider(creditScoreData.getProvider());
		creditScore.setScore(creditScoreData.getScore());
		creditScore.setScoreClass(creditScoreData.getScoreClass());

		return creditScore;
	}

	private static LoanRequest getLoanRequestXml(CreditApplicationRequest request) {
		CreditXmlLoanRequest loanRequestData = request.getLoanRequest();
		if (loanRequestData == null || loanRequestData.getRequestedAmount() == null) {
			return null;
		}
		LoanRequest loanRequest = new LoanRequest();
		loanRequest.setPurpose(loanRequestData.getLoanPurpose());
		loanRequest.setAmount(Double.parseDouble(loanRequestData.getRequestedAmount().getValue()));
		loanRequest.setTermMonths(loanRequestData.getRequestedTermMonths());

		return loanRequest;
	}

	private static Applicant getApplicantXml(CreditApplicationRequest request) {
		CreditXmlApplicant applicantData = request.getApplicant();
		if (applicantData == null) {
			return null;
		}

		Applicant applicant = new Applicant();
		Name name = new Name();
		name.setFirst(applicantData.getFirstName());
		name.setLast(applicantData.getLastName());

		applicant.setName(name);
		applicant.setCustomerId(applicantData.getCustomerId());
		applicant.setDateOfBirth(applicantData.getDateOfBirth());

		if (applicantData.getEmploymentDetails() != null
				&& applicantData.getEmploymentDetails().getMonthlyNetIncome() != null) {
			applicant.setMonthlyNetIncome(
					Double.parseDouble(applicantData.getEmploymentDetails().getMonthlyNetIncome().getValue()));
		} else {
			applicant.setMonthlyNetIncome(0.0);
		}

		return applicant;
	}
}
