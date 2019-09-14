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

	private int ports[] = new int[] { 5000, 5001 };

	public static void main(String[] args) {
		// MysqlConnect conn = MysqlConnect.getDbCon();
		// Router server = new Router();
		// server.startServer();
	}

	public void startServer() {

		try {
			Selector selector = Selector.open();

			for (int port : ports) {
				ServerSocketChannel ssChannel = ServerSocketChannel.open();
				ssChannel.configureBlocking(false);
				ServerSocket sSocket = ssChannel.socket();
				sSocket.bind(new InetSocketAddress(port));
				ssChannel.register(selector, SelectionKey.OP_ACCEPT);

			}

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
				SelectionKey sk = skIterator.next();
				if (sk.isAcceptable()) {
					System.out.println("accept client connection");
					acceptConnection(sk, s);
				} else if (sk.isReadable()) {
					System.out.println("read from client");
					readWriteClient(sk);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			skIterator.remove();
		}
	}

	public void acceptConnection(SelectionKey sk, Selector s) throws IOException {
		ServerSocketChannel server = (ServerSocketChannel) sk.channel();
		SocketChannel sChannel = server.accept();

		sChannel.configureBlocking(false);
		sChannel.register(s, SelectionKey.OP_READ);
	}

	public void readWriteClient(SelectionKey sk) throws IOException {
		SocketChannel schannel = (SocketChannel) sk.channel();
		ByteBuffer bb = ByteBuffer.allocate(1000);

		bb.flip();
		bb.clear();

		int count = schannel.read(bb);
		if (count > 0) {
			bb.flip();
			String input = Charset.forName("UTF-8").decode(bb).toString();
			System.out.println(input);

			bb.flip();
			bb.clear();
			bb.put(processClientRequest(input).getBytes());
			bb.flip();
			bb.rewind();
			schannel.write(bb);

			schannel.close();
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
