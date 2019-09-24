package com.fixme;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.fixme.controlers.Colour;
import com.fixme.controlers.DBTablePrinter;
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
			ResultSet rSet = conn
					.query("SELECT inst_id AS 'ID', inst_name AS 'Name', inst_amount AS 'Amount' FROM instruments");
			if (rSet.isBeforeFirst()) {
				DBTablePrinter.printResultSet(rSet);
			} else {
				Colour.out.red("No instruments to buy!!!\n");
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
