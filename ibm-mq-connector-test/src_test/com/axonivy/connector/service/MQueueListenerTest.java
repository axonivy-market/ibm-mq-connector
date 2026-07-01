package com.axonivy.connector.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest
public class MQueueListenerTest {

	@Test
	public void handleMessageDelegatesTextToHandler() throws JMSException {
		List<String> handledPayloads = new ArrayList<>();
		MQueueListener listener = new MQueueListener("DEV.QUEUE.1", payload -> handledPayloads.add(payload));

		listener.handleMessage(textMessageProxy("hello"));

		assertThat(handledPayloads).containsExactly("hello");
	}

	@Test
	public void handleMessageIgnoresNonTextMessages() throws JMSException {
		List<String> handledPayloads = new ArrayList<>();
		MQueueListener listener = new MQueueListener("DEV.QUEUE.2", payload -> handledPayloads.add(payload));

		listener.handleMessage(messageProxy("ignored"));

		assertThat(handledPayloads).isEmpty();
	}

	@Test
	public void receiveNoWaitProcessesQueuedMessagesAndCommits() throws JMSException {
		List<String> handledPayloads = new ArrayList<>();
		AtomicInteger commitCalls = new AtomicInteger();
		List<Message> queuedMessages = List.of(textMessageProxy("first"), textMessageProxy("second"));
		AtomicInteger index = new AtomicInteger();

		TestMQueueListener listener = new TestMQueueListener("DEV.QUEUE.3", payload -> handledPayloads.add(payload),
				commitCalls, queuedMessages, index);
		listener.start();
		listener.receiveNoWait();

		assertThat(handledPayloads).containsExactly("first", "second");
		assertThat(commitCalls.get()).isEqualTo(2);
	}

	private TextMessage textMessageProxy(String text) {
		return (TextMessage) Proxy.newProxyInstance(
				TextMessage.class.getClassLoader(),
				new Class<?>[] { TextMessage.class },
				(proxy, method, args) -> {
					String name = method.getName();
					if ("getText".equals(name)) {
						return text;
					}
					if ("getJMSMessageID".equals(name)) {
						return "message-id";
					}
					if ("setText".equals(name)) {
						return null;
					}
					return defaultValue(method.getReturnType());
				});
	}

	private Message messageProxy(String text) {
		return (Message) Proxy.newProxyInstance(
				Message.class.getClassLoader(),
				new Class<?>[] { Message.class },
				(proxy, method, args) -> {
					if ("getJMSMessageID".equals(method.getName())) {
						return text;
					}
					return defaultValue(method.getReturnType());
				});
	}

	private static class TestMQueueListener extends MQueueListener {
		private final AtomicInteger commitCalls;
		private final List<Message> queuedMessages;
		private final AtomicInteger index;

		private TestMQueueListener(String queueName, MessageHandler messageHandler, AtomicInteger commitCalls,
				List<Message> queuedMessages, AtomicInteger index) {
			super(queueName, messageHandler);
			this.commitCalls = commitCalls;
			this.queuedMessages = queuedMessages;
			this.index = index;
		}

		@Override
		protected void createConnectionFactory() {
			// no-op for unit tests
		}

		@Override
		protected Connection createConnection() throws JMSException {
			return connectionProxy();
		}

		private Connection connectionProxy() {
			return (Connection) Proxy.newProxyInstance(
					Connection.class.getClassLoader(),
					new Class<?>[] { Connection.class },
					(proxy, method, args) -> {
						String name = method.getName();
						if ("createSession".equals(name)) {
							return sessionProxy();
						}
						if ("start".equals(name)) {
							return null;
						}
						if ("close".equals(name)) {
							return null;
						}
						return defaultValue(method.getReturnType());
					});
		}

		private Session sessionProxy() {
			return (Session) Proxy.newProxyInstance(
					Session.class.getClassLoader(),
					new Class<?>[] { Session.class },
					(proxy, method, args) -> {
						String name = method.getName();
						if ("createQueue".equals(name)) {
							return queueProxy((String) args[0]);
						}
						if ("createConsumer".equals(name)) {
							return messageConsumerProxy();
						}
						if ("commit".equals(name)) {
							commitCalls.incrementAndGet();
							return null;
						}
						if ("close".equals(name)) {
							return null;
						}
						return defaultValue(method.getReturnType());
					});
		}

		private MessageConsumer messageConsumerProxy() {
			return (MessageConsumer) Proxy.newProxyInstance(
					MessageConsumer.class.getClassLoader(),
					new Class<?>[] { MessageConsumer.class },
					(proxy, method, args) -> {
						if ("receiveNoWait".equals(method.getName())) {
							int current = index.getAndIncrement();
							return current < queuedMessages.size() ? queuedMessages.get(current) : null;
						}
						if ("close".equals(method.getName())) {
							return null;
						}
						return defaultValue(method.getReturnType());
					});
		}

		private Queue queueProxy(String name) {
			return (Queue) Proxy.newProxyInstance(
					Queue.class.getClassLoader(),
					new Class<?>[] { Queue.class },
					(proxy, method, args) -> {
						if ("getQueueName".equals(method.getName())) {
							return name;
						}
						return defaultValue(method.getReturnType());
					});
		}
	}

	private static Object defaultValue(Class<?> type) {
		if (type == boolean.class) {
			return false;
		}
		if (type == int.class) {
			return 0;
		}
		if (type == long.class) {
			return 0L;
		}
		if (type == double.class) {
			return 0.0d;
		}
		if (type == float.class) {
			return 0.0f;
		}
		return null;
	}
}
