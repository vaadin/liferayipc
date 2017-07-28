window.com_vaadin_addon_ipcforliferay_LiferayIPC = function() {

	/**
	 * Map from a currently registered event ID to the JavaScript function
	 * object registered for it.
	 */
	var eventIdsListenedTo = {};
	var rpcProxy = this.getRpcProxy();

	this.onStateChange = function() {
		var newEventListeners = {};
		var stateEventIds = this.getState().eventIdsListenedTo;
		for ( var eventIdKey in stateEventIds) {
			if (!stateEventIds.hasOwnProperty(eventIdKey))
				continue;

			var eventId = stateEventIds[eventIdKey];

			if (!eventIdsListenedTo[eventId]) {
				// Register listener for new event id
				var listenerReference = registerListener(eventId);
				newEventListeners[eventId] = listenerReference;
			} else {
				newEventListeners[eventId] = eventIdsListenedTo[eventId];
			}
			delete eventIdsListenedTo[eventId];

		}

		// Unregister remaining listeners
		for ( var eventId in eventIdsListenedTo) {
			if (eventIdsListenedTo.hasOwnProperty(eventId))
				unregisterListener(eventId);
		}

		eventIdsListenedTo = newEventListeners;
	};

	this.onUnregister = function() {
		// Unregister all listeners
		for ( var eventId in eventIdsListenedTo) {
			if (eventIdsListenedTo.hasOwnProperty(eventId))
				unregisterListener(eventId);
		}
		delete eventIdsListenedTo;
	};

	this.registerRpc({
		sendEvent : function(eventId, data) {
			window.Liferay.fire(eventId, data);
		}
	});

	var unregisterListener = function(eventId, listener) {
		window.Liferay.detach(eventId, listener);
	};

	var registerListener = function(eventId) {
		var listener = function(event, data) {
			if (eventIdsListenedTo[eventId]) {
				rpcProxy.sendEvent(eventId, data);
			} else {
				if (console)
					console
							.log("Unexpected client-side IPC message for event id "
									+ eventId);
			}
		};
		window.Liferay.on(eventId, listener);
		return listener;
	};
};
