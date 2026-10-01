# Show, Don't Tell — Java exercise workspace

Java track for the "Show, Don't Tell — Using Examples with Coding Agents" exercise (Java 21 / Maven / JUnit 5 / AssertJ).

## Prerequisites

- Java 21 (Temurin) and Maven, versions pinned in `mise.toml`
- If you use [mise](https://mise.jdx.dev): `mise install` installs both

## Build and run the tests

```sh
mvn test
```

## Layout

| Module | Contents |
| --- | --- |
| `order-domain`, `order-tests` | Order domain with discount behaviour, and its tests (Round 1) |
| `order-book-domain`, `order-book-tests` | OrderBook state machine, and its integration tests (Round 2) |

The exercise starts in its START state: all tests are green, the refactoring participants should do is NOT applied. The custom matcher example lives in `order-tests` (`OrderStateAssertion`).
