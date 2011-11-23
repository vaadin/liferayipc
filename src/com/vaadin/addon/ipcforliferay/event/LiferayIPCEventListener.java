package com.vaadin.addon.ipcforliferay.event;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.vaadin.addon.ipcforliferay.LiferayIPC;
import com.vaadin.tools.ReflectTools;

/**
 * Listener interface of receiving messages sent using the Liferay client-side
 * inter-portlet communication mechanism. Events have an event identifier string
 * based on which they are sent to the correct listeners on the same page and a
 * message String.
 * 
 * @see LiferayIPC
 */
public interface LiferayIPCEventListener extends Serializable {
    /**
     * Internal listener management related field - do not use directly.
     */
    public static final Method eventReceivedMethod = ReflectTools.findMethod(
            LiferayIPCEvent.class, "eventReceived", LiferayIPCEvent.class);

    /**
     * Handle a received Liferay client-side inter-portlet event.
     * 
     * @param event
     *            received event
     */
    public void eventReceived(LiferayIPCEvent event);
}