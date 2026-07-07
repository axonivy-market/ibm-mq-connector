package com.axonivy.connector.imb.mq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditXmlProcessingHints {
	@JacksonXmlProperty(localName = "RequestChannel")
	private String requestChannel;

	@JacksonXmlProperty(localName = "AutoDecisionAllowed")
	private Boolean autoDecisionAllowed;

	@JacksonXmlProperty(localName = "MaxDecisionTimeSec")
	private Integer maxDecisionTimeSec;

	@JacksonXmlProperty(localName = "AutoApproved")
	private Boolean autoApproved;

	public String getRequestChannel() {
		return requestChannel;
	}

	public void setRequestChannel(String requestChannel) {
		this.requestChannel = requestChannel;
	}

	public Boolean getAutoDecisionAllowed() {
		return autoDecisionAllowed;
	}

	public void setAutoDecisionAllowed(Boolean autoDecisionAllowed) {
		this.autoDecisionAllowed = autoDecisionAllowed;
	}

	public Integer getMaxDecisionTimeSec() {
		return maxDecisionTimeSec;
	}

	public void setMaxDecisionTimeSec(Integer maxDecisionTimeSec) {
		this.maxDecisionTimeSec = maxDecisionTimeSec;
	}

	public Boolean getAutoApproved() {
		return autoApproved;
	}

	public void setAutoApproved(Boolean autoApproved) {
		this.autoApproved = autoApproved;
	}

	@Override
	public String toString() {
		return "CreditXmlProcessingHints{" + "requestChannel='" + requestChannel + '\'' + ", autoDecisionAllowed="
				+ autoDecisionAllowed + ", maxDecisionTimeSec=" + maxDecisionTimeSec + ", autoApproved=" + autoApproved
				+ '}';
	}
}
