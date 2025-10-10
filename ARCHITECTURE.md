# System Architecture Design

## Overview
A refactored course registration system with clean layered architecture separating 
concerns between domain models, data persistence, business logic, and user interface.
The system manages student enrollment in courses with capacity limits and waitlist 
functionality.

## Package Structure
overall hierarchy: src -> main -> java -> edu.uca.registration
1. *application*:
    `edu.uca.registration` -> `app` -> `RegistrationApp` and `Main`
2. *model*:
    `edu.uca.registration` -> `model`-> `Enrollment`, `Course`, and `Student`
3. *repositories*:
    `edu.uca.registration` -> `repo` -> (`implementation` -> `JsonDataRepository`),
    `CourseRepository`, `EnrollmentRepository`, and `StudentRepository`
4. *service*:
   `edu.uca.registration` -> `service` -> `RegistrationService`
5. *utility*:
    `edu.uca.registration` -> `utility` -> `Config` and `ValidationUtil`
     




## Data Flow

1. **User Input** → `RegistrationApp` (CLI) → `RegistrationService` (Business Logic)
2. **Business Logic** → Repository Interfaces → `JsonDataRepository` (Persistence)
3. **Persistence** → Single JSON file (`registration_data.json`)
4. **Responses** flow back up the same chain

## Key Design Patterns

- **Repository Pattern**: Abstract data access behind interfaces
- **Layered Architecture**: Clear separation of concerns
- **Dependency Injection**: Services receive dependencies via constructor
- **Single Responsibility**: Each class has one clear purpose
- **Factory Pattern**: JsonDataRepository implements multiple repository interfaces

## Data Storage

- **Format**: JSON (single file for all data)
- **File**: `registration_data.json`
- **Migration**: Automatic CSV → JSON conversion on first run
- **Error Handling**: Graceful handling of missing/corrupted files