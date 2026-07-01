package com.axonivy.connector.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest
public class MQueueProducerTest {

	@Test
	public void sendMessageSendsTextAndCommitsOnce() throws JMSException {
		AtomicInteger createConnectionCalls = new AtomicInteger();
		AtomicInteger commitCalls = new AtomicInteger();
		AtomicInteger rollbackCalls = new AtomicInteger();
		List<String> sentPayloads = new ArrayList<>();

		MQueueProducer producer = new MQueueProducer() {
			@Override
			protected Connection createConnection() {
				createConnectionCalls.incrementAndGet();
				return connectionProxy(commitCalls, rollbackCalls, sentPayloads);
			}
		};

		producer.sendMessage("DEV.QUEUE.1", "hello");

		assertThat(createConnectionCalls.get()).isEqualTo(1);
		assertThat(commitCalls.get()).isEqualTo(1);
		assertThat(rollbackCalls.get()).isZero();
		assertThat(sentPayloads).containsExactly("hello");
	}

	@Test
	public void sendMessagesSendsEachTextAndCommitsForEveryMessage() throws JMSException {
		AtomicInteger commitCalls = new AtomicInteger();
		List<String> sentPayloads = new ArrayList<>();

		MQueueProducer producer = new MQueueProducer() {
			@Override
			protected Connection createConnection() {
				return connectionProxy(commitCalls, new AtomicInteger(), sentPayloads);
			}
		};

		producer.sendMessages("DEV.QUEUE.2", List.of("first", "second"));

		assertThat(commitCalls.get()).isEqualTo(2);
		assertThat(sentPayloads).containsExactly("first", "second");
	}

	@Test
	public void sendMessageWithNullTextDoesNotOpenConnection() {
		AtomicInteger createConnectionCalls = new AtomicInteger();

		MQueueProducer producer = new MQueueProducer() {
			@Override
			protected Connection createConnection() {
				createConnectionCalls.incrementAndGet();
				return null;
			}
		};

		producer.sendMessage("DEV.QUEUE.3", null);

		assertThat(createConnectionCalls.get()).isZero();
	}

	private Connection connectionProxy(AtomicInteger commitCalls, AtomicInteger rollbackCalls, List<String> sentPayloads) {
		return (Connection) Proxy.newProxyInstance(
				Connection.class.getClassLoader(),
				new Class<?>[] { Connection.class },
				(proxy, method, args) -> {
					String name = method.getName();
					if ("createSession".equals(name)) {
						return sessionProxy(commitCalls, rollbackCalls, sentPayloads);
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

	private Session sessionProxy(AtomicInteger commitCalls, AtomicInteger rollbackCalls, List<String> sentPayloads) {
		return (Session) Proxy.newProxyInstance(
				Session.class.getClassLoader(),
				new Class<?>[] { Session.class },
				(proxy, method, args) -> {
					String name = method.getName();
					if ("createTextMessage".equals(name)) {
						return textMessageProxy((String) args[0]);
					}
					if ("createQueue".equals(name)) {
						return queueProxy((String) args[0]);
					}
					if ("createProducer".equals(name)) {
						return messageProducerProxy(sentPayloads);
					}
					if ("commit".equals(name)) {
						commitCalls.incrementAndGet();
						return null;
					}
					if ("rollback".equals(name)) {
						rollbackCalls.incrementAndGet();
						return null;
					}
					if ("close".equals(name)) {
						return null;
					}
					return defaultValue(method.getReturnType());
				});
	}

	private MessageProducer messageProducerProxy(List<String> sentPayloads) {
		return (MessageProducer) Proxy.newProxyInstance(
				MessageProducer.class.getClassLoader(),
				new Class<?>[] { MessageProducer.class },
				(proxy, method, args) -> {
					if ("send".equals(method.getName())) {
						Message message = (Message) args[0];
						if (message instanceof TextMessage textMessage) {
							sentPayloads.add(textMessage.getText());
						}
						return null;
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

	private Object defaultValue(Class<?> type) {
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
