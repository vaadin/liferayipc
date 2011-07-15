package com.vaadin.addon.ipcforliferay.demo.receiver;

import com.vaadin.Application;
import com.vaadin.ui.Window;

public class ReceiverApplication extends Application {

	public void init() {
		setMainWindow(new Window("Receiver", new ReceiverView()));
	}

}
