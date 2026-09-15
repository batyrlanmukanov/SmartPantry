# Source-to-Project Map

## What each provided file contributed

### 1. `Final Project Requirements (1).pdf`

This file defines the Android engineering grading criteria:

- multi-module architecture
- deeplinking
- clean architecture
- dependency injection
- tests
- build variants and flavors

These requirements directly shaped the generated Android Studio project structure.

### 2. `Lecture 10 (2).pdf`

This file defines the SIS project criteria and explains how competitive analysis should be done:

- relevance, novelty, practical significance
- 3 competitors
- statistical analysis
- architecture diagrams
- more than 4 functionalities including auth, profile, AI assistant

It also gives the structure of competitive analysis: strengths, weaknesses, opportunities, positioning, SWOT, and benchmarking categories.

### 3. `Lab1 1.docx`

This file defines the actual product problem and user meaning:

- users forget what food they already have
- food spoils because routines change
- users want reminders, visibility, and convenience
- users reject heavy manual input
- users value simplicity, automation, and money savings

This is the research basis for the app concept.

### 4. `LAB2 (1).pdf`

This file gives the formal requirement set:

- product registration
- inventory database
- expiration monitoring
- inventory visualization
- recommendations
- prioritization of older products
- smart shopping support
- analytics
- storage guidance
- offline functionality
- behavioral feedback
- personalization

It also gives measurable NFRs and risk analysis, which justify the clean architecture and local-first design.

### 5. `LAB3work (1).pdf`

This file gives the quantitative validation:

- visual inventory matters most
- automation alone is not enough
- the problem is relevant across user roles

This is why the generated MVP puts the visual inventory list in the center of the app instead of making barcode scanning the main feature.

### 6. `labwork3 1.xlsx`

This file confirms the actual calculations:

- `Q5 & Q10`: `r = 0.406991`
- `Q12 & Q14`: `r = 0.098168`
- `T-test`: `p = 0.332851`
- `Q12` mean = `4.173076923076923`
- `Q11` mean = `3.9615384615384617`
- `Q7` mean = `3.9038461538461537`

These values are referenced in the analytics documentation and product rationale.

## Final interpretation

So the real assignment is not just "make any Android app".

You need:

1. A technically correct Android app.
2. A product concept grounded in your user research.
3. A project explanation that shows why these features exist.
4. Architecture and competitor documents for the SIS / defense side.

That is exactly why the generated workspace now contains both `code` and `docs`.
