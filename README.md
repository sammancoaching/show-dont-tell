# Show, Don't Tell — Using Examples with Coding Agents

Exercise repository for the learning hour "Show, Don't Tell — Using Examples with Coding Agents". The same two exercises ship in three language tracks: participants refactor multi-assert tests by showing their coding agent one refactored example instead of describing the change in words.

## Choose your track

All three tracks contain the identical exercises — same domain behaviour, same test scenarios, same numbers. Pick the track that matches your participants' stack:

| Track | Directory | Toolchain | Run the tests |
| --- | --- | --- | --- |
| C# | `csharp/` | .NET 10, NUnit | `dotnet test` |
| Java | `java/` | Java 21 (Temurin), Maven, JUnit 5, AssertJ | `mvn test` |
| Python | `python/` | Python 3.13, uv, pytest | `uv sync && uv run pytest` |

Toolchain versions are pinned per track in that directory's `mise.toml`. If you use [mise](https://mise.jdx.dev), run `mise install` inside the track directory you choose; otherwise install the toolchain yourself. Each track has its own `README.md` with details.

## How the exercise works

Every track starts in the same START state: **all tests are green, and the refactoring the participants should do has NOT been applied.**

- **Round 1 — OrderDomain**: six tests assert order totals and line values with scattered, duplicated assertions. The first test has already been refactored as the example, using a custom matcher (`OrderStateConstraint` in C#, `OrderStateAssertion` in Java, `order_state.py` in Python). Participants show that example to their agent and let it refactor the remaining five.
- **Round 2 — OrderBookDomain**: seven multi-step given/when/then tests that each walk an order through several lifecycle states. Participants use the same show-an-example approach to split them into focused tests.

The point of the learning hour: LLMs are pattern matchers — a concrete example communicates the intended refactor faster and more reliably than a verbal description.
