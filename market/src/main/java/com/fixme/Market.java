package com.fixme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * Hello world!
 *
 */
public class Market {

	private int port = 5000;
	private String hostName = "localhost";
	private ByteBuffer bb = ByteBuffer.allocate(1000);

	public static void main(String[] args) {
		Market client = new Market();
		client.getResponseFromServer();
		Market client2 = new Market();
		client2.getResponseFromServer();
	}

	// main client method
	public void getResponseFromServer() {
		try {
			// non blocking client socket
			SocketChannel sc = SocketChannel.open();
			sc.configureBlocking(false);

			InetSocketAddress addr = new InetSocketAddress(hostName, port);
			sc.connect(addr);

			while (!sc.finishConnect()) {
				System.out.println("conneting to server");
			}

			Selector selector = Selector.open();
			sc.register(selector, SelectionKey.OP_READ);
			while (true) {
				if (selector.select() > 0) {
					processServerResponse(selector);
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
					readWriteClient(sk);
				}
				i.remove();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void readWriteClient(SelectionKey sKey) throws IOException {
		SocketChannel sChannel = (SocketChannel) sKey.channel();
		ByteBuffer cBuffer = ByteBuffer.allocate(1000);

		cBuffer.flip();
		cBuffer.clear();

		int count = sChannel.read(cBuffer);
		if (count > 0) {
			cBuffer.flip();
			String input = Charset.forName("UTF-8").decode(cBuffer).toString();
			System.out.println("server msg : " + input);

			cBuffer.flip();
			cBuffer.clear();
			cBuffer.put("something from the Client".getBytes());
			cBuffer.flip();
			cBuffer.rewind();
			sChannel.write(cBuffer);

			sChannel.close();
		}
	}
}
