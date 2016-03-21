package com.rpi.ha.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InstallationReader {
	
	private static final Logger logger = LogManager.getLogger(InstallationReader.class.getName());

	public static int findNumberOfPlugin(){
		logger.info("Reading installation save...");
		File file = new File("Entertain-installer.properties");
		if(!file.exists()){
			logger.warn("No Installation save found.");
			return -1;
		}
		else
		{
			logger.info("Instalation save exists.");
			Properties prop = new Properties();
			try {
				FileInputStream input = new FileInputStream(file);
				prop.load(input);
				
				logger.info("Getting installations...");
				int installs = Integer.parseInt(prop.getProperty("installs"));
				logger.info("There are total: " + installs + " installs.");
				return installs;
				} catch (IOException e) {
					logger.error("Error occurred when reading the save!");
				e.printStackTrace();
				return 0;
			}
		}
	}
	
	public static String getPluginFileName(int row){
		logger.info("Reading installation save...");
		File file = new File("Entertain-installer.properties");
		if(!file.exists()){
			logger.warn("No installation save.");
			return null;
		}
		else
		{ 
			logger.info("Installation save exists");
			Properties prop = new Properties();
			try {
				FileInputStream input = new FileInputStream(file);
				prop.load(input);
				logger.info("Getting installations...");
				int installs = Integer.parseInt(prop.getProperty("installs"));
				logger.info("There is total of " + installs + " installs.");
				if (!(row <= installs)){
					return null;
				}
				logger.info("The target row is correct / possible");
				String key = "plugin-" + row + "-fileName";
				logger.info("Getting property...");
				return prop.getProperty(key);
			} catch (IOException e){
				logger.error("[InstReader] Error occurred when reading the save.");
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public static String getPluginName(int row){
		File file = new File("Entertain-installer.properties");
		if(!file.exists()){
			return null;
		}
		else
		{
			Properties prop = new Properties();
			try {
				FileInputStream input = new FileInputStream(file);
				prop.load(input);
				
				int installs = Integer.parseInt(prop.getProperty("installs"));
				
				if (!(row <= installs)){
					return null;
				}
				
				String key = "plugin-" + row + "-name";
				
				return prop.getProperty(key);
			} catch (IOException e){
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public static String getPluginLogo(int row){
		File file = new File("Entertain-installer.properties");
		if(!file.exists()){
			return null;
		}
		else
		{
			Properties prop = new Properties();
			try {
				FileInputStream input = new FileInputStream(file);
				prop.load(input);
				
				int installs = Integer.parseInt(prop.getProperty("installs"));
				
				if (!(row <= installs)){
					return null;
				}
				
				String key = "plugin-" + row + "-fileLogo";
				
				return prop.getProperty(key);
			} catch (IOException e){
				e.printStackTrace();
				return null;
			}
		}
	}
}
