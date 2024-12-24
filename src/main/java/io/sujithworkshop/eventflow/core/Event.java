package io.sujithworkshop.eventflow.core;

import io.sujithworkshop.eventflow.publisher.EventPublisher;

import java.util.UUID;

public abstract class Event implements EventLifeCycle
{
	private EventStage eventStage;
	private final UUID eventId;
	private final long timestamp;
	private final EventPublisher eventPublisher;
	private final EventStorageStack eventStorageStack;

	public Event(EventPublisher eventPublisher, EventStorageStack eventStorageStack)
	{
		this.eventStage = EventStage.START;
		this.eventId = UUID.randomUUID();
		this.timestamp = System.currentTimeMillis();
		this.eventPublisher = eventPublisher;
		this.eventStorageStack = eventStorageStack;
	}

	public EventStage getEventStage()
	{
		return eventStage;
	}

	private void updateEventStage(EventStage eventStage)
	{
		this.eventStage = eventStage;
	}

	public UUID getEventId()
	{
		return eventId;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public String getEventName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public void start() throws Exception
	{
		updateEventStage(EventStage.START);
		eventStorageStack.push(this);
		publish();
	}

	@Override
	public void prePersist() throws Exception
	{
		updateEventStage(EventStage.PRE_PERSISTENCE);
		publish();
	}

	@Override
	public void postPersist() throws Exception
	{
		updateEventStage(EventStage.POST_PERSISTENCE);
		publish();
	}

	@Override
	public void success() throws Exception
	{
		updateEventStage(EventStage.SUCCESS);
		publish();
	}

	@Override
	public void fail() throws Exception
	{
		updateEventStage(EventStage.FAILED);
		publish();
	}

	@Override
	public void end() throws Exception
	{
		updateEventStage(EventStage.END);
		try (EventStorageCleaner cleaner = getEventStorageCleaner()) //Event ends or not, remove the event from the thread-local stack
		{
			publish();
		}
	}

	protected EventStorageCleaner getEventStorageCleaner()
	{
		return new EventStorageCleaner(eventStorageStack, this);
	}

	protected void publish() throws Exception
	{
		eventPublisher.publish(this);
	}

	@Override
	public String toString()
	{
		return getEventName();
	}
}