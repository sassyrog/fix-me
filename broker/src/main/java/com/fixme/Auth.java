package com.fixme;

import java.io.Console;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import com.fixme.controlers.Colour;
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

	public String signUp() {
		name = "";
		username = "";
		password1 = "";
		password2 = "";

		int track = 0;
		while ((name.equals("") || username.equals("") || password1.equals("")) || track == 0) {
			System.out.println();
			name = console.readLine("Broker Name : ").trim();
			username = console.readLine("Username : ").trim();
			password1 = new String(console.readPassword("Password : ")).trim();
			password2 = new String(console.readPassword("re-type Password : ")).trim();

			System.out.println();
			if (name.equals(""))
				Colour.out.red("Broker Name cannot be empty");
			else if (!Pattern.matches("^[a-zA-Z ]\'?[-a-zA-Z ]+$", name))
				Colour.out.red("Name not right");
			else if (username.equals(""))
				Colour.out.red("Username cannot be empty");
			else if (!Pattern.matches("^[a-zA-Z0-9_]+$", username))
				Colour.out.red("Username not right");
			else if (password1.equals(""))
				Colour.out.red("Password cannot be empty");
			else if (!password2.equals(password1))
				Colour.out.red("Passwords don't match");
			else {
				String coor = console.readLine("\u001B[1;37mIs all the info above correct? (y|n) (Y|N) : \u001B[0m");
				if (coor.equals("y") || coor.equals("Y"))
					track++;
			}
		}
		signUpDB();
		return this.username;
	}

	public String login() {
		username = "";
		password1 = "";
		while (username.equals("") || password1.equals("")) {
			System.out.println();
			username = console.readLine("Username : ").trim();
			password1 = new String(console.readPassword("Password : ")).trim();

			if (username.equals(""))
				Colour.out.red("Username cannot be empty");
			else if (password1.equals(""))
				Colour.out.red("Password cannot be empty");
			else {
				break;
			}
		}
		return loginDB();
	}

	private void signUpDB() {
		try {
			ResultSet rSet = conn.query("SELECT 1 FROM brokers WHERE br_username = '" + this.username + "'");

			if (rSet.next()) {
				Colour.out.red("Username already exists!!!");
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
		}
	}

	private String loginDB() {
		try {
			ResultSet rSet = conn
					.query("SELECT br_username, br_password FROM brokers WHERE br_username = '" + this.username + "'");
			if (rSet.next()) {
				String pHash = rSet.getString("br_password");
				BCrypt.Result res = BCrypt.verifyer().verify(this.password1.toCharArray(), pHash);
				if (res.verified) {
					return this.username;
				} else {
					Colour.out.red("\nCould not login. Please make sure username and password are correct");
					return this.login();
				}
			} else {
				Colour.out.red("\nCould not find username. Please make sure username is correct");
				return this.login();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
