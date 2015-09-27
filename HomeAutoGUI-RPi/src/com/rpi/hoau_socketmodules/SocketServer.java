package com.rpi.hoau_socketmodules;
/*
 *  HomeAuto Socket Modules for ModuleToModule Chatting
 *  Package "com.rpi.hoau_socketmodules"
 *  
 *  Socket Server ---- to let Modules ESP8266/Arduino to report info
 * 
 *  Programmed by Anthony Law. Do Not Copy without permission.
 *  Copyright(c)2015 ANTHONY LAW
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketServer {
	public static int switchStatus1 = 0;
	public static int switchStatus2 = 0;
	public static boolean notend = true;
	public static int timer = 0;
  
	/* TODO WARNING: These code hasn't edited for implementing to
	 * the main program. These code only has tested on the Arduino/
	 * ESP8266. But not edited for system purpose. Please do edit
	 * this, MR. ANTHONY LAW!
	 */
	public static void main() {
	new Thread(){
		public void run(){
			try {
			      int port = 7979;
			      System.out.println("TCPSRV: Set to listen on port " + port);
			      @SuppressWarnings("resource")
				ServerSocket ss = new ServerSocket(port);
			      System.out.println("TCPSRV: Creating new Socket Object...");
			      for (;;) {
			    	System.out.println("TCPSRV: Waiting for client to connect...");
			        Socket client = ss.accept();
			        System.out.println("TCPSRV: Client: " + client.getInetAddress());
			        System.out.println("TCPSRV: Connected! Waiting for input streams...");
			        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			        PrintWriter out = new PrintWriter(client.getOutputStream());
			        String line;
			        String savedLine = "";
			        new Thread(){
			        	public void run(){
			        		while (notend)
			        		{
			        			timer++;
			        			System.out.println("TCPSRV: Timeout 10 sec ends in: " + timer);
			            		try {
			    					Thread.sleep(1000);
			    				} catch (InterruptedException e) {
			    					e.printStackTrace();
			    				}
			        		}
			        	}
			        }.start();
			        while ((line = in.readLine()) != null){
			          if (line.equals("endLine")){
			        	  System.out.println("TCPSRV: Received to end string: " + line);
			        	  break;
			          }
			          if (line.length() == 0)
			          {
			        	  break;
			          }
			          if (timer >= 10)
			          {
			        	  notend = false;
			        	  break;
			          }
			          savedLine = savedLine + line;
			          System.out.println("TCPSRV: Received: " + line);
			        }
			        notend = false;
			        switch (savedLine){
			        case "postSwitchStatus0011":
			        	System.out.println("TCPSRV: Reported Switch Status from 1 for 1 status (ON)");
			        	switchStatus1 = 1;
			        	break;
			        case "postSwitchStatus0010":
			        	System.out.println("TCPSRV: Reported Switch Status from 1 for 0 status (OFF)");
			        	switchStatus1 = 0;
			        	break;
			        }
			        System.out.println("TCPSRV: Recevied: " + line);
			        System.out.println("TCPSRV: Connection closed.");
			        out.close();
			        in.close();
			        client.close();
			      } 
			    }
			    catch (Exception e) {
			      System.err.println("TCPSRV: Exception: " + e);
			      System.err.println("TCPSRV: Error occurred! Check log for more details!");
			    }
		}
	}.start();
    
  }
}