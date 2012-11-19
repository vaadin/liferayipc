/*
 * Copyright 2011 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.addon.ipcforliferay.demo.receiver;

import com.vaadin.addon.ipcforliferay.LiferayIPC;
import com.vaadin.addon.ipcforliferay.event.LiferayIPCEvent;
import com.vaadin.addon.ipcforliferay.event.LiferayIPCEventListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class ReceiverUI extends UI {

    private LiferayIPC liferayIPC;
    private ReceiverView receiverView;

    @Override
    protected void init(VaadinRequest request) {
        receiverView = new ReceiverView();
        setContent(receiverView);
        initializeIPC();
    }

    private void initializeIPC() {
        liferayIPC = new LiferayIPC();
        liferayIPC.extend(this);
        // listen to events with the ID "newPerson" and display the received
        // data
        liferayIPC.addListener("newPerson", new LiferayIPCEventListener() {

            public void eventReceived(LiferayIPCEvent event) {
                // parse the received semicolon separated data
                String[] fields = event.getData().split(";");
                if (fields.length != 3) {
                    Notification
                            .show("Received event whose data could not be parsed: "
                                    + event.getData());
                    return;
                }

                // add a new user to the table based on the message contents
                String firstName = fields[0];
                String lastName = fields[1];
                int age = Integer.parseInt(fields[2]);
                receiverView.addUser(firstName, lastName, age);
            }
        });

    }

}
