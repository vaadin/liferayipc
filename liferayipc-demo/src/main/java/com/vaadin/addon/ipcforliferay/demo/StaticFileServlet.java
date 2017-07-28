/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.addon.ipcforliferay.demo;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinServlet;

/**
 * Servlet for serving static resources for the portlets.
 */
@WebServlet(urlPatterns = "/VAADIN/*", name = "StaticFileServlet", asyncSupported = true)
public class StaticFileServlet extends VaadinServlet {
    @Override
    protected boolean allowServePrecompressedResource(
            HttpServletRequest request, String url) {
        // Without this, Liferay 6.2 (at least some times) serves a double
        // gzipped vaadinBootstrap.js
        return false;
    }
}
