package com.fixme;

import java.io.Console;
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
	private String name;
	private String username;
	private String password1;
	private String password2;

	static private MysqlConnect conn = MysqlConnect.getDbCon();

	public Auth() {
	}

	public boolean signUp() {
		name = "";
		username = "";
		password1 = "";
		password2 = "";

		int track = 0;
		while ((name.equals("") || username.equals("") || password1.equals("")) || track == 0) {
			System.out.println();
			name = console.readLine("Market Name : ");
			username = console.readLine("Username : ");
			password1 = new String(console.readPassword("Password : "));
			password2 = new String(console.readPassword("re-type Password : "));

			System.out.println();
			if (name.equals(""))
				System.out.println("\u001B[1;31mMarket Name cannot be empty\u001B[0m");
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
				String coor = console.readLine("\u001B[1;37mIs all the info above correct? (y|n) (Y|N) : \u001B[0m");
				if (coor.equals("y") || coor.equals("Y"))
					track++;
			}
		}
		signUpDB();
		return true;
	}

	public boolean login() {
		username = "";
		password1 = "";
		while (username.equals("") || password1.equals("")) {
			System.out.println();
			username = console.readLine("Username : ");
			password1 = new String(console.readPassword("Password : "));

			if (username.equals(""))
				System.out.println("\u001B[1;31mUsername cannot be empty\u001B[0m");
			else if (password1.equals(""))
				System.out.println("\u001B[1;31mPassword cannot be empty\u001B[0m");
			else {
				break;
			}
		}
		return loginDB();
	}

	private void signUpDB() {
		try {
			ResultSet rSet = conn.query("SELECT 1 FROM markets WHERE ma_username = '" + this.username + "'");

			if (rSet.next()) {
				System.out.println("Username already exists!!!");
				signUp();
			} else {
				String pHash = BCrypt.withDefaults().hashToString(10, this.password1.toCharArray());
				String query = "INSERT INTO brokers ( ma_name, ma_username, ma_password) VALUES (?,?,?)";
				String vals[] = { this.name, this.username, pHash };
				int res = conn.preparedStringInsert(query, vals);
				if (res == 0)
					throw new SQLException("Something went wrong");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean loginDB() {
		try {
			ResultSet rSet = conn
					.query("SELECT ma_username, ma_password FROM markets WHERE ma_username = '" + this.username + "'");
			if (rSet.next()) {
				String pHash = rSet.getString("ma_password");
				BCrypt.Result res = BCrypt.verifyer().verify(this.password1.toCharArray(), pHash);
				if (res.verified) {
					return true;
				} else {
					System.out.println("\nCould not login. Please make sure username and password are correct");
					this.login();
				}
			} else {
				System.out.println("\nCould not find username. Please make sure username is correct");
				this.login();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}