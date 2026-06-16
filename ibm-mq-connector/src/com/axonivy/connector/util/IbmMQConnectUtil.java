package com.axonivy.connector.util;

import java.io.IOException;
import java.util.Hashtable;

import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

import ch.ivyteam.ivy.environment.Ivy;



public class IbmMQConnectUtil implements AutoCloseable {
	private final MQQueueManager queueManager;
	
	public IbmMQConnectUtil() throws MQException {		
		String host = Ivy.var().get("ibmmqConnector.host");
		int port = Integer.parseInt(Ivy.var().get("ibmmqConnector.port"));
		String channel = Ivy.var().get("ibmmqConnector.channel");
		String queueManager = Ivy.var().get("ibmmqConnector.queueManager");
		String user = Ivy.var().get("ibmmqConnector.user");
		String password = Ivy.var().get("ibmmqConnector.password");
		
		Hashtable<String, Object> properties = new Hashtable<>();
		properties.put(CMQC.HOST_NAME_PROPERTY, host);
		properties.put(CMQC.PORT_PROPERTY, port);
		properties.put(CMQC.CHANNEL_PROPERTY, channel);
		properties.put(CMQC.TRANSPORT_PROPERTY, CMQC.TRANSPORT_MQSERIES_CLIENT);
		properties.put(CMQC.USER_ID_PROPERTY, user);
		properties.put(CMQC.PASSWORD_PROPERTY, password);
		this.queueManager = new MQQueueManager(queueManager, properties);
	}

	public MQQueueManager getQueueManager() {
		return queueManager;
	}
	
	public void putMessage(String queueName, String payload, String label) throws MQException {
		int openOptions = CMQC.MQOO_OUTPUT + CMQC.MQOO_FAIL_IF_QUIESCING;
		MQQueue queue = null;
		try {
			queue = queueManager.accessQueue(queueName, openOptions);
			MQMessage message = new MQMessage();
			message.characterSet = 1208;
			message.format = CMQC.MQFMT_STRING;
			message.writeString(payload);
			queue.put(message, new MQPutMessageOptions());
			Ivy.log().info("Sent " + label + " message to " + queueName);
		} catch (IOException ex) {
			throw new IllegalStateException("Unable to write message payload", ex);
		} finally {
			if (queue != null) {
				queue.close();
			}
		}
	}
	
	@Override
	public void close() {
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
