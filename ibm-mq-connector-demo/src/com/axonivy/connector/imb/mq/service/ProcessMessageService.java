package com.axonivy.connector.imb.mq.service;

import java.util.List;

import com.axonivy.connector.imb.mq.model.AutoApprovalResult;
import com.axonivy.connector.model.CreditXmlMessage;
import com.axonivy.connector.model.LoanJsonMessage;
import com.axonivy.connector.model.MessageDetail;
import com.axonivy.connector.model.MessageFetchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ch.ivyteam.ivy.environment.Ivy;

public class ProcessMessageService {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	private static final XmlMapper XML_MAPPER = new XmlMapper();
	private static final String SIGNAL_CODE = "mqdemo:manualapproval";

	public AutoApprovalResult process(MessageFetchResult fetchResult) {
		AutoApprovalResult autoApprovalResult = new AutoApprovalResult();
		Ivy.log().warn("ProcessMessageService::process:result: " + fetchResult);
		String messageType = fetchResult.getMessageType();
		List<MessageDetail> messageDetails = fetchResult.getMessageDetails();
		for (MessageDetail detail : messageDetails) {
			Ivy.log().info("Processing message detail: " + detail);
			String payload = detail.getPayload();
			try {
				if (isAutoApproval(payload, messageType)) {
					Ivy.log().warn("add message to approvalList to push MQ");
					autoApprovalResult.getAutoApprovalMessages().add(new MessageDetail(messageType, payload));
				} else {
					Ivy.log().warn("add message to manualList to create Tasks");
					autoApprovalResult.getManualMessages().add(new MessageDetail(messageType, payload));
				}
			} catch (Exception ex) {
				Ivy.log().error("Failed to process message detail: " + detail, ex);
			}
		}
		Ivy.log().info("=== call signal Task: " + SIGNAL_CODE);
		String taskData = "\"{\"id\":\"1122\"}\"";
		Ivy.wf().signals().create().data(taskData).send(SIGNAL_CODE);
		return autoApprovalResult;
	}

	private static boolean isAutoApproval(String payload, String messageType) throws Exception {
		Ivy.log().warn("line 43, isAutoApproval:: messageType: " + messageType);
		if ("JSON".equalsIgnoreCase(messageType)) {
			return isApproveJson(JSON_MAPPER.readValue(payload, LoanJsonMessage.class));
		} else if ("XML".equalsIgnoreCase(messageType)) {
			return isApproveXml(XML_MAPPER.readValue(payload, CreditXmlMessage.class));
		}
		throw new IllegalArgumentException("Unsupported message type: " + messageType);
	}

	private static boolean isAutoApproval(int score, double income) {
		Ivy.log().warn("score: " + score + ", income: " + income);
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