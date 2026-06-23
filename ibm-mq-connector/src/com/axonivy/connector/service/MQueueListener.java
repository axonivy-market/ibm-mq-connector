package com.axonivy.connector.service;

import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import com.axonivy.connector.model.AbstractMQueue;
import com.axonivy.connector.model.PropertyManager;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.exec.Sudo;

public class MQueueListener extends AbstractMQueue {
	private static final int WAIT_BEFORE_RESTART_MIN = 5;// seconds
	private static final int WAIT_BEFORE_RESTART_MAX = 60;// seconds

	private static MQueueListener instance = new MQueueListener();
	private MessageConsumer consumer;
	protected long timeout;
	private boolean isPolling = false;
	private String queueName;

	public static MQueueListener getInstance() {
		return instance;
	}

	protected MQueueListener() {
	}

	public void start(String queueName) {
		this.queueName = queueName;
		if (isPolling) {
			return;
		}

		try {
			createConnectionFactory();
			startConsumer();
			Ivy.log().info("IBMMQueueListener::MQ Listener started");
		} catch (Exception e) {
			stopConsumer();
			Ivy.log().error("IBMMQueueListener::Failed to start MQ listener", e);
			return;
		}

		pollLoop();
	}

	public void stop() {
		isPolling = false;
		stopConsumer();
		Ivy.log().warn("IBMMQueueListener::MQ Listener stopped");
	}

	public boolean isPolling() {
		return isPolling;
	}

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

	private void pollLoop() {
		Ivy.log().info("IBMMQueueListener::Started polling queue {0} in Thread {1}", queueName,
				Thread.currentThread().getName());
		isPolling = true;

		while (keepPolling()) {
			try {
				Message message = consumer.receive(timeout);
				if (message != null) {
					handleMessage(message);
					session.commit();
				}
			} catch (JMSException je) {
				if (keepPolling()) {
					Ivy.log().warn("IBMMQueueListener::Failed to receive the message. Restart consumer!", je);
					restartConsumer();
				}
			} catch (Exception e) {
				Ivy.log().warn("IBMMQueueListener::Error while handling message. Rollback!", e);
				rollbackQuietly();
			}
		}

		isPolling = false;
		Ivy.log().info("IBMMQueueListener::Stopped polling queue {0} in Thread {1}", queueName,
				Thread.currentThread().getName());
	}

	protected void handleMessage(Message message) throws JMSException {
		if (isDebugMode()) {
			Ivy.log().warn("IBMMQueueListener::HandleMessage - Read JMS Message {0}", message.getJMSMessageID());
		}
		String text = "";
		if (message instanceof TextMessage textMessage) {

			Ivy.log().warn("implement handle messages interface: " + textMessage);

		} else {
			Ivy.log().warn("IBMMQueueListener::Received non-text message: {0}", message);
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
				Ivy.log().info("IBMMQueueListener::Consumer restarted successfully");
				return;
			} catch (Exception e) {
				Ivy.log().warn("IBMMQueueListener::Failed to restart consumer. Retry in {0} sec.", e,
						waitBeforeRestart);
			}

			try {
				TimeUnit.SECONDS.sleep(waitBeforeRestart);
			} catch (InterruptedException e) {
				Ivy.log().warn("IBMMQueueListener::Interrupted while waiting before restart consumer", e);
				Thread.currentThread().interrupt();
				return;
			}

			waitBeforeRestart = Math.min(waitBeforeRestart * 2, WAIT_BEFORE_RESTART_MAX);
		}

		Ivy.log().warn(
				"IBMMQueueListener::Stopped consumer restart attempts because listener is no longer polling or thread was interrupted");
	}

	private boolean keepPolling() {
		return !Thread.currentThread().isInterrupted() && isPolling;
	}
}
