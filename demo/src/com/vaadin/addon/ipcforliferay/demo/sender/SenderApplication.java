package com.vaadin.addon.ipcforliferay.demo.sender;

import com.vaadin.addon.ipcforliferay.demo.SessionAwarePortletApplication;
import com.vaadin.ui.Window;

public class SenderApplication extends SessionAwarePortletApplication {

    @Override
    public void init() {
        super.init();
        setMainWindow(new Window("Sender", new SenderView()));
    }
}
