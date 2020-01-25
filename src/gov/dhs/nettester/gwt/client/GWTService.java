package gov.dhs.nettester.gwt.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface GWTService extends RemoteService {
	
	String au(String u, String p) throws IllegalArgumentException;
	
	String sendMsg(String msg, byte[] payload)
			throws IllegalArgumentException;
}
