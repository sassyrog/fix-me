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
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.fixme.controlers.TimeMessage;

/**
 * RouterServer
 */
public class RouterServer {
	private int ports[] = new int[] { 5000, 5001 };
	private static long uID = 99999;
	// Router assigned id and Socket channel
	HashMap<String, SocketChannel> markets;
	// Router assigned id, broker id and Socket channel
	HashMap<String, SocketChannel> brokers;
	private SocketChannel marketChannel;
	private SocketChannel brokerChannel;

	public RouterServer() {
		this.markets = new HashMap<String, SocketChannel>();
		this.brokers = new HashMap<String, SocketChannel>();
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

			System.out.println("\u001B[1;32mRouting server is now running...\u001B[0m");
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
					readWriteClient(sKey, s);
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
		sChannel.configureBlocking(false);
		sChannel.register(s, SelectionKey.OP_READ);

		switch (sChannel.socket().getLocalPort()) {
		case 5000: {
			TimeMessage.print("Broker connection!!!");
			break;
		}
		case 5001:
			TimeMessage.print("Market connection!!!");
			break;
		}
	}

	public void readWriteClient(SelectionKey sKey, Selector s) throws IOException {
		SocketChannel sChannel = (SocketChannel) sKey.channel();
		ByteBuffer cBuffer = ByteBuffer.allocate(1000);
		cBuffer.flip();
		cBuffer.clear();

		switch (sChannel.socket().getLocalPort()) {
		case 5000:
			processBrokerToMarket(cBuffer, sChannel);
			sChannel.register(s, SelectionKey.OP_WRITE);
			break;
		case 5001:
			this.marketChannel = sChannel;
			processMarket(cBuffer);
			this.marketChannel.register(s, SelectionKey.OP_WRITE);
			break;
		}
	}

	public void processBrokerToMarket(ByteBuffer cBuffer, SocketChannel sc) throws IOException {
		String clientString;
		// if (this.marketChannel.isConnected()) {
		int count = sc.read(cBuffer);
		if (count > 0) {
			cBuffer.flip();
			clientString = Charset.forName("UTF-8").decode(cBuffer).toString().trim();
			System.out.println("Broker request: " + clientString);
			if (Pattern.matches("new=1", clientString)) {
				Long id = this.nextID();
				String respString = "connected=" + id;
				brokers.put(Long.toString(id), sc);
				brokers.forEach((key, value) -> System.out.println(key + " " + value));
				cBuffer.flip();
				cBuffer.clear();
				cBuffer.put(respString.getBytes());
				cBuffer.flip();
				cBuffer.rewind();
				sc.write(cBuffer);
			} else {
				String someString = this.broadcast(clientString, this.marketChannel);
				cBuffer.flip();
				cBuffer.clear();
				cBuffer.put(someString.getBytes());
				cBuffer.flip();
				cBuffer.rewind();
				this.brokerChannel.write(cBuffer);
			}
		}
		// }
	}

	public void processMarket(ByteBuffer cBuffer) throws IOException {
		String clientString;
		// if (this.marketChannel.isConnected()) {
		int count = this.marketChannel.read(cBuffer);
		if (count > 0) {
			cBuffer.flip();
			clientString = Charset.forName("UTF-8").decode(cBuffer).toString();
			System.out.println("Market request: " + clientString);
		}
	}

	public String broadcast(String msg, SocketChannel channel) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(1000);
		bb.flip();
		bb.clear();
		bb.put(msg.getBytes());
		bb.flip();
		channel.write(bb);

		Selector selector = Selector.open();
		channel.register(selector, SelectionKey.OP_READ);
		while (true) {
			if (selector.select() > 0) {
				Iterator<SelectionKey> i = selector.selectedKeys().iterator();
				while (i.hasNext()) {
					try {
						SelectionKey sk = i.next();
						if (sk.isReadable()) {
							SocketChannel mChannel = (SocketChannel) sk.channel();
							bb.flip();
							bb.clear();

							int count = mChannel.read(bb);
							if (count > 0) {
								bb.rewind();
								String response = Charset.forName("UTF-8").decode(bb).toString();
								i.remove();
								return response;
							}
						}
						i.remove();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public long nextID() {
		return (++this.uID);
	}

	public SocketChannel getMarketChannel() {
		return this.marketChannel;
	}

	public SocketChannel getBrokerChannel() {
		return this.brokerChannel;
	}
}
