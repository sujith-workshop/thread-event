package io.sujithworkshop.eventflow.publisher;

import io.sujithworkshop.eventflow.core.Event;
import io.sujithworkshop.eventflow.listener.exception.ListenerExceptionHandler;

public interface EventPublisher
{
	void publish(Event event) throws Exception;

	void publish(Event event, ListenerExceptionHandler exceptionHandler) throws Exception;
}
