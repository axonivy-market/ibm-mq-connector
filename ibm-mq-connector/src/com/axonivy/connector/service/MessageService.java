package com.axonivy.connector.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.model.MessageDetail;
import com.axonivy.connector.model.MessageFetchRequest;
import com.axonivy.connector.model.MessageFetchResult;
import com.axonivy.connector.model.MessagePushRequest;
import com.axonivy.connector.util.IbmMQConnectUtil;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.CMQC;

import ch.ivyteam.ivy.environment.Ivy;

public class MessageService {
	private String notification;
	public MessageFetchResult fetch(MessageFetchRequest request) {
		MessageFetchResult result = new MessageFetchResult();
		result.setError(getValidatorError(request));
		if (result.getError() != null) {
			return result;
		}
		result.setMessageType(request.getMessageType());

		try (IbmMQConnectUtil mqUtil = new IbmMQConnectUtil()) {
			result.setMessageDetails(fetchMessagesByType(request, mqUtil));
		} catch (MQException ex) {
			result.setError("IBM MQ connection failed");
		} catch (IllegalStateException ex) {
			result.setError("Configuration error: " + ex.getMessage());
		}

		return result;
	}

	public void push(MessagePushRequest request) {		
		try (IbmMQConnectUtil mqUtil = new IbmMQConnectUtil()) {
			for (MessageDetail detail : request.getMessageDetails()) {
				if (detail != null && StringUtils.isNotBlank(detail.getPayload())) {
					mqUtil.putMessage(request.getQueueName(), detail.getPayload(),
							StringUtils.trimToEmpty(detail.getType()));
				}
			}
			Ivy.log().info(MessageFormat.format("Published {0} message(s) to queue: {1}",
					request.getMessageDetails().size(), request.getQueueName()));
		} catch (MQException ex) {
			throw new IllegalStateException("IBM MQ connection failed", ex);
		}
	}

	private static String getValidatorError(MessageFetchRequest request) {
		if (request == null || StringUtils.isBlank(request.getMessageType())) {
			return "MessageType is required.";
		}

		if (StringUtils.isBlank(request.getQueueName())) {
			return "QueueName of MessageRequest is required.";
		}
		return null;
	}

	private static List<MessageDetail> fetchMessagesByType(MessageFetchRequest request, IbmMQConnectUtil mqUtil)
			throws MQException {
		Ivy.log().info("===start listenAndParseMessages " + request);
		int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF + CMQC.MQOO_FAIL_IF_QUIESCING;
		MQQueue queue = null;
		List<MessageDetail> messageDetails = new ArrayList<>();
		try {
			queue = mqUtil.getQueueManager().accessQueue(request.getQueueName(), openOptions);
			for (int index = 1; index <= request.getMaxMessage(); index++) {
				MQMessage message = new MQMessage();
				MQGetMessageOptions getOptions = new MQGetMessageOptions();
				getOptions.options = CMQC.MQGMO_WAIT + CMQC.MQGMO_CONVERT + CMQC.MQGMO_FAIL_IF_QUIESCING;
				getOptions.waitInterval = 5000;

				try {
					queue.get(message, getOptions);
					String payload = StringUtils.trimToEmpty(message.readStringOfByteLength(message.getDataLength()));
					String messageType = detectMessageType(payload);
					if (request.getMessageType().equalsIgnoreCase(messageType)) {
						messageDetails.add(new MessageDetail(false, messageType, payload));
					}
				} catch (MQException ex) {
					if (ex.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE) {
						Ivy.log().info("No more messages available on queue: " + request.getQueueName());
						break;
					}
					throw ex;
				} catch (IOException ex) {
					Ivy.log().error("Failed to read message payload", ex);
				}
			}
		} finally {
			if (queue != null) {
				queue.close();
			}
		}
		Ivy.log().info(String.format("Fetch %d messages from %s", messageDetails.size(), request.getQueueName()));
		return messageDetails;
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
