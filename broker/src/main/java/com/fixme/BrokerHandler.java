package com.fixme;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.fixme.controlers.Colour;
import com.fixme.controlers.DBTablePrinter;
import com.fixme.controlers.Fix;
import com.fixme.controlers.MysqlConnect;

/**
 * BrokerHandler
 */
public class BrokerHandler {
	private MysqlConnect conn = MysqlConnect.getDbCon();
	private Scanner scanner = new Scanner(System.in);
	ResultSet rSet;
	Fix fix = new Fix();
	Broker broker;
	String clientID;

	public BrokerHandler(String _clientID, Broker _broker) {
		this.broker = _broker;
		this.clientID = _clientID;
	}

	public String brokerBuy() {
		try {
			String avMarkets = broker.getResponseFromServer("markets").trim();
			if (!avMarkets.equals("nada")) {
				String query = "SELECT inst.inst_id AS 'ID', inst.inst_name AS 'Name', inst.inst_amount AS 'Quantity Available', ma.ma_name AS 'Market' FROM instruments inst INNER JOIN markets ma ON inst.inst_ma_id = ma.ma_id WHERE ma.ma_id IN ("
						+ avMarkets + ")";
				this.rSet = conn.query(query);
				if (rSet.isBeforeFirst()) {
					DBTablePrinter.printResultSet(rSet);
					rSet.beforeFirst();
					processBuy();
				} else {
					Colour.out.red("No instruments to buy!!!\n");
				}
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return "blah";
	}

	public String brokerSell() {

		return "blah";
	}

	public String processBuy() throws SQLException {
		System.out.println("Please choose the instrument you want to buy");

		int id = inputID();
		int quantity = inputQuantity(id);

		System.out.printf("Are all purchase details above correct? (y|n) : ");

		String correct = this.scanner.next().trim();

		if (correct.equals("y") || correct.equals("Y")) {
			return fix.encode(id, quantity);
		} else {
			processBuy();
		}
		return "";
	}

	public int inputID() throws SQLException {
		int id;
		while (true) {
			try {
				System.out.print("Instrument ID : ");
				id = this.scanner.nextInt();
				while (this.rSet.next()) {
					System.out.println("id ===> " + this.rSet.getInt("ID"));
					if (this.rSet.getInt("ID") == id) {
						this.rSet.beforeFirst();
						return id;
					}
				}
				rSet.beforeFirst();
			} catch (InputMismatchException ime) {
				this.scanner.next();
			}
		}
	}

	public int inputQuantity(int id) throws SQLException {
		int quantity;
		while (true) {
			try {
				System.out.print("Quantity : ");
				quantity = this.scanner.nextInt();
				while (this.rSet.next()) {
					if (this.rSet.getInt("ID") == id) {
						if (quantity < rSet.getFloat("Quantity Available")) {
							rSet.beforeFirst();
							return quantity;
						}
					}
				}
				rSet.beforeFirst();
			} catch (InputMismatchException ime) {
				this.scanner.next();
			}
		}
	}
}
