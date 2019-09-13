package com.fixme;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class Market {

    public void startClient() throws IOException, InterruptedException {

        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 9093);
        SocketChannel client = SocketChannel.open(hostAddress);

        System.out.println("Client... started");

        String threadName = Thread.currentThread().getName();

        // Send messages to server
        String[] messages = new String[] { threadName + ": msg1", threadName + ": msg2", threadName + ": msg3" };

        for (int i = 0; i < messages.length; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(74);
            buffer.put(messages[i].getBytes());
            buffer.flip();
            client.write(buffer);
            System.out.println(messages[i]);
            buffer.clear();
            Thread.sleep(5000);
        }
        client.close();
    }

    public static void main(String[] args) {
        Runnable client = new Runnable() {
            @Override
            public void run() {
                try {
                    new Market().startClient();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        new Thread(client, "client-A").start();
        new Thread(client, "client-B").start();
    }

    // public static void main(String[] args) throws IOException,
    // InterruptedException {

    // InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", 5000);
    // SocketChannel crunchifyClient = SocketChannel.open(crunchifyAddr);

    // log("Connecting to Server on port 5000...");

    // ArrayList<String> companyDetails = new ArrayList<String>();

    // // create a ArrayList with companyName list
    // companyDetails.add("Facebook");
    // companyDetails.add("Twitter");
    // companyDetails.add("IBM");
    // companyDetails.add("Google");
    // companyDetails.add("Crunchify");

    // for (String companyName : companyDetails) {

    // byte[] message = new String(companyName).getBytes();
    // ByteBuffer buffer = ByteBuffer.wrap(message);
    // crunchifyClient.write(buffer);

    // log("sending: " + companyName);
    // buffer.clear();

    // // wait for 2 seconds before sending next message
    // Thread.sleep(2000);
    // }
    // crunchifyClient.close();
    // }

    // private static void log(String str) {
    // System.out.println(str);
    // }
}
