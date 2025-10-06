package edu.uca.registration.service;

import edu.uca.registration.model.*;
import edu.uca.registration.repo.StudentRepository;
import edu.uca.registration.repo.CourseRepository;
import edu.uca.registration.repo.EnrollmentRepository;
import edu.uca.registration.utility.ValidationUtil;
import java.util.List;

public class RegistrationService {
    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;

    public RegistrationService(StudentRepository studentRepo,
                               CourseRepository courseRepo,
                               EnrollmentRepository enrollmentRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
    }

    public Student addStudent(String bannerId, String name, String email) {
        ValidationUtil.validateBannerId(bannerId);
        ValidationUtil.validateName(name);
        ValidationUtil.validateEmail(email);

        if (studentRepo.existsStudent(bannerId)) {
            throw new IllegalArgumentException("Student with ID " + bannerId + " already exists");
        }

        Student student = new Student(bannerId, name, email);
        return studentRepo.save(student);
    }

    public Course addCourse(String code, String title, int capacity) {
        ValidationUtil.validateCourseCode(code);
        ValidationUtil.validateTitle(title);
        ValidationUtil.validateCapacity(capacity);

        if (courseRepo.existsCourse(code)) {
            throw new IllegalArgumentException("Course with code " + code + " already exists");
        }

        Course course = new Course(code, title, capacity);
        return courseRepo.save(course);
    }

    public Enrollment enrollStudent(String studentId, String courseCode) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        Course course = courseRepo.findByCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseCode));

        // Check if already enrolled or waitlisted
        List<Enrollment> existing = enrollmentRepo.findByStudentId(studentId);
        for (Enrollment e : existing) {
            if (e.getCourseCode().equals(courseCode)) {
                throw new IllegalArgumentException("Student already enrolled/waitlisted in this course");
            }
        }

        int enrolledCount = enrollmentRepo.countEnrolledInCourse(courseCode);

        if (enrolledCount < course.getCapacity()) {
            // Enroll directly
            Enrollment enrollment = new Enrollment(studentId, courseCode,
                    Enrollment.Status.ENROLLED, 0);
            return enrollmentRepo.save(enrollment);
        } else {
            // Add to waitlist
            int waitlistPosition = enrollmentRepo.countWaitlistedInCourse(courseCode) + 1;
            Enrollment enrollment = new Enrollment(studentId, courseCode,
                    Enrollment.Status.WAITLISTED, waitlistPosition);
            return enrollmentRepo.save(enrollment);
        }
    }

    public void dropStudent(String studentId, String courseCode) {
        boolean deleted = enrollmentRepo.delete(studentId, courseCode);
        if (!deleted) {
            throw new IllegalArgumentException("Student not enrolled in course");
        }

        // Promote first waitlisted student if any
        List<Enrollment> waitlist = enrollmentRepo.findWaitlistedByCourse(courseCode);
        if (!waitlist.isEmpty()) {
            Enrollment toPromote = waitlist.get(0);
            Enrollment promoted = new Enrollment(toPromote.getStudentId(),
                    toPromote.getCourseCode(), Enrollment.Status.ENROLLED, 0);
            enrollmentRepo.save(promoted);

            // Recalculate waitlist positions for remaining students
            for (int i = 1; i < waitlist.size(); i++) {
                Enrollment w = waitlist.get(i);
                Enrollment updated = new Enrollment(w.getStudentId(), w.getCourseCode(),
                        Enrollment.Status.WAITLISTED, i);
                enrollmentRepo.save(updated);
            }
        }
    }

    public List<Student> listStudents() {
        return studentRepo.findAllStudents();
    }

    public List<Course> listCourses() {
        return courseRepo.findAllCourses();
    }

    public List<Enrollment> getStudentEnrollments(String studentId) {
        return enrollmentRepo.findByStudentId(studentId);
    }

    // Getters for the repositories (useful for statistics)
    public CourseRepository getCourseRepo() { return courseRepo; }
    public StudentRepository getStudentRepo() { return studentRepo; }
    public EnrollmentRepository getEnrollmentRepo() { return enrollmentRepo; }
}