# SE333 Assignment 5

![Build Status](https://github.com/nathan-zavala/SE333-Assignment5/actions/workflows/SE333_CI.yml/badge.svg)

## Project Overview
This project is a Java Maven application that simulates an Amazon shopping cart system with automated testing and CI/CD using GitHub Actions. It includes two parts: backend integration/unit testing and UI testing using Playwright on the DePaul University bookstore website.

## Part 1 — Integration & Unit Tests
- **AmazonIntegrationTest.java** — Integration tests using a real in-memory HSQL database, testing how multiple components work together including ShoppingCart, Database, and price rules.
- **AmazonUnitTest.java** — Unit tests using Mockito mocks to test individual classes in isolation including RegularCost, DeliveryPrice, and ExtraCostForElectronics.

## Part 2 — UI Testing with Playwright

### playwrightTraditional
Manually written Playwright tests in Java covering a full purchase pathway on the DePaul bookstore website:
1. Search and filter for JBL earbuds
2. Shopping cart verification
3. Create account page
4. Contact information page
5. Pickup information page
6. Payment information page
7. Empty cart verification

### playwrightLLM
AI-generated Playwright tests using Claude (via MCP) with a natural language prompt describing the same workflow.

## GitHub Actions
All Actions passed

The CI workflow automatically runs on every push to main and includes:
- Checkstyle static analysis
- JUnit tests (integration + unit + UI)
- JaCoCo code coverage report
- Artifact uploads for Checkstyle and JaCoCo reports

## Reflection: Manual vs AI-Assisted UI Testing

### Ease of Writing and Running Tests
Writing tests manually in Java with Playwright required a deep understanding of the website's HTML structure. Every selector had to be inspected manually using browser developer tools, and each element required careful identification of unique attributes like IDs, class names, and aria labels. This process was time-consuming but gave full control over exactly what was being tested. In contrast, the AI-assisted approach using Claude through MCP was significantly faster to get started — a single natural language prompt describing the workflow generated a complete test class in seconds, without needing to manually inspect HTML elements.

### Accuracy and Reliability of Generated Tests
The manually written tests, while tedious to develop, are highly reliable because each selector was verified against the actual website. Issues like the Brand filter panel collapsing before clicking, duplicate elements, and hidden checkboxes were all discovered and fixed through trial and error. The AI-generated tests, on the other hand, produced code that closely mirrored the manual approach but contained subtle issues — for example, using insufficient wait times before interacting with dynamically loaded elements. This resulted in flaky tests that failed intermittently due to timing issues the AI could not anticipate without actually running the tests against the live website.

### Maintenance Effort
Manual tests require significant effort to maintain because any change to the website's HTML structure requires manually re-inspecting elements and updating selectors. However, since the developer wrote the tests, they have full understanding of why each selector was chosen. AI-generated tests have a similar maintenance burden, but with the added challenge that the developer may not fully understand why certain selectors were used, making debugging more difficult. That said, regenerating tests using AI is faster than rewriting them manually from scratch.

### Limitations and Issues Encountered
The main limitation of manual testing was the time investment required to debug selector issues — for example, discovering that the website uses inconsistent capitalization in filter IDs (facet-brand vs facet-Color vs facet-price), or that checkboxes are hidden behind SVG elements requiring label clicks instead. The AI-generated tests inherited some of these same issues since the AI learned from the existing codebase, but missed some of the fine-tuned fixes that were developed through manual debugging. Overall, AI-assisted testing is a powerful tool for quickly scaffolding test code, but human review and debugging is still necessary to ensure tests are reliable and accurate.

## All Actions Passed