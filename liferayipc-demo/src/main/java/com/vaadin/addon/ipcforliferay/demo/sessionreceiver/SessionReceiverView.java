package com.vaadin.addon.ipcforliferay.demo.sessionreceiver;

import com.vaadin.addon.ipcforliferay.demo.receiver.ReceiverView;

/**
 * Demo view that receives Liferay client side inter-portlet messages (from
 * SenderView) and displays their contents in a table. This portlet reads the
 * actual content of the message from (shared) portlet session attributes
 * identified by the key given in the client-side message.
 */
public class SessionReceiverView extends ReceiverView {

}
