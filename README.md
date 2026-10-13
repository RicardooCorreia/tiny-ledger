# Tiny Ledger

Tiny Ledger is a REST API for creating accounts and recording deposits, withdrawals, and transfers. It also provides current account balances and transaction history.

## Requirements

- Java 25
- Docker running to build a container image

## Run locally

Start the application:

```bash
./gradlew bootRun
```

The API is available at `http://localhost:8080`. Application state is stored in memory and is lost when the application stops.

## Load sample data

With the application running, populate it with four sample accounts and a set of deposits, withdrawals, and transfers:

```bash
./gradlew seedSampleData
```

The seeder prints each account name and ID. It targets `http://localhost:8080` by default; pass a Gradle property to use a different application URL:

```bash
./gradlew seedSampleData -PbaseUrl=http://localhost:8081
```

Running the seeder again creates another set of accounts. The application stores data in memory, so all sample data is cleared when it stops.

## Tests

The test suite covers domain models and use cases, REST controllers and mappings, and the in-memory persistence adapters. Functional tests exercise account lifecycle, deposits, withdrawals, transfers, and transaction history through the running application. Concurrency tests cover account operations and repository access.

Run all tests with:

```bash
./gradlew test
```

The test task also generates JaCoCo HTML and XML coverage reports. Open `build/reports/jacoco/test/html/index.html` for the HTML report; the XML report is at `build/reports/jacoco/test/jacocoTestReport.xml`.

Run the full verification, including the 100% line coverage requirement, with:

```bash
./gradlew check
```

MapStruct-generated mapper implementations and the sample-data seeder are excluded from the coverage report and requirement.

Build a Docker image using Spring Boot Buildpacks with:

```bash
./gradlew bootBuildImage
```

## Architecture

Tiny Ledger uses a lightweight form of hexagonal architecture. The domain is at the center and contains the business logic, expressed as use cases and domain models. The domain does not depend on the details of HTTP or storage.

Ports define how the domain interacts with its dependencies. For example, the account and transaction repository ports describe the operations needed to store and retrieve ledger data. Adapters implement those ports and connect the domain to external concerns:

- **Inbound REST adapters** in `adapters/rest` expose the application over HTTP. Controllers accept and validate requests, invoke use cases, and return mapped responses.
- **Outbound persistence adapters** in `adapters/persistence` implement the repository ports. The current implementations store accounts and transactions in memory, so data is lost when the application stops.
- The `InstantProvider` port supplies the current time to the domain; its adapter is in `adapters`.

In a typical request, a REST controller calls a domain use case. The use case applies business rules through the domain models and uses outbound ports when it needs persistence or the current time. The REST adapter then maps the result to an HTTP response.

## Out of scope and known limitations

- Authentication and authorization are not implemented.
- Data is held in memory; there is no persistent data layer, and state is lost when the application stops.
- Logging and monitoring are not implemented.
- Multi-currency transfers are not supported.
- Account management beyond creation and lookup, including account deletion, is not implemented.
- Caching and other performance optimizations are out of scope.
- Concurrent access is supported by the in-memory repositories and account-level locking. However, there is no durable database transaction spanning a balance change and its transaction record.

## API reference

The API reference is generated from the application's OpenAPI annotations:

- [OpenAPI YAML snapshot](docs/openapi.yaml)
- Live OpenAPI document: `http://localhost:8080/v3/api-docs.yaml` (start the app first)
- Interactive Swagger UI: `http://localhost:8080/swagger-ui.html`

To refresh the checked-in snapshot after changing the API, start the app and run:

```bash
curl -fsS http://localhost:8080/v3/api-docs.yaml -o docs/openapi.yaml
```
