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
		scn.close();
	}

	public Auth getAuth() {
		return this.auth;
	}

	public void getResponseFromServer(String request) {
		try {
			// non blocking client socket
			SocketChannel sc = SocketChannel.open();
			sc.configureBlocking(false);

			InetSocketAddress addr = new InetSocketAddress(hostName, port);
			sc.connect(addr);

			while (!sc.finishConnect()) {
				System.out.println("conneting to server");
			}

			// send request
			bb.flip();
			bb.clear();
			bb.put(request.getBytes());
			bb.flip();
			sc.write(bb);

			// process response
			Selector selector = Selector.open();
			sc.register(selector, SelectionKey.OP_READ);
			while (true) {
				if (selector.select() > 0) {
					if (processServerResponse(selector)) {
						return;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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

						schannel.close();
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
