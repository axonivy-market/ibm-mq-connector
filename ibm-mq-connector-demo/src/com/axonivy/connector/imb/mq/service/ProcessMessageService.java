package com.axonivy.connector.imb.mq.service;

import java.util.List;

import com.axonivy.connector.imb.mq.model.AutoApprovalResult;
import com.axonivy.connector.imb.mq.model.TaskDetail;
import com.axonivy.connector.imb.mq.util.TaskUtil;
import com.axonivy.connector.model.Applicant;
import com.axonivy.connector.model.CreditScore;
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
		String messageType = fetchResult.getMessageType();
		List<MessageDetail> messageDetails = fetchResult.getMessageDetails();
		int countManualMessages = 0;
		for (MessageDetail detail : messageDetails) {
			Ivy.log().info("Processing message detail: " + detail);
			String payload = detail.getPayload();
			try {
				TaskDetail taskDetail = new TaskDetail();
				taskDetail.setAutoApproval(isAutoApproval(payload, messageType));
				if (taskDetail.isAutoApproval()) {
					autoApprovalResult.getAutoApprovalMessages().add(new MessageDetail(true, messageType, payload));
				} else {
					++countManualMessages;
				}
				if ("JSON".equalsIgnoreCase(messageType)) {
					taskDetail.setLoanApplication(
							TaskUtil.convertFromLoanJsonMessage(JSON_MAPPER.readValue(payload, LoanJsonMessage.class)));
				} else if ("XML".equalsIgnoreCase(messageType)) {
					taskDetail.setLoanApplication(TaskUtil
							.convertFromCreditXmlMessage(XML_MAPPER.readValue(payload, CreditXmlMessage.class)));
				}
				Ivy.log().info(
						"=== call signal Task: " + SIGNAL_CODE + ", auto approval: " + taskDetail.isAutoApproval());
				Ivy.log().info("TaskDetail: " + taskDetail);
				Ivy.wf().signals().create().data(taskDetail).send(SIGNAL_CODE);
			} catch (Exception ex) {
				Ivy.log().error("Failed to process message detail: " + detail, ex);
			}			
		}
		autoApprovalResult.setTotalManualMessages(countManualMessages);

		return autoApprovalResult;
	}

	private static boolean isAutoApproval(String payload, String messageType) throws Exception {		
		if ("JSON".equalsIgnoreCase(messageType)) {
			return isApproveJson(JSON_MAPPER.readValue(payload, LoanJsonMessage.class));
		} else if ("XML".equalsIgnoreCase(messageType)) {
			return isApproveXml(XML_MAPPER.readValue(payload, CreditXmlMessage.class));
		}
		throw new IllegalArgumentException("Unsupported message type: " + messageType);
	}

	private static boolean isAutoApproval(int score, double income) {
		Ivy.log().warn("score >= 700 && income >= 4000");
		Ivy.log().warn("score: " + score + ", income: " + income);
		return (score >= 700 && income >= 4000);
	}

	private static boolean isApproveJson(LoanJsonMessage jsonMessage) {
		CreditScore creditScore = jsonMessage.getCreditScore();
		Applicant applicant = jsonMessage.getApplicant();

		return isAutoApproval(getScoreJson(creditScore), getIncomeJson(applicant));
	}

	private static double getIncomeJson(Applicant applicant) {
		double income = (applicant != null && applicant.getMonthlyNetIncome() != null) ? applicant.getMonthlyNetIncome()
				: 0.0;
		return income;
	}

	private static int getScoreJson(CreditScore creditScore) {
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