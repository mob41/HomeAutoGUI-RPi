package org.mob41.pushbullet.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rpi.ha.Conf;

public class PBClient {
	private static final String apikey = Conf.pushbullet_apikey;
	
	private static final String tokenapiurl = "https://api.pushbullet.com/oauth2/token";
	private static final String deviceapiurl = "https://api.pushbullet.com/v2/devices";
	private static final String pushapiurl = "https://api.pushbullet.com/v2/pushes";

	public static final String client_id = Conf.pushbullet_clientid;
	private static final String client_secret = Conf.pushbullet_clientsecret;
	
	public static String convertCodeToAccessToken(String code) throws Exception{
		JSONObject json = new JSONObject();
		json.put("client_id", client_id);
		json.put("client_secret", client_secret);
		json.put("grant_type", "authorization_code");
		json.put("code", code);
		URLConnection connection = new URL(tokenapiurl).openConnection();
		connection.setDoOutput(true); // Triggers POST.
		connection.setRequestProperty("Access-Token", apikey);
		//application/x-www-form-urlencoded;charset=utf-8
		connection.setRequestProperty("Content-Type", "application/json");

		try (OutputStream output = connection.getOutputStream()) {
		    output.write(json.toString().getBytes("utf-8"));
		}
		InputStream response = connection.getInputStream();
		JSONObject jsonout = new JSONObject(getStringFromInputStream(response));
		return jsonout.getString("access_token");
	}
	
	public static JSONObject getUserDevices(String accesstoken) throws Exception{
		URLConnection connection = new URL(deviceapiurl).openConnection();
		connection.setRequestProperty("Access-Token", accesstoken);

		InputStream response = connection.getInputStream();
		JSONObject json = new JSONObject(getStringFromInputStream(response));
		return json;
	}
	
	public static JSONObject getPushes() throws Exception{
		URLConnection connection = new URL(pushapiurl).openConnection();
		connection.setRequestProperty("Access-Token", apikey);

		InputStream response = connection.getInputStream();
		JSONObject json = new JSONObject(getStringFromInputStream(response));
		return json;
	}
	
	public static JSONObject pushNote(String title, String body) throws Exception{
		return pushNote(title, body, null, apikey);
	}
	
	public static JSONObject pushNote(String title, String body, String access_token) throws Exception{
		return pushNote(title, body, null, access_token);
	}
	
	public static JSONObject pushNote(String title, String body, String device_ident, String access_token) throws Exception{
		URLConnection connection = new URL(pushapiurl).openConnection();
		connection.setRequestProperty("Access-Token", access_token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		JSONObject json = new JSONObject();
		if (device_ident != null){
			json.put("device_iden", device_ident);
		}
		json.put("title", title);
		json.put("body", body);
		json.put("type", "note");

		try (OutputStream output = connection.getOutputStream()) {
		    output.write(json.toString().getBytes("utf-8"));
		}
		InputStream response = connection.getInputStream();
		JSONObject responsejson = new JSONObject(getStringFromInputStream(response));
		return responsejson;
	}
	
	public static JSONObject pushLink(String title, String body, String url) throws Exception{
		return pushLink(title, body, url, null, apikey);
	}
	
	public static JSONObject pushLink(String title, String body, String url, String access_token) throws Exception{
		return pushLink(title, body, url, null, access_token);
	}
	
	public static JSONObject pushLink(String title, String body, String url, String device_ident, String access_token) throws Exception{
		URLConnection connection = new URL(pushapiurl).openConnection();
		connection.setRequestProperty("Access-Token", access_token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setDoOutput(true);
		JSONObject json = new JSONObject();
		if (device_ident != null){
			json.put("device_iden", device_ident);
		}
		json.put("url", url);
		json.put("title", title);
		json.put("body", body);
		json.put("type", "note");

		try (OutputStream output = connection.getOutputStream()) {
		    output.write(json.toString().getBytes("utf-8"));
		}
		InputStream response = connection.getInputStream();
		JSONObject responsejson = new JSONObject(getStringFromInputStream(response));
		return responsejson;
	}
	
	public static String getStringFromInputStream(InputStream is) {
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
