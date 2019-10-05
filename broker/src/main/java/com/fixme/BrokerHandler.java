package com.fixme;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
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
		try {
			String avMarkets = broker.getResponseFromServer("markets").trim();
			if (!avMarkets.equals("nada")) {
				String query = "SELECT inst.inst_id AS 'ID', inst.inst_name AS 'Name', inst.inst_amount AS 'Quantity Available', ma.ma_name AS 'Market' FROM instruments inst INNER JOIN markets ma ON inst.inst_ma_id = ma.ma_id WHERE ma.ma_id IN ("
						+ avMarkets + ")";
				ResultSet rSet = conn.query(query);
				if (rSet.isBeforeFirst()) {
					DBTablePrinter.printResultSet(rSet);

					processBuy(clientID, broker);
				} else {
					Colour.out.red("No instruments to buy!!!\n");
				}
			}
			// System.out.println("====> " + hh);
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
		} catch (IOException ie) { // SQLException se) {
			ie.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return "blah";
	}

	static public String brokerSell(String clientID, Broker broker) {

		return "blah";
	}

	public static void processBuy(String clientID, Broker broker) {
		System.out.println("Please choose the instrument you want to buy\n");

		int id = inputID();
		int quantity = inputQuantity();

		System.out.print("Are all purchase details above correct? (y|n) : ");
		String correct = scanner.nextLine().trim();
		if (correct.equals("y"))
			System.out.println(correct);
		else
			processBuy(clientID, broker);

	}

	public static int inputID() {
		while (true) {
			try {
				System.out.print("Instrument ID : ");
				return scanner.nextInt();
			} catch (InputMismatchException ime) {
				scanner.nextLine();
			}
		}
	}

	public static int inputQuantity() {
		while (true) {
			try {
				System.out.print("Quantity : ");
				return scanner.nextInt();
			} catch (InputMismatchException ime) {
				scanner.nextLine();
			}
		}
	}
}
