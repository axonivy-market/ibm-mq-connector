package com.axonivy.connector.imb.mq.model;

import ch.ivyteam.ivy.environment.Ivy;

public class PropertyManagerDemo {
	private static final String IBM_ROOT_PROPERTY = "ibmMQ.";
	private static final String SKIP_LISTENER_PROPERTY = IBM_ROOT_PROPERTY + "skipListener";

	public static boolean getSkipListener() {
		return Boolean.parseBoolean(Ivy.var().get(SKIP_LISTENER_PROPERTY));
	}

}
