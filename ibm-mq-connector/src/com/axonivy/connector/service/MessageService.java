package com.axonivy.connector.service;

import java.io.IOException;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.model.CreditXmlMessage;
import com.axonivy.connector.model.LoanJsonMessage;
import com.axonivy.connector.model.MessageDetail;
import com.axonivy.connector.model.MessageFetchRequest;
import com.axonivy.connector.model.MessagePush;
import com.axonivy.connector.model.MessagePushRequest;
import com.axonivy.connector.model.MessageResult;
import com.axonivy.connector.util.IbmMQConnectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.constants.CMQC;

import ch.ivyteam.ivy.environment.Ivy;

public class MessageService {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	private static final XmlMapper XML_MAPPER = new XmlMapper();

	public MessageResult fetch(MessageFetchRequest request) {
		Ivy.log().info("=====start MessageService::fetch");
		MessageResult result = isValidator(request);

		if (StringUtils.isNotBlank(result.getError())) {
			return result;
		}
		result.setMessageTypeRequest(request.getMessageType());

		try (IbmMQConnectUtil mqUtil = new IbmMQConnectUtil()) {
			return listenAndParseMessages(request, mqUtil);
		} catch (MQException ex) {
			result.setError("IBM MQ connection failed");
		} catch (IllegalStateException ex) {
			result.setError("Configuration error: " + ex.getMessage());
		}

		return result;
	}

	public void push(MessagePushRequest request) {
		try (IbmMQConnectUtil mqUtil = new IbmMQConnectUtil()) {
			for (MessagePush push : request.getMessagePushs()) {
				if (push != null && StringUtils.isNotBlank(push.getPayload())) {
					mqUtil.putMessage(request.getQueueName(), push.getPayload(),
							StringUtils.trimToEmpty(push.getType()));
				}
			}
			Ivy.log().info(MessageFormat.format("Published {0} message(s) to queue: {1}",
					request.getMessagePushs().size(), request.getQueueName()));
		} catch (MQException ex) {
			throw new IllegalStateException("IBM MQ connection failed", ex);
		}
	}

	private static MessageResult isValidator(MessageFetchRequest request) {
		MessageResult result = new MessageResult();
		if (request == null || StringUtils.isBlank(request.getMessageType())) {
			result.setError("MessageType is required.");
			return result;
		}
		Ivy.log().info("=====isValidator::messageType:  " + request.getMessageType());

		if (StringUtils.isBlank(request.getQueueName())) {
			result.setError("QueueName of MessageRequest is required.");
		}
		return result;
	}

	private static MessageResult listenAndParseMessages(MessageFetchRequest request, IbmMQConnectUtil mqUtil)
			throws MQException {
		int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF + CMQC.MQOO_FAIL_IF_QUIESCING;
		MQQueue queue = null;
		MessageResult result = new MessageResult();
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
					MessageDetail detail = parsePayload(payload, request.getMessageType());
					if (detail != null) {
						result.getMessageDetails().add(detail);
					}
				} catch (MQException ex) {
					if (ex.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE) {
						Ivy.log().info("No more messages available on queue: " + request.getQueueName());
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
		return result;
	}

	private static MessageDetail parsePayload(String payload, String messageType) {
		String content = StringUtils.trimToEmpty(payload);
		try {
			if ("xml".equalsIgnoreCase(messageType) && content.startsWith("<")) {
				return new MessageDetail("XML", XML_MAPPER.readValue(content, CreditXmlMessage.class));
			}
			if ("json".equalsIgnoreCase(messageType) && (content.startsWith("{") || content.startsWith("["))) {
				return new MessageDetail("JSON", JSON_MAPPER.readValue(content, LoanJsonMessage.class));
			}
			throw new IllegalStateException("Unsupported message payload format");
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to parse payload as JSON/XML", ex);
		}
	}

}
