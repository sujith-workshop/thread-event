package io.sujithworkshop.threadevent.core;

public interface EventLifeCycle
{
	void start() throws Exception;

	void setPrePersistence() throws Exception;

	void setPostPersistence() throws Exception;

	void success() throws Exception;

	void fail() throws Exception;

	void end() throws Exception;
}
