package com.axonivy.connector.imb.mq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditXmlApplicant {
	@JacksonXmlProperty(localName = "CustomerId")
	private String customerId;

	@JacksonXmlProperty(localName = "FirstName")
	private String firstName;

	@JacksonXmlProperty(localName = "LastName")
	private String lastName;

	@JacksonXmlProperty(localName = "DateOfBirth")
	private String dateOfBirth;

	@JacksonXmlProperty(localName = "Contact")
	private CreditXmlContact contact;

	@JacksonXmlProperty(localName = "EmploymentDetails")
	private CreditXmlEmploymentDetails employmentDetails;

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

	public CreditXmlContact getContact() {
		return contact;
	}

	public void setContact(CreditXmlContact contact) {
		this.contact = contact;
	}

	public CreditXmlEmploymentDetails getEmploymentDetails() {
		return employmentDetails;
	}

	public void setEmploymentDetails(CreditXmlEmploymentDetails employmentDetails) {
		this.employmentDetails = employmentDetails;
	}

	@Override
	public String toString() {
		return "CreditXmlApplicant{" + "customerId='" + customerId + '\'' + ", firstName='" + firstName + '\''
				+ ", lastName='" + lastName + '\'' + ", dateOfBirth='" + dateOfBirth + '\'' + ", contact=" + contact
				+ ", employmentDetails=" + employmentDetails + '}';
	}
}
