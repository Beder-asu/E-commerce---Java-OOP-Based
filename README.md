# E-Commerce (Java OOP Based)

## Project Overview and Use Cases
A modular Java implementation of core e-commerce flows.  
Supported (or intended) use cases:
- List/query products
- Manage a shopping cart (add, update quantity, remove)
- Validate inventory before checkout
- Create orders from cart snapshot
- Simulate payment handling
- Adjust inventory after successful order

Focus: clear domain separation (product, cart, order, payment, inventory) and replaceable abstractions for future persistence and external integrations.

## Tech Stack
- Language: Java (standard JDK; no framework requirement for core domain)
- Testing: JUnit
- Data Layer: In-memory repository stubs (current)
- Data Base: JBDC

## Directory Structures
```
src/
  main/
    java/
      domain/
        product/
        cart/
        order/
        payment/
        inventory/
      service/
      repository/
        inmem/
      util/
  test/
    java/
```
Adjust as concrete classes are introduced (e.g., add adapters for CLI / REST later).

## Build and Run
Plain JDK (no dependencies):
```
javac -d out $(find src -name "*.java")
java -cp out com.example.Main
```

Maven (if using):
```
mvn clean verify
mvn exec:java -Dexec.mainClass="com.example.Main"
```

## Contribution
1. Create a feature branch from main.
2. Follow existing package naming and keep domain logic free of infrastructure details.
3. Add or update tests for new or changed behavior.
4. Use clear commit messages (scope + intent).
5. Open a pull request describing the change and any impact on existing flows.
