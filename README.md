
# Thread Event Framework

Thread Event is a lightweight, pub-sub-based event framework that allows developers to build loosely-coupled, scalable systems by handling events in a structured and extensible way.

---

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [Installation](#installation)
4. [Usage](#usage)
5. [Demo](#demo)
6. [Contributing](#contributing)
7. [License](#license)

---

## Overview

The Thread Event framework simplifies event-driven architecture by enabling developers to:
- Define custom events.
- Implement event lifecycle methods.
- Register listeners for events.
- Publish events and handle them efficiently.

This framework is ideal for projects that require clear separation of concerns and a robust mechanism for event handling.

---

## Features

- **Event Lifecycle Management**: Handle events across multiple stages (START, PRE_PERSISTENCE, POST_PERSISTENCE, SUCCESS, FAILED, END).
- **Custom Listeners**: Implement specialized listeners for specific event types.
- **Exception Handling**: Handle errors in listeners gracefully with a pluggable `ListenerExceptionHandler`.
- **Event Tracking**: Log event processing duration and track listener performance.
- **Extendable Design**: Easily extend or customize functionality.

---

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/sujith-workshop/thread-event.git
   ```
2. Navigate to the project directory:
   ```bash
   cd thread-event
   ```
3. Add the framework to your project as a dependency.

---

## Usage

To start using the framework, follow these steps:

1. **Define Your Events**:  
   Extend the `Event` class to create custom events.

2. **Create Listeners**:  
   Implement `EventListener` for your custom event to define behavior for various lifecycle stages.

3. **Register Listeners**:  
   Use the `ListenerRegistry` to register listeners.

4. **Publish Events**:  
   Use `EventPublisher` to publish events and handle them via registered listeners.

---

## Demo

Below is a quick demo illustrating the usage of the framework:

### Sample Event
```java
public class SampleEvent extends Event {
    public SampleEvent() {
        super();
    }
}
```

### Sample Listener
```java
public class SampleEventListener extends EventListener<SampleEvent> {
    @Override
    protected void onStart(SampleEvent event) throws Exception {
        System.out.println("Handling START stage of SampleEvent: " + event.getEventId());
    }
}
```

### Main Class
```java
public class ThreadEventDemo {
    public static void main(String[] args) throws Exception {
        ListenerRegistry registry = new DefaultListenerRegistry();
        EventPublisher publisher = new EventPublisherBuilder(registry)
            .setLogger(Logger.getLogger("ThreadEventDemo"))
            .build();

        SampleEventListener sampleListener = new SampleEventListener();
        registry.register(sampleListener);

        SampleEvent event = new SampleEvent();
        publisher.publish(event);

        // Switch to the next stage and publish again
        event.setEventStage(EventStage.END);
        publisher.publish(event);
    }
}
```

### Output
```
INFO: Publishing event: SampleEvent (ID: xxxx) with stage: START
INFO: Invoking listener: SampleEventListener for event SampleEvent
Handling START stage of SampleEvent: xxxx
INFO: Listener SampleEventListener processed event SampleEvent successfully
INFO: Publishing event: SampleEvent (ID: xxxx) with stage: SUCCESS
...
```

---

## Contributing

We welcome contributions to improve the framework! Here’s how you can help:
1. Fork the repository.
2. Create a branch for your feature or bug fix.
3. Submit a pull request explaining your changes.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---
