package edu.uca.registration;

import edu.uca.registration.records.Course;
import edu.uca.registration.records.Student;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    static Map<String, Student> students = new LinkedHashMap<>();
    static Map<String, Course> courses = new LinkedHashMap<>();
    static List<String> auditLog = new ArrayList<>();

    public static void main(String[] args) {
        println("=== UCA Course Registration (Baseline) ===");
        println("NOTE: This code is intentionally messy. You'll refactor it.");
        menuLoop();
        println("Goodbye!");
    }

    private static void menuLoop() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            println("\nMenu:");
            println("1) Add student");
            println("2) Add course");
            println("3) Enroll student in course");
            println("4) Drop student from course");
            println("5) List students");
            println("6) List courses");
            println("0) Exit");
            print("Choose: ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addStudentUI(sc); break;
                case "2": addCourseUI(sc); break;
                case "3": enrollUI(sc); break;
                case "4": dropUI(sc); break;
                case "5": listStudents(); break;
                case "6": listCourses(); break;
                case "0": return;
                default: println("Invalid"); break;
            }
        }
    }

    private static void addStudentUI(Scanner sc) {
        print("Banner ID: ");
        String id = sc.nextLine().trim();
        print("Name: ");
        String name = sc.nextLine().trim();
        print("Email: ");
        String email = sc.nextLine().trim();
        Student s = new Student(id, name, email);
        students.put(id, s);
        audit("ADD_STUDENT " + id);
    }

    private static void addCourseUI(Scanner sc) {
        print("Course Code: ");
        String code = sc.nextLine().trim();
        print("Title: ");
        String title = sc.nextLine().trim();
        print("Capacity: ");
        int cap = Integer.parseInt(sc.nextLine().trim());
        Course c = new Course(code, title, cap);
        courses.put(code, c);
        audit("ADD_COURSE " + code);
    }

    private static void enrollUI(Scanner sc) {
        print("Student ID: ");
        String sid = sc.nextLine().trim();
        print("Course Code: ");
        String cc = sc.nextLine().trim();
        Course c = courses.get(cc);
        if (c == null) { println("No such course"); return; }
        if (c.roster.size() >= c.capacity()) {
            c.waitlist.add(sid);
            audit("WAITLIST " + sid + "->" + cc);
        } else {
            c.roster.add(sid);
            audit("ENROLL " + sid + "->" + cc);
        }
    }

    private static void dropUI(Scanner sc) {
        print("Student ID: ");
        String sid = sc.nextLine().trim();
        print("Course Code: ");
        String cc = sc.nextLine().trim();
        Course c = courses.get(cc);
        if (c == null) { println("No such course"); return; }
        if (c.roster.remove(sid)) {
            audit("DROP " + sid + " from " + cc);
            if (!c.waitlist.isEmpty()) {
                String promote = c.waitlist.remove(0);
                c.roster.add(promote);
                audit("PROMOTE " + promote + "->" + cc);
            }
        } else {
            println("Not enrolled");
        }
    }

    private static void listStudents() {
        println("Students:");
        for (Student s : students.values()) println(" - " + s);
    }

    private static void listCourses() {
        println("Courses:");
        for (Course c : courses.values())
            println(" - " + c.code() + " " + c.title() + " cap=" + c.capacity() + " enrolled=" + c.roster.size());
    }


    private static void print(String s){ System.out.print(s); }
    private static void println(String s){ System.out.println(s); }
    private static void audit(String ev){ auditLog.add(LocalDateTime.now() + " | " + ev); }
}
