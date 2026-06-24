package com.axonivy.connector.model;

public class TaskPayload {
    private String messageType;
    private Object data;
    private String rawPayload;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    @Override
    public String toString() {
        return "TaskPayload [messageType=" + messageType + ", data=" + data + ", rawPayload=" + rawPayload + "]";
    }
}
