package com.vaadin.addon.ipcforliferay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.vaadin.addon.ipcforliferay.event.LiferayIPCEvent;
import com.vaadin.addon.ipcforliferay.event.LiferayIPCEventListener;
import com.vaadin.addon.ipcforliferay.shared.LiferayIPCRpc;
import com.vaadin.addon.ipcforliferay.shared.LiferayIPCState;
import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.UI;

/**
 * Enables inter portlet communication using Liferay communication API.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0
 * 
 */
@JavaScript("LiferayIPCConnector.js")
public class LiferayIPC extends AbstractJavaScriptExtension {

    private HashMap<String, List<LiferayIPCEventListener>> eventListeners = new HashMap<String, List<LiferayIPCEventListener>>();

    public LiferayIPC() {
        registerRpc(new LiferayIPCRpc() {

            @Override
            public void sendEvent(String eventId, String message) {
                fireEvent(eventId, message);
            }

        });

    }

    private void fireEvent(String eventId, String data) {
        List<LiferayIPCEventListener> listeners = eventListeners.get(eventId);
        if (listeners == null) {
            return;
        }
        for (Object o : listeners.toArray()) {
            ((LiferayIPCEventListener) o).eventReceived(new LiferayIPCEvent(
                    this, eventId, data));
        }

    }

    /**
     * Adds a listener for Liferay client-side inter-portlet communication.
     * Portlets can send messages (strings) to other Vaadin and non-Vaadin
     * portlets on the same page that listen to the same event ID.
     * 
     * @param eventId
     *            event identifier whose events to listen to
     * @param listener
     */
    public void addListener(String eventId, LiferayIPCEventListener listener) {
        List<LiferayIPCEventListener> listeners = eventListeners.get(eventId);
        if (listeners == null) {
            listeners = new ArrayList<LiferayIPCEventListener>();
            eventListeners.put(eventId, listeners);
            getState().eventIdsListenedTo.add(eventId);
        }
        listeners.add(listener);
    }

    @Override
    protected LiferayIPCState getState() {
        return (LiferayIPCState) super.getState();
    }

    /**
     * Removes a listener for Liferay client-side inter-portlet communication.
     * 
     * @param eventId
     *            event identifier whose events to listen to
     * @param listener
     */
    public void removeListener(String eventId, LiferayIPCEventListener listener) {
        List<LiferayIPCEventListener> listeners = eventListeners.get(eventId);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                getState().eventIdsListenedTo.remove(eventId);
            }
        }
    }

    /**
     * Sends a message to other portlets on the same page over the Liferay
     * client-side inter-portlet communication mechanism. All Vaadin and
     * non-Vaadin portlets on the same page that listen to the same event ID
     * will receive the message.
     * 
     * @param eventId
     * @param message
     */
    public void sendEvent(String eventId, String message) {
        getRpcProxy(LiferayIPCRpc.class).sendEvent(eventId, message);
    }

    /**
     * Attach this extension to the given UI. Required to be able to send and
     * receive events.
     * 
     * @param target
     *            The UI to attach to
     */
    public void extend(UI target) {
        super.extend(target);
    }
}
