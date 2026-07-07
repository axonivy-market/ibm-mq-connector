package com.axonivy.connector.imb.mq.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditXmlCreditScore {
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

	@Override
	public String toString() {
		return "CreditXmlCreditScore{" + "score=" + score + ", scoreClass='" + scoreClass + '\'' + ", provider='"
				+ provider + '\'' + '}';
	}
}
