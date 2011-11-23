package com.vaadin.addon.ipcforliferay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.addon.ipcforliferay.event.LiferayIPCEvent;
import com.vaadin.addon.ipcforliferay.event.LiferayIPCEventListener;
import com.vaadin.addon.ipcforliferay.gwt.client.ui.VLiferayIPC;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;

/**
 * Server side component for the VLiferayIPC widget.
 */
@com.vaadin.ui.ClientWidget(com.vaadin.addon.ipcforliferay.gwt.client.ui.VLiferayIPC.class)
public class LiferayIPC extends AbstractComponent {

    private HashMap<String, List<LiferayIPCEventListener>> eventListeners = new HashMap<String, List<LiferayIPCEventListener>>();
    private List<String> pendingEventIds = new ArrayList<String>();
    private List<String> pendingEventData = new ArrayList<String>();

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);
        target.addAttribute(VLiferayIPC.ATTR_EVENT_IDS_TO_LISTEN_FOR,
                eventListeners.keySet().toArray());
        if (!pendingEventIds.isEmpty()) {
            target.addAttribute(VLiferayIPC.ATTR_TRIGGER_EVENT_IDS,
                    pendingEventIds.toArray());
            target.addAttribute(VLiferayIPC.ATTR_TRIGGER_EVENT_DATA,
                    pendingEventData.toArray());
            pendingEventIds.clear();
            pendingEventData.clear();
        }
    }

    /**
     * Receive and handle events and other variable changes from the client.
     * 
     * {@inheritDoc}
     */
    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
        if (variables.containsKey(VLiferayIPC.VARIABLE_EVENT_ID)) {
            fireEvent((String) variables.get(VLiferayIPC.VARIABLE_EVENT_ID),
                    (String) variables.get(VLiferayIPC.VARIABLE_EVENT_DATA));
        }

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
            requestRepaint();
        }
        listeners.add(listener);
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
        }
        if (listeners.isEmpty()) {
            requestRepaint();
            eventListeners.remove(eventId);
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
        pendingEventIds.add(eventId);
        pendingEventData.add(message);
        requestRepaint();
    }

}
