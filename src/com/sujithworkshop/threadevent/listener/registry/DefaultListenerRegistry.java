package io.sujithworkshop.threadevent.listener.registry;

import io.sujithworkshop.threadevent.core.Event;
import io.sujithworkshop.threadevent.listener.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultListenerRegistry implements ListenerRegistry
{
	private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> eventListenerMap;

	public DefaultListenerRegistry()
	{
		this.eventListenerMap = new HashMap<>();
	}

	@Override
	public void register(EventListener<? extends Event> listener)
	{
		Class<? extends Event> eventType = listener.getEventType();
		eventListenerMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
	}

	@Override
	public List<EventListener<? extends Event>> getListenersForEvent(Class<? extends Event> eventType)
	{
		return eventListenerMap.getOrDefault(eventType, List.of());
	}
}
