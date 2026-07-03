package com.axonivy.connector.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.collections4.CollectionUtils;

import com.axonivy.connector.model.AbstractMQueue;
import com.axonivy.connector.model.PropertyManager;

import ch.ivyteam.ivy.environment.Ivy;

public class MQueueProducer extends AbstractMQueue {
	protected MessageProducer producer;

	@Override
	protected void initializeConnectionProperties() {
		host = PropertyManager.getHost();
		port = PropertyManager.getPort();
		channel = PropertyManager.getChannel();
		queueManager = PropertyManager.getQueueManager();
		username = PropertyManager.getUsername();
		password = PropertyManager.getPassword();
	}

	public void sendMessage(String queueName, String text) {
		if (text == null || text.isBlank()) {
			return;
		}
		sendMessages(queueName, List.of(text));
	}

	public void sendMessages(String queueName, List<String> texts) {
		if (CollectionUtils.isEmpty(texts)) {
			return;
		}
		try {
			startProducer(queueName);

			for (String text : texts) {
				Message message = session.createTextMessage(text);
				producer.send(message);
				session.commit();
				Ivy.log().info("{0}::SendMessage with JMS Message {1}", this.getClass().getSimpleName(),
						message.getJMSMessageID());
			}
			stopProducer();
		} catch (JMSException e) {
			Ivy.log().error("MQueueProducer::sendMessage: got error: ", e);
		} finally {
			stopProducer();
		}
	}

	private void startProducer(String queueName) throws JMSException {
		connection = createConnection();
		session = connection.createSession(true, Session.SESSION_TRANSACTED);
		queue = session.createQueue(queueName);
		producer = session.createProducer(queue);
		connection.start();		
	}

	private void stopProducer() {
		closeQuietly(producer);
		closeQuietly(session);
		closeQuietly(connection);

		connection = null;
		session = null;
		queue = null;
		producer = null;		
	}
}
