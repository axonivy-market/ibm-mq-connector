package com.axonivy.connector.model;

import ch.ivyteam.ivy.environment.Ivy;

public final class PropertyManager {
	private static final String CONNECTOR_ROOT_PROPERTY = "ibmmqConnector.";	
	private static final String HOST_PROPERTY = CONNECTOR_ROOT_PROPERTY + "host";
	private static final String PORT_PROPERTY = CONNECTOR_ROOT_PROPERTY + "port";
	private static final String CHANNEL_PROPERTY = CONNECTOR_ROOT_PROPERTY + "channel";
	private static final String QUEUE_MANAGER_PROPERTY = CONNECTOR_ROOT_PROPERTY + "queueManager";
	private static final String USERNAME_PROPERTY = CONNECTOR_ROOT_PROPERTY + "username";
	private static final String PASSWORD_PROPERTY = CONNECTOR_ROOT_PROPERTY + "password";

	public static String getHost() {
		return Ivy.var().get(HOST_PROPERTY);
	}

	public static int getPort() {
		return Integer.parseInt(Ivy.var().get(PORT_PROPERTY));
	}

	public static String getChannel() {
		return Ivy.var().get(CHANNEL_PROPERTY);
	}

	public static String getQueueManager() {
		return Ivy.var().get(QUEUE_MANAGER_PROPERTY);
	}

	public static String getUsername() {
		return Ivy.var().get(USERNAME_PROPERTY);
	}

	public static String getPassword() {
		return Ivy.var().get(PASSWORD_PROPERTY);
	}

	private PropertyManager() {}
}
