package com.axonivy.connector.imb.mq.model;



import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.environment.Ivy;

public class PropertyManagerDemo {
	private static final String IBM_ROOT_PROPERTY = "ibmMQ.";
	private static final String SKIP_LISTENER_PROPERTY = IBM_ROOT_PROPERTY + "skipListener";

	public static boolean getSkipListener() {
		String value = Ivy.var().get(SKIP_LISTENER_PROPERTY);
		return  StringUtils.isNotBlank(value) && Boolean.parseBoolean(value);
	}

}
