package edu.uca.registration.service;

import edu.uca.registration.model.*;
import edu.uca.registration.repo.implementation.JsonDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class    RegistrationServiceTest {
    private RegistrationService service;
    private JsonDataRepository repo;

    @BeforeEach
    public void setUP() {
        repo = new JsonDataRepository();
        service = new RegistrationService(repo, repo, repo);
    }

    @Test
    public void testRegisterStudent() {
        Student student = service.addStudent("B005", "Test Student", "test@uca.edu");
        assertNotNull(student);
        assertEquals("B005", student.bannerId());
    }

    @Test
    public void testEnrollStudentInCourse() {
        service.addStudent("B010", "Mark Twain", "mark@uca.edu");
        service.addCourse("TEST1010", "Test Course", 2);

        Enrollment enrollment = service.enrollStudent("B010", "TEST1010");
        assertNotNull(enrollment);
        assertEquals(Enrollment.Status.ENROLLED, enrollment.status());
    }

    @Test
    public void testWaitlistWhenCourseFull() {
        service.addStudent("B008", "Jonny B", "jonny@uca.edu");
        service.addStudent("B009", "Blane M", "blane@uca.edu");
        service.addCourse("TEST1020", "Small Course", 1);

        Enrollment firstStudent = service.enrollStudent("B008", "TEST1020");
        assertEquals(Enrollment.Status.ENROLLED, firstStudent.status());

        Enrollment secondStudent = service.enrollStudent("B009", "TEST1020");
        assertEquals(Enrollment.Status.WAITLISTED, secondStudent.status());
        assertEquals(1, secondStudent.waitlistPosition());
    }
}
