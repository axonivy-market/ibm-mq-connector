package com.axonivy.connector.service;

import javax.jms.JMSException;

/**
 * Handle parsed text messages coming from the MQ listener.
 */
public interface MessageHandler {
    /**
     * Handle a text payload received from the queue.
     * @param payload raw text payload
     * @throws JMSException when processing fails
     */
    void handleText(String payload) throws JMSException;
}
