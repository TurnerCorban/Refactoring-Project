package edu.uca.registration.app;

import edu.uca.registration.Main;
import edu.uca.registration.model.*;
import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.utility.Config;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Clock.*;

import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

/*
UI Handler
*/

public class RegistrationApp {
    private final RegistrationService service;
    private final Scanner scanner;
    private final boolean demoMode;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RegistrationApp.class.getName());
    private static final FileHandler fh;

    static {
        try {
            fh = new FileHandler("registration.log");
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RegistrationApp(RegistrationService service, boolean demoMode) throws IOException {
        this.service = service;
        this.scanner = new Scanner(System.in);
        this.demoMode = demoMode;
    }

    public void run() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        println("=== UCA Course Registration (Refactored) ===");
        logger.info(" - UCA Course Registration (Refactored) started");
        logger.info("Using JSON data storage: " + Config.getDataFilePath());

        if (demoMode) {
            println("Demo mode active");
            seedDemoData();
            logger.info("Demo mode: Data has been seeded");
        }

        menuLoop();
        println("Goodbye!");
    }

    private void menuLoop() {
        while (true) {
            println("\nMenu:");
            println("1) Add student");
            println("2) Add course");
            println("3) Enroll student in course");
            println("4) Drop student from course");
            println("5) List students");
            println("6) List courses");
            println("7) Search student enrollments");
            println("0) Exit");
            print("Choose: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": addStudentUI(); logger.info("Add student selected"); break;
                case "2": addCourseUI(); logger.info("Add course selected"); break;
                case "3": enrollUI(); logger.info("Enroll ui selected"); break;
                case "4": dropUI(); logger.info("Drop ui selected"); break;
                case "5": listStudents(); logger.info("List students selected"); break;
                case "6": listCourses(); logger.info("List courses selected"); break;
                case "7": searchStudentEnrollments(); logger.info("Search student enrollments selected"); break;
                case "0": logger.info("Exit selected"); return;
                default: println("Invalid choice"); logger.info("Invalid choice entered"); break;
            }
        }
    }

    private void addStudentUI() {
        try {
            print("Banner ID: ");
            String id = scanner.nextLine().trim();
            print("Name: ");
            String name = scanner.nextLine().trim();
            print("Email: ");
            String email = scanner.nextLine().trim();

            Student student = service.addStudent(id, name, email);
            println("Student added: " + student);
        } catch (Exception e) {
            logger.info(e.getMessage());
            println("Error: " + e.getMessage());
        }
    }

    private void addCourseUI() {
        try {
            print("Course Code: ");
            String code = scanner.nextLine().trim();
            print("Title: ");
            String title = scanner.nextLine().trim();
            print("Capacity: ");
            int cap = Integer.parseInt(scanner.nextLine().trim());

            Course course = service.addCourse(code, title, cap);
            println("Course added: " + course);
            logger.info("Course added: " + course);
        } catch (Exception e) {
            logger.info(e.getMessage());
            println("Error: " + e.getMessage());
        }
    }

    private void enrollUI() {
        try {
            print("Student ID: ");
            String sid = scanner.nextLine().trim();
            print("Course Code: ");
            String cc = scanner.nextLine().trim();

            Enrollment enrollment = service.enrollStudent(sid, cc);
            String status = enrollment.status() == Enrollment.Status.ENROLLED ?
                    "enrolled" : "waitlisted (position: " + enrollment.waitlistPosition() + ")";
            println("Student " + status + " in course");
        } catch (Exception e) {
            logger.info(e.getMessage());
            println("Error: " + e.getMessage());
        }
    }

    private void dropUI() {
        try {
            print("Student ID: ");
            String sid = scanner.nextLine().trim();
            print("Course Code: ");
            String cc = scanner.nextLine().trim();

            service.dropStudent(sid, cc);
            println("Student dropped from course");
        } catch (Exception e) {
            logger.info(e.getMessage());
            println("Error: " + e.getMessage());
        }
    }

    private void listStudents() {
        List<Student> students = service.listStudents();
        println("Students (" + students.size() + "):");
        for (Student s : students) {
            println(" - " + s);
        }
    }

    private void listCourses() {
        List<Course> courses = service.listCourses();
        println("Courses (" + courses.size() + "):");
        for (Course c : courses) {
            int enrolled = service.getEnrollmentRepo().numEnrolledInCourse(c.code());
            int waitlisted = service.getEnrollmentRepo().numWaitlistedInCourse(c.code());
            println(" - " + c + " enrolled=" + enrolled + " waitlisted=" + waitlisted);
        }
    }

    private void searchStudentEnrollments() {
        print("Student ID: ");
        String sid = scanner.nextLine().trim();

        List<Enrollment> enrollments = service.getStudentEnrollments(sid);
        println("Enrollments for student " + sid + ":");
        if (enrollments.isEmpty()) {
            logger.info("No enrollments found for student " + sid);
            println(" - No enrollments found");
        } else {
            for (Enrollment e : enrollments) {
                String status = e.status() == Enrollment.Status.ENROLLED ?
                        "ENROLLED" : "WAITLISTED (position: " + e.waitlistPosition() + ")";
                println(" - " + e.courseCode() + ": " + status);
            }
        }
    }

    private void seedDemoData() {
        try {
            // Only add students if they don't exist
            if (!service.getStudentRepo().existsStudent("B001")) {
                logger.info("Seeded student B001 Alice");
                service.addStudent("B001", "Alice", "alice@uca.edu");
                System.out.println("Added demo student: B001 Alice");
            }

            if (!service.getStudentRepo().existsStudent("B002")) {
                logger.info("Seeded student B002 Brian");
                service.addStudent("B002", "Brian", "brian@uca.edu");
                System.out.println("Added demo student: B002 Brian");
            }

            // Only add courses if they don't exist
            if (!service.getCourseRepo().existsCourse("CSCI4490")) {
                logger.info("Seeded course CSCI4490");
                service.addCourse("CSCI4490", "Software Engineering", 2);
                System.out.println("Added demo course: CSCI4490 Software Engineering");
            }

            if (!service.getCourseRepo().existsCourse("MATH1496")) {
                logger.info("Seeded course MATH1496");
                service.addCourse("MATH1496", "Calculus I", 50);
                System.out.println("Added demo course: MATH1496 Calculus I");
            }

            logger.info("Demo data seeding completed");
        } catch (Exception e) {
            // If data already exists, we'll get exceptions - that's fine
            logger.info(e.getMessage());
            println("Note: " + e.getMessage());
        }
    }

    private void print(String s) { System.out.print(s); }
    private void println(String s) { System.out.println(s); }
}