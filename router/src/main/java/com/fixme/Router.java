package com.fixme;

import com.fixme.RouterServer;
import com.fixme.controlers.TimeMessage;

public class Router {

	public static void main(String[] args) {

		TimeMessage.print("Hello");
		RouterServer rServer = new RouterServer();
		rServer.newRouterServer();
	}

}
