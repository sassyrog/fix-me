package com.fixme;

import com.fixme.controlers.RouterServer;

// import com.fixme.controlers.MysqlConnect;

/**
 * Hello world!
 *
 */
public class Router {

	public static void main(String[] args) {
		// MysqlConnect conn = MysqlConnect.getDbCon();
		RouterServer rServer = new RouterServer();
		rServer.newRouterServer();
	}

}
