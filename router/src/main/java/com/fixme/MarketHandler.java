package com.fixme;

import java.nio.channels.SocketChannel;

/**
 * MarketHandler
 */
public class MarketHandler {
	private String message;
	private SocketChannel sChannel;

	public MarketHandler(SocketChannel sChannel, String message) {
		this.message = message;
		this.sChannel = sChannel;
	}
}