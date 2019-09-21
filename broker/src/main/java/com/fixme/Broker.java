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
			// try {
			// broker.createConnection();
			while (true) {
				System.out.print("instruction: ");
				String instr = scn.nextLine().trim();
				if (instr.equalsIgnoreCase("buy")) {
					BrokerHandler.brokerBuy();
				} else if (instr.equalsIgnoreCase("sell")) {
					System.out.println("++++> sell");
				} else {
					System.out.println("Invalid instruction");
				}

				// broker.getResponseFromServer(scn.nextLine().trim());
			}
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
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
			System.out.println("conneting to server...");
		}
	}

	public void getResponseFromServer(String request) throws IOException {
		this.bb.flip();
		this.bb.clear();
		this.bb.put(request.getBytes());
		this.bb.flip();
		this.sChannel.write(bb);
		Selector selector = Selector.open();
		this.sChannel.register(selector, SelectionKey.OP_READ);
		while (true) {
			if (selector.select() > 0) {
				if (processServerResponse(selector)) {
					return;
				}
			}
		}
	}

	public boolean processServerResponse(Selector s) {
		Iterator<SelectionKey> i = s.selectedKeys().iterator();
		while (i.hasNext()) {
			try {
				SelectionKey sk = i.next();
				if (sk.isReadable()) {
					SocketChannel schannel = (SocketChannel) sk.channel();
					bb.flip();
					bb.clear();

					int count = schannel.read(bb);
					if (count > 0) {
						bb.rewind();
						String response = Charset.forName("UTF-8").decode(bb).toString();
						System.out.println("response: " + response);
						return true;
					}
				}
				i.remove();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
