package com.fixme.controlers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TimeMessage
 */
public class TimeMessage {

	static public void print(String msg) {
		LocalDateTime lTime = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		String formattedDate = lTime.format(myFormatObj);
		System.out.println("[" + formattedDate + "]");
	}
}
