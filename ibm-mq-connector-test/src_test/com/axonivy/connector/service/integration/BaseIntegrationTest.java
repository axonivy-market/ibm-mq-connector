package com.axonivy.connector.test.integration;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.junit.jupiter.api.BeforeEach;

import ch.ivyteam.ivy.environment.AppFixture;

import javax.jms.Connection;
import javax.jms.JMSException;
import com.axonivy.connector.model.AbstractMQueue;

public abstract class BaseIntegrationTest {

  protected static final String HOST = "localhost";
  protected static final int MQ_PORT = 1414;
  protected static final String DOCKER_COMPPOSE_DIR = "../ibm-mq-connector-demo/docker";

  static {
    if (!isPortOpen(HOST, MQ_PORT)) {
      System.out.println("IBM MQ is not running. Starting container via docker-compose...");
      try {
        ProcessBuilder pb = new ProcessBuilder("docker-compose", "up", "-d");
        pb.directory(new File(DOCKER_COMPPOSE_DIR));
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
          throw new RuntimeException("Failed to start docker-compose. Exit code: " + exitCode);
        }
        
        long startTime = System.currentTimeMillis();
        long timeout = 60000;
        while (!isPortOpen(HOST, MQ_PORT)) {
          if (System.currentTimeMillis() - startTime > timeout) {
            throw new RuntimeException("Timeout waiting for IBM MQ container to start on port 1414");
          }
          Thread.sleep(1000);
        }
        System.out.println("IBM MQ container started successfully and port 1414 is open!");
      } catch (Exception e) {
        throw new RuntimeException("Error starting IBM MQ container", e);
      }
    } else {
      System.out.println("IBM MQ is already running on localhost:1414. Reusing existing instance.");
      try {
        // Trigger the background script inside the already running container to preload messages
        ProcessBuilder pb = new ProcessBuilder("docker-compose", "exec", "-T", "ibm-mq", "/etc/mqm/load_messages.sh");
        pb.directory(new File(DOCKER_COMPPOSE_DIR));
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
        System.out.println("Triggered message preloading script inside running container.");
      } catch (Exception e) {
        System.err.println("Failed to trigger message loading inside running container: " + e.getMessage());
      }
    }
  }

  private static boolean isPortOpen(String host, int port) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), 1000);
      return true;
    } catch (Exception e) {
      return false;
    }
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
