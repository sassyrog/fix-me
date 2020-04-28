package com.fixme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.fixme.controlers.Colour;
import com.fixme.controlers.MysqlConnect;

/**
 * Hello world!
 *
 */
public class Broker {
	static private MysqlConnect conn = MysqlConnect.getDbCon();
	private Auth auth = new Auth();
	private int port = 5000;
	private String hostName = "localhost";
	private ByteBuffer bb = ByteBuffer.allocate(1000);
	private SocketChannel sChannel;
	private InetSocketAddress addr;
	private String clientID;
	private int id;
	private String username;

	public static void main(String[] args) {
		Broker broker = new Broker();
		String valid = "";
		Scanner scn = new Scanner(System.in);
		String choice = "";
		while (!choice.equals("s") && !choice.equals("l")) {
			System.out.print("Would you like to sign up or login (s|l): ");
			choice = scn.nextLine().trim();
		}
		if (choice.equals("s")) {
			valid = broker.getAuth().signUp();
		} else if (choice.equals("l")) {
			valid = broker.getAuth().login();
		}

		if (!valid.equals("")) {
			broker.setUsername(valid);
			Colour.out.green("\n\tThis broker is now logged in\n");
			try {
				broker.createConnection();
				while (true) {
					System.out.print("Instruction (buy|sell) : ");
					String instr = scn.nextLine().trim();
					BrokerHandler bh = new BrokerHandler(broker.getCID(), broker);

					if (instr.trim().equalsIgnoreCase("buy")) {
						bh.brokerBuy();
					} else if (instr.trim().equalsIgnoreCase("sell")) {
						bh.brokerSell();
					} else {
						System.out.println("Invalid instruction");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		scn.close();
	}

	private void setUsername(String valid) {
		this.username = valid;
	}

	public Auth getAuth() {
		return this.auth;
	}

	public void createConnection() throws IOException, SQLException {
		ResultSet rSet = conn.query("SELECT br_id FROM brokers WHERE br_username = '" + this.username + "'");
		if (rSet.next())
			this.id = rSet.getInt("br_id");
		this.sChannel = SocketChannel.open();
		this.addr = new InetSocketAddress(this.hostName, this.port);
		this.sChannel.configureBlocking(false);
		this.sChannel.connect(addr);
		while (!sChannel.finishConnect())
			System.out.println("connecting to server...");
		getResponseFromServer("new=" + this.id);
	}

	public String getResponseFromServer(String request) throws IOException {
		((Buffer) this.bb).flip();
		((Buffer) this.bb).clear();
		this.bb.put(request.getBytes());
		((Buffer) this.bb).flip();
		this.sChannel.write(bb);
		Selector selector = Selector.open();
		this.sChannel.register(selector, SelectionKey.OP_READ);
		while (true) {
			if (selector.select() > 0) {
				String respString = processServerResponse(selector).trim();
				if (!respString.equals(""))
					return respString;
			}
		}
	}

	public String processServerResponse(Selector s) {
		Iterator<SelectionKey> i = s.selectedKeys().iterator();
		int count;

		while (i.hasNext()) {
			try {
				SelectionKey sk = i.next();
				if (sk.isReadable()) {
					SocketChannel sc = (SocketChannel) sk.channel();
					((Buffer) bb).flip();
					((Buffer) bb).clear();
					count = sc.read(bb);
					if (count > 0) {
						((Buffer) bb).flip();
						String response = Charset.forName("UTF-8").decode(bb).toString().trim();
						if (Pattern.matches("connected=\\d{6}$", response))
							this.clientID = response.split("=")[1];
						return response;
					}
				}
				i.remove();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public String getCID() {
		return this.clientID;
	}

}
