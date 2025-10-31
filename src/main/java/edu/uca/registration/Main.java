package edu.uca.registration;

import edu.uca.registration.app.RegistrationApp;
import edu.uca.registration.repo.implementation.JsonDataRepository;
import edu.uca.registration.service.RegistrationService;


import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // Check for a --demo flag in command line arguments
        boolean demoMode = false;
        for (String arg : args) {
            if ("--demo".equals(arg)) {
                demoMode = true;
                break;
            }
        }

        // Single repository implementation that handles all data
        JsonDataRepository dataRepo = new JsonDataRepository();

        // But we pass it as separate interfaces to the service
        RegistrationService service = new RegistrationService(
                dataRepo, // as StudentRepository
                dataRepo, // as CourseRepository
                dataRepo  // as EnrollmentRepository
        );

        RegistrationApp app = new RegistrationApp(service, demoMode);
        app.run();
    }
}
