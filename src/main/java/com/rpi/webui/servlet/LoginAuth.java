package com.rpi.webui.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/***
 * <h1>Login Authentication System (Module)</h1>
 * <br>
 * A HashLogin system to be used for security.
 * @author Anthony Law
 *
 */
public class LoginAuth {
	private static List<String> usrpwd;
	private static final Logger logger = LogManager.getLogger(LoginAuth.class.getName());
	
	protected static boolean authClearType(String usr, String pwd){
		int i;
		Object[] urpd = usrpwd.toArray();
		logger.info("Authenticating user...");
		String[] urpdkey;
		String pwdhash;
		for (i = 0; i < urpd.length; i++){
			urpdkey = getUsrPwdKey(i);
			try {
				pwdhash = HashKey.encrypt(pwd, urpdkey[0]);
			} catch (Exception e){
				logger.error("The remote hash is not parsable.");
				break;
			}
			if (urpdkey[1].equals(usr) && urpdkey[2].equals(pwdhash)){
				logger.info("The user is authenticated successfully.");
				return true;
			}
		}
		logger.error("Wrong password / Error occurred.");
		return false;
	}
	
	/*
	 * <--- salt ----> # <--- Username ---> # <--- Hash ---> #
	 * 
	 */
	
	protected static String[] getUsrPwdKey(int index){
		String urpd = (String) usrpwd.toArray()[index];
		String[] output = new String[3];
		int i;
		int tmp;
		if (urpd == null){
			return null;
		}
		for (i = 0; i < urpd.length(); i++){
			if (urpd.charAt(i) == '#'){
				break;
			}
		}
		output[0] = urpd.substring(0,i);
		tmp = i + 1;
		for (i++; i < urpd.length(); i++){
			if (urpd.charAt(i) == '#'){
				break;
			}
		}
		output[1] = urpd.substring(tmp,i);
		tmp = i + 1;
		for (i++; i < urpd.length(); i++){
			if (urpd.charAt(i) == '#'){
				break;
			}
		}
		output[2] = urpd.substring(tmp,i);
		return output;
	}
	
	public static void load(){
		try {
			File file = new File("secret.hap");
			if (!file.exists()){
				create();
				return;
			}
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(file);
			prop.load(in);
			int users = Integer.parseInt(prop.getProperty("users"));
			usrpwd = new ArrayList<String>();
			String key;
			for (int i = 0; i < users; i++){
				key = "hap" + i;
				usrpwd.add(prop.getProperty(key));
			}
			in.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	private static void create(){
		try {
			File file = new File("secret.hap");
			if (!file.exists()){
				file.createNewFile();
			}
			Properties prop = new Properties();
			prop.setProperty("users", "0");
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HAP");
			out.flush();
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private static void writein(){
		try {
			Object[] data = usrpwd.toArray();
			File file = new File("secret.hap");
			if (file.exists()){
				file.delete();
			}
			Properties prop = new Properties();
			int amount = data.length;
			int i;
			String key;
			prop.setProperty("users", Integer.toString(amount));
			for (i = 0; i < amount; i++){
				key = "hap" + i;
				prop.setProperty(key, (String) data[i]);
			}
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "HAP");
			out.flush();
			out.close();
		} catch (IOException e){
			
		}
	}
	
	/***
	 * Launch this function at startup.
	 * Or a NullPointerException will occur.
	 */
	public static void prepare(){
		logger.trace("Preparing for authentication...");
		usrpwd = new ArrayList<String>();
		logger.info("Done.");
	}
	
	protected static int getUserCount(){
		return usrpwd.size();
	}
	
	protected static void removeUser(String username){
		int index = getUserListIndex(username);
		usrpwd.remove(index);
	}
	
	protected static void removeUser(int index){
		usrpwd.remove(index);
	}
	
	protected static int getUserListIndex(String username){
		Object[] up = usrpwd.toArray();
		int i;
		String[] upl;
		for (i = 0; i < up.length; i++){
			upl = getUsrPwdKey(i);
			if (upl[1].equals(username)){
				return i;
			}
		}
		return -1;
	}
	
	protected static void importClearTypeUsrPwd(String cleartypeusr, String cleartypepwd){
		try {
			String salt = HashKey.getRandomSalt();
			String pwdhash = HashKey.encrypt(cleartypepwd, salt);
			String build = salt + "#" + cleartypeusr + "#" + pwdhash + "#";
			usrpwd.add(build);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		writein();
	}
	
	protected static void importHashedUsrPwd(String salt, String usrhash, String pwdhash){
		try {
			String build = salt + "#" + usrhash + "#" + pwdhash + "#";
			usrpwd.add(build);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
