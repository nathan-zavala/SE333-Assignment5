# SE333 Assignment 5

![Build Status](https://github.com/nathan-zavala/SE333-Assignment5/actions/workflows/SE333_CI.yml/badge.svg)

## Project Overview
This project is a Java Maven application that simulates an Amazon shopping cart system.
It includes price rules for regular cost, delivery price, and extra cost for electronics.

## Tests
- **AmazonIntegrationTest.java** - Integration tests that test multiple components working 
together using a real in-memory HSQL database.
- **AmazonUnitTest.java** - Unit tests that test individual classes in isolation using Mockito mocks.

## GitHub Actions
This project uses GitHub Actions for CI. On every push to main, the workflow:
- Runs Checkstyle for static analysis
- Runs all JUnit tests
- Generates and uploads JaCoCo code coverage report

## All Actions Passed