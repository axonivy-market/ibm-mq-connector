package com.axonivy.connector.imb.mq.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import com.axonivy.connector.model.MessagePushRequest;

public final class MessageUtil {
	private MessageUtil() {
	}

	public static MessagePushRequest loadResourceFiles() {
		MessagePushRequest request = new MessagePushRequest();
		request.setQueueName("DEV.QUEUE.1");
		try {
			URL dirUrl = MessageUtil.class.getClassLoader().getResource("resources/data");
			if (dirUrl == null) {
				throw new IllegalStateException("Missing resource directory: data");
			}
			Path dirPath = Paths.get(dirUrl.toURI());
			try (Stream<Path> files = Files.walk(dirPath, 1)) {
				files.filter(Files::isRegularFile).forEach(filePath -> {
					try {
						String content = Files.readString(filePath);
						request.getPayloads().add(content);
					} catch (IOException ex) {
						throw new IllegalStateException("Unable to read resource file: " + filePath, ex);
					}
				});
			}
		} catch (IOException | URISyntaxException ex) {
			throw new IllegalStateException("Unable to read resource directory data: " + ex);
		}

		return request;
	}
}
