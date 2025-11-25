# ADR 3: InMemory Repository for Order Persistence

## Context
Our Café POS system needs to persist Order objects so they can be retrieved later 
(e.g., for checkout after adding items). We needed to decide between using a real database 
or an in-memory solution for this project.

Constraints:
- Focus on design patterns, not database technology
- Need to demonstrate repository pattern
- Must support testing without external dependencies

## Decision
We implemented `InMemoryOrderRepository` using a `HashMap<Long, Order>` to store orders in memory. 
The repository implements the `OrderRepository` interface, allowing us to swap implementations later if needed.

Implementation: `InMemoryOrderRepository.java`

## Alternatives Considered

### 1. SQL Database (e.g. PostgreSQL)
- **Pros**: Persistent across restarts, industry-standard, complex queries, data integrity.
- **Cons**: Requires setup and configuration, adds external dependency, slower for testing, focus shifts away from design patterns.

### 2. File-Based Storage (JSON/XML)
- **Pros**: Simple persistence, no database setup, human-readable.
- **Cons**: Slow for large datasets, serialization complexity, file I/O overhead.

### 3. NoSQL Database (e.g., MongoDB)
- **Pros**: Flexible schema, easy document storage.
- **Cons**: External dependency, setup overhead, adds complexity.

## Consequences

### Positive Consequences
- **Simplicity**: No database setup or configuration needed.
- **Fast execution**: Tests run instantly without database connection.
- **No external dependencies**: Project runs anywhere with just Java.
- **Focus on patterns**: We can focus on design patterns over database concerns.
- **Interface-based**: Easy to swap for real database via `OrderRepository` interface.

### Negative Consequences
- **Not persistent**: All data lost when application stops
- **Limited querying**: Can't do complex queries like "all orders today"
- **Memory constraints**: Large order volumes would exhaust memory
- **No transactions**: Can't roll back if operation fails partway

## Notes
For a project focusing on design patterns, the benefits of simplicity and quick access 
outweigh the lack of persistence. The repository interface abstraction means we could add a real database 
implementation later without changing application or domain code.

If this were to be a production-ready system, we would replace `InMemoryOrderRepository` with a real database, like SQL or NoSQL.