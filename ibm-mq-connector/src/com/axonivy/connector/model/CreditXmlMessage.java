package com.axonivy.connector.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "Document")
public class CreditXmlMessage {
	@JacksonXmlProperty(localName = "CreditApplicationRequest")
	private CreditApplicationRequest creditApplicationRequest;

	public CreditApplicationRequest getCreditApplicationRequest() {
		return creditApplicationRequest;
	}

	public void setCreditApplicationRequest(CreditApplicationRequest creditApplicationRequest) {
		this.creditApplicationRequest = creditApplicationRequest;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CreditApplicationRequest {
		@JacksonXmlProperty(localName = "MsgHdr")
		private MsgHdr msgHdr;

		@JacksonXmlProperty(localName = "Applicant")
		private Applicant applicant;

		@JacksonXmlProperty(localName = "LoanRequest")
		private LoanRequest loanRequest;

		@JacksonXmlProperty(localName = "FinancialProfile")
		private FinancialProfile financialProfile;

		@JacksonXmlProperty(localName = "ProcessingHints")
		private ProcessingHints processingHints;

		public MsgHdr getMsgHdr() {
			return msgHdr;
		}

		public void setMsgHdr(MsgHdr msgHdr) {
			this.msgHdr = msgHdr;
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

		public FinancialProfile getFinancialProfile() {
			return financialProfile;
		}

		public void setFinancialProfile(FinancialProfile financialProfile) {
			this.financialProfile = financialProfile;
		}

		public ProcessingHints getProcessingHints() {
			return processingHints;
		}

		public void setProcessingHints(ProcessingHints processingHints) {
			this.processingHints = processingHints;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class MsgHdr {
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
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Applicant {
		@JacksonXmlProperty(localName = "CustomerId")
		private String customerId;

		@JacksonXmlProperty(localName = "FirstName")
		private String firstName;

		@JacksonXmlProperty(localName = "LastName")
		private String lastName;

		@JacksonXmlProperty(localName = "DateOfBirth")
		private String dateOfBirth;

		@JacksonXmlProperty(localName = "Contact")
		private Contact contact;

		@JacksonXmlProperty(localName = "EmploymentDetails")
		private EmploymentDetails employmentDetails;

		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getDateOfBirth() {
			return dateOfBirth;
		}

		public void setDateOfBirth(String dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}

		public Contact getContact() {
			return contact;
		}

		public void setContact(Contact contact) {
			this.contact = contact;
		}

		public EmploymentDetails getEmploymentDetails() {
			return employmentDetails;
		}

		public void setEmploymentDetails(EmploymentDetails employmentDetails) {
			this.employmentDetails = employmentDetails;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Contact {
		@JacksonXmlProperty(localName = "Email")
		private String email;

		@JacksonXmlProperty(localName = "Phone")
		private String phone;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EmploymentDetails {
		@JacksonXmlProperty(localName = "Status")
		private String status;

		@JacksonXmlProperty(localName = "Employer")
		private String employer;

		@JacksonXmlProperty(localName = "JobTitle")
		private String jobTitle;

		@JacksonXmlProperty(localName = "MonthlyNetIncome")
		private Amount monthlyNetIncome;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getEmployer() {
			return employer;
		}

		public void setEmployer(String employer) {
			this.employer = employer;
		}

		public String getJobTitle() {
			return jobTitle;
		}

		public void setJobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
		}

		public Amount getMonthlyNetIncome() {
			return monthlyNetIncome;
		}

		public void setMonthlyNetIncome(Amount monthlyNetIncome) {
			this.monthlyNetIncome = monthlyNetIncome;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class LoanRequest {
		@JacksonXmlProperty(localName = "LoanPurpose")
		private String loanPurpose;

		@JacksonXmlProperty(localName = "RequestedAmount")
		private Amount requestedAmount;

		@JacksonXmlProperty(localName = "RequestedTermMonths")
		private Integer requestedTermMonths;

		public String getLoanPurpose() {
			return loanPurpose;
		}

		public void setLoanPurpose(String loanPurpose) {
			this.loanPurpose = loanPurpose;
		}

		public Amount getRequestedAmount() {
			return requestedAmount;
		}

		public void setRequestedAmount(Amount requestedAmount) {
			this.requestedAmount = requestedAmount;
		}

		public Integer getRequestedTermMonths() {
			return requestedTermMonths;
		}

		public void setRequestedTermMonths(Integer requestedTermMonths) {
			this.requestedTermMonths = requestedTermMonths;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class FinancialProfile {
		@JacksonXmlProperty(localName = "MonthlyExpenses")
		private Amount monthlyExpenses;

		@JacksonXmlProperty(localName = "CreditScore")
		private CreditScore creditScore;

		public Amount getMonthlyExpenses() {
			return monthlyExpenses;
		}

		public void setMonthlyExpenses(Amount monthlyExpenses) {
			this.monthlyExpenses = monthlyExpenses;
		}

		public CreditScore getCreditScore() {
			return creditScore;
		}

		public void setCreditScore(CreditScore creditScore) {
			this.creditScore = creditScore;
		}
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class CreditScore {
		@JacksonXmlProperty(localName = "Score")
		private Integer score;

		@JacksonXmlProperty(localName = "ScoreClass")
		private String scoreClass;

		@JacksonXmlProperty(localName = "Provider")
		private String provider;

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
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ProcessingHints {
		@JacksonXmlProperty(localName = "RequestChannel")
		private String requestChannel;

		@JacksonXmlProperty(localName = "AutoDecisionAllowed")
		private Boolean autoDecisionAllowed;

		@JacksonXmlProperty(localName = "MaxDecisionTimeSec")
		private Integer maxDecisionTimeSec;

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
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Amount {
		@JacksonXmlProperty(isAttribute = true, localName = "currency")
		private String currency;

		@JacksonXmlText
		private String value;

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
