package com.rpi.webui.servlet;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Session controlling for LoginServlet in HomeAutoSys WebUI
 */
public class SessionAuth implements Runnable {
	/***
	 * <h1>Session authentication running thread</h1>
	 * This session thread can be only used by calling the function: launch()
	 */
	protected static SessionAuth sesthread;
	
	private static final Logger logger = LogManager.getLogger(SessionAuth.class.getName());

	private ActionListener clocking = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			checkTimedOutSession();
			timer.restart();
		}
	};
	private Timer timer = new Timer(10000, clocking);
	private boolean running = false;
	private List<String> sessions;
	/*
	 * Default Timeout as 1 Hour (3600000 ms)
	 */
	protected static final int DEFAULT_TIMEOUT = 3600000;
	/*
	A session code:
	
	<--Session Key--> # <-- username --> # <--MacAddr--> # <--RegisterTime--> # <--Timeout--> #
	
	e.g. aASDASD9j21oi # peter # DD-22-DD-FF-VV # 14424392343 # 360000 #
	
	*/
	protected SessionAuth(){
		setup();
	}
	
	private void setup(){
		logger.trace("SessionAuth: Setting up...");
		sessions = new ArrayList<String>();
		logger.info("SessionAuth: Success.");
	}
	
	protected String registerSession(String sessionkey, String username, String macaddr, String registertime, String timeout){
		logger.info("SessionAuth: Registering session at " + sessionkey + " at " + macaddr + "...");
		String[] ses;
		int i;
		for (i = 0; i < sessions.toArray().length; i++){
			ses = getSession(i);
			if (sessionkey.equals(ses[0]) || username.equals(ses[1]) || macaddr.equals(ses[2]) || registertime.equals(ses[3]) || timeout.equals(ses[4])){
				logger.info("SessionAuth: Session exists. Using: " + ses[0]);
				//Previous session still exist then return -2
				return (String) ses[0];
			}
		}
		//Write in session
		String data = sessionkey + "#" + username + "#" + macaddr + "#" + registertime + "#" + timeout + "#";
		try {
			sessions.add(data);
		} catch (Exception e){
			return "-1";
		}
		return sessionkey;
	}
	
	protected String registerSession(String[] data){
		return registerSession(data[0], data[1], data[2], data[3], data[4]);
	}
	
	protected String registerSession(String sessionkey, String username, String macaddr, int registertime, int timeout){
		return registerSession(sessionkey, username, macaddr, Integer.toString(registertime), Integer.toString(timeout));
	}
	
	protected String[] getSession(int index){
		Object[] ses = sessions.toArray();
		String sessioncode = (String) ses[index];
		String[] output = new String[5];
		int i;
		int tmp;
		
		if (sessioncode == null){
			return null;
		}
		for (i = 0; i < sessioncode.length(); i++){
			if (sessioncode.charAt(i) == '#'){
				break;
			}
		}
		output[0] = sessioncode.substring(0,i);
		tmp = i + 1;
		for (i++; i < sessioncode.length(); i++){
			if (sessioncode.charAt(i) == '#'){
				break;
			}
		}
		output[1] = sessioncode.substring(tmp,i);
		tmp = i + 1;
		for (i++; i < sessioncode.length(); i++){
			if (sessioncode.charAt(i) == '#'){
				break;
			}
		}
		output[2] = sessioncode.substring(tmp,i);
		tmp = i + 1;
		for (i++; i < sessioncode.length(); i++){
			if (sessioncode.charAt(i) == '#'){
				break;
			}
		}
		output[3] = sessioncode.substring(tmp,i);
		tmp = i + 1;
		for (i++; i < sessioncode.length(); i++){
			if (sessioncode.charAt(i) == '#'){
				break;
			}
		}
		output[4] = sessioncode.substring(tmp,i);
		return output;
	}
	
	@Override
	public void run() {
		start();
	}
	
	public static void launch(){
		sesthread = new SessionAuth();
		sesthread.start();
	}
	
	protected void checkTimedOutSession(){
		Calendar cal = Calendar.getInstance();
		Calendar regcal = Calendar.getInstance();
		Calendar endcal = Calendar.getInstance();
		Date regdate;
		int i;
		for (i = 0; i < sessions.size(); i++){
			String[] ses = getSession(i);
			if (ses == null){
				break;
			}
			long regtime = Long.parseLong(ses[3]);
			regdate = new Date(regtime);
			regcal.setTime(regdate);
			endcal.setTime(regdate);
			endcal.add(Calendar.MILLISECOND, Integer.parseInt(ses[4]));
			if (!(cal.before(endcal) && cal.after(regcal))){
				logger.info("SessionAuth: Session timeout on " + ses[0]);
				sessions.remove(i);
			}
		}
	}
	
	protected boolean unregisterSession(String sessionkey){
		String[] ses;
		int i;
		for (i = 0; i < sessions.size(); i++){
			ses = getSession(i);
			if (ses[i] == sessionkey){
				break;
			}
		}
		try {
			sessions.remove(i);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	protected boolean isSessionAvailable(String sessionkey){
		int i;
		String[] ses;
		for (i = 0; i < sessions.size(); i++){
			ses = getSession(i);
			if (ses[0].equals(sessionkey)){
				return true;
			}
		}
		return false;
	}
	
	protected int getSessionIndex(String sessionkey){
		int i;
		String[] ses;
		for (i = 0; i < sessions.toArray().length; i++){
			ses = getSession(i);
			if (ses[0].equals(sessionkey)){
				return i;
			}
		}
		return i;
	}
	
	protected void start(){
		if (!running){
			running = true;
			timer.start();
		}
	}
	
	protected void stop(){
		if (running){
			running = false;
			timer.stop();
		}
	}
	
	protected void restart(){
		stop();
		start();
	}
	
	protected boolean isRunning(){
		return running;
	}
}
