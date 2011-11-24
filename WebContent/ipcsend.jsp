<%
/**
 * Demo portlet for sending Liferay client-side IPC messages.
 */
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

This JSP portlet sends Liferay client side inter-portlet events to other portlets on the same page.

<script>
function send_message() {
    var firstname = document.getElementById('firstname_text').value;
    var lastname = document.getElementById('lastname_text').value;
    var age = document.getElementById('age_text').value;

    var message = firstname + ';' + lastname + ';' + age;

    Liferay.fire('newPerson', message);
}
</script>

<form id="person_details">
<input type="text" id="firstname_text" value="John" />
<input type="text" id="lastname_text" value="Smith" />
<input type="text" id="age_text" value="44" />
</form>

<input type="button" value="Send message" onclick="send_message()" />
