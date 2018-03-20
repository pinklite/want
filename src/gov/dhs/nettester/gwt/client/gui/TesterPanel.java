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
package gov.dhs.nettester.gwt.client.gui;

import gov.dhs.nettester.gwt.client.GWTService;
import gov.dhs.nettester.gwt.client.GWTServiceAsync;

import java.util.Date;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.Image;

/**
 * 
 * @author steveq@nist.gov
 */
public class TesterPanel extends DockLayoutPanel {

	private static final String VERSION = "1.0a";
	public Logger log = Logger.getLogger("TesterPanel");
	private final GWTServiceAsync appVetService = GWT.create(GWTService.class);
	private String hostUrl = null;
	private Timer pollingTimer = null;
	private Label statusLabel = null;
	private MessageDialogBox messageDialogBox = null;
	private PushButton startButton = null;
	private PushButton stopButton = null;
	private PushButton resetButton = null;
	private long startAppTime = 0;
	private boolean sendLocked = false;
	private Label elapsedLabel = null;
	private Label numMessagesSentLabel = null;
	private int numMessages = 0;
	private long minLatency = 99999999;
	private long minUploadLatency = 99999999;
	private long minUploadErrorLatency = 99999999;
	private long maxUploadLatency = 0;
	private long maxUploadErrorLatency = 0;

	private long maxLatency = 0;
	private Label numErrorsLabel = null;
	private int numErrors = 0;
	private long minErrorLatency = 99999999;
	private long maxErrorLatency = 0;
	private Label numRecoveredLabel = null;
	private boolean inErrorState = false;
	private long errorStartTime = 0;
	private int numRecovered = 0;
	private long minRecoveryLatency = 99999999;
	private long maxRecoveryLatency = 0;
	private VerticalPanel mainPanel;
	private Label clientIpLabel = null;
	private TextBox uTextBox = null;
	private PasswordTextBox pTextBox = null;
	private PushButton submitButton = null;
	private TextArea logTextArea = null;
	private String logText = "";
	private DateTimeFormat fmt = null;
	private boolean connectionLossDetected = false;
	private String myClientIp = null;
	private TextBox pollingIntervalBox;
	private TextBox payloadBox = null;
	private FormPanel formPanel = null;
	private static final int MIN_INTERVAL = 1000; // ms
	private static final int MAX_INTERVAL = 99999; // ms
	private static final int MIN_PAYLOAD = 32; // KB
	private static final int MAX_PAYLOAD = 9999; // KB
	private static final int MAX_INTERVAL_DIGITS = 5; // Max number of digits

	private Label sentLatencyLabel = null;
	private Label lostLatencyLabel = null;
	private Label reconnectLatencyLabel = null;
	private Label uploadLatencyLabel = null;
	private Label uploaderrorLatencyLabel = null;

	// for interval box
	private static final int MAX_PAYLOAD_DIGITS = 4; // Max number of digits for
	// payload box
	private FileUpload fileUpload = null;
	private Hidden hidden = null;
	private int numUploaded = 0;
	private int numUploadErrors = 0;
	private Label filesUploadedLabel = null;
	private Label uploadErrorsLabel = null;
	private Label uploadStatus = null;
	private PushButton uploadButton = null;
	private long fileUploadTime = 0;
	private PushButton resetUploadDataButton = null;
//	private PushButton copyButton = null;  // Copy is not working on DHS laptops

	public TesterPanel() {
		super(Unit.PX);
		setStyleName("mainPanel");

		fmt = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
		hostUrl = GWT.getHostPageBaseURL();
		setSize("100%", "");

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setStyleName("headerPanel");
		addNorth(verticalPanel, 85.0);
		verticalPanel.setWidth("100%");

		HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_3);
		
		Image image = new Image("images/logo.png");
		horizontalPanel_3.add(image);
		horizontalPanel_3.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_3.setCellHeight(image, "30px");
		image.setSize("", "30px");

		Label lblNewLabel = new Label("Web App Network Tester");
		horizontalPanel_3.add(lblNewLabel);
		horizontalPanel_3.setCellVerticalAlignment(lblNewLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblNewLabel.setHeight("38px");
		lblNewLabel.setStyleName("headerStyle2");
		lblNewLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		HorizontalPanel horizontalPanel_4 = new HorizontalPanel();
		horizontalPanel_4
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(horizontalPanel_4);
		verticalPanel.setCellVerticalAlignment(horizontalPanel_4,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_4.setSize("550px", "34px");

		Label lblUsername = new Label("Username: ");
		lblUsername.setStyleName("headerStyle");
		lblUsername.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel_4.add(lblUsername);
		horizontalPanel_4.setCellVerticalAlignment(lblUsername,
				HasVerticalAlignment.ALIGN_MIDDLE);
		lblUsername.setHeight("");

		uTextBox = new TextBox();
		horizontalPanel_4.add(uTextBox);
		horizontalPanel_4.setCellVerticalAlignment(uTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);
		uTextBox.setSize("150px", "10px");

		Label lblNewLabel_1 = new Label("Password: ");
		lblNewLabel_1.setStyleName("headerStyle");
		horizontalPanel_4.add(lblNewLabel_1);
		lblNewLabel_1.setHeight("");
		horizontalPanel_4.setCellVerticalAlignment(lblNewLabel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);

		pTextBox = new PasswordTextBox();
		pTextBox.setText("");
		horizontalPanel_4.add(pTextBox);
		pTextBox.setSize("150px", "10px");
		horizontalPanel_4.setCellVerticalAlignment(pTextBox,
				HasVerticalAlignment.ALIGN_MIDDLE);

		submitButton = new PushButton("Submit");
		submitButton.setStyleName("gwt-PushButton gwt-PushButton-up myButton");
		submitButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				au();
			}
		});
		submitButton.setHTML("Submit");
		horizontalPanel_4.add(submitButton);
		horizontalPanel_4.setCellHorizontalAlignment(submitButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		submitButton.setSize("70px", "");
		horizontalPanel_4.setCellVerticalAlignment(submitButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		ScrollPanel scrollPanel = new ScrollPanel();
		add(scrollPanel);

		mainPanel = new VerticalPanel();
		scrollPanel.setWidget(mainPanel);
		mainPanel.setStyleName("centerPanel");
		mainPanel.setSize("100%", "100%");
		mainPanel.setVisible(false);

		mainPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel horizontalPanel_8 = new HorizontalPanel();
		horizontalPanel_8.setStyleName("ipPanel blackBox borderStyle");
		mainPanel.add(horizontalPanel_8);
		mainPanel.setCellVerticalAlignment(horizontalPanel_8,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_8.setSize("100%", "22px");

		clientIpLabel = new Label("Client IP:");
		clientIpLabel.setStyleName("gwt-Label ipPanel");
		clientIpLabel.setTitle("The IP address associated with my browser");

		horizontalPanel_8.add(clientIpLabel);
		clientIpLabel.setWidth("");
		horizontalPanel_8.setCellWidth(clientIpLabel, "50%");
		horizontalPanel_8.setCellVerticalAlignment(clientIpLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		clientIpLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainPanel.setCellVerticalAlignment(clientIpLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);

		Label serviceUrlLabel = new Label("Service URL: " + hostUrl);
		serviceUrlLabel.setTitle("The URL of the network tester service");
		horizontalPanel_8.add(serviceUrlLabel);
		serviceUrlLabel.setWidth("");
		horizontalPanel_8.setCellWidth(serviceUrlLabel, "50%");
		horizontalPanel_8.setCellVerticalAlignment(serviceUrlLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		serviceUrlLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		mainPanel.setCellVerticalAlignment(serviceUrlLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);

		CaptionPanel cptnpnlControls = new CaptionPanel("Polling Test");
		cptnpnlControls.setCaptionHTML("<h2>Polling Test</h2>");
		cptnpnlControls.setStyleName("captionPanel");
		mainPanel.add(cptnpnlControls);
		cptnpnlControls.setSize("", "");

		VerticalPanel verticalPanel_2 = new VerticalPanel();
		cptnpnlControls.setContentWidget(verticalPanel_2);
		verticalPanel_2.setSize("100%\r\n", "28px");

		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel_1.setStyleName("rowStyle");
		horizontalPanel_1
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_2.add(horizontalPanel_1);
		verticalPanel_2.setCellVerticalAlignment(horizontalPanel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		mainPanel.setCellVerticalAlignment(horizontalPanel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setWidth("100%");

		Label pollingIntervalLabel = new Label("Polling Interval (ms): ");
		pollingIntervalLabel.setStyleName("controlStyle");
		pollingIntervalLabel.setTitle("The delay between polling messages");

		horizontalPanel_1.add(pollingIntervalLabel);
		pollingIntervalLabel.setWidth("");
		horizontalPanel_1.setCellVerticalAlignment(pollingIntervalLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);

		pollingIntervalBox = new TextBox();
		pollingIntervalBox.setTitle("Interval must be between " + MIN_INTERVAL
				+ " and " + MAX_INTERVAL + "ms");
		pollingIntervalBox.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				String pollingIntervalStr = pollingIntervalBox.getText();
				if (!pollingIntervalStr.matches("[0-9]+")) {
					showMessageDialog("Invalid Parameter",
							"Interval must be between " + MIN_INTERVAL
							+ " and " + MAX_INTERVAL + " ms", true);
					return;
				}
			}
		});

		pollingIntervalBox.setMaxLength(MAX_INTERVAL_DIGITS);
		pollingIntervalBox.setAlignment(TextAlignment.RIGHT);
		pollingIntervalBox.setText(new Integer(MIN_INTERVAL).toString());
		horizontalPanel_1.add(pollingIntervalBox);
		pollingIntervalBox.setHeight("10px");
		horizontalPanel_1.setCellVerticalAlignment(pollingIntervalBox,
				HasVerticalAlignment.ALIGN_MIDDLE);

		Label lblPayloadkb = new Label("Payload (KB): ");
		lblPayloadkb.setTitle("The size of the message payload (in KB)");
		horizontalPanel_1.add(lblPayloadkb);
		lblPayloadkb.setWidth("");
		horizontalPanel_1.setCellVerticalAlignment(lblPayloadkb,
				HasVerticalAlignment.ALIGN_MIDDLE);

		payloadBox = new TextBox();
		payloadBox.setTitle("Payload must be between " + MIN_PAYLOAD + " and "
				+ MAX_PAYLOAD + " KB");
		payloadBox.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				String payloadStr = payloadBox.getText();
				if (!payloadStr.matches("[0-9]+")) {
					showMessageDialog("Invalid Parameter",
							"Payload must be between " + MIN_PAYLOAD + " and "
									+ MAX_PAYLOAD + "ms", true);
					return;
				}
			}
		});
		payloadBox.setMaxLength(MAX_PAYLOAD_DIGITS);

		payloadBox.setAlignment(TextAlignment.RIGHT);
		payloadBox.setText(new Integer(MIN_PAYLOAD).toString());
		horizontalPanel_1.add(payloadBox);
		payloadBox.setHeight("10px");
		horizontalPanel_1.setCellVerticalAlignment(payloadBox,
				HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel horizontalPanel_10 = new HorizontalPanel();
		horizontalPanel_10
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_10
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.add(horizontalPanel_10);
		horizontalPanel_1.setCellHorizontalAlignment(horizontalPanel_10,
				HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel_10.setWidth("");

		startButton = new PushButton("Start");
		horizontalPanel_10.add(startButton);
		startButton.setStyleName("gwt-PushButton gwt-PushButton-up myButton2");
		startButton.setTitle("Start polling the server");

		startButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				// Validate polling range
				String pollingIntervalStr = pollingIntervalBox.getText();
				if (!pollingIntervalStr.matches("[0-9]+")) {
					showMessageDialog("Invalid Parameter",
							"Interval must be between " + MIN_INTERVAL
							+ " and " + MAX_INTERVAL + " ms", true);
					return;
				}

				int pollingInterval = new Integer(pollingIntervalStr)
				.intValue();
				if (pollingInterval < MIN_INTERVAL
						|| pollingInterval > MAX_INTERVAL) {
					showMessageDialog("Invalid Parameter",
							"Interval must be between " + MIN_INTERVAL
							+ " and " + MAX_INTERVAL + " ms", true);
					return;
				}

				// Validate payload size
				String payloadStr = payloadBox.getText();
				if (!payloadStr.matches("[0-9]+")) {
					showMessageDialog("Invalid Parameter",
							"Payload must be between " + MIN_PAYLOAD + " and "
									+ MAX_PAYLOAD + " KB", true);
					return;
				}

				int payloadSize = new Integer(payloadStr).intValue();
				if (payloadSize < MIN_PAYLOAD || payloadSize > MAX_PAYLOAD) {
					showMessageDialog("Invalid Parameter",
							"Payload must be between " + MIN_PAYLOAD + " and "
									+ MAX_PAYLOAD + " KB", true);
					return;
				}

				if (numMessages == 0) {
					startAppTime = System.currentTimeMillis();
					logIt("Starting network tester on " + myClientIp);
					logIt("Polling interval: " + pollingInterval + "ms");
					logIt("Payload size: " + payloadSize + "KB");
					logIt("Polling " + hostUrl);
				} else {
					logIt("Retarted network tester");
				}

				startButton.setEnabled(false);
				stopButton.setEnabled(true);
				resetButton.setEnabled(false);
				pollingIntervalBox.setEnabled(false);
				payloadBox.setEnabled(false);
				statusLabel.setText("Status: Running");
				statusLabel.getElement().getStyle().setColor("#00ff00");

				pollingTimer.scheduleRepeating(pollingInterval);
			}
		});
		startButton.setHTML("Start");
		horizontalPanel_1.setCellHorizontalAlignment(startButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_1.setCellVerticalAlignment(startButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		startButton.setWidth("40px");
		stopButton = new PushButton("Stop");
		horizontalPanel_10.add(stopButton);
		stopButton.setStyleName("gwt-PushButton gwt-PushButton-up myButton2");
		stopButton.setTitle("Stop polling the server");

		stopButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				logIt("Stopped network tester");

				startButton.setEnabled(true);
				stopButton.setEnabled(false);
				resetButton.setEnabled(true);
				pollingIntervalBox.setEnabled(true);
				payloadBox.setEnabled(true);

				if (pollingTimer.isRunning()) {
					pollingTimer.cancel();
				}
				statusLabel.getElement().getStyle().setColor("white");
				statusLabel.setText("Status: Stopped");
				sendLocked = false;
			}
		});
		stopButton.setHTML("Stop");
		stopButton.setWidth("40px");
		horizontalPanel_1.setCellVerticalAlignment(stopButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(stopButton,
				HasHorizontalAlignment.ALIGN_CENTER);

		stopButton.setEnabled(false);

		resetButton = new PushButton("Reset");
		horizontalPanel_10.add(resetButton);
		resetButton.setStyleName("gwt-PushButton gwt-PushButton-up myButton2");
		resetButton.setTitle("Reset all values and elapsed time");

		resetButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				logIt("Reset network tester");
				pollingIntervalBox.setText(new Integer(MIN_INTERVAL).toString());
				payloadBox.setText(new Integer(MIN_PAYLOAD).toString());
				startAppTime = System.currentTimeMillis();
				elapsedLabel.setText("Duration: 0d 0h 0m 0s");
				numMessagesSentLabel.setText("0");
				numMessages = 0;

				sentLatencyLabel.setText("Latency: N/A");
				minLatency = 99999999;
				maxLatency = 0;
				// Reset errors
				numErrorsLabel.getElement().getStyle().setColor("black");
				numErrorsLabel.setText("0");
				numErrors = 0;
				sentLatencyLabel.setText("Latency: N/A");
				lostLatencyLabel.getElement().getStyle().setColor("black");
				lostLatencyLabel.setText("Latency: N/A");
				reconnectLatencyLabel.setText("Latency: N/A");
				reconnectLatencyLabel.getElement().getStyle().setColor("black");

				minErrorLatency = 99999999;
				maxErrorLatency = 0;
				// Reset recovery
				numRecoveredLabel.getElement().getStyle().setColor("black");
				numRecovered = 0;
				numRecoveredLabel.setText("0");

				reconnectLatencyLabel.setText("Latency: N/A");
				reconnectLatencyLabel.getElement().getStyle().setColor("black");

				errorStartTime = 0;
				numRecovered = 0;
				minRecoveryLatency = 99999999;
				maxRecoveryLatency = 0;
				// Flags
				connectionLossDetected = false;
				sendLocked = false;
				inErrorState = false;
				resetButton.setEnabled(false);
			}
		});
		horizontalPanel_1.setCellVerticalAlignment(resetButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_1.setCellHorizontalAlignment(resetButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		resetButton.setWidth("40px");
		resetButton.setEnabled(false);

		HorizontalPanel horizontalPanel_5 = new HorizontalPanel();
		horizontalPanel_5
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_5.setStyleName("statusPanel");
		verticalPanel_2.add(horizontalPanel_5);
		horizontalPanel_5.setWidth("100%\r\n");

		statusLabel = new Label("Status: Ready");
		horizontalPanel_5.add(statusLabel);
		statusLabel.setWidth("");
		horizontalPanel_5.setCellVerticalAlignment(statusLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		statusLabel.setStyleName("statusPanel");
		statusLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		mainPanel.setCellVerticalAlignment(statusLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);

		elapsedLabel = new Label("Elapsed: 0d 0h 0m 0s");
		horizontalPanel_5.add(elapsedLabel);
		elapsedLabel.setStyleName("statusPanel");
		horizontalPanel_5.setCellVerticalAlignment(elapsedLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		elapsedLabel.setWidth("");
		elapsedLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

		HorizontalPanel horizontalPanel_9 = new HorizontalPanel();
		horizontalPanel_9
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		horizontalPanel_9.setStyleName("rowStyle borderStyle");
		verticalPanel_2.add(horizontalPanel_9);
		verticalPanel_2.setCellWidth(horizontalPanel_9, "100%");
		horizontalPanel_9.setWidth("100%");
		verticalPanel_2.setCellVerticalAlignment(horizontalPanel_9,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_2.setCellHorizontalAlignment(horizontalPanel_9,
				HasHorizontalAlignment.ALIGN_CENTER);

		VerticalPanel verticalPanel_3 = new VerticalPanel();
		verticalPanel_3.setStyleName("blackBox valuePadding");

		horizontalPanel_9.add(verticalPanel_3);
		horizontalPanel_9.setCellWidth(verticalPanel_3, "33%");
		verticalPanel_3.setWidth("100%");
		horizontalPanel_9.setCellVerticalAlignment(verticalPanel_3,
				HasVerticalAlignment.ALIGN_MIDDLE);

		Label lblNewLabel_7 = new Label("Messages Sent");
		lblNewLabel_7.setStyleName("valueHeader");
		verticalPanel_3.add(lblNewLabel_7);
		lblNewLabel_7.setWidth("");
		verticalPanel_3.setCellHorizontalAlignment(lblNewLabel_7,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_3.setCellVerticalAlignment(lblNewLabel_7,
				HasVerticalAlignment.ALIGN_MIDDLE);

		numMessagesSentLabel = new Label("0");
		numMessagesSentLabel.setStyleName("largeNumber");
		numMessagesSentLabel
		.setTitle("The number of messages successfully sent/received from the server");

		verticalPanel_3.add(numMessagesSentLabel);
		verticalPanel_3.setCellHorizontalAlignment(numMessagesSentLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_3.setCellVerticalAlignment(numMessagesSentLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		numMessagesSentLabel.setWidth("");
		numMessagesSentLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		sentLatencyLabel = new Label("Latency: N/A");
		sentLatencyLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_3.add(sentLatencyLabel);
		verticalPanel_3.setCellVerticalAlignment(sentLatencyLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_3.setCellHorizontalAlignment(sentLatencyLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		sentLatencyLabel.setWidth("");

		VerticalPanel verticalPanel_5 = new VerticalPanel();
		verticalPanel_5.setStyleName("blackBox valuePadding");
		horizontalPanel_9.add(verticalPanel_5);
		horizontalPanel_9.setCellWidth(verticalPanel_5, "33%");
		verticalPanel_5.setWidth("100%");

		Label lblNewLabel_8 = new Label("Lost Connections");
		lblNewLabel_8.setStyleName("valueHeader");
		lblNewLabel_8
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_5.add(lblNewLabel_8);
		lblNewLabel_8.setWidth("");
		verticalPanel_5.setCellVerticalAlignment(lblNewLabel_8,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_5.setCellHorizontalAlignment(lblNewLabel_8,
				HasHorizontalAlignment.ALIGN_CENTER);

		numErrorsLabel = new Label("0");
		numErrorsLabel.setStyleName("largeNumber");
		numErrorsLabel.setTitle("The number of lost connections");
		verticalPanel_5.add(numErrorsLabel);
		verticalPanel_5.setCellVerticalAlignment(numErrorsLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_5.setCellHorizontalAlignment(numErrorsLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		numErrorsLabel.setWidth("");
		numErrorsLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		VerticalPanel verticalPanel_6 = new VerticalPanel();
		verticalPanel_6.setStyleName("blackBox valuePadding");
		horizontalPanel_9.add(verticalPanel_6);
		verticalPanel_6.setWidth("100%");

		Label lblNewLabel_9 = new Label("Recovered Connections");
		lblNewLabel_9.setStyleName("valueHeader");

		lostLatencyLabel = new Label("Latency: N/A");
		lostLatencyLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_5.add(lostLatencyLabel);
		verticalPanel_5.setCellVerticalAlignment(lostLatencyLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_5.setCellHorizontalAlignment(lostLatencyLabel,
				HasHorizontalAlignment.ALIGN_CENTER);

		lblNewLabel_9
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_6.add(lblNewLabel_9);
		lblNewLabel_9.setWidth("");
		verticalPanel_6.setCellVerticalAlignment(lblNewLabel_9,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_6.setCellHorizontalAlignment(lblNewLabel_9,
				HasHorizontalAlignment.ALIGN_CENTER);

		numRecoveredLabel = new Label("0");
		numRecoveredLabel.setStyleName("largeNumber");
		numRecoveredLabel.setTitle("The number of reestablished connections");

		verticalPanel_6.add(numRecoveredLabel);
		verticalPanel_6.setCellVerticalAlignment(numRecoveredLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_6.setCellHorizontalAlignment(numRecoveredLabel,
				HasHorizontalAlignment.ALIGN_CENTER);
		numRecoveredLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		numRecoveredLabel.setWidth("");

		reconnectLatencyLabel = new Label("Latency: N/A");
		verticalPanel_6.add(reconnectLatencyLabel);
		verticalPanel_6.setCellVerticalAlignment(reconnectLatencyLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_6.setCellHorizontalAlignment(reconnectLatencyLabel,
				HasHorizontalAlignment.ALIGN_CENTER);

		CaptionPanel cptnpnlUploadTest = new CaptionPanel("Upload Test");
		cptnpnlUploadTest.setCaptionHTML("<h2>Upload Test</h2>");
		cptnpnlUploadTest.setStyleName("captionPanel");
		mainPanel.add(cptnpnlUploadTest);
		mainPanel.setCellVerticalAlignment(cptnpnlUploadTest,
				HasVerticalAlignment.ALIGN_MIDDLE);
		cptnpnlUploadTest.setHeight("");

		VerticalPanel verticalPanel_1 = new VerticalPanel();
		verticalPanel_1
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_1.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		cptnpnlUploadTest.setContentWidget(verticalPanel_1);
		verticalPanel_1.setSize("100%", "");

		CaptionPanel cptnpnlNewPanel_1 = new CaptionPanel("Log");
		cptnpnlNewPanel_1.setCaptionHTML("<h2>Log</h2>");
		cptnpnlNewPanel_1.setStyleName("captionPanel");
		mainPanel.add(cptnpnlNewPanel_1);
		mainPanel.setCellVerticalAlignment(cptnpnlNewPanel_1,
				HasVerticalAlignment.ALIGN_MIDDLE);
		cptnpnlNewPanel_1.setSize("", "");

		VerticalPanel verticalPanel_4 = new VerticalPanel();
		cptnpnlNewPanel_1.setContentWidget(verticalPanel_4);
		verticalPanel_4.setSize("100%", "100%");
		verticalPanel_4.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel horizontalPanel_7 = new HorizontalPanel();
		horizontalPanel_7.setStyleName("statusPanel");
		horizontalPanel_7
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_4.add(horizontalPanel_7);
		horizontalPanel_7.setSize("100%", "100%");
		verticalPanel_4.setCellVerticalAlignment(horizontalPanel_7,
				HasVerticalAlignment.ALIGN_MIDDLE);

		PushButton pshbtnNewButton = new PushButton("Clear");
		pshbtnNewButton
		.setStyleName("gwt-PushButton gwt-PushButton-up myButton2");
		horizontalPanel_7.add(pshbtnNewButton);
		horizontalPanel_7.setCellVerticalAlignment(pshbtnNewButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		pshbtnNewButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				logText = "";
				logTextArea.setText(logText);
			}
		});
		pshbtnNewButton.setWidth("40px");
		verticalPanel_4.setCellVerticalAlignment(pshbtnNewButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		
//		copyButton = new PushButton("Copy");
//
//		copyButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				logTextArea.setFocus(true);
//				logTextArea.selectAll();
//				if (!copyToClipboard()) {
//					showMessageDialog("Copy Error",
//							"Automatic copy disabled on this browser. Select and copy log text manually", true);
//					copyButton.setEnabled(false);
//					copyButton.setVisible(false);
//				}
//			}
//			
//			private native boolean copyToClipboard() /*-{
//		    	return $doc.execCommand('copy');
//			}-*/;
//		});
//		
//		horizontalPanel_7.add(copyButton);
//		horizontalPanel_7.setCellHorizontalAlignment(copyButton, HasHorizontalAlignment.ALIGN_CENTER);
//		copyButton.setWidth("40px");
//		horizontalPanel_7.setCellWidth(copyButton, "40px");

		HorizontalPanel horizontalPanel_6 = new HorizontalPanel();
		horizontalPanel_6
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_1.add(horizontalPanel_6);
		verticalPanel_1.setCellHorizontalAlignment(horizontalPanel_6,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_6.setSize("100%", "");
		verticalPanel_1.setCellVerticalAlignment(horizontalPanel_6,
				HasVerticalAlignment.ALIGN_MIDDLE);

		formPanel = new FormPanel();
		horizontalPanel_6.add(formPanel);
		horizontalPanel_6.setCellVerticalAlignment(formPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		formPanel.setWidth("");
		formPanel.setAction(GWT.getHostPageBaseURL() + "FileUploadService");
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);

		//formPanel.addFormHandler(new FileUploadHandler(formPanel));
		formPanel.addSubmitHandler(new FileSubmitHandler());
		formPanel.addSubmitCompleteHandler(new FileSubmitCompleteHandler());

		VerticalPanel formItemsPanel = new VerticalPanel();
		formItemsPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		formPanel.setWidget(formItemsPanel);
		formItemsPanel.setSize("100%", "56px");

		hidden = new Hidden("Hidden name");
		hidden.setName("uname");
		formItemsPanel.add(hidden);
		hidden.setSize("1", "1");
		formItemsPanel.setCellVerticalAlignment(hidden,
				HasVerticalAlignment.ALIGN_MIDDLE);

		HorizontalPanel horizontalPanel_11 = new HorizontalPanel();
		horizontalPanel_11
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		formItemsPanel.add(horizontalPanel_11);
		horizontalPanel_11.setSize("100%", "");

		fileUpload = new FileUpload();
		fileUpload.setTitle("Choose any file to test the system");
		horizontalPanel_11.add(fileUpload);
		horizontalPanel_11.setCellVerticalAlignment(fileUpload,
				HasVerticalAlignment.ALIGN_MIDDLE);
		fileUpload.setStyleName("gwt-FileUpload statusPanel");
		fileUpload.setName("fileupload");
		formItemsPanel.setCellVerticalAlignment(fileUpload,
				HasVerticalAlignment.ALIGN_MIDDLE);
		fileUpload.setWidth("547px");
		fileUpload.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent arg0) {
				String selectedFile = fileUpload.getFilename();
				if (selectedFile == null || selectedFile.isEmpty()) {
					uploadButton.setEnabled(false);
				} else {
					uploadButton.setEnabled(true);
				}
			}
		});

		uploadButton = new PushButton("Upload");
		uploadButton.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				uploadButton.setEnabled(false);
				
				String selectedFile = fileUpload.getFilename();
				if (selectedFile == null && selectedFile.isEmpty()) {
					showMessageDialog("Error", "No file has been selected",
							true);
					return;
				}
				logIt("Uploading " + selectedFile + "...");

				try {
					formPanel.submit();
				} catch (Exception e) {
					logIt("Form submit: " + e.getMessage());
				}
			}
		});
		uploadButton.setStyleName("gwt-PushButton gwt-PushButton-up myButton2");
		uploadButton
		.setTitle("Upload the selected file.");
		uploadButton.setEnabled(false);

		horizontalPanel_11.add(uploadButton);
		horizontalPanel_11.setCellWidth(uploadButton, "40px");
		horizontalPanel_11.setCellVerticalAlignment(uploadButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_11.setCellHorizontalAlignment(uploadButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		formItemsPanel.setCellVerticalAlignment(uploadButton,
				HasVerticalAlignment.ALIGN_MIDDLE);

		horizontalPanel_6.setCellVerticalAlignment(uploadButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_6.setCellHorizontalAlignment(uploadButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		uploadButton.setSize("40px", "");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setStyleName("statusPanel");
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		formItemsPanel.add(horizontalPanel);
		horizontalPanel.setWidth("100%");
		formItemsPanel.setCellVerticalAlignment(horizontalPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);

		uploadStatus = new Label("Status: Ready");
		horizontalPanel.add(uploadStatus);
		uploadStatus.setWidth("");
		horizontalPanel.setCellVerticalAlignment(uploadStatus,
				HasVerticalAlignment.ALIGN_MIDDLE);
		uploadStatus.setStyleName("statusPanel");

		resetUploadDataButton = new PushButton("Reset");
		resetUploadDataButton
		.setStyleName("gwt-PushButton gwt-PushButton-up myButton2");
		resetUploadDataButton.setEnabled(false);
		resetUploadDataButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				logIt("File uploads reset");
				// uploads
				numUploadErrors = 0;
				uploadErrorsLabel.setText("" + numUploadErrors);
				uploadErrorsLabel.getElement().getStyle().setColor("black");
				numUploaded = 0;
				filesUploadedLabel.setText("" + numUploaded);
				filesUploadedLabel.getElement().getStyle().setColor("black");
				uploadLatencyLabel.setText("Latency: N/A");
				uploadLatencyLabel.getElement().getStyle().setColor("black");

				uploaderrorLatencyLabel.setText("Latency: N/A");
				uploaderrorLatencyLabel.getElement().getStyle()
				.setColor("black");

				minUploadLatency = 0;
				maxUploadLatency = 0;
				minUploadErrorLatency = 0;
				maxUploadErrorLatency = 0;
				resetUploadDataButton.setEnabled(false);

			}
		});
		resetUploadDataButton.setTitle("Reset file upload data");
		horizontalPanel.add(resetUploadDataButton);
		horizontalPanel.setCellWidth(resetUploadDataButton, "40px");
		horizontalPanel.setCellVerticalAlignment(resetUploadDataButton,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.setCellHorizontalAlignment(resetUploadDataButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		resetUploadDataButton.setWidth("40px");

		HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		horizontalPanel_2.setStyleName("rowStyle borderStyle");
		horizontalPanel_2
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_1.add(horizontalPanel_2);
		verticalPanel_1.setCellVerticalAlignment(horizontalPanel_2,
				HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_2.setWidth("100%");

		VerticalPanel verticalPanel_7 = new VerticalPanel();
		verticalPanel_7.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_7
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.add(verticalPanel_7);
		horizontalPanel_2.setCellWidth(verticalPanel_7, "50%");
		horizontalPanel_2.setCellHorizontalAlignment(verticalPanel_7,
				HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel_2.setCellVerticalAlignment(verticalPanel_7,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_7.setStyleName("blackBox valuePadding");
		verticalPanel_7.setWidth("100%");

		Label lblFilesUploaded = new Label("Files Uploaded");
		lblFilesUploaded
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		lblFilesUploaded.setStyleName("valueHeader");
		verticalPanel_7.add(lblFilesUploaded);
		lblFilesUploaded.setWidth("");

		filesUploadedLabel = new Label("0");
		filesUploadedLabel.setStyleName("largeNumber");
		filesUploadedLabel.setTitle("The number of uploaded files");
		filesUploadedLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_7.add(filesUploadedLabel);
		filesUploadedLabel.setWidth("");

		HorizontalPanel horizontalPanel_16 = new HorizontalPanel();
		verticalPanel_7.add(horizontalPanel_16);
		horizontalPanel_16.setWidth("100%");

		uploadLatencyLabel = new Label("Latency: N/A");
		verticalPanel_7.add(uploadLatencyLabel);

		VerticalPanel verticalPanel_8 = new VerticalPanel();
		verticalPanel_8.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_8
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_8.setStyleName("blackBox valuePadding");
		horizontalPanel_2.add(verticalPanel_8);
		horizontalPanel_2.setCellWidth(verticalPanel_8, "50%");
		horizontalPanel_2.setCellVerticalAlignment(verticalPanel_8,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_8.setWidth("100%");

		Label lblUploadErrors = new Label("Upload Errors");
		lblUploadErrors.setStyleName("valueHeader");
		verticalPanel_8.add(lblUploadErrors);
		lblUploadErrors.setWidth("");

		uploadErrorsLabel = new Label("0");
		uploadErrorsLabel.setStyleName("largeNumber");
		uploadErrorsLabel.setTitle("The number of file upload errors");

		uploadErrorsLabel
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_8.add(uploadErrorsLabel);
		uploadErrorsLabel.setWidth("");

		uploaderrorLatencyLabel = new Label("Latency: N/A");
		verticalPanel_8.add(uploaderrorLatencyLabel);

		HorizontalPanel horizontalPanel_17 = new HorizontalPanel();
		verticalPanel_8.add(horizontalPanel_17);
		horizontalPanel_17.setWidth("100%");

		HorizontalPanel horizontalPanel_12 = new HorizontalPanel();
		horizontalPanel_12
		.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel_12
		.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		verticalPanel_4.add(horizontalPanel_12);
		horizontalPanel_12.setWidth("100%");
		verticalPanel_4.setCellVerticalAlignment(horizontalPanel_12,
				HasVerticalAlignment.ALIGN_MIDDLE);

		logTextArea = new TextArea();
		horizontalPanel_12.add(logTextArea);
		logTextArea.setReadOnly(true);
		logTextArea.setStyleName("boxsizingBorder blackBox valuePadding");
		verticalPanel_4.setCellVerticalAlignment(logTextArea,
				HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel_4.setCellHorizontalAlignment(logTextArea,
				HasHorizontalAlignment.ALIGN_CENTER);
		logTextArea.setSize("100%", "200px");

		pollingTimer = new Timer() {
			public void run() {
				if (!sendLocked) {
					sendLocked = true;
					long currentAppTime = System.currentTimeMillis();
					long elapsedAppTime = currentAppTime - startAppTime;
					String elapsedAppTimeString = getElapsed(elapsedAppTime);
					elapsedLabel.setText("Elapsed: " + elapsedAppTimeString);

					numMessages++;
					pollServer(numMessages);
				} else {
					// Do nothing
				}
			}
		};
	}

	public void logIt(String message) {
		Date now = new Date();
		String dateStr = fmt.format(now);
		String msgStr = dateStr.concat(": " + message + "\n");
		logText = logText.concat(msgStr);
		logTextArea.setText(logText);
	}

	public void au() {
		String u = uTextBox.getText();
		hidden.setValue(u);

		String p = pTextBox.getText();
		appVetService.au(u, p, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				showMessageDialog("Error",
						"Could not authenticate with server.", true);
			}

			@Override
			public void onSuccess(String clientIp) {
				if (clientIp == null || clientIp.isEmpty()) {
					showMessageDialog("Authentication Error",
							"Invalid username or password", true);
				} else {
					myClientIp = clientIp;
					clientIpLabel.setText("Client IP: " + myClientIp);
					mainPanel.setVisible(true);
					uTextBox.setEnabled(false);
					pTextBox.setEnabled(false);
					submitButton.setEnabled(false);
					startButton.setFocus(true);
				}
			}
		});
	}

	public void pollServer(final int numMessages) {
		String u = uTextBox.getText();
		String interval = pollingIntervalBox.getText();
		// Set payload (need this here since user may change payload size
		String payloadStr = payloadBox.getText();
		int payloadSize = new Integer(payloadStr).intValue() * 1024; // Using
		// IEC
		// standard
		// 1024
		// bytes/KB
		byte[] payload = new byte[payloadSize];
		final long sendTime = System.currentTimeMillis();
		String msg = u + "; int: " + interval + "; payld: " + payloadSize
				+ "; numMsg: " + numMessages;
		statusLabel.setText("Status: Running");
		statusLabel.getElement().getStyle().setColor("#00ff00");

		appVetService.sendMsg(msg, payload, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {

				if (caught instanceof InvocationException) {
					StatusCodeException sce = (StatusCodeException) caught;
					int statusCode = sce.getStatusCode();
					// NOTE: Packet loss rate here is defined as the number
					// of failed connection attempts over the number of 
					// successful messages sent. This formula does not 
					// consider number of packets per message.
					double packetLossRate = (double)(numErrors+1) / (double)numMessages; // numErrors starts at 0, so add 1
					String packetLossRateCategory = null;
					if (packetLossRate < 0.01)
						packetLossRateCategory = "LOW";
					else if (packetLossRate >= 0.01 && packetLossRate < 0.09)
						packetLossRateCategory = "MODERATE";
					else if (packetLossRate >= 0.09 && packetLossRate < 0.20)
						packetLossRateCategory = "HIGH";
					else if (packetLossRate >= 0.20)
						packetLossRateCategory = "VERY HIGH";

					String formattedPLR = NumberFormat.getFormat("0.0000").format(packetLossRate);
					
					if (statusCode == 0) {
						/*
						 * A service invocation can fail to complete cleanly for
						 * many reasons, including: - The network connection to
						 * the server is unavailable - The host web server is
						 * not available - The server is not available
						 */
						logIt("ERROR: Lost connection [" + (numErrors+1) + " of " + numMessages + " attempts, PLR=" + formattedPLR + " " + packetLossRateCategory + "]");  
					} else {
						logIt("ERROR: Lost connection (HTTP " + statusCode + ") [" + (numErrors+1) + " of " + numMessages + " attempts, PLR=" + formattedPLR + " " + packetLossRateCategory + "]");
					}
				} else {
					/*
					 * This exception can be caused by the following problems: -
					 * The requested RemoteService cannot be located via
					 * Class.forName(String) on the server. - The requested
					 * RemoteService interface is not implemented by the
					 * RemoteServiceServlet instance which is configured to
					 * process the request. - The requested service method is
					 * not defined or inherited by the requested RemoteService
					 * interface. - One of the types used in the RemoteService
					 * method invocation has had fields added or removed. - The
					 * client code receives a type from the server which it
					 * cannot deserialize.
					 */
					logIt("ERROR: " + caught.getMessage());
				}

				if (!inErrorState) {
					inErrorState = true;
					errorStartTime = System.currentTimeMillis();
				}

				numErrors++;
				numErrorsLabel.getElement().getStyle().setColor("red");
				numErrorsLabel.setText("" + numErrors);

				long recdTime = System.currentTimeMillis();
				long roundTripLatency = recdTime - sendTime;
				String labelContent = "Latency: ";

				if (roundTripLatency < minErrorLatency)
					minErrorLatency = roundTripLatency;

				if (minErrorLatency == 99999999)
					labelContent += "N/A";
				else
					labelContent += minErrorLatency + "ms";

				labelContent += " - ";

				if (roundTripLatency > maxErrorLatency)
					maxErrorLatency = roundTripLatency;
				if (maxErrorLatency == 0)
					labelContent += "N/A";

				else
					labelContent += maxErrorLatency + "ms";

				if (!connectionLossDetected) {
					connectionLossDetected = true;
				}

				lostLatencyLabel.getElement().getStyle().setColor("red");
				lostLatencyLabel.setText(labelContent);

				// clean up
				labelContent = null;
				recdTime = 0;
				roundTripLatency = 0;
			}

			@Override
			public void onSuccess(String result) {

				if (inErrorState) {
					numRecovered++;
					numRecoveredLabel.getElement().getStyle().setColor("#00ffff");

					numRecoveredLabel.setText("" + numRecovered);

					String labelContent = "Latency: ";

					long errorRecoveryElapsed = System.currentTimeMillis()
							- errorStartTime;

					if (errorRecoveryElapsed < minRecoveryLatency) {
						minRecoveryLatency = errorRecoveryElapsed;
					}

					if (minRecoveryLatency == 99999999) {
						labelContent += "N/A";
					} else {
						labelContent += minRecoveryLatency + "ms";
					}

					labelContent += " - ";

					if (errorRecoveryElapsed > maxRecoveryLatency) {
						maxRecoveryLatency = errorRecoveryElapsed;
					}

					if (maxRecoveryLatency == 0) {
						labelContent += "N/A";
					} else {
						labelContent += maxRecoveryLatency + "ms";
					}

					reconnectLatencyLabel.getElement().getStyle()
					.setColor("#00ffff");
					reconnectLatencyLabel.setText(labelContent);

					connectionLossDetected = false;
					logIt("Reestablished lost connection");

					inErrorState = false;
				}

				numMessagesSentLabel.setText("" + numMessages);

				long recdTime = System.currentTimeMillis();
				long roundTripLatency = recdTime - sendTime;
				String labelContent = "Latency: ";
				if (roundTripLatency < minLatency)
					minLatency = roundTripLatency;
				if (minLatency == 99999999)
					labelContent += "N/A";
				else
					labelContent += minLatency + "ms";

				labelContent += " - ";

				if (roundTripLatency > maxLatency)
					maxLatency = roundTripLatency;
				if (maxLatency == 0)
					labelContent += "N/A";
				else
					labelContent += maxLatency + "ms";

				sentLatencyLabel.setText(labelContent);

			}
		});

		// clean up
		msg = null;
		payload = null;
		payloadStr = null;
		interval = null;
		u = null;

		sendLocked = false;
	}

	private String getElapsed(long diff) {
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);

		return diffDays + "d " + diffHours + "h " + diffMinutes + "m "
		+ diffSeconds + "s";
	}

	public void showMessageDialog(String windowTitle, String message,
			boolean isError) {
		messageDialogBox = new MessageDialogBox(message, isError);
		messageDialogBox.setText(windowTitle);
		messageDialogBox.center();
		messageDialogBox.closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				messageDialogBox.hide();
				messageDialogBox = null;
			}
		});
	}

	class FileSubmitHandler implements SubmitHandler {

		@Override
		public void onSubmit(SubmitEvent event) {
			//log.info("SubmitEvent called: " + event.toDebugString());
			String fileName = fileUpload.getFilename();
			fileUploadTime = System.currentTimeMillis();
			uploadStatus.setText("Status: Uploading " + fileName + "...");
			uploadStatus.getElement().getStyle().setColor("#00ffff");
		}

	}

	class FileSubmitCompleteHandler implements SubmitCompleteHandler {

		@Override
		public void onSubmitComplete(SubmitCompleteEvent event) {
			//log.info("DEBUG: " + event.toDebugString());
			//log.info("SubmitCompleteEvent called: " + event.getResults());
			resetUploadDataButton.setEnabled(true);

			String result = event.getResults();

			if (result == null) {
				result = "ERROR: Lost connection during file upload";
				computeFileUploadError(result);
				uploadButton.setEnabled(true);

				return;
			}
			String lowercaseResult = result.toLowerCase();
			if (lowercaseResult.indexOf("http") > -1
					|| lowercaseResult.indexOf("error") > -1
					|| lowercaseResult.indexOf("fail") > -1
					|| lowercaseResult.indexOf("exception") > -1) {

				// Likely found an issue
				computeFileUploadError(result);
				return;

			} else {
				numUploaded++;
				filesUploadedLabel.setText("" + numUploaded);
				String fileName = fileUpload.getFilename();

				uploadStatus.setText("Status: Successfully uploaded "
						+ fileName);
				uploadStatus.getElement().getStyle().setColor("#00ff00");
				long currentAppTime = System.currentTimeMillis();
				long elapsedAppTime = currentAppTime - fileUploadTime;
				String labelContent = "Latency: ";

				if (elapsedAppTime < minUploadLatency)
					minUploadLatency = elapsedAppTime;
				if (minUploadLatency == 99999999)
					labelContent += "N/A";
				else
					labelContent += minUploadLatency + "ms";

				labelContent += " - ";

				if (elapsedAppTime > maxUploadLatency)
					maxUploadLatency = elapsedAppTime;
				if (maxUploadLatency == 0)
					labelContent += "N/A";
				else
					labelContent += maxUploadLatency + "ms";

				uploadLatencyLabel.setText(labelContent);
				logIt(result);

			}
			
			uploadButton.setEnabled(true);

		}

		public void computeFileUploadError(String result) {
			numUploadErrors++;
			uploadErrorsLabel.setText("" + numUploadErrors);
			uploadErrorsLabel.getElement().getStyle().setColor("red");
			uploadStatus.setText("Status: " + result);
			uploadStatus.getElement().getStyle().setColor("red");

			long currentAppTime = System.currentTimeMillis();
			long elapsedAppTime = currentAppTime - fileUploadTime;
			String labelContent = "Latency: ";

			if (elapsedAppTime < minUploadErrorLatency)
				minUploadErrorLatency = elapsedAppTime;
			if (minUploadErrorLatency == 99999999)
				labelContent += "N/A";
			else {
				labelContent += minUploadErrorLatency + "ms";
			}

			labelContent += " - ";

			if (elapsedAppTime > maxUploadErrorLatency)
				maxUploadErrorLatency = elapsedAppTime;
			if (maxUploadErrorLatency == 0)
				labelContent += "N/A";
			else {
				labelContent += maxUploadErrorLatency + "ms";
			}

			uploaderrorLatencyLabel.getElement().getStyle()
			.setColor("red");
			uploaderrorLatencyLabel.setText(labelContent);
			logIt(result);
		}

	}
}
