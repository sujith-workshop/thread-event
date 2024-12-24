package io.sujithworkshop.eventflow.core;

public enum EventStage
{
	START,
	PRE_PERSISTENCE,
	POST_PERSISTENCE,
	SUCCESS,
	FAILED,
	END
}