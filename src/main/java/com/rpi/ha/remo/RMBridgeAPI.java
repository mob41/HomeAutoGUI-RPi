package com.rpi.ha.remo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import com.rpi.ha.Conf;

public class RMBridgeAPI {
	public static InputStream postData(String url, JSONObject data) throws IOException{
		URLConnection connection = new URL(url).openConnection();
		connection.setDoOutput(true); // Triggers POST.
		connection.setRequestProperty("Content-Type", "application/json");

		try (OutputStream output = connection.getOutputStream()) {
		    output.write(data.toString().getBytes("utf-8"));
		}
		return connection.getInputStream();
	}
	
	public static JSONObject rmbridge_getCode(String mac) throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1003);
		rmcmd.put("command", "get_code");
		rmcmd.put("mac", mac);
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}
	
	public static JSONObject rmbridge_sendCode(String mac, String data) throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1004);
		rmcmd.put("command", "send_code");
		rmcmd.put("mac", mac);
		rmcmd.put("data", data);
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}
	
	public static JSONObject rmbridge_learnCode(String mac) throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1002);
		rmcmd.put("command", "learn_code");
		rmcmd.put("mac", mac);
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}
	
	public static JSONObject rmbridge_getDevices() throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1000);
		rmcmd.put("command", "registered_devices");
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}
	
	public static JSONObject rmbridge_probeDevices() throws IOException{
		JSONObject rmcmd = new JSONObject();
		rmcmd.put("api_id", 1001);
		rmcmd.put("command", "probe_devices");
		return new JSONObject(getStringFromInputStream(postData(Conf.rmbridge_url, rmcmd)));
	}

	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
}
