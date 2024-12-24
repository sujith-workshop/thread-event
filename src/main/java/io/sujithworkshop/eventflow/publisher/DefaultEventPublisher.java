package io.sujithworkshop.eventflow.publisher;

import io.sujithworkshop.eventflow.core.Event;
import io.sujithworkshop.eventflow.core.EventStage;
import io.sujithworkshop.eventflow.listener.EventListener;
import io.sujithworkshop.eventflow.listener.exception.DefaultListenerExceptionHandler;
import io.sujithworkshop.eventflow.listener.exception.ListenerExceptionHandler;
import io.sujithworkshop.eventflow.listener.registry.ListenerRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultEventPublisher implements EventPublisher
{
	private final ListenerRegistry listenerRegistry;
	private final ListenerExceptionHandler exceptionHandler;
	private final EventTracker eventTracker;
	private final Logger logger;
	private final Map<Event, Set<EventListener<? extends Event>>> eventListenersMap;
	private final long startTime;

	public DefaultEventPublisher(ListenerRegistry listenerRegistry)
	{
		this(listenerRegistry, Logger.getLogger(DefaultEventPublisher.class.getSimpleName()));
	}

	public DefaultEventPublisher(ListenerRegistry listenerRegistry, Logger logger)
	{
		this(listenerRegistry, new DefaultListenerExceptionHandler(logger), logger);
	}

	public DefaultEventPublisher(ListenerRegistry listenerRegistry, ListenerExceptionHandler exceptionHandler, Logger logger)
	{
		this.listenerRegistry = listenerRegistry;
		this.exceptionHandler = exceptionHandler;
		this.logger = logger;
		this.eventTracker = new EventTracker(logger);
		this.eventListenersMap = new HashMap<>();
		this.startTime = System.currentTimeMillis();
	}

	public void publish(Event event) throws Exception
	{
		publish(event, exceptionHandler);
	}

	public void publish(Event event, ListenerExceptionHandler exceptionHandler) throws Exception
	{
		String eventName = event.getEventName();
		UUID eventId = event.getEventId();

		logger.log(Level.INFO, "Publishing event: {0} (ID: {1}) with stage: {2}", new Object[] { eventName, eventId, event.getEventStage() }); //NO I18N

		Set<EventListener<? extends Event>> eventListeners = getEventListeners(event);
		logger.log(Level.INFO, "Found {0} listeners for event {1} (ID: {2})", new Object[]{ eventListeners.size(), eventName, eventId }); //NO I18N

		publish(event, eventListeners, exceptionHandler);

		logger.log(Level.INFO, "Event {0} (ID: {1}) processing completed", new Object[] { eventName, eventId } );
		if (event.getEventStage() == EventStage.END)
		{
			onEventCompleted(event);
		}
	}

	protected Set<EventListener<? extends Event>> getEventListeners(Event event)
	{
		Class<? extends Event> eventType = event.getClass();
		return eventListenersMap.computeIfAbsent(event, e -> listenerRegistry.getListenersForEvent(eventType));
	}

	protected void publish(Event event, Set<EventListener<? extends Event>> eventListeners, ListenerExceptionHandler exceptionHandler) throws Exception
	{
		EventContext context = eventTracker.startTracking(event);

		for (EventListener<?> listener : eventListeners)
		{
			publish(event, listener, exceptionHandler, context);
		}

		eventTracker.completeTracking(context);
	}

	protected void publish(Event event, EventListener<? extends Event> listener, ListenerExceptionHandler exceptionHandler, EventContext context) throws Exception
	{
		String eventName = event.getEventName();
		UUID eventId = event.getEventId();

		String listenerName = listener.getName();
		try
		{
			logger.log(Level.INFO, "Invoking listener: {0} for event {1} (ID: {2})", new Object[]{ listenerName, eventName, eventId }); //NO I18N
			listener.listen(event);
			logger.log(Level.INFO, "Listener {0} processed event {1} (ID: {2}) successfully", new Object[] { listenerName, eventName, eventId }); //NO I18N

			context.recordListenerProcessing(listener);
		}
		catch (Exception e)
		{
			logger.log(Level.INFO, "Listener {0} threw an exception in the event {1} (ID: {2}) and so calling the exception handler {3}", new Object[] { listenerName, eventName, eventId, exceptionHandler.toString(), e });
			try
			{
				exceptionHandler.handleListenerException(event, listener, e);
			}
			catch (Exception ex)
			{
				logger.log(Level.INFO, "Exception handler {0} threw an exception while handling the listener {1} exception in the event {2} (ID: {3})", new Object[] { exceptionHandler.toString(), listenerName, eventName, eventId, ex });
				eventListenersMap.remove(event);
				throw e;
			}
		}
	}

	protected void onEventCompleted(Event event)
	{
		String eventName = event.getEventName();
		UUID eventId = event.getEventId();

		eventListenersMap.remove(event);
		logger.log(Level.INFO, "Time taken for the event {0} (ID: {1}) to complete: {2} ms", new Object[] { eventName, eventId, System.currentTimeMillis() - startTime });
	}
}
