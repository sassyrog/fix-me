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
public class Market {

	private int port = 5001;
	private String clientID;
	private String hostName = "localhost";
	private ByteBuffer cBuffer = ByteBuffer.allocate(1000);
	private Auth auth = new Auth();
	static private MysqlConnect conn = MysqlConnect.getDbCon();
	private String username;
	private int id;

	public static void main(String[] args) {
		Market market = new Market();

		String valid = "";
		Scanner scn = new Scanner(System.in);
		String choice = "";
		while (!choice.equals("s") && !choice.equals("l")) {
			System.out.print("Would you like to sign up or login (s|l): ");
			choice = scn.nextLine().trim();
		}
		if (choice.equals("s")) {
			valid = market.getAuth().signUp().trim();
		} else if (choice.equals("l")) {
			valid = market.getAuth().login().trim();
		}

		if (!valid.equals("")) {
			market.setUsername(valid);
			Colour.out.green("\n\tThis market is now logged in\n");
			market.marketNIO();
		}
		scn.close();
	}

	public Auth getAuth() {
		return this.auth;
	}

	// main market method
	public void marketNIO() {
		try {
			ResultSet rSet = conn.query("SELECT ma_id FROM markets WHERE ma_username = '" + this.username + "'");
			if (rSet.next()) {
				this.id = rSet.getInt("ma_id");
			}
			// non blocking client socket
			SocketChannel sc = SocketChannel.open();
			InetSocketAddress addr = new InetSocketAddress(hostName, port);
			sc.configureBlocking(false);
			sc.connect(addr);

			while (!sc.finishConnect())
				System.out.println("connecting to server");
			String req = "new=" + this.id;

			((Buffer) this.cBuffer).flip();
			((Buffer) this.cBuffer).clear();
			this.cBuffer.put(req.getBytes());
			((Buffer) this.cBuffer).flip();
			sc.write(cBuffer);

			Selector selector = Selector.open();
			sc.register(selector, SelectionKey.OP_READ);
			while (true) {
				if (selector.select() > 0) {
					processServerIO(selector);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public boolean processServerIO(Selector s) {
		Iterator<SelectionKey> i = s.selectedKeys().iterator();
		while (i.hasNext()) {
			try {
				SelectionKey sk = i.next();
				if (sk.isReadable()) {
					serverReadWrite(sk);
				}
				i.remove();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void serverReadWrite(SelectionKey sKey) throws IOException {
		SocketChannel sChannel = (SocketChannel) sKey.channel();

		((Buffer) cBuffer).flip();
		((Buffer) cBuffer).clear();

		int count = sChannel.read(cBuffer);

		if (count > 0) {
			((Buffer) cBuffer).flip();
			String input = Charset.forName("UTF-8").decode(cBuffer).toString();
			System.out.println("server msg : " + input);
			if (Pattern.matches("connected=\\d{6}$", input)) {
				this.clientID = input.split("=")[1];
				System.out.println("=====> " + this.clientID);
			} else {
				((Buffer) cBuffer).flip();
				((Buffer) cBuffer).clear();
				cBuffer.put("something from the Market".getBytes());
				((Buffer) cBuffer).flip();
				// cBuffer.rewind();
				sChannel.write(cBuffer);
			}
			// sChannel.close();
		}
	}

	public void setUsername(String uName) {
		this.username = uName;
	}
}
