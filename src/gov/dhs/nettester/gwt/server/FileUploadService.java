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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadService extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {

		FileItemFactory factory = null;
		ServletFileUpload upload = null;
		List<FileItem> items = null;
		Iterator<FileItem> iter = null;

		try {
			factory = new DiskFileItemFactory();
			upload = new ServletFileUpload(factory);
			items = upload.parseRequest(request);
			iter = items.iterator();
			String incomingParameter = null;
			FileItem uploadedFileItem = null;
			String uname = null;

			while (iter.hasNext()) {
				FileItem item = iter.next();
				incomingParameter = item.getFieldName();
				if (item.isFormField()) {
					if (incomingParameter.equals("uname")) {
						uname = item.getString();
					}
				} else {
					// Files are attachments, not form fields
					if (incomingParameter.equals("fileupload")) {
						uploadedFileItem = item;
					}
				}
			}

			long fileSizeKB = uploadedFileItem.getSize() / 1024;
			//System.out.println("[NETTESTER] " + uname + "; uploaded : "
			//		+ uploadedFileItem.getName() + "(" + fileSizeKB + " KB)");
			sendHttpResponse(response, HttpServletResponse.SC_OK,
					"Server received " + uploadedFileItem.getName() + " ("
							+ fileSizeKB + "KB)", false);

		} catch (RuntimeException e) {
			//System.out.println("GOTd RUNTIME EXCEPTION: " + e.getMessage());
			sendHttpResponse(response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage(), true);
			e.printStackTrace();
		} catch (Exception e) {
			//System.out.println("GOTs EXCEPTION: " + e.getMessage());
			sendHttpResponse(response,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private boolean sendHttpResponse(HttpServletResponse response,
			int httpResponseCode, String message, boolean errorMessage) {
		PrintWriter out = null;
		try {
			response.setStatus(httpResponseCode);
			response.setContentType("text/html");
			out = response.getWriter();
			out.print(message);
			out.flush();
			if (errorMessage) {
				System.err.println("Returned HTTP " + httpResponseCode + "\n"
						+ "message = " + message);
			}
			//System.out.println("SENT RESPONSE: " + message);

			return true;
		} catch (final IOException e) {
			//System.out.println("EXCEPTION IN SEND HTTP RESPONSE");
			System.err.println(e.toString());
			return false;
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
		}
	}
}
