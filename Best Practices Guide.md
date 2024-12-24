# Best Practices for Using EventFlow Framework

This document provides a comprehensive guide to best practices when working with the **EventFlow Framework**. Following these guidelines will help you create robust, maintainable, and scalable event-driven systems.

---

## Event Stages and Best Practices

The EventFlow Framework organizes event processing into well-defined lifecycle stages. Each stage has a specific role and recommended guidelines for optimal use.

---

### **Start Stage**

The **Start** stage is the entry point of the event lifecycle, responsible for initializing and preparing the event.

#### Recommended Tasks:

- Initialize required event data.
- Perform validations to ensure the event is suitable for processing.
- Derive and enrich event-related details necessary for subsequent stages.

#### Avoid:

- Performing database (write) operations, as this stage is meant to execute before the bean.

---

### **Before Persistence Stage**

This stage allows operations required before persisting the event data.

#### Recommended Tasks:

- Handle operations like initializations, validations, derivations that couldn't be done in the **Start** stage.
- Execute final checks or preparations before the event is saved.

#### Avoid:

- Repeating validations or derivations already handled in the **Start** stage.

---

### **After Persistence Stage**

This stage is used for tail operations closely tied to the entity's persistence.

#### Recommended Tasks:

- Execute only operations that must succeed with the entity's persistence.
  Keep logic lightweight to reduce rollback risks.
- Log and capture failures for debugging.

#### Important Notes:

- If a failure occurs in this stage, the event will be rolled back.
- Move non-critical operations to **End**, **Success**, or **Failure** stages to improve performance, as this stage holds an open database instance (bean).

---

### **Success Stage**

This stage handles actions after the event has been successfully processed.

#### Recommended Tasks:

- Send notifications (e.g., email, push alerts).
- Trigger downstream workflows or updates in dependent systems.
- Log success metrics for observability.

#### Avoid:

- Holding or modifying the persisted entity instance.

#### Best Practices:

- Gradually move operations from **After Persistence** to **Success** to quickly release the db connection (by closing the bean) and improve performance.

---

### **Failure Stage**

This stage is invoked when event processing fails. It allows for compensating, fallback, or recovery mechanisms.

#### Recommended Tasks:

- Log failure metrics and error details for debugging.
- Notify relevant stakeholders or systems about the failure.
- Trigger compensating actions or clean-ups.
- Ensure appropriate mechanisms are in place to revert partial changes.

#### Avoid:

- Introducing heavy recovery logic; delegate to asynchronous mechanisms if needed.

---

### **End Stage**

This stage is for final operations that do not depend on the event's result (success or failure).

#### Recommended Tasks:

- Clean up resources or temporary data.
- Log event closure metrics for to monitor event performance.

#### Best Practices:

- Prioritize asynchronous execution for non-critical tasks.

---
## Additional Best Practices

**Listener Registry Best Practices**
* **Ensure Proper Registration:** Listeners should be registered at application startup. Register dynamically only when absolutely necessary.
* **Use Dedicated Registries for Each Module:** Keep separate registries for each module to avoid conflicts and maintain separation of concerns.
* **Use as Singleton for Cross-Module Communication:** If your Listener Registry needs to support cross-module event communication, ensure it is implemented as a **singleton**. This allows external modules to register their own listeners and subscribe to your events seamlessly, promoting extensibility and modularity.

**Event Storage Stack Best Practices**
_(These practices are already handled by the framework using the default `Event` and `AutoManagedEvent` classes. In case of customization, they must be ensured manually.)_
* **Manage Event Stack Efficiently:** Always `push()` an event when it starts and `popUntil()` it when it ends.
* **Prevent Stack Pollution:** Do not leave events in the stack unnecessarily.
* **Ensure CleanUp:** Ensure `event.end()` is always called to remove the event from the stack.

**Nested Event Handling**
* Manage Dependencies Carefully: If one event depends on another, ensure it does not create a deadlocks or block/hold the execution indefinitely.

---

## General Guidelines

### Do's

1. **Update event details before moving to the next stage** to ensure listeners receive the latest information.
2. **Trigger Events (stages) from a single, dedicated Point** to maintain a well-defined API and avoid redundancy.
3. **Use Separate Listener Classes** for each listening module to keep them decoupled and maintainable.
4. **Gradually migrate long-running and non-critical operations** from **After Persistence** to non-bean stages (**Success**, **Failure**, and **End**) to improve performance. Later, consider moving these operations to asynchronous tasks (e.g., Kafka) if possible.

### Don'ts

1. **Do not pass data between stages using event variables** to avoid unintended dependencies.
2. **Avoid Database Writes in Start Stage**; instead, use **Before Persistence** for such operations.
3. **Do not misuse event stages**: Use stages for their intended purpose and avoid mixing responsibilities to maintain clarity.
4. **Always handle exceptions and log errors** to prevent hidden failures.

---

## Final Thoughts
By following these best practices, you can:
✅ Ensure a **structured and scalable** event-driven system.
✅ Maintain **clear separation of concerns** across event stages.
✅ Improve **performance and maintainability** of event processing.

Implement these practices to harness the full potential of the EventFlow Framework and build resilient, efficient event-driven architectures! 🚀
