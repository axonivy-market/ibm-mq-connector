package com.axonivy.connector.imb.mq.service;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.imb.mq.model.CreditXmlMessage;
import com.axonivy.connector.imb.mq.model.LoanApplication;
import com.axonivy.connector.imb.mq.model.LoanJsonMessage;
import com.axonivy.connector.imb.mq.model.TaskDetail;
import com.axonivy.connector.imb.mq.util.TaskUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ch.ivyteam.ivy.environment.Ivy;

public class ProcessMessageService {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	private static final XmlMapper XML_MAPPER = new XmlMapper();
	private static final String SIGNAL_CODE = "mqdemo:approval";
	private static final ProcessMessageService INSTANCE = new ProcessMessageService();

	public static ProcessMessageService getInstance() {
		return INSTANCE;
	}

	public void process(String payload) {
		processMessage(payload, null);
	}

	public void processFilter(String payload, String messageType) {
		if (StringUtils.isBlank(messageType)) {
			Ivy.log().error("Message type is empty, cannot process message: " + payload);
			return;
		}
		processMessage(payload, messageType);
	}

	private TaskDetail processMessage(String payload, String messageType) {
		Ivy.log().info("ProcessMessageService::processMessage: payload: {0}, messageType: {1}", payload, messageType);
		LoanApplication loanApplication = getLoanApplication(payload, messageType);
		if (loanApplication == null) {
			return null;
		}

		TaskDetail taskDetail = new TaskDetail();
		taskDetail.setAutoApproval(isAutoApproval(loanApplication));
		taskDetail.setLoanApplication(loanApplication);

		Ivy.log().info("=== call signal Task: " + SIGNAL_CODE + ", auto approval: " + taskDetail.isAutoApproval());
		Ivy.log().info("TaskDetail: " + taskDetail);
		Ivy.wf().signals().create().data(taskDetail).send(SIGNAL_CODE);

		Ivy.log().info("=== End processMessage.");

		return taskDetail;
	}

	private static LoanApplication getLoanApplication(String payload, String messageTypeRequest) {
		String messageType = detectMessageType(payload);
		if (messageType == null) {
			return null;
		}

		if (messageTypeRequest != null && !messageType.equalsIgnoreCase(messageTypeRequest)) {
			Ivy.log().info("Ignore the message by filter {0} only", messageTypeRequest);
			return null;
		}

		LoanApplication loanApplication = null;
		try {
			loanApplication = "JSON".equalsIgnoreCase(messageType)
					? TaskUtil.convertFromLoanJsonMessage(JSON_MAPPER.readValue(payload, LoanJsonMessage.class))
					: TaskUtil.convertFromCreditXmlMessage(XML_MAPPER.readValue(payload, CreditXmlMessage.class));
		} catch (JsonProcessingException e) {
			Ivy.log().error("Failed to parse mapper: ", e);
		}
		if (!validateLoanApplication(loanApplication)) {
			Ivy.log().error("Invalid loan application: " + loanApplication);
			return null;
		}

		return loanApplication;
	}

	private static boolean validateLoanApplication(LoanApplication loanApplication) {
		return !ObjectUtils.anyNull(loanApplication, loanApplication.getApplicant(), loanApplication.getCreditScore());
	}

	private static boolean isAutoApproval(LoanApplication loan) {
		return autoApproval(loan.getCreditScore().getScore(), loan.getApplicant().getMonthlyNetIncome());
	}

	private static boolean autoApproval(int score, double income) {
		return (score >= 700 && income >= 4000);
	}

	public static String detectMessageType(String payload) {
		String content = StringUtils.trimToEmpty(payload);
		if (content.startsWith("<")) {
			return "XML";
		}
		if (content.startsWith("{")) {
			return "JSON";
		}
		return null;
	}
}