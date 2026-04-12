# api-automation-restassured

REST API automation framework using RestAssured, TestNG, schema validation, reusable request specifications, and OAuth token support.

## Stack

- Java 17
- RestAssured
- TestNG
- Jackson
- JSON Schema Validator
- Maven

## Highlights

- Reusable request and response specs
- Environment-driven configuration
- OAuth token caching utility
- JSON schema validation
- Dynamic payload support
- Clean separation between client, models, utils, and tests

## Run

```bash
mvn clean test
mvn clean test -Denv=staging
```