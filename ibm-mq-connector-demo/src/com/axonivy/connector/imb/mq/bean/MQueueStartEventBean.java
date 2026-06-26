package com.axonivy.connector.imb.mq.bean;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.imb.mq.service.ProcessMessageHandler;
import com.axonivy.connector.service.MQueueListener;

import ch.ivyteam.api.PublicAPI;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;
import ch.ivyteam.ivy.process.extension.ProgramConfig;
import ch.ivyteam.ivy.process.extension.ui.ExtensionUiBuilder;
import ch.ivyteam.ivy.process.extension.ui.UiEditorExtension;
import ch.ivyteam.ivy.service.ServiceException;

public class MQueueStartEventBean extends AbstractProcessStartEventBean {
	private static final String QUEUE_NAME_FIELD = "queueNameField";
	private boolean isPolling = false;
	private boolean isSkipInitializing = true;
	private String queueName = "";
	private MQueueListener mqListener;
	private IProcessStartEventBeanRuntime eventRuntime;

	public MQueueStartEventBean() {
		super("Run IBM MQ  StartEventBean", "Subscribe to MQ Queue");
	}

	@Override
	public void initialize(IProcessStartEventBeanRuntime eventRuntime, ProgramConfig programConfig) {
		super.initialize(eventRuntime, programConfig);
		this.eventRuntime = eventRuntime;
		queueName = getQueueName();
		mqListener = new MQueueListener(queueName, new ProcessMessageHandler());

		Ivy.log().debug("MQueueStartEventBean::initialize");
	}


	@Override
	@PublicAPI
	public void poll() {
		Ivy.log().debug("MQueueStartEventBean::will skip: isPolling: {0} || isSkipInitializing: {1} ", isPolling,
				isSkipInitializing);

		if (StringUtils.isBlank(queueName)) {
			Ivy.log().warn("MQueueStartEventBean::queueName is required.");
			return;
		}
		if (isPolling || isSkipInitializing) {
			return;
		}
		isPolling = true;
		eventRuntime.threads().boundToEventLifecycle(() -> {
			mqListener.start();
			mqListener.receive();
		});

	}

	protected String getQueueName() {
		return getConfig().get(QUEUE_NAME_FIELD);
	}

	public static class Editor extends UiEditorExtension {

		@Override
		public void initUiFields(ExtensionUiBuilder ui) {
			ui.label("Queue Name:").create();
			ui.textField(QUEUE_NAME_FIELD).create();

			String helpTopic = String.format("""
					Queue name:
					The queue from which messages will be received.
					""");
			ui.label(helpTopic).multiline().create();
		}
	}
}
