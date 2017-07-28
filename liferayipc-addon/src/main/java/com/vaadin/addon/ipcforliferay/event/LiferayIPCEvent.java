package com.vaadin.addon.ipcforliferay.event;

import java.util.EventObject;

import com.vaadin.addon.ipcforliferay.LiferayIPC;

/**
 * Event class for messages sent using the Liferay client-side inter-portlet
 * communication mechanism. Events have an event identifier string based on
 * which they are sent to the correct listeners on the same page and a message
 * String.
 *
 * For communicating non-String data, either encode the data in the message
 * (e.g. Base64 encoded serialized Java objects or custom serialization using
 * JSON) or use a separate channel for communicating the real content when
 * notified by this mechanism.
 *
 * @see LiferayIPC
 */
public class LiferayIPCEvent extends EventObject {

    private String eventId;
    private String data;

    /**
     * Constructor for an event with a given event id and message.
     *
     * @param source
     *            the source of the event
     * @param eventId
     *            the id of the event
     * @param data
     *            the message
     */
    public LiferayIPCEvent(LiferayIPC source, String eventId, String data) {
        super(source);
        this.eventId = eventId;
        this.data = data;
    }

    /**
     * Returns the event identifier for the received message.
     *
     * @return the event id
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Returns the message content for the received message.
     *
     * @return the message payload string
     */
    public String getData() {
        return data;
    }

}
