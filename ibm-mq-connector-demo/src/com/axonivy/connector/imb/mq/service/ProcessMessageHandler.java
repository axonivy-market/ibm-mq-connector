package com.axonivy.connector.imb.mq.service;

import com.axonivy.connector.model.TaskPayload;
import com.axonivy.connector.service.MessageHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import ch.ivyteam.ivy.environment.Ivy;

/**
 * Demo implementation that parses JSON or XML and sends a workflow signal.
 */
public class ProcessMessageHandler implements MessageHandler {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final String SIGNAL_CODE = "mqdemo:manualapproval";

    @Override
    public void handleText(String payload) {
        try {
            TaskPayload tp = new TaskPayload();
			String messageType = detectMessageType(payload);
            tp.setMessageType(messageType);
            tp.setRawPayload(payload);

            if ("JSON".equalsIgnoreCase(messageType)) {
                JsonNode node = JSON_MAPPER.readTree(payload);
                tp.setData(node);
            } else if ("XML".equalsIgnoreCase(messageType)) {
                JsonNode node = XML_MAPPER.readTree(payload);
                tp.setData(node);
            } else {
                tp.setData(payload);
            }

            Ivy.log().info("ProcessMessageHandler(demo)::sending signal {0} with payload {1}", SIGNAL_CODE, tp);
            Ivy.wf().signals().create().data(tp).send(SIGNAL_CODE);
        } catch (Exception ex) {
            Ivy.log().error("ProcessMessageHandler(demo)::Failed to process payload", ex);
        }
    }
    
    private String detectMessageType(String text) {
		if (text == null) {
			return "UNKNOWN";
		}
		String trimmed = text.trim();
		if (trimmed.startsWith("<")) {
			return "XML";
		}
		if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
			return "JSON";
		}
		return "PLAINTEXT";
	}
}
