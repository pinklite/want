package gov.dhs.nettester.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GWTServiceAsync {
	
	void au(String u, String p, AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void sendMsg(String msg, byte[] payload, 
			AsyncCallback<String> callback) throws IllegalArgumentException;



}
