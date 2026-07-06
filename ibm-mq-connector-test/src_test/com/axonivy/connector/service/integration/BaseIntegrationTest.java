package com.axonivy.connector.test.integration;

import java.io.File;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import ch.ivyteam.ivy.environment.AppFixture;

import javax.jms.Connection;
import javax.jms.JMSException;
import com.axonivy.connector.model.AbstractMQueue;

public abstract class BaseIntegrationTest {

  protected static final String HOST;
  protected static final int MQ_PORT;
  protected static final String DOCKER_COMPPOSE_FILE_PATH = "../ibm-mq-connector-test/docker/docker-compose.yaml";
  private static final String FINISHED_SETUP_LOG_TEXT_REGEX = ".*Messages preloaded!.*";

  @SuppressWarnings("resource")
  protected static final ComposeContainer MQ_CONTAINER;

  static {
    System.out.println("Starting IBM MQ container via ComposeContainer...");
    MQ_CONTAINER = new ComposeContainer(new File(DOCKER_COMPPOSE_FILE_PATH))
        .withBuild(true)
        .withExposedService("ibm-mq", 1414)
        .withExposedService("ibm-mq", 9443)
        .waitingFor("ibm-mq", Wait.forLogMessage(FINISHED_SETUP_LOG_TEXT_REGEX, 1).withStartupTimeout(Duration.ofMinutes(2)));

    MQ_CONTAINER.start();
    HOST = MQ_CONTAINER.getServiceHost("ibm-mq", 1414);
    MQ_PORT = MQ_CONTAINER.getServicePort("ibm-mq", 1414);
    System.out.println("IBM MQ container started successfully! Host: " + HOST + ", Mapped Port: " + MQ_PORT);
  }

  @BeforeEach
  protected void setupConnector(AppFixture fixture) {
    fixture.var("ibmmqConnector.host", HOST);
    fixture.var("ibmmqConnector.port", String.valueOf(MQ_PORT));
    fixture.var("ibmmqConnector.queueManager", "QM1");
    fixture.var("ibmmqConnector.channel", "DEV.APP.SVRCONN");
    fixture.var("ibmmqConnector.username", "app");
    fixture.var("ibmmqConnector.password", "123456");

    // Wait until JMS system is fully ready to accept connections
    waitForJmsReady();
  }

  private void waitForJmsReady() {
    System.out.println("Waiting for JMS connection to be fully ready...");
    long startTime = System.currentTimeMillis();
    long timeout = 45000; // 45 seconds
    while (true) {
      try {
        ConnectionTester tester = new ConnectionTester();
        tester.testConnect();
        System.out.println("JMS connection is ready!");
        return;
      } catch (Exception e) {
        if (System.currentTimeMillis() - startTime > timeout) {
          throw new RuntimeException("Timeout waiting for JMS connection to be ready: " + e.getMessage(), e);
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(ie);
        }
      }
    }
  }

  private static class ConnectionTester extends AbstractMQueue {
    public void testConnect() throws JMSException {
      Connection conn = null;
      try {
        conn = createConnection();
      } finally {
        closeQuietly(conn);
      }
    }

    @Override
    protected void initializeConnectionProperties() {
      this.host = HOST;
      this.port = MQ_PORT;
      this.queueManager = "QM1";
      this.channel = "DEV.APP.SVRCONN";
      this.username = "app";
      this.password = "123456";
    }
  }
}
