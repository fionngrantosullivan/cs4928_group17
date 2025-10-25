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
