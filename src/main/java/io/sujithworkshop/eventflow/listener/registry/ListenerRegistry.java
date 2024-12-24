package io.sujithworkshop.eventflow.listener.registry;

import io.sujithworkshop.eventflow.core.Event;
import io.sujithworkshop.eventflow.listener.EventListener;

import java.util.Set;

public interface ListenerRegistry
{
	void register(Class<? extends EventListener<? extends Event>> listenerClass);

	Set<EventListener<? extends Event>> getListenersForEvent(Class<? extends Event> eventType);
}
