package edu.uca.registration.app;

import edu.uca.registration.model.Enrollment;
import edu.uca.registration.repo.implementation.JsonDataRepository;
import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.utility.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationSystemTest {
    private RegistrationService service;
    private JsonDataRepository repo;

    @BeforeEach
    public void setUp() {
        String filename = Config.getDataFilePath();

        //Clears the JSON data to ensure a clean slate for tests
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write("{}"); // Write an empty JSON object
            System.out.println("JSON file cleared: " + filename);
        } catch (IOException e) {
            System.err.println("Error clearing JSON file: " + e.getMessage());
        }

        repo = new JsonDataRepository();
        service = new RegistrationService(repo, repo, repo);
    }

    @Test
    public void testCompleteWorkflow() {
        service.addStudent("B020", "Full flow Test", "fullflow@uca.edu");
        service.addCourse("FLOW1001", "Full flow Course", 5);

        Enrollment enrollment = service.enrollStudent("B020", "FLOW1001");
        assertEquals(Enrollment.Status.ENROLLED, enrollment.status());

        var enrollments = service.getStudentEnrollments("B020");
        assertEquals(1, enrollments.size());
        assertEquals("FLOW1001", enrollments.get(0).courseCode());

        service.dropStudent("B020", "FLOW1001");
        enrollments = service.getStudentEnrollments("B020");
        assertTrue(enrollments.isEmpty());
    }
}
