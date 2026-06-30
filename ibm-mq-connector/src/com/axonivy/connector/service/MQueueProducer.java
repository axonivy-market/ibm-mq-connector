package com.axonivy.connector.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.collections4.CollectionUtils;

import com.axonivy.connector.model.AbstractMQueue;
import com.axonivy.connector.model.PropertyManager;

import ch.ivyteam.ivy.environment.Ivy;

public class MQueueProducer extends AbstractMQueue {
	private static final int SEND_RETRY_COUNT = 5;
	private static final int WAIT_BEFORE_RETRY_MIN = 5;// seconds

	protected MessageProducer producer;
	private boolean started = false;

	@Override
	protected void initializeConnectionProperties() {
		host = PropertyManager.getHost();
		port = PropertyManager.getPort();
		channel = PropertyManager.getChannel();
		queueManager = PropertyManager.getQueueManager();
		username = PropertyManager.getUsername();
		password = PropertyManager.getPassword();
		mqDebugMessages = PropertyManager.getDebugMessages();
	}

	public void sendMessage(String queueName, String text) {
		if (text == null) {
			return;
		}
		try {
			startProducer(queueName);
			sendWithRetry(queueName,  () -> {
				return session.createTextMessage(text);
			});
			stopProducer();
		} catch (JMSException e) {
			Ivy.log().error("MQueueProducer::sendMessage: got error: ", e);
		}

	}

	public void sendMessages(String queueName, List<String> texts) {
		if (CollectionUtils.isEmpty(texts)) {
			return;
		}
		try {
		startProducer(queueName);
			for (String text : texts) {
				sendWithRetry(queueName,  () -> {
					return session.createTextMessage(text);
				});
			}
			stopProducer();
		} catch (JMSException e) {
			Ivy.log().error("MQueueProducer::sendMessage: got error: ", e);
		}
	}

	private synchronized void sendWithRetry(String queueName, MessageSupplier messageSupplier) {
		long waitBeforeRetry = WAIT_BEFORE_RETRY_MIN;

		for (int attempt = 1; attempt <= SEND_RETRY_COUNT; attempt++) {
			try {
				if (!started) {
					startProducer(queueName);
				}

				var message = messageSupplier.get();
				producer.send(message);
				session.commit();
				if (isDebugMode()) {
					Ivy.log().warn("{0}::SendMessage with JMS Message {1}", this.getClass().getSimpleName(),
							message.getJMSMessageID());
					if (message instanceof TextMessage textMessage) {
						Ivy.log().warn("{0}::Send message with text: {1}", this.getClass().getSimpleName(),
								textMessage.getText());
					}
				}
				return;
			} catch (JMSException e) {
				rollbackQuietly();
				stopProducer();

				if (attempt >= SEND_RETRY_COUNT) {
					Ivy.log().error("{0}::Failed to send message to MQ WFM after {1} attempts", e,
							this.getClass().getSimpleName(), SEND_RETRY_COUNT);
					return;
				}

				Ivy.log().warn("{0}::Failed to send message to MQ WFM. Retry in {1} sec. (attempt {2}/{3})", e,
						this.getClass().getSimpleName(), waitBeforeRetry, attempt, SEND_RETRY_COUNT);
			}

			try {
				TimeUnit.SECONDS.sleep(waitBeforeRetry);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Ivy.log().warn("{0}::Interrupted while waiting before retrying MQ send", e,
						this.getClass().getSimpleName());
				return;
			}

			waitBeforeRetry = waitBeforeRetry * 2;
		}
	}

	private void startProducer(String queueName) throws JMSException {
		connection = createConnection();
		session = connection.createSession(true, Session.SESSION_TRANSACTED);
		queue = session.createQueue(queueName);
		producer = session.createProducer(queue);
		connection.start();
		started = true;
	}

	private void stopProducer() {
		closeQuietly(producer);
		closeQuietly(session);
		closeQuietly(connection);

		connection = null;
		session = null;
		queue = null;
		producer = null;
		started = false;
	}

	@FunctionalInterface
	private interface MessageSupplier {
		Message get() throws JMSException;
	}

}
