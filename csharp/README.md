# Show, Don't Tell — C# exercise workspace

Workspace for the "Show, Don't Tell — Using Examples with Coding Agents" exercise (C# / .NET 10 / NUnit).

## Prerequisites

- .NET 10 SDK (version pinned in `global.json`)
- If you use [mise](https://mise.jdx.dev): `mise install` installs the SDK listed in `mise.toml`

## Build and run the tests

```sh
dotnet build
dotnet test
```

## Layout

| Directory | Contents |
| --- | --- |
| `src/OrderDomain`, `tests/OrderTests` | Order domain with discount behaviour, and its tests |
| `src/OrderBookDomain`, `tests/OrderBookTests` | OrderBook state machine, and its integration tests |

Open `ShowDontTell.sln` in your IDE (Rider, Visual Studio, VS Code with the C# extension).
