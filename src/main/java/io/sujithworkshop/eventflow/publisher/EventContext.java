package io.sujithworkshop.eventflow.publisher;

import io.sujithworkshop.eventflow.core.Event;
import io.sujithworkshop.eventflow.listener.EventListener;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventContext
{
	private final Event event;
	private final Instant startTime;
	private final List<EventListener<?>> processedListeners = new ArrayList<>();

	public EventContext(Event event)
	{
		this.event = event;
		this.startTime = Instant.ofEpochMilli(event.getTimestamp());
	}

	public void recordListenerProcessing(EventListener<?> listener)
	{
		processedListeners.add(listener);
	}

	public Event getEvent()
	{
		return event;
	}

	public Duration getProcessingDuration()
	{
		Instant endTime = Instant.now();
		return Duration.between(startTime, endTime);
	}

	public List<EventListener<?>> getProcessedListeners()
	{
		return Collections.unmodifiableList(processedListeners);
	}
}
