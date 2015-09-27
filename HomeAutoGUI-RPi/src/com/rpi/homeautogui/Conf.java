package com.rpi.homeautogui;

import javax.swing.JFrame;

public class Conf {
/*
 * Default Config for GUI
 *
 * Read Config: String moduleIP = #com.rpi.homeautogui.#Config.module1ip
 * Edit Config: #com.rpi.homeautogui.Config.#module1ip = 1.1.1.1;
 * 
 * ## - No need to add Package if you are in the same package.
 */
	
	//  Settings for Arduino ESP8266 (HomeAutoModule_Prg.ino)
	//
	//  Remember to upload the prg to Arduino to use these
	//  Socket Connections.
	public static boolean module1 = true; //Activiate Module 1's connection
	public static String module1ip = "192.168.168.124"; //Target IP
	public static int module1port = 7979; //Target Port (Don't Change This, usually it will break ESP8266 connection)
		
	//DO NOT TOUCH THIS. Not Implemented!
	public static boolean module2 = false; //Activiate Module 2's connection
	public static String module2ip = ""; //Target IP
	public static int module2port = 7979; //Target Port (Don't Change This, usually it will break ESP8266 connection)
	
	//  Settings for windows
	//
	//  To make unfullscreen, set windowUndecorated to false.
	//  And windowExtended to JFrame.NORMAL
	//TODO Future. Place the window fullscreen config here.
	public static boolean windowUndecorated = true; //Undecorated. Window with No Border
	public static int windowExtended = JFrame.MAXIMIZED_BOTH; //JFrame.MAXIMIZED_BOTH. Maximized both Vert. and Hert.
	
	//  WARNING: DO NOT TOUCH THIS!!!
	//  You might break the read Config file function!
	//
	//java.io read from file Function. NOT IMPLEMENTED!
	//import java.io
	//public static void readConf(){
	//
	//}
	
	//Device Name. MUST BE FINAL
	public static final String DeviceName = "HomeAutoTSGUI-RPi";
}
