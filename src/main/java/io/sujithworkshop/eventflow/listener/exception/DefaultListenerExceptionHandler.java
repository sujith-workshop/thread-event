package io.sujithworkshop.eventflow.listener.exception;

import io.sujithworkshop.eventflow.core.Event;
import io.sujithworkshop.eventflow.listener.EventListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultListenerExceptionHandler extends ListenerExceptionHandler
{
	public DefaultListenerExceptionHandler(Logger logger)
	{
		super(logger);
	}

	@Override
	public void handleListenerException(Event event, EventListener<?> listener, Exception e) throws Exception
	{
		String eventName = event.getClass().getSimpleName();
		String listenerName = listener.getClass().getSimpleName();
		String errorMsg = String.format("Error processing event %s with listener %s", eventName, listenerName); //NO I18N
		Logger logger = getLogger();
		logger.log(Level.INFO, errorMsg, e);
		throw e;
	}
}