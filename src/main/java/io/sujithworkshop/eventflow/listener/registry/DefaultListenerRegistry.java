package io.sujithworkshop.eventflow.listener.registry;

import io.sujithworkshop.eventflow.core.Event;
import io.sujithworkshop.eventflow.listener.EventListener;
import io.sujithworkshop.eventflow.util.ListenerClassMap;

import java.util.HashSet;
import java.util.Set;

public class DefaultListenerRegistry implements ListenerRegistry
{
	private final ListenerClassMap eventListenerClassMap;

	public DefaultListenerRegistry(ListenerClassMap eventListenerClassMap)
	{
		this.eventListenerClassMap = eventListenerClassMap;
	}

	@Override
	public void register(Class<? extends EventListener<? extends Event>> listenerClass)
	{
		try
		{
			EventListener<? extends Event> listener = listenerClass.getDeclaredConstructor().newInstance();
			Class<? extends Event> eventType = listener.getEventType();
			eventListenerClassMap.computeIfAbsent(eventType, k -> new HashSet<>()).add(listenerClass);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<EventListener<? extends Event>> getListenersForEvent(Class<? extends Event> eventType)
	{
		Set<Class<? extends EventListener<? extends Event>>> listenerClasses = eventListenerClassMap.getOrDefault(eventType, Set.of());
		return getListenerInstances(listenerClasses);
	}

	protected Set<EventListener<? extends Event>> getListenerInstances(Set<Class<? extends EventListener<? extends Event>>> listenerClasses)
	{
		Set<EventListener<? extends Event>> listeners = new HashSet<>();
		for (Class<? extends EventListener<? extends Event>> listenerClass : listenerClasses)
		{
			try
			{
				EventListener<? extends Event> listener = getListenerInstance(listenerClass);
				listeners.add(listener);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}

		return listeners;
	}

	protected EventListener<? extends Event> getListenerInstance(Class<? extends EventListener<? extends Event>> listenerClass)
	{
		try
		{
			return listenerClass.getDeclaredConstructor().newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
