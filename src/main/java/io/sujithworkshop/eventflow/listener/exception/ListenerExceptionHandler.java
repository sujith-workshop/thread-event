package io.sujithworkshop.eventflow.listener.exception;

import io.sujithworkshop.eventflow.core.Event;
import io.sujithworkshop.eventflow.listener.EventListener;

import java.util.logging.Logger;

public abstract class ListenerExceptionHandler
{
	private final Logger logger;

	public ListenerExceptionHandler(Logger logger)
	{
		this.logger = logger;
	}

	abstract public void handleListenerException(Event event, EventListener<?> listener, Exception e) throws Exception;

	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}

	protected Logger getLogger()
	{
		return logger;
	}
}
