package com.axonivy.connector.imb.mq.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditScore implements Serializable {
	private static final long serialVersionUID = 1L;
	
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

