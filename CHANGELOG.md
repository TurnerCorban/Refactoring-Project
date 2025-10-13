# Refactoring Changes Log

## Major Architectural Changes

### 1. Layered Architecture Implementation
**Changes**:
- Separated monolithic `Main` class into distinct layers
- Created `records/` package for domain entities
- Created `repo/` package for data access interfaces
- Created `service/` package for business logic
- Created `app/` package for presentation layer
- Created `util/` package for shared utilities
- Kept `Main` as the only runnable file that leads to `RegistrationApp`

**Impact**:
- Clear separation of concerns
- Improved testability
- Better maintainability

### 2. Repository Pattern Implementation
**Changes**:
- Created `StudentRepository`, `CourseRepository`, and `EnrollmentRepository`
interfaces.
- Implemented `JsonDataRepository` as the single implementation for all data
- Replaced global static maps with proper data persistence
- added automatic CSV to JSON migration

### 3. Business Logic 
**Changes**
- Moved all business rules from CLI to `RegistrationService`
- Centralized enrollment logic, capacity checks, and waitlist management
- Removed business logic from UI layer

### 4. Validation System
**Changes**
- Created `ValidationUtil` class with comprehensive validation methods
- Added validation for Banner IDs, emails, course codes, and capacities
- Implemented proper exception handling

### 5. Configuration 
**Changes**
- Created `Config` class for external configuration
- Made storage implementation configurable
- Externalized file paths via system properties 

### 6. JSON Storage Implementation
**Changes**
- Replaced multiple CSV files with one JSON file
- Implemented automatic migration from CSV format
- Robust error handling for file operations

### 7. Error Handling Enhancement
**Changes**
- Replaced `println` messages with proper exception handling
- Added error messages in CLI
- Added comprehensive input validation
