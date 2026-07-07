package com.axonivy.connector.test.integration;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.axonivy.connector.service.MQueueListener;

import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest
public class MQueueIntegrationIT extends BaseIntegrationTest {

  @Test
  public void testProducerAndListenerWithRealMQ() throws Exception {
    List<String> listenerReceived = new ArrayList<>();
    MQueueListener listener = new MQueueListener("DEV.QUEUE.1", listenerReceived::add);

    listener.start();
    
    // Read all currently available messages
    for (int i = 0; i < 5 && listenerReceived.size() < 3; i++) {
      listener.receiveNoWait();
      if (listenerReceived.size() < 3) {
        Thread.sleep(1000);
      }
    }
    
    int messageCount = listenerReceived.size();
    assertThat(messageCount)
      .as("The amount of messages should be exactly 3")
      .isEqualTo(3);

    for (String val : listenerReceived) {
      System.out.println("Received message: " + val);
    }
    
    assertThat(listenerReceived)
      .as("Listener should read exactly 3 preloaded messages from Docker queue")
      .containsExactly(
        "Preloaded Message from Docker 1",
        "Preloaded Message from Docker 2",
        "Preloaded Message from Docker 3"
      );

    listener.stop();
  }
}
