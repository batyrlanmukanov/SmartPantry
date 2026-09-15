# SmartPantry

SmartPantry is a multi-module Android application for managing household food supplies and reducing food waste.

## Project goals

- Reduce forgotten and expired food through visual inventory tracking.
- Support decision-making with a rule-based AI assistant.
- Provide authentication, profile management, analytics, and inventory workflows.
- Demonstrate `multi-module architecture`, `clean architecture`, `dependency injection`, `deeplinking`, `build flavors`, and `tests`.

## Modules

- `app`: navigation, deeplinks, flavor config, app shell
- `core:model`: shared domain models
- `core:domain`: repository contracts and use cases
- `core:database`: Room entities, DAO, database
- `core:data`: repository implementations and DI bindings
- `core:ui`: Compose theme and reusable UI widgets
- `feature:auth`: local authentication flow
- `feature:inventory`: food inventory, add item, item detail, status updates
- `feature:assistant`: AI assistant recommendations
- `feature:analytics`: usage and waste analytics
- `feature:profile`: user profile and preferences

## Build notes

- Kotlin: `2.3.20`
- Android Gradle Plugin: `9.1.0`
- Compose BOM: `2026.04.01`
- Room: `2.8.4`
- Navigation: `2.9.7`
- WorkManager: `2.11.2`
- Hilt: `2.57.1`

## Flavors

- `demo`: preloads demo inventory data
- `full`: starts with an empty inventory

## Deeplinks

- `smartpantry://assistant`
- `smartpantry://inventory/{itemId}`

## Docs

- [Requirements Checklist](./docs/requirements-checklist.md)
- [Architecture Diagrams](./docs/architecture-diagrams.md)
- [Competitive Analysis](./docs/competitive-analysis.md)
- [Source-to-Project Map](./docs/source-to-project-map.md)
- [Feature Roadmap](./docs/feature-roadmap.md)
