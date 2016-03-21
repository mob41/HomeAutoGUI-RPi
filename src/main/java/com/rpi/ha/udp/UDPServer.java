package com.rpi.ha.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mob41.pushbullet.api.PBServer;

import com.rpi.ha.ui.UI;

public class UDPServer implements Runnable {

	private static final Logger logger = LogManager.getLogger("UDPServer");
	
	private static Thread thread;
	
	private static UDPServer runnable;
	
	private boolean end = false;
	
	public static Thread getThread(){
		return thread;
	}
	
	public static UDPServer getRunnable(){
		return runnable;
	}
	
	public static void startThread(){
		runnable = new UDPServer();
		thread = new Thread(runnable);
		thread.setName("HA_UDPServer");
		thread.start();
	}
	
	public boolean isRunning(){
		return !end;
	}
	
	public void turnon(){
		run();
	}
	
	public void restart(){
		shutdown();
		turnon();
	}
	
	public void shutdown(){
		if (!end){
			end = false;
		}
	}
	
	@Override
	public void run(){
		if (!end){
			end = false;
			try {
				int port = 6099;
				DatagramSocket dsocket = new DatagramSocket(port);
			    byte[] buffer = new byte[2048];
			    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			    
			    while (!end){
			    	dsocket.receive(packet);
			    	String msg = new String(buffer, 0 , packet.getLength());
			    	logger.info("Recevied UDP Packet from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + "): " + msg);
			    	switch (msg){
			    	case "bellevent":
			    		logger.info("Reported Bell Event from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + ")");
			    		logger.info("Pushing note to all pushbullet users...");
			    		PBServer.pushToAllUsers("Door bell is rang!", "Go and see who's there!");
			    		logger.info("Pushed!");
			    		break;
			    	default:
			    		logger.info("Unknown operation from " + packet.getAddress().getHostName() + " (" + packet.getAddress().getHostAddress() + "): " + msg);
			    	}
			    	packet.setLength(buffer.length);
			    }
			    
			    dsocket.close();
			} catch (Exception e){
				logger.error(e);
			}
		}
	}
}
