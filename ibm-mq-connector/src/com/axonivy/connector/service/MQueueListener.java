package com.axonivy.connector.service;

import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.axonivy.connector.model.AbstractMQueue;
import com.axonivy.connector.model.PropertyManager;

import ch.ivyteam.ivy.environment.Ivy;

public class MQueueListener extends AbstractMQueue {
	private static final int WAIT_BEFORE_RESTART_MIN = 5;// seconds
	private static final int WAIT_BEFORE_RESTART_MAX = 60;// seconds

	private MessageConsumer consumer;
	private boolean isPolling = false;
	private String queueName;
	private MessageHandler messageHandler;

	public MQueueListener(String queueName, MessageHandler messageHandler) {
		this.queueName = queueName;
		this.messageHandler = messageHandler;
	}

	public void start() {
		if (isPolling) {
			return;
		}
		try {
			createConnectionFactory();
			startConsumer();
			Ivy.log().info("MQueueListener::start...");
		} catch (Exception e) {
			stopConsumer();
			Ivy.log().error("MQueueListener::Failed to start: ", e);
		}
	}

	public void stop() {
		isPolling = false;
		stopConsumer();
		Ivy.log().warn("MQueueListener::stop()");
	}

	public boolean isPolling() {
		return isPolling;
	}

	@Override
	protected void initializeConnectionProperties() {
		skipListener = PropertyManager.getSkipListener();
		host = PropertyManager.getHost();
		port = PropertyManager.getPort();
		channel = PropertyManager.getChannel();
		queueManager = PropertyManager.getQueueManager();
		username = PropertyManager.getUsername();
		password = PropertyManager.getPassword();
		mqDebugMessages = PropertyManager.getDebugMessages();
	}

	public void receiveNoWait() {
		Ivy.log().info("MQueueListener::receiveNoWait queue {0} in Thread {1}", queueName,
				Thread.currentThread().getName());
		try {
			Message message;
			int receivedCount = 0;
			while ((message = consumer.receiveNoWait()) != null) {
				handleMessage(message);
				session.commit();
				receivedCount++;
			}
			Ivy.log().info("MQueueListener::receiveNoWait drained {0} messages", receivedCount);
		} catch (JMSException je) {
			Ivy.log().warn("MQueueListener::receiveNoWait failed to receive the message.", je);
		}
	}

	public void receive() {
		Ivy.log().info("MQueueListener::Started polling queue {0} in Thread {1}", queueName,
				Thread.currentThread().getName());
		isPolling = true;

		while (keepPolling()) {
			try {
				Message message = consumer.receive(5000);
				if (message != null) {
					handleMessage(message);
					session.commit();
				}
			} catch (JMSException je) {
				if (keepPolling()) {
					Ivy.log().warn("MQueueListener::Failed to receive the message. Restart consumer!", je);
					restartConsumer();
				}
			} catch (Exception e) {
				Ivy.log().warn("MQueueListener::Error while handling message. Rollback!", e);
				rollbackQuietly();
			}
		}

		isPolling = false;
		Ivy.log().info("MQueueListener::Stopped polling queue {0} in Thread {1}", queueName,
				Thread.currentThread().getName());
	}

	protected void handleMessage(Message message) throws JMSException {
		Ivy.log().warn("MQueueListener::HandleMessage - Read JMS Message {0}", message.getJMSMessageID());
		String text = "";
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			text = textMessage.getText();
			if (messageHandler != null) {
				messageHandler.handleText(text);
			} else {
				Ivy.log().warn("MQueueListener::No MessageHandler registered - message ignored");
			}
		} else {
			Ivy.log().warn("MQueueListener::Received non-text message: {0}", message);
		}
	}

	private void startConsumer() throws JMSException {
		connection = createConnection();
		session = connection.createSession(true, Session.SESSION_TRANSACTED);
		queue = session.createQueue(this.queueName);
		consumer = session.createConsumer(queue);
		connection.start();
	}

	private void stopConsumer() {
		closeQuietly(consumer);
		closeQuietly(session);
		closeQuietly(connection);

		connection = null;
		session = null;
		queue = null;
		consumer = null;
	}


	private void restartConsumer() {
		long waitBeforeRestart = WAIT_BEFORE_RESTART_MIN;

		while (keepPolling()) {
			stopConsumer();

			try {
				startConsumer();
				Ivy.log().info("MQueueListener::Consumer restarted successfully");
				return;
			} catch (Exception e) {
				Ivy.log().warn("MQueueListener::Failed to restart consumer. Retry in {0} sec.", e, waitBeforeRestart);
			}

			try {
				TimeUnit.SECONDS.sleep(waitBeforeRestart);
			} catch (InterruptedException e) {
				Ivy.log().warn("MQueueListener::Interrupted while waiting before restart consumer", e);
				Thread.currentThread().interrupt();
				return;
			}

			waitBeforeRestart = Math.min(waitBeforeRestart * 2, WAIT_BEFORE_RESTART_MAX);
		}

		Ivy.log().warn(
				"MQueueListener::Stopped consumer restart attempts because listener is no longer polling or thread was interrupted");
	}

	private boolean keepPolling() {
		return !Thread.currentThread().isInterrupted() && isPolling;
	}
}