package gov.dhs.nettester.gwt.server;

import gov.dhs.nettester.gwt.client.GWTService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;


import java.util.HashMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class PollingService extends RemoteServiceServlet implements GWTService {

	private static final long serialVersionUID = 1L;
	public static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");

	public static HashMap<String, String> hashmap = null;

	@Override
	/** Authenticate against hardcoded credentials. Developer can extend to provide
	 * more robust authentication.
	 */
	public String au(String u, String p) throws IllegalArgumentException {
		if (u == null || u.isEmpty() || p == null || p.isEmpty()) {
			return null;
		}

		if (u.equals("want") && p.equals("want")) {
			System.out.println("[NETTESTER] User: " + u + " logged in.");
			String clientIp = getThreadLocalRequest().getRemoteAddr();
			return clientIp;
		} else {
			return null;
		}
	}

	public String sendMsg(String msg, byte[] payload) {

		//System.out.println("[NETTESTER] " + msg);
		return "Received " + msg;
	}




}
