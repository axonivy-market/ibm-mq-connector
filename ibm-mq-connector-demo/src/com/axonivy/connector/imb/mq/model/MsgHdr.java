package com.axonivy.connector.imb.mq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MsgHdr {
	@JacksonXmlProperty(localName = "MsgId")
	private String msgId;

	@JacksonXmlProperty(localName = "CreDtTm")
	private String creDtTm;

	@JacksonXmlProperty(localName = "CorrelationId")
	private String correlationId;

	@JacksonXmlProperty(localName = "ReplyToQueue")
	private String replyToQueue;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getCreDtTm() {
		return creDtTm;
	}

	public void setCreDtTm(String creDtTm) {
		this.creDtTm = creDtTm;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getReplyToQueue() {
		return replyToQueue;
	}

	public void setReplyToQueue(String replyToQueue) {
		this.replyToQueue = replyToQueue;
	}

	@Override
	public String toString() {
		return "MsgHdr{" + "msgId='" + msgId + '\'' + ", creDtTm='" + creDtTm + '\'' + ", correlationId='"
				+ correlationId + '\'' + ", replyToQueue='" + replyToQueue + '\'' + '}';
	}
}
