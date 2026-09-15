# SmartPantry Project Document Outline

## 1. Project Overview

- Project title: `SmartPantry`
- Type: Android mobile application
- Goal: reduce household food waste through inventory visibility, expiration awareness, analytics, and assistant guidance

## 2. Relevance

- Many students and young adults forget what food they already have at home.
- This leads to duplicate purchases, expired food, and unnecessary spending.
- The problem is practical, everyday, and financially important.
- The project is relevant for both students and employed users, which is supported by Lab 3 results.

## 3. Novelty

- The project does not act as a general chatbot.
- It combines `inventory tracking + expiration monitoring + waste analytics + profile-based assistant`.
- The assistant is domain-specific: it works only with the user's pantry data and helps reduce food waste.
- Unlike general AI tools, SmartPantry gives recommendations grounded in the user's current stored products and savings goal.

## 4. Practical Significance

- Helps users use products before expiration.
- Reduces wasted food and money loss.
- Improves planning with simple visual inventory and assistant prompts.
- Works as a local-first mobile tool for daily home use.

## 5. Objectives

### Design objectives

- Identify the food waste problem and user pain points.
- Define functional and non-functional requirements.
- Choose Android, Kotlin, Room, Hilt, Compose, and WorkManager.
- Design the application architecture and data model.

### Implementation objectives

- Build local authentication flow.
- Implement profile and personalization module.
- Implement food inventory CRUD and status tracking.
- Implement AI assistant logic based on inventory and profile.
- Implement analytics and waste indicators.
- Add deeplinks, flavors, modular structure, and tests.

## 6. Competitive Analysis

- Competitor 1: `NoWaste`
- Competitor 2: `Pantry Check`
- Competitor 3: `SuperCook`

Suggested table columns:

- Product
- Main purpose
- Inventory tracking
- Expiration alerts
- Assistant / recommendation logic
- Analytics
- Target audience
- Main weakness
- Opportunity for SmartPantry

## 7. Statistical Analysis

- `r = 0.406991` between forgetfulness and need for visual inventory
- `r = 0.098168` between automation and perceived waste reduction
- `p = 0.332851` for students vs employees

Interpretation:

- Visual inventory is the strongest practical feature for the MVP.
- Automation alone is not enough without visibility.
- The problem is relevant across user groups, not only one audience.

## 8. Main Functionalities

- Authentication
- User profile
- Add food item
- View inventory
- Mark item as used
- Mark item as wasted
- Item detail screen
- AI assistant
- Analytics

## 9. AI Assistant Difference

Question: `How is your AI different from regular GPT or Gemini?`

Suggested answer:

`SmartPantry assistant is not a general-purpose AI. It is a domain-specific food management assistant connected to the user's own inventory, item expiration data, and savings goal. It gives focused advice such as what to cook from available ingredients, what expires soon, and how to reduce waste. GPT or Gemini answer general questions from broad world knowledge, while SmartPantry assistant is designed for one practical scenario and uses structured in-app data.`

## 10. Architecture

Include:

- Use-case diagram
- Sequence diagram
- Class diagram
- ER diagram

Explain briefly:

- `core:domain` contains business logic and use cases
- `core:data` contains repository implementations
- `core:database` contains Room entities and DAO
- `feature:*` modules contain screen-level UI and view models

## 11. Technical Stack

- Kotlin
- Jetpack Compose
- Room
- Hilt
- WorkManager
- Navigation Compose
- DataStore

## 12. Conclusion

- SmartPantry solves a real and validated problem.
- The app combines practical features with research-based justification.
- The project satisfies both engineering and presentation requirements.

## 13. Submission Format

- `Markdown` files are good as working drafts and source material.
- For submission or defense, prepare a separate `DOCX` or `PDF` document.
- Best flow: finalize content in Markdown, then move it into Word/Google Docs and export to PDF.
