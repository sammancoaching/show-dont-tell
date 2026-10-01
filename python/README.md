# Show, Don't Tell — Python exercise workspace

Python track for the "Show, Don't Tell — Using Examples with Coding Agents" exercise (Python 3.13 / uv / pytest).

## Prerequisites

- Python 3.13 and [uv](https://docs.astral.sh/uv/), versions pinned in `mise.toml`
- If you use [mise](https://mise.jdx.dev): `mise install` installs both

## Set up and run the tests

```sh
uv sync
uv run pytest
```

## Layout

| Directory | Contents |
| --- | --- |
| `src/order_domain`, `tests/order_tests` | Order domain with discount behaviour, and its tests (Round 1) |
| `src/order_book_domain`, `tests/order_book_tests` | OrderBook state machine, and its integration tests (Round 2) |

The exercise starts in its START state: all tests are green, the refactoring participants should do is NOT applied. The custom matcher example lives in `tests/order_tests` (`order_state.py`).
