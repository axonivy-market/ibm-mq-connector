package com.axonivy.connector.service;

import java.util.Hashtable;

import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

import ch.ivyteam.ivy.environment.Ivy;

public class mqService {
	public static MQQueueManager connect() throws MQException {
		String host = Ivy.var().get("ibmmqConnector.host");
		int port = Integer.parseInt(Ivy.var().get("ibmmqConnector.port"));
		String channel = Ivy.var().get("ibmmqConnector.channel");
		String queueManager = Ivy.var().get("ibmmqConnector.queueManager");
		String user = Ivy.var().get("ibmmqConnector.user");
		String password = Ivy.var().get("ibmmqConnector.password");
		
		return connectMQ(host, port, channel, queueManager, user, password);
	}
	
	
	private static MQQueueManager connectMQ(String host, int port, String channel, String queueManager, String user,
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

	public static void disconnect(MQQueueManager queueManager) {
		if (queueManager == null) {
			return;
		}
		try {
			if (queueManager.isConnected()) {
				queueManager.disconnect();
			}
		}
		catch (MQException ex) {
			// TODO: return Ivy error
			System.err.println("IBM MQ disconnect failed: " + ex.getMessage());
		}
	}
}
