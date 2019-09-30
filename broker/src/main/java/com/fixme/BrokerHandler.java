package com.fixme;

import java.io.IOException;
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

	static public String brokerBuy(String clientID, Broker broker) {
		// try {
		// broker.getResponseFromServer("markets");
		// System.out.println("Here's the list of all available instruments");
		// ResultSet rSet = conn.query(
		// "SELECT inst.inst_id AS 'ID', inst.inst_name AS 'Name', inst.inst_amount AS
		// 'Amount', ma.ma_name AS 'Market' FROM instruments inst INNER JOIN markets ma
		// ON inst.inst_ma_id = ma.ma_id");
		// if (rSet.isBeforeFirst()) {
		// DBTablePrinter.printResultSet(rSet);
		// } else {
		// Colour.out.red("No instruments to buy!!!\n");
		// }
		// } catch (IOException ie) { // SQLException se) {
		// ie.printStackTrace();
		// // // TODO: handle exception
		// }
		return "blah";
	}

	static public String brokerSell(String clientID, Broker broker) {

		return "blah";
	}
}
