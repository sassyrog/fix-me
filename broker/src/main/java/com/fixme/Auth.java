package com.fixme;

import java.io.Console;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.fixme.controlers.MysqlConnect;

import at.favre.lib.crypto.bcrypt.BCrypt;;

/**
 * Auth
 */
public class Auth {
	private Console console = System.console();
	private String name = "";
	private String username = "";
	private String password1 = "";
	private String password2 = "";

	static private MysqlConnect conn = MysqlConnect.getDbCon();

	public Auth() {
	}

	public void signUp() {
		int track = 0;
		while ((name.equals("") || username.equals("") || password1.equals("")) || track == 0) {
			System.out.println();
			name = console.readLine("Broker Name : ");
			username = console.readLine("Username : ");
			password1 = new String(console.readPassword("Password : "));
			password2 = new String(console.readPassword("re-type Password : "));

			System.out.println();
			if (name.equals(""))
				System.out.println("\u001B[1;31mBroker Name cannot be empty\u001B[0m");
			else if (!Pattern.matches("^[a-zA-Z]\'?[-a-zA-Z]+$", name))
				System.out.println("\u001B[1;31mName not right\u001B[0m");
			else if (username.equals(""))
				System.out.println("\u001B[1;31mUsername cannot be empty\u001B[0m");
			else if (!Pattern.matches("^[a-zA-Z0-9_]+$", username))
				System.out.println("\u001B[1;31mUsername not right\u001B[0m");
			else if (password1.equals(""))
				System.out.println("\u001B[1;31mPassword cannot be empty\u001B[0m");
			else if (!password2.equals(password1))
				System.out.println("\u001B[1;31mPasswords don't match\u001B[0m");
			else {
				String coor = console.readLine("Is all the info above correct (y|n) (Y|N) : ");
				if (coor.equals("y") || coor.equals("Y"))
					track++;
			}
		}

		signUpDB();

	}

	public void signUpDB() {
		try {
			ResultSet rSet = conn.query("SELECT 1 FROM brokers WHERE br_username = '" + this.username + "'");

			if (rSet.next()) {
				System.out.println("Username already exists!!!");
				signUp();
			} else {
				String pHash = BCrypt.withDefaults().hashToString(10, this.password1.toCharArray());
				String query = "INSERT INTO brokers ( br_name, br_username, br_password,  br_ip) VALUES (?,?,?,?)";
				String vals[] = { this.name, this.username, pHash, "1" };
				int res = conn.preparedStringInsert(query, vals);
				if (res == 0)
					throw new SQLException("Something went wrong");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
}