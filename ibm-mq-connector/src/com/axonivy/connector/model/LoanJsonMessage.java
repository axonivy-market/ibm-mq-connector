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
	public static class Applicant {
		private String customerId;
		private Name name;
		private String dateOfBirth;
		private Double monthlyNetIncome;

		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}

		public Name getName() {
			return name;
		}

		public void setName(Name name) {
			this.name = name;
		}

		public String getDateOfBirth() {
			return dateOfBirth;
		}

		public void setDateOfBirth(String dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}

		public Double getMonthlyNetIncome() {
			return monthlyNetIncome;
		}

		public void setMonthlyNetIncome(Double monthlyNetIncome) {
			this.monthlyNetIncome = monthlyNetIncome;
		}

		@Override
		public String toString() {
			return "Applicant{" +
					"customerId='" + customerId + '\'' +
					", name=" + name +
					", dateOfBirth='" + dateOfBirth + '\'' +
					", monthlyNetIncome=" + monthlyNetIncome +
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

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class LoanRequest {
		private String purpose;
		private Double amount;
		private Integer termMonths;

		public String getPurpose() {
			return purpose;
		}

		public void setPurpose(String purpose) {
			this.purpose = purpose;
		}

		public Double getAmount() {
			return amount;
		}

		public void setAmount(Double amount) {
			this.amount = amount;
		}

		public Integer getTermMonths() {
			return termMonths;
		}

		public void setTermMonths(Integer termMonths) {
			this.termMonths = termMonths;
		}

		@Override
		public String toString() {
			return "LoanRequest{" +
					"purpose='" + purpose + '\'' +
					", amount=" + amount +
					", termMonths=" + termMonths +
					'}';
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CreditScore {
		private Integer score;
		private String scoreClass;
		private String provider;
		private Boolean negativeEntries;

		public Integer getScore() {
			return score;
		}

		public void setScore(Integer score) {
			this.score = score;
		}

		public String getScoreClass() {
			return scoreClass;
		}

		public void setScoreClass(String scoreClass) {
			this.scoreClass = scoreClass;
		}

		public String getProvider() {
			return provider;
		}

		public void setProvider(String provider) {
			this.provider = provider;
		}

		public Boolean getNegativeEntries() {
			return negativeEntries;
		}

		public void setNegativeEntries(Boolean negativeEntries) {
			this.negativeEntries = negativeEntries;
		}

		@Override
		public String toString() {
			return "CreditScore{" +
					"score=" + score +
					", scoreClass='" + scoreClass + '\'' +
					", provider='" + provider + '\'' +
					", negativeEntries=" + negativeEntries +
					'}';
		}
	}
	
}
