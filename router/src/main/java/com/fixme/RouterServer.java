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

/**
 * RouterServer
 */
public class RouterServer {
	private int ports[] = new int[] { 5000, 5001 };
	private SocketChannel marketChannel;

	public RouterServer() {
	}

	public void newRouterServer() {
		try {
			Selector selector = Selector.open();

			for (int port : ports) {
				ServerSocketChannel ssChannel = ServerSocketChannel.open();
				ssChannel.configureBlocking(false);
				ServerSocket sSocket = ssChannel.socket();
				sSocket.bind(new InetSocketAddress(port));
				ssChannel.register(selector, SelectionKey.OP_ACCEPT);
			}

			System.out.println("Routing server is now running...");
			while (true) {
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
					acceptConnection(sKey, s);
				} else if (sKey.isReadable()) {
					/*
					 * some logic for IP lookup to go here
					 */
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
		this.marketChannel = ssChannel.accept();

		this.marketChannel.configureBlocking(false);
		this.marketChannel.register(s, SelectionKey.OP_READ);
		System.out.println("Connection from Market is got!!!");
		this.broadcast("Shit");
	}

	public void readWriteClient(SelectionKey sKey) throws IOException {
		this.marketChannel = (SocketChannel) sKey.channel();
		ByteBuffer cBuffer = ByteBuffer.allocate(1000);

		cBuffer.flip();
		cBuffer.clear();

		int count = this.marketChannel.read(cBuffer);
		if (count > 0) {
			cBuffer.flip();
			String input = Charset.forName("UTF-8").decode(cBuffer).toString();
			System.out.println(input);

			cBuffer.flip();
			cBuffer.clear();
			cBuffer.put(processClientRequest(input).getBytes());
			cBuffer.flip();
			cBuffer.rewind();
			this.marketChannel.write(cBuffer);
			this.marketChannel.close();
		}
	}

	public void broadcast(String msg) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1000);
		bb.flip();
		bb.clear();
		bb.put(msg.getBytes());
		bb.flip();
		this.marketChannel.write(bb);
	}

	public String processClientRequest(String input) {
		return "Some response to go here";
	}
}
