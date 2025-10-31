# UCA Course Registration – Baseline (for Refactoring Assignment)
## GitHub Link: https://github.com/TurnerCorban/Refactoring-Project

Refactored code by Nate Hazeslip and Corban Turner

## To run tests, [click here](testing.md)

## Run
```bash

mvn -q -DskipTests package
java -jar target/Refactoring-Project-0.1.0.jar

```

## Run (demo mode)
```bash

mvn -q -DskipTests package
java -jar target/Refactoring-Project-0.1.0.jar --demo

```

## Execute with Maven
```bash

mvn exec:java

```

## Execute with Maven (demo mode)
```bash

mvn exec:java -Dexec.args="--demo"

```