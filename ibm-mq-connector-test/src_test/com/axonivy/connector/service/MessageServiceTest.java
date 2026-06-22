package com.axonivy.connector.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import com.axonivy.connector.model.MessageDetail;
import com.axonivy.connector.model.MessageFetchRequest;
import com.axonivy.connector.model.MessageFetchResult;
import com.axonivy.connector.model.MessagePushRequest;

import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest
public class MessageServiceTest {

  private static final String IMAGE = "icr.io/ibm-messaging/mq:latest";
  private static final String QUEUE_MANAGER = "QM1";
  private static final String QUEUE_NAME = "DEV.QUEUE.1";
  private static final String PASSWORD = "123456";
  private static final int MQ_PORT = 1414;
  private static final int CONSOLE_PORT = 9443;

  private static GenericContainer<?> mqContainer;
  private MessageService messageService;

  @BeforeAll
  static void startMqContainer() {
    mqContainer = new GenericContainer<>(DockerImageName.parse(IMAGE))
        .withEnv("LICENSE", "accept")
        .withEnv("MQ_QMGR_NAME", QUEUE_MANAGER)
        .withEnv("MQ_APP_PASSWORD", PASSWORD)
        .withEnv("MQ_ADMIN_PASSWORD", PASSWORD)
        .withCopyFileToContainer(MountableFile.forHostPath(findDemoMqscFile()), "/etc/mqm/20-dev-channels.mqsc")
        .withExposedPorts(MQ_PORT, CONSOLE_PORT)
        .waitingFor(Wait.forListeningPorts().withStartupTimeout(Duration.ofMinutes(5)));
    mqContainer.start();
  }

  @AfterAll
  static void stopMqContainer() {
    if (mqContainer != null) {
      mqContainer.stop();
    }
  }

  @BeforeEach
  void setup() {
    messageService = new MessageService();
  }

  @Test
  void shouldPushAndFetchJsonMessageFromDemoResources() throws IOException {
    String payload = readDemoMessage("message-auto.json");

    MessagePushRequest pushRequest = new MessagePushRequest();
    pushRequest.setQueueName(QUEUE_NAME);
    pushRequest.getMessageDetails().add(new MessageDetail(false, "JSON", payload));
    messageService.push(pushRequest);

    MessageFetchResult result = messageService.fetch(buildFetchRequest("JSON"));

    assertThat(result.getError()).isNull();
    assertThat(result.getMessageType()).isEqualTo("JSON");
    assertThat(result.getMessageDetails()).hasSize(1);
    assertThat(result.getMessageDetails().get(0).getPayload()).isEqualTo(payload);
    assertThat(result.getNotification()).contains("Fetched 1 JSON message(s)");
  }

  @Test
  void shouldPushAndFetchXmlMessageFromDemoResources() throws IOException {
    String payload = readDemoMessage("message-manual.xml");

    MessagePushRequest pushRequest = new MessagePushRequest();
    pushRequest.setQueueName(QUEUE_NAME);
    pushRequest.getMessageDetails().add(new MessageDetail(false, "XML", payload));
    messageService.push(pushRequest);

    MessageFetchResult result = messageService.fetch(buildFetchRequest("XML"));

    assertThat(result.getError()).isNull();
    assertThat(result.getMessageType()).isEqualTo("XML");
    assertThat(result.getMessageDetails()).hasSize(1);
    assertThat(result.getMessageDetails().get(0).getPayload()).isEqualTo(payload);
    assertThat(result.getNotification()).contains("Fetched 1 XML message(s)");
  }

  @Test
  void shouldDetectJsonMessageType() throws IOException {
    String payload = readDemoMessage("message-auto.json");
    assertThat(MessageService.detectMessageType(payload)).isEqualTo("JSON");
  }

  @Test
  void shouldDetectXmlMessageType() throws IOException {
    String payload = readDemoMessage("message-manual.xml");
    assertThat(MessageService.detectMessageType(payload)).isEqualTo("XML");
  }

  private static MessageFetchRequest buildFetchRequest(String messageType) {
    MessageFetchRequest request = new MessageFetchRequest();
    request.setQueueName(QUEUE_NAME);
    request.setMessageType(messageType);
    request.setMaxMessage(10);
    return request;
  }

  private static String readDemoMessage(String fileName) throws IOException {
    Path dataDir = findDemoDataDir();
    return Files.readString(dataDir.resolve(fileName), StandardCharsets.UTF_8);
  }

  private static Path findDemoDataDir() {
    Path current = Paths.get("").toAbsolutePath();
    while (current != null) {
      Path candidate = current.resolve("ibm-mq-connector-demo").resolve("src").resolve("resources").resolve("data");
      if (Files.isDirectory(candidate)) {
        return candidate;
      }
      current = current.getParent();
    }
    throw new IllegalStateException("Unable to locate ibm-mq-connector-demo/src/resources/data");
  }

  private static Path findDemoMqscFile() {
    Path current = Paths.get("").toAbsolutePath();
    while (current != null) {
      Path candidate = current.resolve("ibm-mq-connector-demo").resolve("docker").resolve("mq-config")
          .resolve("20-dev-channels.mqsc");
      if (Files.isRegularFile(candidate)) {
        return candidate;
      }
      current = current.getParent();
    }
    throw new IllegalStateException("Unable to locate ibm-mq-connector-demo/docker/mq-config/20-dev-channels.mqsc");
  }
}
