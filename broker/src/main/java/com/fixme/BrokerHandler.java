package com.fixme;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.fixme.controlers.MysqlConnect;

/**
 * BrokerHandler
 */
public class BrokerHandler {
	static private MysqlConnect conn = MysqlConnect.getDbCon();
	static Scanner scanner = new Scanner(System.in);

	static public String brokerBuy() {
		try {
			System.out.println("Here's the list of all available instruments");
			ResultSet rSet = conn.query("SELECT inst_name, inst_id, inst_amount FROM instruments");
			if (!rSet.wasNull()) {
				rSet.next();
				System.out.println("inst_name : " + rSet.getString("inst_name"));
			} else {
				System.out.println("some stuff");
			}
		} catch (SQLException se) {
			se.printStackTrace();
			// TODO: handle exception
		}
		return "blah";
	}

	static public String brokerSell() {

		return "blah";
	}
}
