package com.axonivy.connector.imb.mq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact {
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

	@Override
	public String toString() {
		return "Contact{" + "email='" + email + '\'' + ", phone='" + phone + '\'' + '}';
	}
}
