package com.rpi.hoau_socketmodules;
/*
 *  HomeAuto Socket Modules for ModuleToModule Chatting
 *  Package "com.rpi.hoau_socketmodules"
 *  
 *  Socket Connector ---- to connect to Module ESP8266/Arduino
 * 
 *  Programmed by Anthony Law. Do Not Copy without permission.
 *  Copyright(c)2015 ANTHONY LAW
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.rpi.homeautogui.Conf;

public class SocketClient {
	static String sentence;
	static String serverReply;
	public static String client(String text, String ip, int port, int Connect_Timeout){
		try{
			/*
			 * Some codes are from:
			 * https://systembash.com/a-simple-java-tcp-server-and-tcp-client/
			 * http://stackoverflow.com/questions/32273886/sending-data-to-esp8266-wi-fi-chip-from-android-device
			 */
			new BufferedReader( new InputStreamReader(System.in));
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), Connect_Timeout);
			DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
			//TODO Not implemented the BothSide Message Test Connection
			//BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			sentence = text;
			outToServer.writeBytes("\n" + sentence + '\n');
			Thread.sleep(1500);
			//TODO Not implemented the BothSide Message Test Connection
			//serverReply = inFromServer.readLine();
			serverReply = "connectionok";
			socket.close();
		}
		catch (Exception e){
			//Simply kicked out if exception (Can't connect/Offline)
			System.out.print("SOCKCLIENT: Could not connect to target " + ip + ":" + port + "\n");
			//Return String "error"
			return "error";
		}
		return serverReply;
	}
	
	public static void getStatus(){
		String defaultGetMsg = "getSwitchStatus"; //Default GET message. DO NOT CHANGE because it will ABSOLUTELY break the connection.
		int defaultCntTimeOut = 1000; //Default Connect Timeout. (1000)
		
		if (Conf.module1){client(defaultGetMsg, Conf.module1ip, Conf.module1port, defaultCntTimeOut);}
		if (Conf.module2){client(defaultGetMsg, Conf.module2ip,Conf.module2port, defaultCntTimeOut);}
	}
}
