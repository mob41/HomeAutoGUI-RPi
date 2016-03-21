package org.mob41.pushbullet.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.json.JSONObject;

import com.rpi.webui.servlet.HashKey;
import com.rpi.ha.Conf;
import com.rpi.webui.servlet.ClassBridge;
import com.rpi.webui.servlet.SessionAuth;

public class PBServer{
	
	/*
	 A PB-Code:
	 
	 <-- Username --> # <-- entoken --> # <-- ensalt (Layer 2) --> #
	 */
	private static List<String> pbdata = new ArrayList<String>(50);
	private static final String salt = Conf.pushbullet_serversalt;
	
	public static void ready(){
		pbdata = new ArrayList<String>(50);
		loadFile();
	}
	
	public static void pushToAllUsers(String title, String desc) throws Exception{
		String[] pbdata;
		String detoken;
		String accesstoken;
		for (int i = 0; i < getSize(); i++){
			pbdata = PBServer.getPBCode(i);
			detoken = ClassBridge.decrypt(pbdata[1], pbdata[2]);
			accesstoken = PBServer.deentok(detoken);
			PBClient.pushNote(title, desc, accesstoken);
		}
	}
	
	public static int getSize(){
		return pbdata.size();
	}
	
	public static int getCodeIndex(String username){
		String[] data;
		int i;
		for (i = 0; i < pbdata.toArray().length; i++){
			data = getPBCode(i);
			if (data[0].equals(username)){
				return i;
			}
		}
		return -1;
	}
	
	public static int getMaskIndex(String maskedpbcode){
		String mask;
		String[] pbcodearr;
		int i;
		for (i = 0; i < pbdata.toArray().length; i++){
			pbcodearr = getPBCode(i);
			mask = "******" + pbcodearr[1].substring(5, 10) + "******";
			if (mask.equals(maskedpbcode)){
				return i;
			}
		}
		return -1;
	}
	
	public static int amountOfPBCode(String username){
		int amount = 0;
		int i;
		String[] pbcode;
		for (i = 0; i < pbdata.toArray().length; i++){
			pbcode = getPBCode(i);
			if (pbcode[0].equals(username)){
				amount++;
			}
		}
		return amount;
	}
	
	public static String[] getUserPB(String username){
		int amount = amountOfPBCode(username);
		if (amount <= 0){
			return null;
		}
		String[] data;
		String[] output = new String[amount];
		int i;
		int j = 0;
		for (i = 0; i < pbdata.toArray().length; i++){
			data = getPBCode(i);
			if (data[0].equals(username)){
				output[j] = pbdata.get(i);
				j++;
			}
		}
		return output;
	}
	
	public static boolean removeByMask(String maskedpbcode){
		int index = getMaskIndex(maskedpbcode);
		try {
			pbdata.remove(index);
			writeIn();
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	public static boolean register(String username, String access_token){
		try {
			if (isAccessTokenExist(access_token)){
				return false;
			}
			String build = buildCode(username, access_token);
			pbdata.add(build);
			writeIn();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isAccessTokenExist(String accesstoken){
		try {
			int i;
			String[] pbcode;
			for (i = 0; i < pbdata.toArray().length; i++){
				pbcode = getPBCode(i);
				String encode = pbcode[1];
				String decode = ClassBridge.decrypt(encode, pbcode[2]);
				String accesscode = PBServer.deentok(decode);
				if (accesscode.equals(accesstoken)){
					return true;
				}
			}
			return false;
		} catch (Exception e){
			return false;
		}
	}
	
	public static boolean isEncryptedTokenExist(String entoken){
		int i;
		String[] pbcode;
		for (i = 0; i < pbdata.toArray().length; i++){
			pbcode = getPBCode(i);
			String encode = pbcode[1];
			if (encode.equals(entoken)){
				return true;
			}
		}
		return false;
	}
	
	public static String buildCode(String username, String access_token) throws Exception{
		String entoken = ClassBridge.encrypt(access_token, salt);
		String salt2 = ClassBridge.getRandomSalt();
		String enentoken = ClassBridge.encrypt(entoken, salt2);
		String build = username + "#" + enentoken + "#" + salt2  + "#";
		return build;
	}
	
	public static String deentok(String entoken) throws Exception{
		String detoken = ClassBridge.decrypt(entoken, salt);
		return detoken;
	}
	
	public static boolean registerViaSessionKey(String sessionkey, String access_token){
		String username = convertSessionKeyToUsername(sessionkey);
		return register(username, access_token);
	}
	
	public static String convertSessionKeyToUsername(String sessionkey){
		return ClassBridge.convertSessionKeyToUsername(sessionkey);
	}
	
	public static String[] getPBCode(int index){
		String pbcode = pbdata.get(index);
		String[] output = new String[3];
		int i;
		int tmp;
		for (i = 0; i < pbcode.length(); i++){
			if (pbcode.charAt(i) == '#'){
				break;
			}
		}
		output[0] = pbcode.substring(0, i);
		tmp = i + 1;
		for (i++; i < pbcode.length(); i++){
			if (pbcode.charAt(i) == '#'){
				break;
			}
		}
		output[1] = pbcode.substring(tmp, i);
		tmp = i + 1;
		for (i++; i < pbcode.length(); i++){
			if (pbcode.charAt(i) == '#'){
				break;
			}
		}
		output[2] = pbcode.substring(tmp, i);
		return output;
	}
	
	private static void loadFile(){
		try {
			File file = new File("ha_pbsave.properties");
			if (!file.exists()){
				createFile();
				return;
			}
			Properties prop = new Properties();
			FileInputStream in = new FileInputStream(file);
			prop.load(in);
			String data;
			int pbs = Integer.parseInt(prop.getProperty("pbs"));
			for (int i = 0; i < pbs; i++){
				data = prop.getProperty("pb" + i);
				pbdata.add(data);
			}
			in.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void writeIn(){
		try {
			File file = new File("ha_pbsave.properties");
			if (!file.exists()){
				createFile();
				return;
			}
			Properties prop = new Properties();
			Object[] data = pbdata.toArray();
			int pbs = data.length;
			prop.setProperty("pbs", Integer.toString(pbs));
			for (int i = 0; i < pbs; i++){
				prop.setProperty("pb" + i, (String) data[i]);
			}
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "PBSave");
			out.flush();
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void createFile(){
		try {
			File file = new File("ha_pbsave.properties");
			if (!file.exists()){
				file.createNewFile();
			}
			Properties prop = new Properties();
			prop.setProperty("pbs", "0");
			FileOutputStream out = new FileOutputStream(file);
			prop.store(out, "PBSave");
			out.flush();
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
