package com.fixme;

import java.io.IOException;

import com.fixme.RouterServer;
import com.fixme.controlers.TimeMessage;

// import com.fixme.controlers.MysqlConnect;

/**
 * Hello world!
 *
 */
public class Router {

	public static void main(String[] args) {

		try {
			TimeMessage.print("Hello");
			// MysqlConnect conn = MysqlConnect.getDbCon();
			RouterServer rServer = new RouterServer();
			rServer.newRouterServer();
			rServer.broadcast("Something funny");

		} catch (IOException e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
