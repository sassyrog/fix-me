package com.fixme;

import java.util.Scanner;

import com.fixme.controlers.MysqlConnect;

/**
 * BrokerHandler
 */
public class BrokerHandler {
	private MysqlConnect conn = MysqlConnect.getDbCon();

	static public String brokerBuy() {
		Scanner scanner = new Scanner(System.in);

		scanner.close();
		return "blah";
	}

	static public String brokerSell() {
		Scanner scanner = new Scanner(System.in);

		scanner.close();
		return "blah";
	}
}
