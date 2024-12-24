package io.sujithworkshop.eventflow.util;

import io.sujithworkshop.eventflow.core.Event;
import io.sujithworkshop.eventflow.listener.EventListener;

import java.util.HashMap;
import java.util.Set;

public class ListenerClassMap extends HashMap<Class<? extends Event>, Set<Class<? extends EventListener<? extends Event>>>>
{
}
