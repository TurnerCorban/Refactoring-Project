package edu.uca.registration.app;

import edu.uca.registration.model.*;
import edu.uca.registration.repo.*;
import edu.uca.registration.repo.implementation.*;
import edu.uca.registration.service.RegistrationService;
import edu.uca.registration.utility.Config;

import java.util.List;
import java.util.Scanner;

public class RegistrationApp {
    private final RegistrationService service;
    private final Scanner scanner;
    private boolean demoMode;

    public RegistrationApp(RegistrationService service, boolean demoMode) {
        this.service = service;
        this.scanner = new Scanner(System.in);
        this.demoMode = demoMode;
    }

    public void run() {
        println("=== UCA Course Registration (Refactored) ===");
        println("Using JSON data storage: " + Config.getDataFilePath());

        if (demoMode) {
            seedDemoData();
            println("Demo mode: Data has been seeded");
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
                case "1": addStudentUI(); break;
                case "2": addCourseUI(); break;
                case "3": enrollUI(); break;
                case "4": dropUI(); break;
                case "5": listStudents(); break;
                case "6": listCourses(); break;
                case "7": searchStudentEnrollments(); break;
                case "0": return;
                default: println("Invalid choice"); break;
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
        } catch (Exception e) {
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
            String status = enrollment.getStatus() == Enrollment.Status.ENROLLED ?
                    "enrolled" : "waitlisted (position: " + enrollment.getWaitlistPosition() + ")";
            println("Student " + status + " in course");
        } catch (Exception e) {
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
            int enrolled = service.getEnrollmentRepo().countEnrolledInCourse(c.getCode());
            int waitlisted = service.getEnrollmentRepo().countWaitlistedInCourse(c.getCode());
            println(" - " + c + " enrolled=" + enrolled + " waitlisted=" + waitlisted);
        }
    }

    private void searchStudentEnrollments() {
        print("Student ID: ");
        String sid = scanner.nextLine().trim();

        List<Enrollment> enrollments = service.getStudentEnrollments(sid);
        println("Enrollments for student " + sid + ":");
        if (enrollments.isEmpty()) {
            println(" - No enrollments found");
        } else {
            for (Enrollment e : enrollments) {
                String status = e.getStatus() == Enrollment.Status.ENROLLED ?
                        "ENROLLED" : "WAITLISTED (position: " + e.getWaitlistPosition() + ")";
                println(" - " + e.getCourseCode() + ": " + status);
            }
        }
    }

    private void seedDemoData() {
        try {
            // Only add students if they don't exist
            if (!service.getStudentRepo().existsStudent("B001")) {
                service.addStudent("B001", "Alice", "alice@uca.edu");
                System.out.println("Added demo student: B001 Alice");
            }

            if (!service.getStudentRepo().existsStudent("B002")) {
                service.addStudent("B002", "Brian", "brian@uca.edu");
                System.out.println("Added demo student: B002 Brian");
            }

            // Only add courses if they don't exist
            if (!service.getCourseRepo().existsCourse("CSCI4490")) {
                service.addCourse("CSCI4490", "Software Engineering", 2);
                System.out.println("Added demo course: CSCI4490 Software Engineering");
            }

            if (!service.getCourseRepo().existsCourse("MATH1496")) {
                service.addCourse("MATH1496", "Calculus I", 50);
                System.out.println("Added demo course: MATH1496 Calculus I");
            }

            println("Demo data seeding completed");
        } catch (Exception e) {
            // If data already exists, we'll get exceptions - that's fine
            println("Note: " + e.getMessage());
        }
    }

    private void print(String s) { System.out.print(s); }
    private void println(String s) { System.out.println(s); }

    public static void main(String[] args) {
        // Check for --demo flag in command line arguments
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