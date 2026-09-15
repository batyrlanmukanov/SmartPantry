# Requirements Checklist

## Course extraction

From the final project requirement file and lecture slides, the final submission has two layers:

1. Android engineering criteria:
- Multi-module architecture
- Deeplinking
- Clean architecture
- Dependency Injection
- Tests
- Build variants and flavors

2. SIS / project presentation criteria:
- Relevance, novelty, and practical significance
- Competitive analysis with 3 competitors
- Statistical analysis
- Architecture diagrams: use-case, sequence, class, ER
- More than 4 functionalities including authentication, user profile, AI assistant, and main features

## How this generated project satisfies them

### Android engineering

- `Multi-module architecture`: implemented with `app`, `core:*`, and `feature:*` modules.
- `Deeplinking`: implemented for inventory detail and assistant screens.
- `Clean architecture`: repository contracts and use cases live in `core:domain`; implementations live in `core:data`; UI lives in `feature:*`.
- `Dependency Injection`: implemented with Hilt.
- `Tests`:
  - unit tests in `core:domain` for analytics and assistant insight logic
  - instrumented UI test in `app` for sign-in to inventory flow
  - instrumented integration test in `app` for auth -> assistant -> message flow
- `Build variants & flavors`: `demo` and `full` flavors in the `app` module.

### SIS / startup criteria

- `Relevance`: food waste is a real household problem validated by your user research.
- `Novelty`: combines visual inventory, waste analytics, and assistant logic in a student-focused Android product.
- `Practical significance`: helps users reduce waste, save money, and lower cognitive load.
- `Competitive analysis`: see [competitive-analysis.md](./competitive-analysis.md).
  - benchmark matrix + positioning + SWOT: see [competitive-benchmark.md](./competitive-benchmark.md)
- `Statistical analysis`: your Lab 3 results are integrated into product decisions:
  - `r = 0.41` for forgetfulness vs visual inventory
  - `r = 0.10` for automation vs perceived waste reduction
  - `p = 0.33` for students vs employees
  - before/after evaluation plan: see [methodology-before-after.md](./methodology-before-after.md)
- `Architecture diagrams`: see [architecture-diagrams.md](./architecture-diagrams.md).
- `Test strategy`: see [test-strategy.md](./test-strategy.md).
- `4+ functionalities`: auth, profile, inventory management, assistant, analytics, item detail, food status updates.
  - auth flow now includes both sign up and sign in modes
  - auth form validates email format, password length, and confirm password on registration
  - smart notifications with reminder-hour targeting and 1-2 day expiry focus
  - shopping list with manual add + auto-generation from expiry and stock gaps
  - quick actions (consume, waste, move storage, quick edit qty/expiry)
  - assistant cook-now mode for <=2 day expiry prioritization
  - analytics KPI upgrade (saved KZT, wasted KZT, used-before-expiry %, projected weekly trend)

## Functional mapping from Lab 2

Mapped directly from your QCA:

- `FR1 Product registration`: add item manually
- `FR2 Inventory database`: Room local storage
- `FR3 Expiration monitoring`: inventory sorted by urgency and expiring-soon signals
- `FR4 Inventory visualization`: main inventory screen
- `FR5 Recommendation engine`: assistant screen
- `FR6 Usage prioritization`: assistant highlights items close to expiry
- `FR7 Smart shopping list`: represented through low-stock and restock advice logic
- `FR8 Consumption analytics`: analytics screen
- `FR9 Storage guidance`: assistant tips by category
- `FR10 Offline functionality`: local-first storage using Room and DataStore
- `FR11 Behavioral feedback`: analytics screen
- `FR12 Personalization module`: profile-based recommendations

## What to show during defense

1. Open `demoDebug` flavor.
2. Show login.
3. Show inventory with preloaded items.
4. Open a product by deeplink.
5. Show assistant recommendations.
6. Show analytics and explain how Lab 3 findings shaped the UI.
7. Show profile and personalization.
8. Show module structure in Android Studio.
9. Run tests:
   - `./gradlew :core:domain:test`
   - `./gradlew :app:connectedDemoDebugAndroidTest`
