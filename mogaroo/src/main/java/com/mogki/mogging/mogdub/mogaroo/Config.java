package com.mogki.mogging.mogdub.mogaroo;

public class Config {
	public static final String DEF_USERNAME = "jessega";
	public static final String DEF_SUMM_URL = "https://sdb.admin.washington.edu/timeschd/uwnetid/tsstat.asp?QTRYR=WIN+2012&CURRIC=MATH";
	public static final String DEF_SLN = "16262";
	
	public static final int WIDTH = 875;
	public static final int HEIGHT = 800;
	public static final int X_LOC_DRIFT = 400;
	public static final int Y_LOC_DRIFT = 200;
	public static final String TITLE = "Mogaroo";
	public static final String VERSION = "0.1-SNAPSHOT";
	
	public static final String ENCODING = "utf-8";
	public static final String LOGIN_HOST = "https://weblogin.washington.edu";
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.107 Safari/535.1";
	public static final String MULTIPART_POST = "application/x-www-form-urlencoded";
	public static final String NL = System.getProperty("line.separator");
	public static final int HTTP_TIMEOUT = 10000;
	private Config() {
		
	}
	
}
