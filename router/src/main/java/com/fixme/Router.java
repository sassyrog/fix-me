package com.fixme;

import com.fixme.RouterServer;
import com.fixme.controlers.TimeMessage;

// import com.fixme.controlers.MysqlConnect;

/**
 * Hello world!
 *
 */
public class Router {

	public static void main(String[] args) {
		TimeMessage.print("Hello");
		// MysqlConnect conn = MysqlConnect.getDbCon();
		RouterServer rServer = new RouterServer();
		rServer.newRouterServer();
	}

}
