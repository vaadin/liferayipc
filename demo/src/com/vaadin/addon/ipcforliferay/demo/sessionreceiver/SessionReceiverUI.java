package com.vaadin.addon.ipcforliferay.demo.sessionreceiver;

import java.io.IOException;

import com.vaadin.addon.ipcforliferay.LiferayIPC;
import com.vaadin.addon.ipcforliferay.demo.SessionAwarePortletUI;
import com.vaadin.addon.ipcforliferay.demo.data.Person;
import com.vaadin.addon.ipcforliferay.event.LiferayIPCEvent;
import com.vaadin.addon.ipcforliferay.event.LiferayIPCEventListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;

public class SessionReceiverUI extends SessionAwarePortletUI {

    private LiferayIPC liferayIPC;
    private SessionReceiverView receiverView;

    @Override
    protected void init(VaadinRequest request) {
        receiverView = new SessionReceiverView();
        setContent(receiverView);
        initializeIPC();
    }

    private void initializeIPC() {
        liferayIPC = new LiferayIPC();
        liferayIPC.extend(this);

        // listen to events with the ID "newPersonInSession" and display the
        // received data
        liferayIPC.addListener("newPersonInSession",
                new LiferayIPCEventListener() {

                    public void eventReceived(LiferayIPCEvent event) {
                        // Get the user from the shared session attribute.
                        // Note that between portlets in the same WAR, the
                        // object could also be passed directly without
                        // serializing it.
                        String key = event.getData();
                        Person person;
                        try {
                            person = (Person) getSessionAttribute(
                                    "LIFERAY_SHARED_vaadin_person_" + key,
                                    TransferMode.BASE64);
                        } catch (IOException e) {
                            Notification
                                    .show("Received event but failed to retrieve person from the portlet session (I/O exception)");
                            return;
                        } catch (ClassNotFoundException e) {
                            Notification
                                    .show("Received event but failed to retrieve person from the portlet session (class not found)");
                            return;
                        }

                        if (null == person) {
                            Notification
                                    .show("Received event but person not found in shared session");
                            return;
                        }

                        // add a new user to the table based on the message
                        // contents
                        receiverView.addUser(person.getFirstName(),
                                person.getLastName(), person.getAge());
                    }
                });

    }

}
