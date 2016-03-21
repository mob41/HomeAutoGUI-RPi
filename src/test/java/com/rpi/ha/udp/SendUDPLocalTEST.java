package com.rpi.ha.udp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendUDPLocalTEST {

	public static void main(String args[]) throws Exception
	   {
		try {
			BufferedReader inFromUser =
			         new BufferedReader(new InputStreamReader(System.in));
		      String host = "localhost";
		      int port = 6099;

		      byte[] message = inFromUser.readLine().getBytes();

		      // Get the internet address of the specified host
		      InetAddress address = InetAddress.getByName(host);

		      // Initialize a datagram packet with data and address
		      DatagramPacket packet = new DatagramPacket(message, message.length,
		          address, port);

		      // Create a datagram socket, send the packet through it, close it.
		      DatagramSocket dsocket = new DatagramSocket();
		      dsocket.send(packet);
		      dsocket.close();
		    } catch (Exception e) {
		      System.err.println(e);
		    }
	   }

}
