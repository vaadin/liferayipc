package com.vaadin.addon.ipcforliferay.gwt.client.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

public class VLiferayIPC extends Widget implements Paintable {

    public static final String ATTR_EVENT_IDS_TO_LISTEN_FOR = "A_EV_IDS";

    public static final String VARIABLE_EVENT_ID = "VEI";

    public static final String VARIABLE_EVENT_DATA = "VED";

    public static final String ATTR_TRIGGER_EVENT_IDS = "ATEI";

    public static final String ATTR_TRIGGER_EVENT_DATA = "ATED";

    /** The client side widget identifier */
    protected String paintableId;

    /** Reference to the server connection object. */
    protected ApplicationConnection client;

    /**
     * Map from a currently registered event ID to the JavaScript function
     * object registered for it.
     */
    private Map<String, JavaScriptObject> eventListeners = new HashMap<String, JavaScriptObject>();

    /**
     * The constructor should first call super() to initialize the component and
     * then handle any initialization relevant to Vaadin.
     */
    public VLiferayIPC() {
        setElement(Document.get().createDivElement());
        Style style = getElement().getStyle();
        style.setWidth(0, Unit.PX);
        style.setHeight(0, Unit.PX);
        style.setOverflow(Overflow.HIDDEN);
    }

    /**
     * Called whenever an update is received from the server
     */
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        this.client = client;
        if (uidl.hasAttribute("cached")) {
            return;
        }

        paintableId = uidl.getId();

        // prepare to unregister events not listed by the server
        Set<String> eventIdsToUnregister = new HashSet<String>(
                eventListeners.keySet());

        for (String eventId : uidl
                .getStringArrayAttribute(ATTR_EVENT_IDS_TO_LISTEN_FOR)) {
            if (!eventListeners.containsKey(eventId)) {
                JavaScriptObject listener = registerListener(eventId);
                eventListeners.put(eventId, listener);
            }
            // do not unregister event id if server tells still in use
            eventIdsToUnregister.remove(eventId);
        }

        // unregister event IDs not listed by the server
        for (String eventId : eventIdsToUnregister) {
            unregisterListener(eventId, eventListeners.get(eventId));
            eventListeners.remove(eventId);
        }

        if (uidl.hasAttribute(ATTR_TRIGGER_EVENT_IDS)) {
            String[] triggerEventIds = uidl
                    .getStringArrayAttribute(ATTR_TRIGGER_EVENT_IDS);
            String[] triggerEventData = uidl
                    .getStringArrayAttribute(ATTR_TRIGGER_EVENT_DATA);
            for (int i = 0; i < triggerEventIds.length; i++) {
                triggerEvent(triggerEventIds[i], triggerEventData[i]);
            }
        }

    }

    private native void triggerEvent(String eventId, Object data)
    /*-{
        $wnd.Liferay.fire(eventId,data);
    }-*/;

    /**
     * Listener method called by the Liferay javascript API when a client side
     * IPC event is received. Notifies the server of the event with the related
     * data.
     * 
     * @param eventId
     * @param data
     */
    public void receivedEvent(String eventId, String data) {
        if (eventListeners.containsKey(eventId)) {
            client.updateVariable(paintableId, VARIABLE_EVENT_ID, eventId,
                    false);
            client.updateVariable(paintableId, VARIABLE_EVENT_DATA, data, true);
        } else {
            VConsole.log("Unexpected client-side IPC message for event id "
                    + eventId);
        }
    }

    private native void unregisterListener(String eventId,
            JavaScriptObject listener)
    /*-{
        var instance = this;
        $wnd.Liferay.detach(eventId, listener);
    }-*/;

    private native JavaScriptObject registerListener(String eventId)
    /*-{
        var instance = this;
        var listener = function(event,data) {
            instance.@com.vaadin.addon.ipcforliferay.gwt.client.ui.VLiferayIPC::receivedEvent(Ljava/lang/String;Ljava/lang/String;)(eventId,data.toString());
        };
        $wnd.Liferay.on(eventId,listener);
        return listener;
    }-*/;

}
