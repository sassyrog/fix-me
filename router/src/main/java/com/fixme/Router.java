package com.fixme;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.net.SocketAddress;

/**
 * Hello world!
 *
 */
public class Router {

	private Selector selector;

	private InetSocketAddress listenAddress;
	private final static int PORT = 9093;

	public static void main(String[] args) throws Exception {
		try {
			new Router("localhost", 9093).startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Router(String address, int port) throws IOException {
		listenAddress = new InetSocketAddress(address, PORT);
	}

	/**
	 * Start the server
	 *
	 * @throws IOException
	 */
	private void startServer() throws IOException {
		this.selector = Selector.open();
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// bind server socket channel to port
		serverChannel.socket().bind(listenAddress);
		serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

		System.out.println("Server started on port >> " + PORT);

		while (true) {
			// wait for events
			int readyCount = selector.select();
			if (readyCount == 0) {
				continue;
			}

			// process selected keys...
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator iterator = readyKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = (SelectionKey) iterator.next();

				// Remove key from set so we don't process it twice
				iterator.remove();

				if (!key.isValid()) {
					continue;
				}

				if (key.isAcceptable()) { // Accept client connections
					this.accept(key);
				} else if (key.isReadable()) { // Read from client
					this.read(key);
				} else if (key.isWritable()) {
					// write data to client...
				}
			}
		}
	}

	// accept client connection
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = serverChannel.accept();
		channel.configureBlocking(false);
		Socket socket = channel.socket();
		SocketAddress remoteAddr = socket.getRemoteSocketAddress();
		System.out.println("Connected to: " + remoteAddr);

		/*
		 * Register channel with selector for further IO (record it for read/write
		 * operations, here we have used read operation)
		 */
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	// read from the socket channel
	private void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int numRead = -1;
		numRead = channel.read(buffer);

		if (numRead == -1) {
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connection closed by client: " + remoteAddr);
			channel.close();
			key.cancel();
			return;
		}

		byte[] data = new byte[numRead];
		System.arraycopy(buffer.array(), 0, data, 0, numRead);
		System.out.println("Got: " + new String(data));
	}

	// public static void main(String[] args) throws IOException {

	// // Selector: multiplexor of SelectableChannel objects
	// Selector selector = Selector.open(); // selector is open here

	// // ServerSocketChannel: selectable channel for stream-oriented listening
	// sockets
	// ServerSocketChannel crunchifySocket = ServerSocketChannel.open();
	// InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", 5000);

	// // Binds the channel's socket to a local address and configures the socket to
	// // listen for connections
	// crunchifySocket.bind(crunchifyAddr);

	// // Adjusts this channel's blocking mode.
	// crunchifySocket.configureBlocking(false);

	// int ops = crunchifySocket.validOps();
	// SelectionKey selectKy = crunchifySocket.register(selector, ops, null);

	// // Infinite loop..
	// // Keep server running
	// while (true) {

	// log("i'm a server and i'm waiting for new connection and buffer select...");
	// // Selects a set of keys whose corresponding channels are ready for I/O
	// // operations
	// selector.select();

	// // token representing the registration of a SelectableChannel with a Selector
	// Set<SelectionKey> crunchifyKeys = selector.selectedKeys();
	// Iterator<SelectionKey> crunchifyIterator = crunchifyKeys.iterator();

	// while (crunchifyIterator.hasNext()) {
	// SelectionKey myKey = crunchifyIterator.next();

	// // Tests whether this key's channel is ready to accept a new socket
	// connection
	// if (myKey.isAcceptable()) {
	// SocketChannel crunchifyClient = crunchifySocket.accept();

	// // Adjusts this channel's blocking mode to false
	// crunchifyClient.configureBlocking(false);

	// // Operation-set bit for read operations
	// crunchifyClient.register(selector, SelectionKey.OP_READ);
	// log("Connection Accepted: " + crunchifyClient.getLocalAddress() + "\n");

	// // Tests whether this key's channel is ready for reading
	// } else if (myKey.isReadable()) {

	// SocketChannel crunchifyClient = (SocketChannel) myKey.channel();
	// ByteBuffer crunchifyBuffer = ByteBuffer.allocate(256);
	// crunchifyClient.read(crunchifyBuffer);
	// String result = new String(crunchifyBuffer.array()).trim();

	// log("Message received: " + result);

	// if (result.equals("Crunchify")) {
	// crunchifyClient.close();
	// log("\nIt's time to close connection as we got last company name
	// 'Crunchify'");
	// log("\nServer will keep running. Try running client again to establish new
	// connection");
	// }
	// }
	// crunchifyIterator.remove();
	// }
	// }
	// }

	// private static void log(String str) {
	// System.out.println(str);
	// }
}
