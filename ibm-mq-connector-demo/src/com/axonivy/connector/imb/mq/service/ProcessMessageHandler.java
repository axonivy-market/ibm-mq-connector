package com.axonivy.connector.imb.mq.service;

import com.axonivy.connector.service.MessageHandler;

public class ProcessMessageHandler implements MessageHandler {
    @Override
    public void handleText(String payload) {
        ProcessMessageService.getInstance().process(payload);
    }


}
