/* This software was developed by employees of the Department of 
 * Homeland Security (DHS), an agency of the Federal Government.
 * Pursuant to title 15 United States Code Section 105, works of DHS
 * employees are not subject to copyright protection in the United States
 * and are considered to be in the public domain.  As a result, a formal
 * license is not needed to use the software.
 * 
 * This software is provided by DHS as a service and is expressly
 * provided "AS IS".  NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
 * OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
 * AND DATA ACCURACY.  NIST does not warrant or make any representations
 * regarding the use of the software or the results thereof including, but
 * not limited to, the correctness, accuracy, reliability or usefulness of
 * the software.
 * 
 * Permission to use this software is contingent upon your acceptance
 * of the terms of this agreement.
 */
package gov.dhs.nettester.gwt.server;

import gov.dhs.nettester.gwt.client.GWTService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;


import java.util.HashMap;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author stephen.quirolgico@hq.dhs.gov
 */
public class PollingService extends RemoteServiceServlet implements GWTService {

	private static final long serialVersionUID = 1L;
	public static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");
	private static String NETTESTER_HOME = null;
	//public static Logger log = null;
	public static HashMap<String, String> hashmap = null;

	static {
		NETTESTER_HOME = System.getenv("NETTESTER_HOME");
		if (NETTESTER_HOME == null) {
			System.err.println("Environment variable NETTESTER_HOME not set.");
		} else {
			System.out.println("Environment variable NETTESTER_HOME set: " + NETTESTER_HOME);

			// Make sure user file exists. If not, create it.
			File usersFile = new File(NETTESTER_HOME + "/nettester_users.txt");
			if (usersFile.exists()) {
				// Load creds
				hashmap = new HashMap<String,String>();  
				BufferedReader br = null;
				FileReader fr = null;
				try {
					fr = new FileReader(usersFile);
					br = new BufferedReader(fr);
					String line = null;
					while ((line = br.readLine()) != null) {
						String[] creds = line.split("\\s+");
						hashmap.put(creds[0],  creds[1]);
					}
					br.close();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				} finally {
					if (fr != null) {
						try {
							fr.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fr = null;
					}
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						br = null;
					}
				}
			} else {
				try {
					usersFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String au(String u, String p) throws IllegalArgumentException {
		if (u == null || u.isEmpty() || p == null || p.isEmpty()) {
			return null;
		}
		String pStored = hashmap.get(u);
		if (pStored == null || pStored.isEmpty()) {
			System.out.println("[NETTESTER] Attempted login by unknown user: " + u + ". Aborting.");
			return null;
		}
		if (pStored.equals(p)) {
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
