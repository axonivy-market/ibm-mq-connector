package com.axonivy.connector.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanJsonMessage {
	private MsgHeader msgHeader;
	private Applicant applicant;
	private LoanRequest loanRequest;
	private CreditScore creditScore;

	public MsgHeader getMsgHeader() {
		return msgHeader;
	}

	public void setMsgHeader(MsgHeader msgHeader) {
		this.msgHeader = msgHeader;
	}

	public Applicant getApplicant() {
		return applicant;
	}

	public void setApplicant(Applicant applicant) {
		this.applicant = applicant;
	}

	public LoanRequest getLoanRequest() {
		return loanRequest;
	}

	public void setLoanRequest(LoanRequest loanRequest) {
		this.loanRequest = loanRequest;
	}

	public CreditScore getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(CreditScore creditScore) {
		this.creditScore = creditScore;
	}

	@Override
	public String toString() {
		return "LoanJsonMessage{" +
				"msgHeader=" + msgHeader +
				", applicant=" + applicant +
				", loanRequest=" + loanRequest +
				", creditScore=" + creditScore +
				'}';
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class MsgHeader {
		private String msgId;
		private String createdAt;
		private String correlationId;
		private String replyToQueue;

		public String getMsgId() {
			return msgId;
		}

		public void setMsgId(String msgId) {
			this.msgId = msgId;
		}

		public String getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
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
			return "MsgHeader{" +
					"msgId='" + msgId + '\'' +
					", createdAt='" + createdAt + '\'' +
					", correlationId='" + correlationId + '\'' +
					", replyToQueue='" + replyToQueue + '\'' +
					'}';
		}
	}	

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Name {
		private String first;
		private String last;

		public String getFirst() {
			return first;
		}

		public void setFirst(String first) {
			this.first = first;
		}

		public String getLast() {
			return last;
		}

		public void setLast(String last) {
			this.last = last;
		}

		@Override
		public String toString() {
			return "Name{" +
					"first='" + first + '\'' +
					", last='" + last + '\'' +
					'}';
		}
	}
	
}
