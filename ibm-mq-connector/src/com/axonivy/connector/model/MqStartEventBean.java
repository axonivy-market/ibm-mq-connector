package com.axonivy.connector.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.service.MessageService;

import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventResponse;
import ch.ivyteam.ivy.process.extension.ProgramConfig;
import ch.ivyteam.ivy.request.RequestException;
import ch.ivyteam.ivy.service.ServiceException;
import ch.ivyteam.ivy.process.extension.ui.ExtensionUiBuilder;
import ch.ivyteam.ivy.process.extension.ui.UiEditorExtension;
import ch.ivyteam.log.Logger;

/**
 * {@link MqStartEventBean} to listen to IBM MQ queues.
 */
public class MqStartEventBean extends AbstractProcessStartEventBean {

	private static final String QUEUE_NAME_FIELD = "queueNameField";
	private static final String MESSAGE_TYPE_FIELD = "messageTypeField";
	private static final String SYNCHRONOUS_FIELD = "synchronousField";
	private static final String MAX_MESSAGE_FIELD = "maxMessageField";
	private static final String WAIT_INTERVAL_FIELD = "waitIntervalField";

	private final MqReader reader = new MqReader();

	public MqStartEventBean() {
		super("MqStartEventBean", "Listen on IBM MQ queues");
	}

	@Override
	public void initialize(IProcessStartEventBeanRuntime eventRuntime, ProgramConfig configuration) {
		super.initialize(eventRuntime, configuration);
		eventRuntime.poll().disable();
		eventRuntime.threads().boundToEventLifecycle(reader::run);
		log().debug("Initialized MqStartEventBean.");
	}

	@Override
	public synchronized void start() throws ServiceException {
		reader.start(getQueueName(), getMessageType(), getMaxMessage(), getWaitIntervalMs(), isSynchronous());
		log().info("Started MqStartEventBean.");
	}

	@Override
	public synchronized void stop() throws ServiceException {
		reader.stop();
		super.stop();
		log().info("Stopped MqStartEventBean.");
	}

	@Override
	public void poll() {
		log().warn("Did not expect call to poll (polling was disabled in MqStartEventBean). ");
	}

	protected Logger log() {
		return getEventBeanRuntime().getRuntimeLogLogger();
	}

	protected String getQueueName() {
		return getConfig().get(QUEUE_NAME_FIELD);
	}

	protected String getMessageType() {
		return getConfig().get(MESSAGE_TYPE_FIELD);
	}

	protected boolean isSynchronous() {
		String synchronousString = getConfig().get(SYNCHRONOUS_FIELD);
		return StringUtils.isNotBlank(synchronousString) && Boolean.valueOf(synchronousString);
	}

	protected int getMaxMessage() {
		String maxMessage = getConfig().get(MAX_MESSAGE_FIELD);
		if (StringUtils.isBlank(maxMessage)) {
			return 10;
		}
		try {
			return Integer.parseInt(maxMessage);
		} catch (NumberFormatException ex) {
			return 10;
		}
	}

	protected int getWaitIntervalMs() {
		String waitInterval = getConfig().get(WAIT_INTERVAL_FIELD);
		if (StringUtils.isBlank(waitInterval)) {
			return 5000;
		}
		try {
			return Integer.parseInt(waitInterval);
		} catch (NumberFormatException ex) {
			return 5000;
		}
	}

	private final class MqReader {

		private final class AsyncRequest {
			private final MessageDetail messageDetail;
			private final Future<IProcessStartEventResponse> future;

			private AsyncRequest(MessageDetail messageDetail, Future<IProcessStartEventResponse> future) {
				this.messageDetail = messageDetail;
				this.future = future;
			}
		}

		private final List<AsyncRequest> asyncRequests = new ArrayList<AsyncRequest>();
		private String queueName;
		private String messageType;
		private int maxMessage;
		private int waitIntervalMs;
		private boolean synchronous;
		private volatile boolean running;
		private final MessageService messageService = new MessageService();

		public synchronized void start(String queueName, String messageType, int maxMessage, int waitIntervalMs,
				boolean synchronous) {
			this.queueName = queueName;
			this.messageType = StringUtils.trimToEmpty(messageType);
			this.maxMessage = maxMessage;
			this.waitIntervalMs = waitIntervalMs;
			this.synchronous = synchronous;
			this.running = true;
		}

		public void stop() {
			this.running = false;
		}

		public void run() {
			while (running && !Thread.currentThread().isInterrupted()) {
				if (StringUtils.isBlank(queueName)) {
					log().error("MQ queue name must be configured for MqStartEventBean.");
					return;
				}

				MessageFetchRequest request = new MessageFetchRequest();
				request.setQueueName(queueName);
				request.setMessageType(messageType);
				request.setMaxMessage(maxMessage);

				try {
					MessageFetchResult fetchResult = messageService.receive(request);
					if (StringUtils.isNotBlank(fetchResult.getError())) {
						log().error("IBM MQ fetch failed for queue {0}: {1}", queueName, fetchResult.getError());
						try {
							Thread.sleep(waitIntervalMs);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							return;
						}
						continue;
					}

					if (fetchResult.getMessageDetails().isEmpty()) {
						try {
							Thread.sleep(waitIntervalMs);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							return;
						}
						continue;
					}

					for (MessageDetail detail : fetchResult.getMessageDetails()) {
						startProcess(detail);
					}
					consumeAsyncRequests();
					try {
						Thread.sleep(waitIntervalMs);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					}
				} catch (Exception ex) {
					log().error("Error while polling IBM MQ queue {0}.", ex, queueName);
					try {
						Thread.sleep(waitIntervalMs);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					}
				}
			}
		}

		private void startProcess(MessageDetail detail) {
			log().debug("Firing task to handle MQ queue ''{0}'' message type ''{1}''.", queueName, detail.getType());
			try {
				if (synchronous) {
					IProcessStartEventResponse eventResponse = getEventBeanRuntime().processStarter()
						.withReason(MessageFormat.format("Received MQ message from queue {0}", queueName))
						.withParameter("messageDetail", detail)
						.withParameter("queueName", queueName)
						.withParameter("messageType", detail.getType())
						.start();
					logResponse(detail, eventResponse);
				} else {
					Future<IProcessStartEventResponse> future = getEventBeanRuntime().processStarter()
						.withReason(MessageFormat.format("Received MQ message from queue {0}", queueName))
						.withParameter("messageDetail", detail)
						.withParameter("queueName", queueName)
						.withParameter("messageType", detail.getType())
						.startAsync();
					asyncRequests.add(new AsyncRequest(detail, future));
				}
			} catch (RequestException e) {
				logError(detail, e);
			}
		}

		private void logError(MessageDetail detail, Exception e) {
			log().error("MQ queue ''{0}'' message type ''{1}'' caused exception while firing a new task.", e,
					queueName, detail.getType());
		}

		private void logResponse(MessageDetail detail, IProcessStartEventResponse eventResponse) {
			log().debug("MQ queue ''{0}'' message type ''{1}'' was handled by task {2} and returned with parameters: {3}.",
					queueName, detail.getType(), eventResponse.getStartedTask().getId(),
					eventResponse.getParameters().keySet().stream().sorted().collect(Collectors.joining(", ")));
		}

		private void consumeAsyncRequests() {
			List<AsyncRequest> consumed = new ArrayList<AsyncRequest>();
			for (AsyncRequest asyncRequest : asyncRequests) {
				if (asyncRequest.future.isDone()) {
					consumed.add(asyncRequest);
					try {
						IProcessStartEventResponse eventResponse = asyncRequest.future.get();
						logResponse(asyncRequest.messageDetail, eventResponse);
					} catch (ExecutionException ex) {
						logError(asyncRequest.messageDetail, ex);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
						return;
					}
				}
			}
			asyncRequests.removeAll(consumed);
		}
	}

	/**
	 * The editor to configure a {@link MqStartEventBean}.
	 */
	public static class Editor extends UiEditorExtension {

		@Override
		public void initUiFields(ExtensionUiBuilder ui) {
			ui.label("MQ Queue Name:").create();
			ui.textField(QUEUE_NAME_FIELD).create();

			ui.label("Message Type:").create();
			ui.textField(MESSAGE_TYPE_FIELD).create();

			ui.label("Synchronous:").create();
			ui.textField(SYNCHRONOUS_FIELD).create();

			ui.label("Max Messages per Poll:").create();
			ui.textField(MAX_MESSAGE_FIELD).create();

			ui.label("Wait Interval (ms):").create();
			ui.textField(WAIT_INTERVAL_FIELD).create();

			String helpTopic = "Queue name:\n" +
				"The IBM MQ queue that should be polled for messages.\n\n" +
				"Message type:\n" +
				"Optional filter for XML or JSON payloads. Leave empty to receive all supported message types.\n\n" +
				"Synchronous:\n" +
				"If true, the started process will be executed synchronously and the listener waits for completion.\n\n" +
				"Max messages per poll:\n" +
				"Max number of messages to fetch from IBM MQ in a single poll cycle. Default: 10\n\n" +
				"Wait Interval (ms):\n" +
				"How long IBM MQ waits for a new message before returning control. Default: 5000";
			ui.label(helpTopic).multiline().create();
		}
	}
}
