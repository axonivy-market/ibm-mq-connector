package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditApplicationRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @JacksonXmlProperty(localName = "MsgHdr")
    private MsgHdr msgHdr;

    @JacksonXmlProperty(localName = "Applicant")
    private CreditXmlApplicant applicant;

    @JacksonXmlProperty(localName = "LoanRequest")
    private CreditXmlLoanRequest loanRequest;

    @JacksonXmlProperty(localName = "FinancialProfile")
    private CreditXmlFinancialProfile financialProfile;

    @JacksonXmlProperty(localName = "ProcessingHints")
    private CreditXmlProcessingHints processingHints;

    public MsgHdr getMsgHdr() {
        return msgHdr;
    }

    public void setMsgHdr(MsgHdr msgHdr) {
        this.msgHdr = msgHdr;
    }

    public CreditXmlApplicant getApplicant() {
        return applicant;
    }

    public void setApplicant(CreditXmlApplicant applicant) {
        this.applicant = applicant;
    }

    public CreditXmlLoanRequest getLoanRequest() {
        return loanRequest;
    }

    public void setLoanRequest(CreditXmlLoanRequest loanRequest) {
        this.loanRequest = loanRequest;
    }

    public CreditXmlFinancialProfile getFinancialProfile() {
        return financialProfile;
    }

    public void setFinancialProfile(CreditXmlFinancialProfile financialProfile) {
        this.financialProfile = financialProfile;
    }

    public CreditXmlProcessingHints getProcessingHints() {
        return processingHints;
    }

    public void setProcessingHints(CreditXmlProcessingHints processingHints) {
        this.processingHints = processingHints;
    }

    @Override
    public String toString() {
        return "CreditApplicationRequest{" + "msgHdr=" + msgHdr + ", applicant=" + applicant + ", loanRequest="
                + loanRequest + ", financialProfile=" + financialProfile + ", processingHints=" + processingHints + '}';
    }
}
