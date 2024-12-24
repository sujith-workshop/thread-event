# EventFlow Framework Documentation

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Framework Architecture](#framework-architecture)
4. [Event Lifecycle](#event-lifecycle)
5. [Event Publishing Process](#event-publishing-process)
6. [Event Listening](#event-listening)
7. [Exception Handling](#exception-handling)
8. [Event Tracking and Monitoring](#event-tracking-and-monitoring)
9. [Implementation Examples](#implementation-examples)
10. [Best Practices](#best-practices)
11. [Roadmap](#extending-the-framework)

## Introduction

EventFlow is a **lightweight, synchronous event management framework** inspired by Guava's EventBus. It provides a structured, stage-based event handling, ensuring clear separation of concerns, predictable execution, and precise visibility into event processing.

EventFlow simplifies event-driven architecture by enabling developers to:
- Define custom events.
- Create and register listeners for events.
- Publish events and handle them efficiently.

### What choose EventFlow?

EventFlow builds on traditional pub-sub model but introduces several intuitive features that make it stand out:

- **Structured Event Lifecycle**: Events follow distinct stages—Start, Before Persistence, After Persistence, Success, Failure, and End, providing unique purpose for each stage and ensures an organized processing.

- **Stage-Specific Execution**: Listeners' operations execute precisely at the right stage, ensuring predictable and error-free event handling. This precise execution completely eliminates uncertainty in event handling.

- **Transparent Processing**: Gain clear visibility into the entity, event, and stage you're working on, making the event processing flow transparent and easy to follow.

- **Developer-Friendly API**: Intuitive lifecycle hooks for each stage reduce boilerplate code and enhance modularity and maintainability.

- **Flexible Error Handling**: Provides global and event-specific error handlers for fine-grained control.

---

## Features

- **Synchronous Event Processing**: Ensures predictable execution within the current thread.
- **Well-Defined Lifecycle Stages**: Manages event progression through multiple stages like `Start`, `Pre Persistence`, `Post Persistence`, `Success`, `Failed`, and `End`.
- **Custom Listeners**: Enables stage-specific event handling.
- **Built-in Exception Handling**: Provides customizable exception handling strategies for the exceptions thrown from listeners.
- **Event Tracking**: Tracks event processing details, including execution time and listeners involved.

---

## Framework Architecture

The EventFlow Framework follows a modular design with clear separation of concerns:

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Event Sources  │────▶│    Publisher    │────▶│    Listeners    │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        │                       │                       │
        ▼                       ▼                       ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│ Event Lifecycle │     │Exception Handler│     │ Event Tracking  │
└─────────────────┘     └─────────────────┘     └─────────────────┘
        │                                               │
        ▼                                               ▼
┌─────────────────┐                           ┌─────────────────┐
│Storage Stack    │                           │   Monitoring    │
└─────────────────┘                           └─────────────────┘
```

EventFlow incorporates the following design patterns:
- **Observer Pattern**: For event publishing and subscription
- **Strategy Pattern**: For customizable exception handling.
- **Factory Pattern**: For dynamic listener instantiation.

## Event

An event represents a significant occurrence in the system that might interest multiple components. It is the core entity in the framework. It supports multiple lifecycle stages to allow precise control over event handling.

## Event Lifecycle

The event lifecycle is a core concept of the framework, providing a structured approach to event processing:

1. **START**: When an event is created and initialized
   - Initial context and data are set
   - Event is pushed to the storage stack
   - Listeners registered for this event are notified

2. **PRE_PERSISTENCE**: Before any data is persisted
   - Validation can occur at this stage
   - Data preparation can be performed
   - Listeners can modify or enhance event data

3. **POST_PERSISTENCE**: After data has been persisted
   - Additional operations that depend on persistence
   - Cascading operations can be triggered
   - Secondary validations can occur

4. **SUCCESS**: When the operation completes successfully
   - Confirmation operations
   - Notification of success to other systems
   - Cleanup of successful operations

5. **FAILED**: When the operation encounters an error
   - Error handling and recovery
   - Logging of failure details
   - Notification of failures

6. **END**: Final stage regardless of success or failure
   - Resource cleanup
   - Event is removed from the storage stack
   - Final metrics are collected

## Event Publishing Process

The event publishing process follows these steps:

1. An event is created and its lifecycle begins
2. The event publisher retrieves appropriate listeners from the registry
3. The listeners are called in the order they were registered
4. For each listener:
   - The listener's appropriate lifecycle method is called
   - Exceptions are handled through the exception handler
   - Metrics are recorded
5. When the event reaches the END stage:
   - Final metrics are logged
   - Listeners are cleaned up

## Event Listening

Listeners are components interested in specific types of events. They:
- Register to receive notifications for particular events
- Can act on specific stages in the event lifecycle
- Follow a type-safe design with generics, that allows to define exactly which event they're interested in
- Are instantiated for every event (and not for every stage)

To create a new listener:

1. Extend the `EventListener<E>` abstract class with your specific event type
2. Override the lifecycle methods you're interested in
3. Register your listener with the registry

The framework automatically handles:
- Type checking to ensure listeners receive appropriate events
- Instantiation of listeners when needed
- Routing events to the correct lifecycle method based on the event's current stage

## Listener Registry
The Listener Registry is responsible for managing event listeners, ensuring that each event type is associated with the correct set of listeners. It acts as a lookup table that maps events to their corresponding listeners, allowing the framework to efficiently invoke the appropriate handlers.

**Key Responsibilities**
1. **Registering Listeners:** Listeners are registered against specific event types.
2. **Fetching Listeners:** When an event is published, the registry retrieves all relevant listeners for execution. 
3. **Ensuring Type Safety:** Uses generics to enforce type compatibility between events and listeners.

The framework provides a `DefaultListenerRegistry` implementation that maintains an internal mapping of event types to listener classes.

## Event Storage Stack

The Event Storage Stack is a thread-local storage mechanism that maintains the hierarchy of events currently being processed within a thread. It allows tracking of nested executed events.

**Key Responsibilities**
1. **Maintaining Execution Context:** Ensures each thread retains a proper execution flow of event processing.
2. **Supporting Nested Events:** Allows one event to trigger another while maintaining processing order.
3. **Automatic Cleanup:** Events are removed from the stack when their lifecycle reaches the END stage.

## Exception Handling

The framework provides a robust exception handling mechanism:

1. Exceptions in listeners are caught by the publisher
2. The publisher delegates to a `ListenerExceptionHandler`
3. The default handler logs the exception and re-throws it
4. Custom handlers can be introduced for specialized behavior

The exception handling flow ensures:
- Errors are properly logged with required event and listener info
- Resources (instantiated listeners) are still properly cleaned up
- Custom recovery strategies can be implemented (by extending EventPublisher)

## Event Tracking and Monitoring

The framework includes built-in tracking capabilities:

1. Events are timestamped when created
2. Each event has a unique UUID
3. The `EventTracker` records:
   - Total processing time
   - Which listeners processed the event
   - Detailed logging of each stage

Monitoring information includes:
- Event names and IDs
- Processing duration
- Listener execution details
- Success/failure status

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/sujith-workshop/thread-event.git -b main
   ```
2. Navigate to the project directory:
   ```bash
   cd thread_event
   ```
3. Add the framework to your project as a dependency.

---

## Implementation Examples

### Complete Usage Example

```java
// 1. Create a custom event
public class OrderCreateEvent extends Event {
    private final Order order;
    
    public OrderCreateEvent(EventPublisher publisher, EventStorageStack stack, Order order) {
        super(publisher, stack);
        this.order = order;
    }
    
    public Order getOrder() {
        return order;
    }
}

// 2. Create listeners
public class OrderValidationListener extends EventListener<OrderCreateEvent> {
	
    @Override
    protected void onStart(OrderCreateEvent event) throws Exception {
        
        Order order = event.getOrder();
		
        if (order.getItems().isEmpty()) {
            throw new ValidationException("Order must contain at least one item");
        }
    }
}

public class InventoryUpdateListener extends EventListener<OrderCreateEvent> {
    
    @Override
    protected void afterPersistence(OrderCreateEvent event) throws Exception {
		
        Order order = event.getOrder();
        inventoryService.reduceStock(order.getItems());
    }
}

// 3. Register listeners
static {
    ListenerRegistry listenerRegistry = new DefaultListenerRegistry();
    listenerRegistry.register(OrderValidationListener.class);
    listenerRegistry.register(InventoryUpdateListener.class);
}

// 4. Use the event in business logic
public void createOrder(Order order) {
    OrderCreateEvent event = new OrderCreateEvent(publisher, storageStack, order);
    
    try (AutoManagedEvent managed = new AutoManagedEvent(event)) {
        // Pre-persistence actions
        event.prePersist();
        
        // Persist the order
        orderRepository.save(order);
        
        // Post-persistence actions
        event.postPersist();
        
        // Mark as successful
        event.success();
    } catch (Exception e) {
        // The AutoManagedEvent will handle fail() and end()
        log.error("Order creation failed", e);
        throw new OrderCreationException(e);
    }
}
```

---

## Best Practices
To ensure optimal usage of the EventFlow Framework, follow the [Best Practices Guide](https://github.com/sujith-workshop/thread-event/blob/main/Best%20Practices%20Guide.md). It provides detailed recommendations for event handling, listener management, and performance optimizations.

---

## Contributing
We welcome contributions to enhance EventFlow! Follow these steps:

1. Fork the repository.
2. Create a feature branch.
3. Submit a merge request describing your changes.

---

## Roadmap
- **Asynchronous Event Handling**: Enable asynchronous listener execution by pushing the event to Message broker like Kafka, ZQueue at the end of the event processing.

EventFlow provides a structured, predictable, and scalable approach to event-driven development. Start using it today to simplify event management in your applications!