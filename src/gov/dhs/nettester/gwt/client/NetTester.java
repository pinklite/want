package gov.dhs.nettester.gwt.client;

import gov.dhs.nettester.gwt.client.gui.TesterPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author stephen.quirolgico@hq.dhs.gov
 */
public class NetTester implements EntryPoint {

	@Override
	public void onModuleLoad() {
		displayLogin();
	}

	public void displayLogin() {
		final TesterPanel loginPanel = new TesterPanel();
		final RootLayoutPanel rootPanel = RootLayoutPanel.get();
		rootPanel.add(loginPanel);
	}

}
