package com.axonivy.connector.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.model.LoanJsonMessage;
import com.axonivy.connector.model.LoanRequest;
import com.axonivy.connector.model.LoanResult;
import com.axonivy.connector.util.IbmMQConnectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

import ch.ivyteam.ivy.environment.Ivy;

public class LoanProcessingService {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	private static final XmlMapper XML_MAPPER = new XmlMapper();
	private static final String QUEUE_NAME = Ivy.var().get("ibmmqConnector.queueName");
	private static final int MAX_MESSAGES = 5;

	public LoanResult process(LoanRequest loanRequest) {
		Ivy.log().info("===== start loanProcessingService");
		LoanResult result = isValidator(loanRequest);
		if (StringUtils.isNotBlank(result.getError())) {
			return result;
		}
		result.setMessageType(loanRequest.getMessageType());

		try (IbmMQConnectUtil mqUtil = new IbmMQConnectUtil()) {
			listenAndParseMessages(result, QUEUE_NAME, mqUtil, MAX_MESSAGES);
		} catch (MQException ex) {
			result.setError("IBM MQ connection failed");
			return result;
		} catch (IllegalStateException ex) {
			result.setError("Configuration error: " + ex.getMessage());
			return result;
		}		

		if ("json".equalsIgnoreCase(result.getMessageType())) {
			for (LoanJsonMessage jsonMessage : result.getJsonMessages()) {
				// handle automatic approve
				autoApprove(jsonMessage, result);
			}
			String message = String.format("Total messages: %d JSON, %d auto-approval and %d manual.",
					result.getJsonMessages().size(), result.getAutoApprovedCount(), result.getManualReviewCount());

			result.setMessage(message);
		}

		return result;
	}

	private static void autoApprove(LoanJsonMessage jsonMessage, LoanResult result) {
		LoanJsonMessage.CreditScore creditScore = jsonMessage.getCreditScore();
		LoanJsonMessage.Applicant applicant = jsonMessage.getApplicant();

		int score = (creditScore != null && creditScore.getScore() != null) ? creditScore.getScore() : 0;
		double income = (applicant != null && applicant.getMonthlyNetIncome() != null) ? applicant.getMonthlyNetIncome()
				: 0.0;

		if (score >= 700 && income >= 4000) {
			result.setAutoApprovedCount(result.getAutoApprovedCount() + 1);
		} else {
			result.setManualReviewCount(result.getManualReviewCount() + 1);
		}
	}

	private static void listenAndParseMessages(LoanResult result, String queueName, IbmMQConnectUtil mqUtil,
			int maxMessages) throws MQException {
		int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF + CMQC.MQOO_FAIL_IF_QUIESCING;
		MQQueue queue = null;
		try {
			queue = mqUtil.getQueueManager().accessQueue(queueName, openOptions);
			for (int index = 1; index <= maxMessages; index++) {
				MQMessage message = new MQMessage();
				MQGetMessageOptions getOptions = new MQGetMessageOptions();
				getOptions.options = CMQC.MQGMO_WAIT + CMQC.MQGMO_CONVERT + CMQC.MQGMO_FAIL_IF_QUIESCING;
				getOptions.waitInterval = 5000;

				try {
					queue.get(message, getOptions);
					String payload = StringUtils.trimToEmpty(message.readStringOfByteLength(message.getDataLength()));
					if ("json".equalsIgnoreCase(result.getMessageType()) && payload.startsWith("{")) {
						result.getJsonMessages().add(JSON_MAPPER.readValue(payload, LoanJsonMessage.class));
					}
				} catch (MQException ex) {
					if (ex.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE) {
						Ivy.log().info("No more messages available on queue: " + queueName);
						break;
					}
					throw ex;
				} catch (IOException ex) {
					throw new IllegalStateException("Unable to read message payload", ex);
				}
			}
		} finally {
			if (queue != null) {
				queue.close();
			}
		}
	}

	public static void putMessages(List<String> payloads, String label) {
		try (IbmMQConnectUtil mqUtil = new IbmMQConnectUtil()) {
			for (String payload : payloads) {
				mqUtil.putMessage(QUEUE_NAME, payload, label.toUpperCase());
			}
			Ivy.log().info(MessageFormat.format("Published {0} {1} message(s) to queue: {2}", payloads.size(),
					label.toUpperCase(), QUEUE_NAME));
		} catch (MQException ex) {
			throw new IllegalStateException("IBM MQ connection failed", ex);
		}
	}

	private LoanResult isValidator(LoanRequest loanRequest) {
		LoanResult result = new LoanResult();
		if (loanRequest == null || StringUtils.isBlank(loanRequest.getMessageType())) {
			result.setError("MessageType is required.");
			return result;
		}
		Ivy.log().info("===== start loanRequest" + loanRequest.getMessageType());

		if (StringUtils.isBlank(QUEUE_NAME)) {
			result.setError("Missing configuration: ibmmqConnector.queueName");
		}
		return result;
	}	

	public static void putMessage(MQQueueManager queueManager, String queueName, String payload, String label)
			throws MQException {
		int openOptions = CMQC.MQOO_OUTPUT + CMQC.MQOO_FAIL_IF_QUIESCING;
		MQQueue queue = null;
		try {
			queue = queueManager.accessQueue(queueName, openOptions);
			MQMessage message = new MQMessage();
			message.characterSet = 1208;
			message.format = CMQC.MQFMT_STRING;
			message.writeString(payload);

			MQPutMessageOptions putOptions = new MQPutMessageOptions();
			queue.put(message, putOptions);
			Ivy.log().info("Sent " + label + " message to " + queueName);
		} catch (IOException e) {
			Ivy.log().error(e.getMessage());
		} finally {
			if (queue != null) {
				queue.close();
			}
		}
	}
}
