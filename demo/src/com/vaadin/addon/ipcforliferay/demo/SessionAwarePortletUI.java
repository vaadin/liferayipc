package com.vaadin.addon.ipcforliferay.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.portlet.PortletSession;

import biz.source_code.base64Coder.Base64Coder;

import com.vaadin.server.VaadinPortletSession;
import com.vaadin.ui.UI;

/**
 * Portlet application class with helper functions for accessing portlet
 * application context, portlet session and portlet session attributes.
 */
public abstract class SessionAwarePortletUI extends UI {

    /**
     * Mode in which to encode session attributes.
     * 
     * Other possible transfer modes could include XML serialization, JSON
     * serialization etc.
     */
    public enum TransferMode {
        /**
         * Transfer data "as is" - suitable for communication between portlets
         * in the same WAR.
         */
        OBJECT,
        /**
         * Transfer data as a serialized byte array - suitable for communication
         * between portlets in different WARs.
         */
        SERIALIZED,
        /**
         * Transfer data as a Base-64 encoded serialized byte array - suitable
         * for communication between portlets in different WARs.
         */
        BASE64
    };

    /**
     * Returns the current portlet session.
     * 
     * @return portlet session
     */
    public PortletSession getPortletSession() {
        return ((VaadinPortletSession) getSession()).getPortletSession();
    }

    /**
     * Sets an APPLICATION_SCOPE attribute in the portlet session.
     * 
     * @param name
     * @param value
     */
    public void setSessionAttribute(String name, Object value) {
        try {
            setSessionAttribute(name, value, TransferMode.OBJECT);
        } catch (IOException e) {
            // not possible when transfer mode is OBJECT
            e.printStackTrace();
        }
    }

    /**
     * Sets an APPLICATION_SCOPE attribute in the portlet session with optional
     * encoding.
     * 
     * @param name
     *            attribute name
     * @param value
     *            attribute value
     * @param mode
     *            {@link TransferMode} for the encoding of the value
     * @throws IOException
     *             if serialization fails with {@link IOException}, never thrown
     *             if mode is {@link TransferMode#OBJECT}
     */
    public void setSessionAttribute(String name, Object value, TransferMode mode)
            throws IOException {
        // serialize if necessary
        if (mode == TransferMode.SERIALIZED || mode == TransferMode.BASE64) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(value);
            } finally {
                closeStream(oos);
            }
            if (mode == TransferMode.BASE64) {
                value = new String(Base64Coder.encode(baos.toByteArray()));
            } else {
                value = baos.toByteArray();
            }
        }
        getPortletSession().setAttribute(name, value,
                PortletSession.APPLICATION_SCOPE);
    }

    /**
     * Removes an APPLICATION_SCOPE attribute from the portlet session.
     * 
     * @param name
     *            attribute name
     */
    public void removeSessionAttribute(String name) {
        getPortletSession().removeAttribute(name,
                PortletSession.APPLICATION_SCOPE);
    }

    /**
     * Retrieves an APPLICATION_SCOPE attribute from the portlet session.
     * 
     * @param name
     *            attribute name
     * @return attribute value
     */
    public Object getSessionAttribute(String name) {
        try {
            return getSessionAttribute(name, TransferMode.OBJECT);
        } catch (IOException e) {
            // not possible when transfer mode is OBJECT
        } catch (ClassNotFoundException e) {
            // not possible when transfer mode is OBJECT
        }
        // only to keep the compiler happy, never reached
        return null;
    }

    /**
     * Retrieves an APPLICATION_SCOPE attribute from the portlet session.
     * 
     * @param name
     *            attribute name
     * @param mode
     *            {@link TransferMode} for decoding of the value
     * @return attribute value
     * @throws IOException
     *             if deserialization fails with {@link IOException}, never
     *             thrown if mode is {@link TransferMode#OBJECT}
     * @throws ClassNotFoundException
     *             if deserialization fails with {@link ClassNotFoundException},
     *             never thrown if mode is {@link TransferMode#OBJECT}
     * @throws IllegalArgumentException
     *             if the value of the requested attribute is not of the correct
     *             type for the selected serialization method
     */
    public Object getSessionAttribute(String name, TransferMode mode)
            throws IOException, ClassNotFoundException {
        Object value = getPortletSession().getAttribute(name,
                PortletSession.APPLICATION_SCOPE);
        if (null == value) {
            return null;
        }
        switch (mode) {
        case BASE64:
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(
                        "Requested attribute value must be a String");
            }
            value = Base64Coder.decode((String) value);
            // fall-through to the SERIALIZED case
        case SERIALIZED:
            if (!(value instanceof byte[])) {
                throw new IllegalArgumentException(
                        "Requested attribute value must be a byte[]");
            }
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream((byte[]) value));
            try {
                value = ois.readObject();
            } finally {
                closeStream(ois);
            }
        case OBJECT:
            // no conversions necessary
            break;
        }
        return value;
    }

    private void closeStream(Closeable stream) {
        if (null != stream) {
            try {
                stream.close();
            } catch (IOException e) {
                // nothing to do, ignore failure to close stream
            }
        }
    }

}
