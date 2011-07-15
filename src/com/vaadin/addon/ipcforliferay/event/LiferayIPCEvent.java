package com.vaadin.addon.ipcforliferay.event;

import com.vaadin.addon.ipcforliferay.LiferayIPC;
import com.vaadin.ui.Component.Event;

public class LiferayIPCEvent extends Event {

	private String eventId;
	private String data;

	public LiferayIPCEvent(LiferayIPC source, String eventId, String data) {
		super(source);
		this.eventId = eventId;
		this.data = data;
	}

	public String getEventId() {
		return eventId;
	}

	public String getData() {
		return data;
	}

}