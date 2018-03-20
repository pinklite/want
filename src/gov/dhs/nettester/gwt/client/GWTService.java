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
package gov.dhs.nettester.gwt.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author steveq@nist.gov
 */
@RemoteServiceRelativePath("greet")
public interface GWTService extends RemoteService {
	
	String au(String u, String p) throws IllegalArgumentException;
	
	String sendMsg(String msg, byte[] payload)
			throws IllegalArgumentException;
}
