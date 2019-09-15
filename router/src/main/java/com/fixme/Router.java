package com.fixme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

// import com.fixme.controlers.MysqlConnect;

/**
 * Hello world!
 *
 */
public class Router {

	private int port = 5000;
	// private int ports[] = new int[] { 5000, 5001 };

	public static void main(String[] args) {
		// MysqlConnect conn = MysqlConnect.getDbCon();
		Router server = new Router();
		server.startServer();
	}

	public void startServer() {

		try {
			Selector selector = Selector.open();

			// for (int port : ports) {
			ServerSocketChannel ssChannel = ServerSocketChannel.open();
			ssChannel.configureBlocking(false);
			ServerSocket sSocket = ssChannel.socket();
			sSocket.bind(new InetSocketAddress(port));
			ssChannel.register(selector, SelectionKey.OP_ACCEPT);

			// }

			while (true) {
				System.out.println("waiting for client connection");
				if (selector.select() > 0) {
					performIO(selector);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void performIO(Selector s) {
		Iterator<SelectionKey> skIterator = s.selectedKeys().iterator();

		while (skIterator.hasNext()) {
			try {
				SelectionKey sKey = skIterator.next();
				if (sKey.isAcceptable()) {
					System.out.println("accept client connection");
					acceptConnection(sKey, s);
				} else if (sKey.isReadable()) {
					System.out.println("read from client");
					readWriteClient(sKey);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			skIterator.remove();
		}
	}

	public void acceptConnection(SelectionKey sKey, Selector s) throws IOException {
		ServerSocketChannel ssChannel = (ServerSocketChannel) sKey.channel();
		SocketChannel sChannel = ssChannel.accept();

		System.out.println("IP : " + ssChannel.socket().getLocalPort());
		sChannel.configureBlocking(false);
		sChannel.register(s, SelectionKey.OP_READ);
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
			System.out.println(input);

			cBuffer.flip();
			cBuffer.clear();
			cBuffer.put(processClientRequest(input).getBytes());
			cBuffer.flip();
			cBuffer.rewind();
			sChannel.write(cBuffer);

			sChannel.close();
		}
	}

	public String processClientRequest(String input) {
		if (input.startsWith("deals")) {
			return "upto 20% off on fashion";
		} else {
			return "invalid request";
		}
	}
}
