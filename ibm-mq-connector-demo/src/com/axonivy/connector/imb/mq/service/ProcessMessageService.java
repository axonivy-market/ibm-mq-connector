package com.axonivy.connector.imb.mq.service;

import java.util.List;

import com.axonivy.connector.model.MessageDetail;
import com.axonivy.connector.model.MessageFetchResult;

import ch.ivyteam.ivy.environment.Ivy;

public class ProcessMessageService {
	
	public void process(MessageFetchResult result) {
		Ivy.log().warn("ProcessMessageService::process:result: " + result);

		List<MessageDetail> details = result.getMessageDetails();
		for (MessageDetail detail : details) {
			Ivy.log().info("Processing message detail: " + detail);
			

		}		
		
		
		result.getMessageDetails().forEach(detail -> {
			
//			boolean autoApproval = isAutoApproval(detail.getScore(), detail.getIncome());
//			detail.setAutoApproval(autoApproval);
			Ivy.log().info("Message detail after processing: " + detail);
		});		
		
	}
	
	private static boolean isAutoApproval(int score, double income) {
		return (score >= 700 && income >= 4000);
	}
}
