package com.rpi.ha.scene;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.swing.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rpi.ha.Conf;
import com.rpi.ha.remo.BLRemote;
import com.rpi.ha.remo.RMBridgeAPI;
import com.rpi.ha.ui.BlankScreensaver;
import com.rpi.ha.ui.UI;

public class SceneThread implements Runnable{
	
	private static final Logger logger = LogManager.getLogger(SceneThread.class.getName());
	
	private static boolean isRunning = false;
	
	private Timer clockingTimer;
	
	private void doAction(List<String[]> actionsdata, String[] scenesdata){
		String[] actionvaluecode;
		System.out.println("Running action!");
		Object[] data;
		for (int i = 0; i < actionsdata.size(); i++){
			data = actionsdata.get(i);
			switch ((String) data[2]){
			case "stop-scrnsave":
				logger.info("Stop Scrn Saving");
				UI.window.timedNoScreenSaver = true;
				if (BlankScreensaver.screensaver){
					try {
						BlankScreensaver.frame.dispose();
					} catch (NullPointerException ignore) {}
				}
				break;
			case "sendbutton":
				actionvaluecode = SceneSave.seperateActionValueCode((String) data[3]);
				int buttonindex = BLRemote.getButtonIndex(actionvaluecode[0], actionvaluecode[1]);
				String[] code = BLRemote.buttons.get(buttonindex);
				switch(Conf.rmcontrol_usingMethod){
				default:
				case "rm-bridge":
					try {
						RMBridgeAPI.rmbridge_sendCode(actionvaluecode[2], code[2]);
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case "sdk":
					//TODO Wait for SDK~!!!! Broadlink is too slow
					logger.info("No Broadlink DNA-Kit SDK exist.");
					break;
				}
				break;
			case "delay":
				int delay = 500;
				try {
					delay = Integer.parseInt((String) data[3]);
				} catch (NumberFormatException e){
					delay = 500;
				}
				if (delay > 20000 || delay < 0){
					delay = 500;
				}
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	public void activateScene(String uid){
		String[] scenesdata = SceneSave.getScene(uid);
		List<String[]> actionsdata = SceneSave.getAllActions(uid);
		doAction(actionsdata, scenesdata);
	}
	
	private ActionListener clocking = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			UI.window.scenetablemodel.setRowCount(0);
			int numOfScenes = SceneSave.getAllScenesAmount();
			for (int i = 0; i < numOfScenes; i++){
				UI.window.scenetablemodel.addRow(SceneSave.convertSaveDataIntoTableData(SceneSave.getScene(i)));
			}
			List<String[]> actionsdata;
			String[] triggersdata;
			String[] scenesdata;
			for (int i = 0; i < triggers; i++){
				triggersdata = SceneSave.getTrigger(i);
				scenesdata = SceneSave.getScene(triggersdata[1]);
				actionsdata = SceneSave.getAllActions(scenesdata[1]);
				switch (triggersdata[0]){
				case "time-period":
					if (isNowInTimePeriod(triggersdata[2])){
						doAction(actionsdata, scenesdata);
					}
					break;
				case "spec-time":
					System.out.println("Checking spec time..");
					if (isNowSpecTime(triggersdata[2])){
						System.out.println("Is spec time!");
						if (scenesdata[2].equals("false")){
							System.out.println("Scene isn't running!" + scenesdata[2]);
							scenesdata[2] = "true";
							SceneSave.setSceneNewData(scenesdata[1], scenesdata);
							doAction(actionsdata, scenesdata);
						}
						else
						{
							System.out.println("Scene is running. Cancelled.");
						}
					}
					else {
						System.out.println("Not spec time.");
						if (!scenesdata[2].equals("false")){
							System.out.println("Memory didn't show not spec time!" + scenesdata[2]);
							scenesdata[2] = "false";
							SceneSave.setSceneNewData(scenesdata[1], scenesdata);
							System.out.println("Set! " + scenesdata[2]);
						}
					}
					break;
				}
			}
			/*
			for (int i = 0; i < scenes; i++){
				triggersdata = SceneSaveReader.getTrigger(i);
				boolean onTrigger = false;
				switch(trigger[i]){
				//TODO Add more triggers
				default:
				case 0:
					onTrigger = isNowSceneTime(i);
					break;
				}
				if (onTrigger){
					switch(action[i]){
					//TODO Add more actions
					default:
					case 0:
						UI.window.timedNoScreenSaver = true;
						if (BlankScreensaver.screensaver){
							try {
								BlankScreensaver.frame.dispose();
							} catch (NullPointerException ignore) {}
						}
					}
				}
				else
				{
					switch(action[i]){
					//TODO Add more actions
					default:
					case 0:
						UI.window.timedNoScreenSaver = false;		
					}
				}
			}
			*/
		}
		
	};
	
	private int scenes;
	
	private int triggers;
	
	public SceneThread(){
		setup();
	}
	
	public void setup(){
		scenes = SceneSave.getAllScenesAmount();
		triggers = SceneSave.getAllTriggersAmount();
		if (scenes == 0){
			return;
		}
	}

	@Override
	public void run() {
		if (!isRunning){
			isRunning = true;
			logger.info("SceneRun is running.");
			clockingTimer = new Timer(5000, clocking);
			if (scenes == 0 || scenes == -1){
				logger.warn("No scenes defined. The thread is shutting down.");
				stop();
				return;
			}
			else
			{
				logger.info("There are " + scenes + " activated.");
			}
			clockingTimer.start();
		}
	}
	
	public void stop(){
		if (isRunning){
			isRunning = false;
			logger.warn("SceneRun is stopped.");
			clockingTimer.stop();
		}
	}
	
	public void restart(){
		logger.info("SceneRun is restarting.");
		stop();
		setup();
		run();
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public static boolean isNowInTimePeriod(String timeperiod_during){
		Calendar cal = Calendar.getInstance();
		Calendar sttime = Calendar.getInstance();
		Calendar edtime = Calendar.getInstance();
		String during = "";
		int[] time;
		boolean afst; //AFter STart time
		boolean bfed; //BeFore EnD time
		during = timeperiod_during;
		time = SceneSave.transDuringIntoTime(during);
		sttime.set(Calendar.HOUR_OF_DAY, time[0]);
		sttime.set(Calendar.MINUTE, time[1]);
		edtime.set(Calendar.HOUR_OF_DAY, time[2]);
		edtime.set(Calendar.MINUTE, time[3]);
		afst = cal.after(sttime);
		bfed = cal.before(edtime);
		return afst && bfed;
	}
	
	public static boolean isNowSpecTime(String spectime_during){
		Calendar cal = Calendar.getInstance();
		Calendar tartime = Calendar.getInstance();
		String during = "";
		int[] time;
		during = spectime_during;
		time = SceneSave.transSpecTimeDuringIntoTime(during);
		tartime.set(Calendar.HOUR_OF_DAY, time[0]);
		tartime.set(Calendar.MINUTE, time[1]);
		return cal.get(Calendar.HOUR_OF_DAY) == time[0] && cal.get(Calendar.MINUTE) == time[1];
	}
	
	/*
	public static boolean isNowAtTime(String spectimeduring){
		Calendar cal = Calendar.getInstance();
		Calendar sttime = Calendar.getInstance();
		Calendar edtime = Calendar.getInstance();
		String during = "";
		int[] time;
		boolean afst; //AFter STart time
		boolean bfed; //BeFore EnD time
		during = timeperiod_during;
		time = SceneSaveReader.transDuringIntoTime(during);
		sttime.set(Calendar.HOUR_OF_DAY, time[0]);
		sttime.set(Calendar.MINUTE, time[1]);
		edtime.set(Calendar.HOUR_OF_DAY, time[2]);
		edtime.set(Calendar.MINUTE, time[3]);
		afst = cal.after(sttime);
		bfed = cal.before(edtime);
		return afst && bfed;
	}
	*/
}
