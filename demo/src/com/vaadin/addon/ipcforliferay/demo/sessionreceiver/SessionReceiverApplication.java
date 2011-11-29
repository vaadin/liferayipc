package com.vaadin.addon.ipcforliferay.demo.sessionreceiver;

import com.vaadin.addon.ipcforliferay.demo.SessionAwarePortletApplication;
import com.vaadin.ui.Window;

public class SessionReceiverApplication extends SessionAwarePortletApplication {

    @Override
    public void init() {
        super.init();
        setMainWindow(new Window("Receiver", new SessionReceiverView()));
    }

}
