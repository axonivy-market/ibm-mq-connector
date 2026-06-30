package com.axonivy.connector.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest
public class PropertyManagerTest {

	@Test
	public void readsChannelFromIvyVariables(AppFixture fixture) {
		fixture.var("ibmmqConnector.queueManager", "QM1");
		fixture.var("ibmmqConnector.channel", "DEV.APP.SVRCONN");		
		fixture.var("ibmmqConnector.port", "18");	
		fixture.var("ibmmqConnector.username", "app");		
		fixture.var("ibmmqConnector.password", "123456");

		assertThat(PropertyManager.getQueueManager()).isEqualTo("QM1");
		assertThat(PropertyManager.getChannel()).isEqualTo("DEV.APP.SVRCONN");
		assertThat(PropertyManager.getUsername()).isEqualTo("app");
		assertThat(PropertyManager.getPassword()).isEqualTo("123456");
		assertThat(PropertyManager.getPort()).isEqualTo(18);
	}

}
