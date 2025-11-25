# ADR 2: Four-Layer Architecture

## Context
As our Café POS system grew from simple demos to a complete application with multiple patterns,
we needed a clear architectural structure to organize code and maintain separation of functions.
Without clear boundaries, the system risked becoming extremely messy, and business logic,
UI, and infrastructure became entangled.

We needed to support:
- Multiple UI possibilities (console, GUI, web)
- Different data storage options (in-memory, database)
- Clear business rules that don't depend on technical details
- Testability at each layer
- Progressive addition of design patterns without breaking existing code

## Decision
We adopted a classic four-layer architecture with domain logic organized by business concept:

### 1. Presentation Layer (`com.cafepos.ui`)
**Responsibility**: Handle user interaction and display

**Components**:
- `OrderController` - MVC Controller, handles user commands
- `ConsoleView` - MVC View, displays output to console
- `EventWiringDemo` - Demonstrates event-driven UI updates

---

### 2. Application Layer (`com.cafepos.app`, `com.cafepos.app.events`)
**Responsibility**: Coordinate application workflows and use cases

**Components**:
- `CheckoutService` - Orchestrates checkout process with pricing
- `ReceiptFormatter` - Formats receipts for display
- `EventBus` - Pub-sub event system for decoupling
- Events: `OrderCreated`, `OrderPaid`, `OrderEvent`

---

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

---

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

---

**Dependency Direction**: Presentation → Application → Domain ← Infrastructure

This means:
- Presentation depends on Application
- Application depends on Domain
- Infrastructure depends on Domain
- Domain depends has no dependencies

## Alternatives Considered

### 1. Hexagonal Architecture (Ports & Adapters)
- **Pros**: Very clean separation, domain completely isolated, highly testable.
- **Cons**: Perhaps somewhat overly complex for a project of this size.

### 2. Two-Tier (UI + Backend)
- **Pros**: Simpler, fewer layers, less overhead, faster initial development.
- **Cons**: Business logic tends to leak into UI or data access, harder to test and doesn't scale as well.

### 3. Three-Layer (Presentation, Business, Data)
- **Pros**: Simpler but still clear separation, easy to understand.
- **Cons**: Application services and domain logic could get mixed, coordination logic unclear.

## Consequences

### Positive Consequences
- **Clear separation**: Each layer has a single, well-defined responsibility
- **Testability**: Can test each layer independently without external dependencies
- **Flexibility**: Easy to swap UI (console → GUI) or storage (in-memory → database) without changing business logic
- **Maintainability**: Developers know exactly where code belongs based on its responsibility
- **Scalability**: Can split into microservices later down the line if needed
- **Learning support**: Architecture makes it easy to demonstrate progressive pattern addition
- **Parallel development**: Teams can work on different layers simultaneously

### Negative Consequences
- **Boilerplate**: More packages and structure than simpler approaches
- **Overhead**: Sometimes data flows through multiple layers
- **Navigation complexity**: Related code may be spread across multiple packages

## Notes

The architecture is demonstrated in:
- `Week10Demo_MVC.java` - Shows MVC pattern within this architecture
- `Wiring.java` - Shows how components are wired together across layers
- Test files show how each layer can be tested independently (13 test classes)