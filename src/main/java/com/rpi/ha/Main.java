package com.rpi.ha;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mob41.pushbullet.api.PBServer;

import com.rpi.ha.ann.AnnounceThread;
import com.rpi.ha.err.ErrorHandler;
import com.rpi.ha.jettysrv.JettyServ;
import com.rpi.ha.remo.BLRemote;
import com.rpi.ha.scene.SceneSave;
import com.rpi.ha.udp.UDPServer;
import com.rpi.ha.ui.UI;
import com.rpi.ha.widget.KmbApi;
import com.rpi.ha.widget.TimeFormatting;
import com.rpi.webui.servlet.LoginAuth;
import com.rpi.webui.servlet.NotifySchedule;
import com.rpi.webui.servlet.SessionAuth;

public class Main {
	
	private static final Logger logger = LogManager.getLogger(Main.class.getName());

	public static void main(String[] args) throws Exception {
		try {
			Runtime.getRuntime().exec("feh --bg-center /home/pi/status/10-starting-errorhandler.fw.png");
		} catch (Exception ignore){}
		//Start ErrorHandler
		ErrorHandler.launch();
		try {
			Runtime.getRuntime().exec("feh --bg-center /home/pi/status/30-starting-gui.fw.png");
		} catch (Exception ignore){}
		UI.main();
		
		Conf.startTime = TimeFormatting.getLoggingTime();
		
		
		if (Conf.logging){
			//Print Description
			System.out.println("=========================================");
			System.out.println("Home Automation Graphics User Interface Program");
			System.out.println("Designed for Raspberry Pi TFT Touchscreen 480x320");
			System.out.println("");
			System.out.println("Programmed by Anthony Law.");
			System.out.println("");
			System.out.println("Copyright (c) 2015 Anthony Law. DO NOT COPY.");
			System.out.println("=========================================");
		}
		
		File latestLog = new File("halog-latest.log");
		if(latestLog.exists()){
			latestLog.delete();
		}
		logger.trace("Reading settings...");
		Conf.readConf();
		
		logger.trace("System is starting...");
		try {
			Runtime.getRuntime().exec("feh --bg-center /home/pi/status/50-starting-apis.fw.png");
		} catch (Exception ignore){}
		//Load scenes
		SceneSave.load();
		//Load Remotes
		BLRemote.load();
		//Load KMB database
		System.out.println(Boolean.toString(KmbApi.loadDatabase(false)));
		//Start API - Session Authentication
		SessionAuth.launch();
		//Start API - Login Authentication
		LoginAuth.prepare();
		LoginAuth.load();
		//Start API - Notification Schedule
		NotifySchedule.launch();
		//Start API - PBServer
		PBServer.ready();
		//Start API - WebUI
		try {
			Runtime.getRuntime().exec("feh --bg-center /home/pi/status/80-starting-jetty.fw.png");
		} catch (Exception ignore){}
		new Thread(){
			public void run(){
				try {
					JettyServ.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		logger.trace("Starting UDP Communication server...");
		UDPServer.startThread();
		logger.trace("Starting Announcement control...");
		AnnounceThread.startThread();
		Thread.sleep(1000);
		try {
			Runtime.getRuntime().exec("feh --bg-center /home/pi/status/95-finalizing.fw.png");
		} catch (Exception ignore){}
		Thread.sleep(4000);
		try {
			Runtime.getRuntime().exec("feh --bg-center /home/pi/status/normal.fw.png");
		} catch (Exception ignore){}
	}

}
