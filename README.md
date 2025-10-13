\# Factory vs Manual chaining for application developers



* The Factory pattern (e.g. "ESP+SHOT") makes more sense for exposing to application developers. It provides a simple, string-based interface to enters orders into and is much easier to use and less error prone that wrapping each object manually. Manual chaining requires application developers to understand how decorators are handled and manage null checking themselves, while the Factory pattern allows us to wrap it all in one neat string, also making for much easier UI and API integration in the future.
