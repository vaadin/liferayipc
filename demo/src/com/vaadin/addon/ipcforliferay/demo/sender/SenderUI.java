package com.vaadin.addon.ipcforliferay.demo.sender;

import java.io.IOException;
import java.util.UUID;

import com.vaadin.addon.ipcforliferay.LiferayIPC;
import com.vaadin.addon.ipcforliferay.demo.SessionAwarePortletUI;
import com.vaadin.addon.ipcforliferay.demo.data.Person;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;

public class SenderUI extends SessionAwarePortletUI {

    private LiferayIPC liferayIPC;

    @Override
    protected void init(VaadinRequest request) {
        setContent(new SenderView());
        initializeIPC();
    }

    private void initializeIPC() {
        liferayIPC = new LiferayIPC();
        liferayIPC.extend(this);
    }

    /**
     * Send information about a person to other portlets as a suitably formatted
     * (semicolon separated) string using client-side IPC.
     * 
     * @param firstName
     * @param lastName
     * @param age
     */
    public void sendPersonViaClient(String firstName, String lastName, int age) {
        // format the message for easy parsing (semicolon separated)
        // no escaping of semicolons etc. is performed here
        liferayIPC.sendEvent("newPerson", firstName + ";" + lastName + ";"
                + age);
    }

    /**
     * Send information about a person to other portlets by putting the person
     * in a shared session attribute and then notifying other portlets of its
     * availability via client-side IPC without sending the actual data via the
     * client.
     * 
     * This requires the sending portlet to be configured for shared session
     * attributes (private-session-attributes must be false in
     * liferay-portlet.xml).
     * 
     * Note that this simple implementation does not clean up attributes added
     * to the session.
     * 
     * @param firstName
     * @param lastName
     * @param age
     */
    public void sendPersonViaSession(String firstName, String lastName, int age) {
        Person person = new Person(firstName, lastName, age);

        // generate a random key for the data to transfer
        String key = UUID.randomUUID().toString();

        // Put the person object in the session.
        // Note that between portlets in the same WAR, the
        // object could also be passed directly without
        // serializing it.
        try {
            setSessionAttribute("LIFERAY_SHARED_vaadin_person_" + key, person,
                    TransferMode.BASE64);
        } catch (IOException e) {
            Notification.show("Storing a person in the portlet session failed");
            return;
        }

        // This only notifies listeners that a Person object has been
        // put or updated in the shared session. The message sent via the client
        // only contains an identifier with which the receiving portlets can
        // retrieve the data from the session.
        liferayIPC.sendEvent("newPersonInSession", key);
    }

}
