# SE-433 Shopping Application

CLI shopping app built with **Java 17**, **Maven**, and **JUnit 5**.

## Prerequisites

- Java JDK **17**
- Maven 3.8+

```bash
java -version
mvn -version
```

## Build & Run

```bash
mvn package
java -jar target/shopping-app-1.0.0.jar
```

Or in IntelliJ: open `pom.xml` → run `ShoppingApp.main()`.

**Menu:** 1 Add · 2 Total · 3 View cart · 4 Edit qty · 5 Remove · 6 Checkout · 7 Exit

## Tests

```bash
mvn test                    # all 62 tests
mvn test -Dtest=ClassName   # one class
mvn clean test              # clean rebuild
```

## Coverage & Mutation Testing (IntelliJ)

**Coverage:** Right-click `src/test/java` → **Run with Coverage**

**PIT:** Install PIT plugin → run with:

| Field | Value |
|-------|-------|
| Target classes | `shopping.*` |
| Target tests | `shopping.*` |
| Report dir | `target/pit-reports` |

Report: `target/pit-reports/index.html`

## Rules

- **Tax:** 6% for IL, CA, NY; 0% elsewhere
- **Shipping:** Standard $10 (free if subtotal > $50) · Next day $25
- **Limits:** qty ≥ 1 · total $1.00–$99,999.99
