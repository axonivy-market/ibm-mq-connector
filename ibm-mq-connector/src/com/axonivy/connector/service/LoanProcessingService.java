package com.axonivy.connector.service;

import java.io.IOException;
import java.util.Hashtable;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.model.LoanJsonMessage;
import com.axonivy.connector.model.LoanRequest;
import com.axonivy.connector.model.LoanResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
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
		MQQueueManager queueManager = null;

		try {
			queueManager = this.connect();
			listenAndParseMessages(result, QUEUE_NAME, queueManager, MAX_MESSAGES);
		} catch (MQException ex) {
			Ivy.log().error("IBM MQ connection failed. Reason code=" + ex.reasonCode + ", message=" + ex.getMessage());
			result.setError("IBM MQ connection failed");
			return result;
		} catch (IllegalStateException ex) {
			Ivy.log().error("Configuration error: " + ex.getMessage());
			result.setError("Configuration error: " + ex.getMessage());
			return result;
		} finally {
			disconnect(queueManager);
		}

		if ("json".equalsIgnoreCase(result.getMessageType())) {
			for (LoanJsonMessage jsonMessage : result.getJsonMessages()) {
				// handle automatic approve
				autoApprove(jsonMessage, result);
			}
			String message = String.format("Total messages fetched: %d JSON, %d auto-approval, and %d manual.",
					result.getJsonMessages().size(), result.getAutoApprovedCount(), result.getManualReviewCount());

			result.setMessage(message);
		}

		return result;
	}
	
	private static void autoApprove(LoanJsonMessage jsonMessage, LoanResult result) {
		LoanJsonMessage.CreditScore creditScore = jsonMessage.getCreditScore();
		LoanJsonMessage.Applicant applicant = jsonMessage.getApplicant();

		int score = (creditScore != null && creditScore.getScore() != null) ? creditScore.getScore() : 0;
		double income = (applicant != null && applicant.getMonthlyNetIncome() != null) ? applicant.getMonthlyNetIncome() : 0.0;

		if (score >= 700 && income >= 4000) {
			result.setAutoApprovedCount(result.getAutoApprovedCount() + 1);
		} else {
			result.setManualReviewCount(result.getManualReviewCount() + 1);
		}
	}

	private static void listenAndParseMessages(LoanResult result, String queueName, MQQueueManager queueManager,
			int maxMessages) throws MQException {
		int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF + CMQC.MQOO_FAIL_IF_QUIESCING;
		MQQueue queue = null;
		try {
			queue = queueManager.accessQueue(queueName, openOptions);
			LoanJsonMessage jsonMessage = null;
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
						System.out.println("No more messages available on queue: " + queueName);
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

	private MQQueueManager connect() {
		String host = Ivy.var().get("ibmmqConnector.host");
		int port = Integer.parseInt(Ivy.var().get("ibmmqConnector.port"));
		String channel = Ivy.var().get("ibmmqConnector.channel");
		String queueManager = Ivy.var().get("ibmmqConnector.queueManager");
		String user = Ivy.var().get("ibmmqConnector.user");
		String password = Ivy.var().get("ibmmqConnector.password");

		try {
			Ivy.log().info("===== start connectMQ");
			return connectMQ(host, port, channel, queueManager, user, password);
		} catch (MQException e) {
			Ivy.log().error("Can not connect to IBM MQ: " + e.getMessage());
		}
		return null;
	}

	private MQQueueManager connectMQ(String host, int port, String channel, String queueManager, String user,
			String password) throws MQException {
		Hashtable<String, Object> properties = new Hashtable<>();
		properties.put(CMQC.HOST_NAME_PROPERTY, host);
		properties.put(CMQC.PORT_PROPERTY, port);
		properties.put(CMQC.CHANNEL_PROPERTY, channel);
		properties.put(CMQC.TRANSPORT_PROPERTY, CMQC.TRANSPORT_MQSERIES_CLIENT);
		properties.put(CMQC.USER_ID_PROPERTY, user);
		properties.put(CMQC.PASSWORD_PROPERTY, password);
		return new MQQueueManager(queueManager, properties);
	}

	public void disconnect(MQQueueManager queueManager) {
		if (queueManager == null) {
			return;
		}
		try {
			if (queueManager.isConnected()) {
				queueManager.disconnect();
			}
		} catch (MQException ex) {
			Ivy.log().error("IBM MQ disconnect failed: " + ex.getMessage());
		}
	}
}
