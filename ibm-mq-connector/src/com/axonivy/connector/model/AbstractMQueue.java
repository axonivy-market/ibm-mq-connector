package com.axonivy.connector.model;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import ch.ivyteam.ivy.environment.Ivy;

public abstract class AbstractMQueue {
	private static final int RECONNECT_TIMEOUT = 300;// seconds

	protected MQQueueConnectionFactory connectionFactory;
	protected Connection connection;
	protected Session session;
	protected Queue queue;

	protected boolean skipListener;
	protected String host;
	protected int port;	
	protected String queueManager;
	protected String channel;
	protected String username;
	protected String password;
	protected String SSLCipherSuite;
	protected int transportType;
	protected boolean mqDebugMessages;
	protected String additionalStringValues;
	protected String additionalBooleanValues;

	protected Map<String, String> additionalStringValuesMap;
	protected Map<String, Boolean> additionalBooleanValuesMap;

	protected abstract void initializeConnectionProperties();

	protected void createConnectionFactory() throws JMSException {
		initializeConnectionProperties();

		connectionFactory = new MQQueueConnectionFactory();
		connectionFactory.setHostName(host);
		connectionFactory.setPort(port);
		connectionFactory.setQueueManager(queueManager);
		connectionFactory.setChannel(channel);
		connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
		connectionFactory.setClientReconnectOptions(WMQConstants.WMQ_CLIENT_RECONNECT_Q_MGR);
		connectionFactory.setClientReconnectTimeout(RECONNECT_TIMEOUT);		
	}

	protected Connection createConnection() throws JMSException {
		if (connectionFactory == null) {
			createConnectionFactory();
		}
		return StringUtils.isNoneBlank(username, password) ? connectionFactory.createConnection(username, password)
				: connectionFactory.createConnection();
	}

	protected boolean isDebugMode() {
		return BooleanUtils.isTrue(mqDebugMessages);
	}

	protected void rollbackQuietly() {
		if (session != null) {
			try {
				session.rollback();
			} catch (Exception e) {
				if (isDebugMode()) {
					Ivy.log().warn("{0}::Failed to rollback session: {1}", this.getClass().getSimpleName(),
							e.getMessage());
				}
			}
		}
	}

	protected void closeQuietly(AutoCloseable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Exception e) {
				if (isDebugMode()) {
					Ivy.log().warn("{0}::Failed to close {1}: {2}", this.getClass().getSimpleName(),
							resource.getClass().getSimpleName(), e.getMessage());
				}
			}
		}
	}
}
