# ADR 1: Use EventBus for UI-Application Decoupling

## Context
In our Café POS system, the UI layer needs to be notified of application events (order created, order paid) 
without creating tight coupling between presentation and application layers. 
We needed a way to communicate across architectural boundaries while maintaining clean separation of concerns.

The system has four layers (Presentation, Application, Domain, Infrastructure) and we wanted to avoid:
- Direct dependencies from Application → UI
- Violation of dependency inversion principle
- Tight coupling that makes testing difficult

## Decision
We implemented an EventBus using a publish-subscribe pattern. The EventBus:
- Is contained within the `com.cafepos.app.events` package.
- Uses typed events (`OrderCreated`, `OrderPaid`) that implement the `OrderEvent` interface.
- Allows any component to subscribe to specific event types.
- Allows any component to emit events without knowing subscribers.

Implementation seen in: `EventBus.java`, `EventWiringDemo.java`

## Alternatives Considered

### 1. Direct Method Calls from Application to UI
- **Pros**: Simple and straightforward.
- **Cons**: Creates direct dependency from Application → UI, enforces tight coupling.

### 2. Observer Pattern on Domain Objects
- **Pros**: Already built into domain model, no additional infrastructure.
- **Cons**: Domain objects would know about UI concerns, mixing business logic with notification logic.

## Consequences

### Positive Consequences
- **Decoupling**: Application layer has zero knowledge of UI.
- **Testability**: Can test event emission without UI present.
- **Extensibility**: Easy to add new event listeners without modifying existing code.
- **Type safety**: Typed events prevent runtime errors.

### Negative Consequences
- **Indirection**: Harder to trace event flow through code
- **Runtime coupling**: Event subscriptions not visible at compile time
- **No guarantees**: No compile-time check that events have subscribers
- **Debugging complexity**: Event flow is less explicit than direct calls

## Notes
The EventBus is demonstrated in `EventWiringDemo.java` where we show:
- Subscribing to `OrderCreated` and `OrderPaid` events
- Emitting events after operations
- UI components reacting to application events without direct coupling