package com.axonivy.connector.service;

import java.util.Hashtable;

import com.axonivy.connector.model.LoanRequest;
import com.axonivy.connector.model.LoanResult;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

import ch.ivyteam.ivy.environment.Ivy;

public class loanProcessingService {
	public LoanResult process(LoanRequest loanRequest) {
		Ivy.log().info("===== start loanProcessingService");
		LoanResult result = new LoanResult();
		
		Ivy.log().info("===== start loanRequest", loanRequest);
		
		if (loanRequest == null || loanRequest.getMessageType() == null) {
			loanRequest = new LoanRequest();
			result.setError("MessageType is required.");
			return result;
		}
		MQQueueManager queueManager = this.connect();
		if (queueManager == null) {			
			result.setError("Can not connect to IBM MQ.");
		}
		Ivy.log().info("Connect MQ successfully");
		disconnect(queueManager);
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
		}
		catch (MQException ex) {
			// TODO: return Ivy error
			System.err.println("IBM MQ disconnect failed: " + ex.getMessage());
		}
	}
}
