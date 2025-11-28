# CS4928 Café POS System - Group 17

This system encompasses a Point-Of-Sale system for a café using Java. Across the course of this project, we learnt several new programming practices, paradigms, and principles that have assisted us in the creation of easily testable, extensible, modular and flexible code. These include things such as the enforcement of the SOLID principles during the project creation process, and the implementation of various design patterns. Through following the lab steps and implementing these practices, we've produced a result that we're happy with, which fulfills the POS Café system that was the primary objective of the project, but also remains extensible and flexible despite its wide array of functions.

## SOLID Principles

This project demonstrates adherence to SOLID principles throughout the codebase:

| Principle             | Description                                         | Implementation Example                                                                                                                                                                                                                                 |
|-----------------------|-----------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Single Responsibility | Each class has one reason to change                 | `DiscountPolicy` - calculates discounts<br>`TaxPolicy` - calculates tax<br>`ReceiptPrinter` - formats receipts<br>`CheckoutService` - orchestrates checkout<br>`OrderController` - handles user commands                                               |
| Open/Closed           | Open for extension, closed for modification         | Add new discount types by implementing `DiscountPolicy` interface<br>Add payment methods by implementing `PaymentStrategy` interface<br>Add order states by implementing `State` interface<br>No existing code needs modification                      |
| Liskov Substitution   | Subtypes must be substitutable for their base types | Any `DiscountPolicy` implementation works with `PricingService`<br>Any `PaymentStrategy` works with `Order.pay()`<br>Any `State` implementation works with `OrderFSM`<br>Any `OrderRepository` implementation works in controllers                     |
| Interface Segregation | Clients shouldn't depend on methods they don't use  | `DiscountPolicy` and `TaxPolicy` are separate interfaces<br>`OrderObserver` has single `updated()` method<br>`Command` interface has optional `undo()`<br>Small, focused interfaces prevent forced implementations                                     |
| Dependency Inversion  | Depend on abstractions, not concretions             | `CheckoutService` depends on `DiscountPolicy`/`TaxPolicy` interfaces<br>`OrderController` depends on `OrderRepository` interface<br>`OrderFSM` depends on `State` interface<br>Infrastructure implements domain interfaces (`InMemoryOrderRepository`) |

---

## Design Patterns Implemented

This project implements the following design patterns:
- **Command** - Decouples button presses from domain logic with undo/macro support
- **Adapter** - Integrates legacy printer without modifying core code
- **Composite** - Hierarchical menu structures
- **Iterator** - Traverses composite menu structures and filtering
- **State** - Models order lifecycle with explicit state transitions
- **Strategy** - Payment methods and pricing policies
- **Observer** - Order notifications
- **Decorator** - Product customization
- **Factory** - Product creation from recipe strings
- **MVC** - Separation of concerns between Model, View, and Controller

---

## Architecture Overview

The system follows a four-layer architecture:

### 1. Presentation Layer (`com.cafepos.ui`)
**Responsibility**: Handle user interaction and display

**Components**:
- `OrderController` - MVC Controller, handles user commands
- `ConsoleView` - MVC View, displays output to console
- `EventWiringDemo` - Demonstrates event-driven UI updates

### 2. Application Layer (`com.cafepos.app`, `com.cafepos.app.events`)
**Responsibility**: Coordinate application workflows and use cases

**Components**:
- `CheckoutService` - Orchestrates checkout process with pricing
- `ReceiptFormatter` - Formats receipts for display
- `EventBus` - Pub-sub event system for decoupling
- Events: `OrderCreated`, `OrderPaid`, `OrderEvent`

### 3. Domain Layer (Multiple packages organized by business concept)
**Responsibility**: Core business logic and rules

**Packages**:

#### Core Domain (`com.cafepos.domain`)
- `Order` - Order aggregate root
- `LineItem` - Order line items
- `OrderIds` - Order ID generation
- `OrderRepository` - Repository interface (domain contract)
- `OrderObserver`, `OrderPublisher` - Observer pattern interfaces
- `KitchenDisplay`, `DeliveryDesk`, `CustomerNotifier` - Observer implementations

#### Common/Shared Kernel (`com.cafepos.common`)
- `Money` - Money value object (prevents primitive obsession)
- `Product` - Product interface
- `SimpleProduct` - Basic product implementation
- `Priced` - Pricing interface

#### Product Customization (`com.cafepos.decorator`)
- `ProductDecorator` - Base decorator
- `ExtraShot`, `OatMilk`, `SizeLarge`, `Syrup` - Concrete decorators for product customization

#### Payment (`com.cafepos.payment`)
- `PaymentStrategy` - Strategy interface
- `CashPayment`, `CardPayment`, `WalletPayment` - Payment strategy implementations

#### Pricing (`com.cafepos.pricing`)
- `PricingService` - Domain service for price calculations
- `DiscountPolicy` - Strategy interface for discounts
- `NoDiscount`, `LoyaltyPercentDiscount`, `FixedCouponDiscount` - Discount implementations
- `TaxPolicy` - Strategy interface for tax calculation
- `FixedRateTaxPolicy` - Tax policy implementation
- `DiscountPolicyFactory` - Factory for creating discount policies
- `ReceiptPrinter` - Receipt formatting logic

#### Order Lifecycle (`com.cafepos.state`)
- `State` - State interface
- `OrderFSM` - Finite state machine context
- `NewState`, `PreparingState`, `ReadyState`, `DeliveredState`, `CancelledState` - Order state implementations

#### Menu System (`com.cafepos.menu`)
- `MenuComponent` - Component base class
- `Menu` - Composite for hierarchical menus
- `MenuItem` - Leaf node for individual menu items
- `CompositeIterator` - Iterator for traversing menu hierarchy

#### Product Creation (`com.cafepos.factory`)
- `ProductFactory` - Factory for creating decorated products from recipe strings

#### Command System (`com.cafepos.command`)
- `Command` - Command interface
- `AddItemCommand`, `PayOrderCommand`, `MacroCommand` - Command implementations
- `PosRemote` - Command invoker with undo support
- `OrderService` - Domain service acting as command receiver

**Note**: The domain layer is organized by business concept rather than technical layer. Each package represents one
domain concept (payment strategies, order lifecycle, pricing rules, etc.), following Domain-Driven Design principles.

### 4. Infrastructure Layer (`com.cafepos.infra`, `com.cafepos.catalog`, `com.cafepos.printing`)
**Responsibility**: Technical implementations and external system adapters

**Packages**:

#### Persistence (`com.cafepos.infra`)
- `InMemoryOrderRepository` - In-memory implementation of OrderRepository
- `Wiring` - Dependency injection / component wiring configuration

#### Catalog (`com.cafepos.catalog`)
- `Catalog` - Catalog interface
- `InMemoryCatalog` - In-memory product catalog implementation

#### External Systems (`com.cafepos.printing`)
- `Printer` - Modern printer interface
- `LegacyPrinterAdapter` - Adapter for integrating legacy thermal printer

## Architecture Decision Records (ADRs)

This project includes Architecture Decision Records documenting key architectural choices:

- **ADR 1**: EventBus for UI-Application Decoupling (`docs/ADR-1-eventbus-for-decoupling.md`)
- **ADR 2**: Four-Layer Architecture (`docs/ADR-2-four-layer-architecture.md`)
- **ADR 3**: InMemory Repository (`docs/ADR-3-inmemory-repository.md`)

Each ADR documents the context, alternatives considered, decision made, and consequences.

## Testing

The project includes comprehensive test coverage with 13 test classes:
- Command and Adapter pattern tests
- Composite and State pattern tests
- Integration tests
- Domain logic tests
- Payment strategy tests
- Pricing and discount tests

# Lab Reflections

Below are the lab reflections required to be placed within the README. Additional reflections required across the labs were submitted in the form of .txt files.

## Factory vs Manual chaining for application developers

* The Factory pattern (e.g. "ESP+SHOT") makes more sense for exposing to application developers. It provides a simple, string-based interface to enters orders into and is much easier to use and less error prone that wrapping each object manually. Manual chaining requires application developers to understand how decorators are handled and manage null checking themselves, while the Factory pattern allows us to wrap it all in one neat string, also making for much easier UI and API integration in the future.

## Week 6 Refactoring Reflection

**Smells Removed:** 
God Class and Long Method in OrderManagerGod.process() (9 responsibilities placed into separate classes).
Primitive Obsession (discount/tax strings that get used are now contained within relative classes).
Global/Static State (TAX_PERCENT, LAST_DISCOUNT_CODE → constructor injection).
Duplicated Logic (inline Money/BigDecimal math now encapsulated in policies).
Shotgun Surgery risk (tax/discount logic spread throughout OrderManagerGod is now centralized in policy classes).

**Refactorings Applied:** 
Extracted classes (DiscountPolicy (different discount types implement it), TaxPolicy, PricingService, ReceiptPrinter).
Replaceed conditional with polymorphism (different discount types).
Introduce strategy pattern (DiscountPolicy, TaxPolicy).
Extract Method (pricing pipeline).
Dependency Injection (CheckoutService constructor).
Remove Global State (new approach eliminates static variables).

**SOLID Principles Satisfied:** 
Single Responsibility (each class has one reason to change: DiscountPolicy calculates discounts, TaxPolicy calculates tax, ReceiptPrinter formats, CheckoutService orchestrates)
Open/Closed (can add new discount or tax types by creating new DiscountPolicy/TaxPolicy implementations without modifying existing code).
Liskov Substitution (all DiscountPolicy implementations work when they're passed into PricingService without it knowing which one).Interface Segregation (clients wouldn't be forced to implement unused methods, e.g. DiscountPolicy and TaxPolicy are independent of one another in terms of the methods they implement).
Dependency Inversion (CheckoutService depends on abstractions: DiscountPolicy/TaxPolicy interfaces, not concrete implementations).

**Adding New Discount Type:** 
Create a new class that implements the DiscountPolicy interface, implement the discountOf(Money) method with the discount logic, optionally add a new corresponding token to DiscountPolicyFactory.create() if factory-based creation is required. No changes required to any of the other classes implementing DiscountPolicy.

## Week 10 Layering vs Partitioning Trade-offs Reflection

**Layered Monolith vs. Microservices:**
We chose to keep everything within a layered monolith for now to prioritise simplicity during this project, and
maintain focus on implementing the design patterns studied during this module, as well as to speed up development,
since maintaining separation of microservices could provide an additional layer of consideration in the project.
A layered monolith also means easier debugging since we'd have the full stack trace upon any error occurring.

**Natural Candidates for Future Partitioning:**
The payment package (`com.cafepos.payment`) would be a clear seam for future partitioning, as it could become a 
Payment service that could even be integrated with a real-world payment API like Stripe in the future.

The observer pattern we've implemented in `com.cafepos.domain` could also be partitioned into a separate Notification 
service, a popular choice when incorporating microservices into a system.

A final possible service could include the `com.cafepos.menu` package, creating a separate Menu service, which could be 
even more useful if the café menu required frequent updates and changes.

**Connectors/Protocols to Define if Splitting:**
For each microservice, we could define REST API endpoints for important actions associated with them, e.g. 
`POST /api/payments/{orderId}` for processing the payment of a particular order. For communication between 
microservices, we could implement a messaging broker like Kafka or RabbitMQ. Connectors between these packages in the 
current monolith code, e.g. EventBus, could be displaced by these message brokers.
