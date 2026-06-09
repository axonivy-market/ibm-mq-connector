package com.axonivy.connector.imb.mq.service;

import java.util.List;

import com.axonivy.connector.model.CreditXmlMessage;
import com.axonivy.connector.model.LoanJsonMessage;
import com.axonivy.connector.model.MessageDetail;
import com.axonivy.connector.model.MessageFetchResult;

import ch.ivyteam.ivy.environment.Ivy;

public class ProcessMessageService {

	public void process(MessageFetchResult result) {
		Ivy.log().warn("ProcessMessageService::process:result: " + result);
		String messageType = result.getMessageType();
		List<MessageDetail> messageDetails = result.getMessageDetails();
		for (MessageDetail detail : messageDetails) {
			Ivy.log().info("Processing message detail: " + detail);
			Object payload = detail.getPayload();
			if ("JSON".equalsIgnoreCase(messageType) && payload instanceof LoanJsonMessage) {
				LoanJsonMessage jsonMessage = LoanJsonMessage.class.cast(payload);
				if (isApproveJson(jsonMessage)) { 
					Ivy.log().warn("add JSON to approvalList to push MQ");
				} else {
					Ivy.log().warn("add JSON to manualList to create Tasks");					 

				}
			} else if (payload instanceof CreditXmlMessage) {
				CreditXmlMessage xmlMessage = CreditXmlMessage.class.cast(payload);
				if (isApproveXml(xmlMessage)) {
					Ivy.log().warn("add XML to approvalList to push MQ");					

				} else {
					
					Ivy.log().warn("add XML to manualList to create Tasks");
				}
			}
		}
	}

	private static boolean isAutoApproval(int score, double income) {
		return (score >= 700 && income >= 4000);
	}

	private static boolean isApproveJson(LoanJsonMessage jsonMessage) {
		LoanJsonMessage.CreditScore creditScore = jsonMessage.getCreditScore();
		LoanJsonMessage.Applicant applicant = jsonMessage.getApplicant();

		return isAutoApproval(getScoreJson(creditScore), getIncomeJson(applicant));
	}

	private static double getIncomeJson(LoanJsonMessage.Applicant applicant) {
		double income = (applicant != null && applicant.getMonthlyNetIncome() != null) ? applicant.getMonthlyNetIncome()
				: 0.0;
		return income;
	}

	private static int getScoreJson(LoanJsonMessage.CreditScore creditScore) {
		int score = (creditScore != null && creditScore.getScore() != null) ? creditScore.getScore() : 0;
		return score;
	}

	private static boolean isApproveXml(CreditXmlMessage xmlMessage) {
		CreditXmlMessage.CreditApplicationRequest request = xmlMessage.getCreditApplicationRequest();
		if (request == null) {
			return false;
		}
		return isAutoApproval(getScoreXml(request), getIncomeXml(request));
	}

	private static double getIncomeXml(CreditXmlMessage.CreditApplicationRequest request) {
		CreditXmlMessage.Applicant applicant = request.getApplicant();
		double income = (applicant != null && applicant.getEmploymentDetails() != null
				&& applicant.getEmploymentDetails().getMonthlyNetIncome() != null
						&& applicant.getEmploymentDetails().getMonthlyNetIncome().getValue() != null)
								? Double.parseDouble(applicant.getEmploymentDetails().getMonthlyNetIncome().getValue())
						: 0.0;
		return income;
	}

	private static int getScoreXml(CreditXmlMessage.CreditApplicationRequest request) {
		CreditXmlMessage.FinancialProfile financialProfile = request.getFinancialProfile();
		int score = (financialProfile != null && financialProfile.getCreditScore() != null)
				? financialProfile.getCreditScore().getScore()
				: 0;
		return score;
	}
}
