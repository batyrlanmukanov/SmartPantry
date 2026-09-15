# Test Strategy

## Why UI and integration tests are required

Unit tests validate isolated business logic, but they do not prove that user-critical Android flows work end-to-end in the app shell.

To fully cover the course test criterion, SmartPantry now includes:

- unit tests (`core:domain`) for use-case and analytics logic
- UI test (`app:androidTest`) for sign-in to inventory rendering
- integration test (`app:androidTest`) for auth -> assistant -> user message flow

## Implemented instrumented tests

- `MainFlowUiTest#signIn_opensInventoryScreen`
  - enters credentials
  - taps `Continue`
  - verifies inventory UI appears (`Add food item`, `Active items`)

- `AssistantIntegrationTest#authToAssistant_messageFlow_works`
  - signs in
  - opens `Assistant` tab
  - sends prompt in assistant input
  - verifies sent message appears in chat

## How to run

- Unit tests:
  - `./gradlew :core:domain:test`
- Instrumented tests (requires emulator/device):
  - `./gradlew :app:connectedDemoDebugAndroidTest`

## Defense evidence checklist

- Open test classes in `app/src/androidTest/...`
- Run `connectedDemoDebugAndroidTest`
- Show passing `MainFlowUiTest` and `AssistantIntegrationTest`
- Explain how this complements unit tests in `core:domain`
