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

	private int port = 5001;
	private String hostName = "localhost";
	private ByteBuffer cBuffer = ByteBuffer.allocate(1000);

	public static void main(String[] args) {
		Market client = new Market();
		client.marketNIO();
	}

	// main market method
	public void marketNIO() {
		try {
			// non blocking client socket
			SocketChannel sc = SocketChannel.open();
			InetSocketAddress addr = new InetSocketAddress(hostName, port);
			sc.configureBlocking(false);
			sc.connect(addr);

			while (!sc.finishConnect()) {
				System.out.println("conneting to server");
			}

			Selector selector = Selector.open();
			sc.register(selector, SelectionKey.OP_READ);
			while (true) {
				if (selector.select() > 0) {
					processServerIO(selector);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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

		cBuffer.flip();
		cBuffer.clear();

		int count = sChannel.read(cBuffer);

		// try {
		if (count > 0) {
			cBuffer.flip();
			String input = Charset.forName("UTF-8").decode(cBuffer).toString();
			System.out.println("server msg : " + input);

			// Thread.sleep(3000);
			cBuffer.flip();
			cBuffer.clear();
			cBuffer.put("something from the Market".getBytes());
			cBuffer.flip();
			cBuffer.rewind();
			sChannel.write(cBuffer);

			// sChannel.close();
		}

		// } catch (InterruptedException ie) {
		// ie.printStackTrace();
		// }
	}
}
