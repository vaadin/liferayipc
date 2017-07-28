<%
/**
 * Demo portlet for receiving Liferay client-side IPC messages.
 */
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

This JSP portlet receives Liferay client side inter-portlet events from other portlets on the same page and shows them as JavaScript alerts.

<script>
Liferay.on('newPerson', function(event, data) {
    alert(data.split(';'));
});
</script>
