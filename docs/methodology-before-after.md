# Methodology: Before/After Statistical Plan

## Goal

Measure whether SmartPantry reduces household food waste and improves timely consumption.

## Design

- Baseline period: 14 days without SmartPantry features enabled
- Intervention period: 14 days with SmartPantry active usage
- Target users: students living independently

## Metrics

- `Wasted KZT` per week
- `% Used before expiry`
- `Expiring soon count` (items with <=2 days left)
- `Saved KZT` (used amount minus wasted amount)
- `Reminder response rate` (items marked used/wasted within 24h after reminder)

## Statistical tests

- Paired t-test (or Wilcoxon if non-normal) for before/after comparison
- Effect size (Cohen's d) for practical significance
- Confidence interval (95%) for mean waste reduction

## Example success criteria

- At least 15% reduction in `Wasted KZT`
- At least 20% increase in `% Used before expiry`
- Positive improvement in anti-waste trend score
