package com.axonivy.connector.bean;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.connector.service.MQueueListener;

import ch.ivyteam.api.PublicAPI;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.eventstart.AbstractProcessStartEventBean;
import ch.ivyteam.ivy.process.eventstart.IProcessStartEventBeanRuntime;
import ch.ivyteam.ivy.process.extension.ProgramConfig;
import ch.ivyteam.ivy.process.extension.ui.ExtensionUiBuilder;
import ch.ivyteam.ivy.process.extension.ui.UiEditorExtension;

public class MQeueListenerBean extends AbstractProcessStartEventBean {
	private static final String QUEUE_NAME_FIELD = "queueNameField";
	private boolean isPolling = false;

	public MQeueListenerBean() {
		super("Run IBM Message Qeue Listenner", "Subscribe to MQ Qeue");
	}

	@Override
	public void initialize(IProcessStartEventBeanRuntime eventRuntime, ProgramConfig programConfig) {		
		super.initialize(eventRuntime, programConfig);
		Ivy.log().debug("MQeueListenerStartEventBean::initialize");
	}

	@Override
	@PublicAPI
	public void poll() {
		String queueName = getQueueName();
		if (StringUtils.isBlank(queueName)) {
 		   Ivy.log().warn("Queue name is blank, skipping listener start");
    		return;
		}
		if (isPolling) {
			return;
		}
		isPolling = true;
		

		MQueueListener.getInstance().start(queueName);
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
