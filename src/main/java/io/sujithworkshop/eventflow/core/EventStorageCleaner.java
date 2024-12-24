package io.sujithworkshop.eventflow.core;

public class EventStorageCleaner implements AutoCloseable
{
	private final Event event;
	private final EventStorageStack eventStorageStack;

	public EventStorageCleaner(EventStorageStack eventStorageStack, Event event)
	{
		this.eventStorageStack = eventStorageStack;
		this.event = event;
	}

	@Override
	public void close() throws Exception
	{
		eventStorageStack.popUntil(event);
	}
}
