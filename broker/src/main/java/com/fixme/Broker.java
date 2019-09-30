package com.fixme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.fixme.controlers.Colour;

/**
 * Hello world!
 *
 */
public class Broker {
	private Auth auth = new Auth();
	private int port = 5000;
	private String hostName = "localhost";
	private ByteBuffer bb = ByteBuffer.allocate(1000);
	private SocketChannel sChannel;
	private InetSocketAddress addr;
	private String clientID;

	public static void main(String[] args) {
		Broker broker = new Broker();
		boolean valid = false;
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

		if (valid) {
			Colour.out.green("\n\tThis broker is now logged in\n");
			try {
				broker.createConnection();
				while (true) {
					System.out.print("Instruction (buy|sell) : ");
					String instr = scn.nextLine().trim();
					if (instr.trim().equalsIgnoreCase("buy")) {
						BrokerHandler.brokerBuy(broker.getCID(), broker);
					} else if (instr.trim().equalsIgnoreCase("sell")) {
						BrokerHandler.brokerSell(broker.getCID(), broker);
					} else {
						System.out.println("Invalid instruction");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		scn.close();
	}

	public Auth getAuth() {
		return this.auth;
	}

	public void createConnection() throws IOException {
		this.sChannel = SocketChannel.open();
		this.addr = new InetSocketAddress(this.hostName, this.port);
		this.sChannel.configureBlocking(false);
		this.sChannel.connect(addr);
		while (!sChannel.finishConnect()) {
			System.out.println("connecting to server...");
		}
		getResponseFromServer("new=1");
	}

	public String getResponseFromServer(String request) throws IOException {
		this.bb.flip();
		this.bb.clear();
		this.bb.put(request.getBytes());
		this.bb.flip();
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
					bb.flip();
					bb.clear();
					count = sc.read(bb);
					if (count > 0) {
						bb.flip();
						String response = Charset.forName("UTF-8").decode(bb).toString().trim();
						if (Pattern.matches("connected=\\d{6}$", response)) {
							this.clientID = response.split("=")[1];
						} else
							System.out.println("response: " + response);
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
