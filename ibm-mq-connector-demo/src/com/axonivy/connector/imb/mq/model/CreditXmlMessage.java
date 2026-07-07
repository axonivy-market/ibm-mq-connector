package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Document")
public class CreditXmlMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @JacksonXmlProperty(localName = "CreditApplicationRequest")
    private CreditApplicationRequest creditApplicationRequest;

    public CreditApplicationRequest getCreditApplicationRequest() {
        return creditApplicationRequest;
    }

    public void setCreditApplicationRequest(CreditApplicationRequest creditApplicationRequest) {
        this.creditApplicationRequest = creditApplicationRequest;
    }

    @Override
    public String toString() {
        return "CreditXmlMessage{" + "creditApplicationRequest=" + creditApplicationRequest + '}';
    }
}
