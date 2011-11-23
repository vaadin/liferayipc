package com.vaadin.addon.ipcforliferay.event;

import java.lang.reflect.Method;

import com.vaadin.tools.ReflectTools;

public interface LiferayIPCEventListener {
    public static final Method eventReceivedMethod = ReflectTools.findMethod(
            LiferayIPCEvent.class, "eventReceived", LiferayIPCEvent.class);

    public void eventReceived(LiferayIPCEvent event);
}