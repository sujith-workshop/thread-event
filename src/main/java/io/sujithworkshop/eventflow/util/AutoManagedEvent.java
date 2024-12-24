package io.sujithworkshop.eventflow.util;

import io.sujithworkshop.eventflow.core.EventLifeCycle;

public class AutoManagedEvent implements AutoCloseable
{
	private final EventLifeCycle lifeCycle;

	public AutoManagedEvent(EventLifeCycle lifeCycle) throws Exception
	{
		this.lifeCycle = lifeCycle;

		try
		{
			lifeCycle.start();
		}
		catch (Exception e)
		{
			lifeCycle.fail();
			lifeCycle.end();
			throw e;
		}
	}

	@Override
	public void close() throws Exception
	{
		lifeCycle.end();
	}
}
