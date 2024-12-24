package io.sujithworkshop.eventflow.core;

import java.util.Optional;

public interface EventStorageStack
{
	void push(Event event) throws Exception;

	void popUntil(Event event) throws Exception;

	Optional<Event> peek();
}
