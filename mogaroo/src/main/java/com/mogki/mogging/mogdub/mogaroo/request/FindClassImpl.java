package com.mogki.mogging.mogdub.mogaroo.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mogki.mogging.mogdub.mogaroo.Config;
import com.mogki.mogging.mogdub.mogaroo.ui.MainWindow;
import com.mogki.mogging.mogdub.mogaroo.validation.FindClassValidator;

public class FindClassImpl implements FindClass {
	private final FindClassParams m_params;
	
	public FindClassImpl(FindClassParams params) {
		m_params = params;
		
		if (!FindClassValidator.validateBundledInput(m_params)) {
			MainWindow.logMessage("FindClassValidator returned false for input " + m_params);
		}
	}
	
	private List<String> getLinesFromIs(InputStream is) throws IOException {
		List<String> lines = new ArrayList<String>();		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		String line = reader.readLine();
		
		while (line != null) {
			lines.add(line);
			line = reader.readLine();
		}
		
		reader.close();
		return lines;
	}
	
	private void login() throws Exception {
		if (FindClassValidator.validateBundledInput(m_params)) {
			MainWindow.logMessage("Logging in...");
			
			URL url = new URL(Config.LOGIN_HOST);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestProperty("User-Agent", Config.USER_AGENT);
			urlConn.setConnectTimeout(Config.HTTP_TIMEOUT);
			urlConn.setReadTimeout(Config.HTTP_TIMEOUT);
						
			List<String> lines = getLinesFromIs(urlConn.getInputStream());
			
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestProperty("User-Agent", Config.USER_AGENT);
			urlConn.setConnectTimeout(Config.HTTP_TIMEOUT);
			urlConn.setReadTimeout(Config.HTTP_TIMEOUT);
			
			//urlConn.setRequestProperty("pass", m_params.password);
			urlConn.setRequestProperty("Content-Type", Config.MULTIPART_POST);
			urlConn.setRequestMethod("POST");
			urlConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());

			writer.write("user=" + URLEncoder.encode(m_params.username, Config.ENCODING)
							+ "&pass=" + URLEncoder.encode(m_params.password, Config.ENCODING));
		    
			//MainWindow.logMessage("Setting user='" + m_params.username + "', pass='" + m_params.password + "'");
			
			// copied from the python script..
			for (String line : lines) {
				if (line.contains("input type=\"hidden\"")) {
					Pattern p = Pattern.compile(".*input type=\"hidden\" name=\"(.*)\" value=\"(.*)\".*", Pattern.CASE_INSENSITIVE);
					Matcher m = p.matcher(line);
										
					if (m.matches() && m.groupCount() == 2) {
						String key = m.group(1);
						String value = m.group(2);
						//urlConn.setRequestProperty(key, value);
						writer.write("&" + URLEncoder.encode(key, Config.ENCODING) + "=" + URLEncoder.encode(value, Config.ENCODING));
						MainWindow.logMessage("Setting '" + key + "' = '" + value + "'");
					}
				}
			}
			
			writer.flush();
			writer.close();
									
			lines = getLinesFromIs(urlConn.getInputStream());
			
			for (String line : lines) {
				System.out.println(line);
			}
						
		}
		else {
			MainWindow.logMessage("Aborting login.");
		}	

	}
	
	@Override
	public int getFreeSlots() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasFreeSlot() {
		
		try {
			login();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static class FindClassParams {
		// TODO: May want to encrypt password eventually.
		public final String username;
		public final String password;
		public final String slnNum;
		public final String enrollSumUrl;
		
		public FindClassParams(String uName, String pwd, String sln, String enrSummUrl) {
			username = uName;
			password = pwd;
			slnNum = sln;
			enrollSumUrl = enrSummUrl;
		}
		
		@Override
		public String toString() {
			return username + ", " + password.replaceAll(".", "\\*") + ", " + slnNum + ", " + enrollSumUrl;
		}
	}
}
