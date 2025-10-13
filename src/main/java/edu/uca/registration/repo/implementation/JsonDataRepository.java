package edu.uca.registration.repo.implementation;

import edu.uca.registration.model.Course;
import edu.uca.registration.model.Enrollment;
import edu.uca.registration.model.Student;
import edu.uca.registration.repo.StudentRepository;
import edu.uca.registration.repo.CourseRepository;
import edu.uca.registration.repo.EnrollmentRepository;
import edu.uca.registration.utility.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public class JsonDataRepository implements StudentRepository, CourseRepository, EnrollmentRepository {
    private final String filePath;
    private final ObjectMapper mapper;
    private DataStore dataStore;

    // Internal data structure
    private static class DataStore {
        public Map<String, Student> students = new LinkedHashMap<>();
        public Map<String, Course> courses = new LinkedHashMap<>();
        public Map<String, Enrollment> enrollments = new LinkedHashMap<>();
    }

    public JsonDataRepository() {
        this.filePath = Config.getDataFilePath();
        this.mapper = new ObjectMapper();
        loadFromFile();
        importCsvOnce(); // Check if we need to migrate from the old CSV format
    }

    // Helper method for enrollment keys
    private String getEnrollmentKey(String studentId, String courseCode) {
        return studentId + ":" + courseCode;
    }

    // ========== StudentRepository Implementation ==========

    @Override
    public Optional<Student> findById(String bannerId) {
        return Optional.ofNullable(dataStore.students.get(bannerId));
    }

    @Override
    public List<Student> findAllStudents() {
        return new ArrayList<>(dataStore.students.values());
    }

    @Override
    public Student save(Student student) {
        dataStore.students.put(student.bannerId(), student);
        saveToFile();
        return student;
    }

    @Override
    public boolean deleteStudent(String bannerId) {
        // Also remove related enrollments
        dataStore.enrollments.entrySet().removeIf(entry ->
                entry.getValue().studentId().equals(bannerId));

        boolean removed = dataStore.students.remove(bannerId) != null;
        if (removed) saveToFile();
        return removed;
    }

    @Override
    public boolean existsStudent(String bannerId) {
        return dataStore.students.containsKey(bannerId);
    }

    // ========== CourseRepository Implementation ==========

    @Override
    public Optional<Course> findByCode(String code) {
        return Optional.ofNullable(dataStore.courses.get(code));
    }

    @Override
    public List<Course> findAllCourses() {
        return new ArrayList<>(dataStore.courses.values());
    }

    @Override
    public Course save(Course course) {
        dataStore.courses.put(course.code(), course);
        saveToFile();
        return course;
    }

    @Override
    public boolean deleteCourse(String code) {
        // Also remove related enrollments
        dataStore.enrollments.entrySet().removeIf(entry ->
                entry.getValue().courseCode().equals(code));

        boolean removed = dataStore.courses.remove(code) != null;
        if (removed) saveToFile();
        return removed;
    }

    @Override
    public boolean existsCourse(String code) {
        return dataStore.courses.containsKey(code);
    }

    // ========== EnrollmentRepository Implementation ==========

    @Override
    public List<Enrollment> findByStudentId(String studentId) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : dataStore.enrollments.values()) {
            if (enrollment.studentId().equals(studentId)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    @Override
    public List<Enrollment> findByCourseCode(String courseCode) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : dataStore.enrollments.values()) {
            if (enrollment.courseCode().equals(courseCode)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    @Override
    public List<Enrollment> findWaitlistedByCourse(String courseCode) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : dataStore.enrollments.values()) {
            if (enrollment.courseCode().equals(courseCode) &&
                    enrollment.status() == Enrollment.Status.WAITLISTED) {
                result.add(enrollment);
            }
        }
        // Sort by waitlist position
        result.sort(Comparator.comparingInt(Enrollment::waitlistPosition));
        return result;
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        dataStore.enrollments.put(
                getEnrollmentKey(enrollment.studentId(), enrollment.courseCode()),
                enrollment
        );
        saveToFile();
        return enrollment;
    }

    @Override
    public boolean delete(String studentId, String courseCode) {
        boolean removed = dataStore.enrollments.remove(getEnrollmentKey(studentId, courseCode)) != null;
        if (removed) saveToFile();
        return removed;
    }

    @Override
    public int numEnrolledInCourse(String courseCode) {
        return (int) dataStore.enrollments.values().stream()
                .filter(e -> e.courseCode().equals(courseCode) &&
                        e.status() == Enrollment.Status.ENROLLED)
                .count();
    }

    @Override
    public int numWaitlistedInCourse(String courseCode) {
        return (int) dataStore.enrollments.values().stream()
                .filter(e -> e.courseCode().equals(courseCode) &&
                        e.status() == Enrollment.Status.WAITLISTED)
                .count();
    }

    // ========== File Operations ==========

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            dataStore = new DataStore();
            System.out.println("file not found, creating new file");
            return;
        }

        try {
            dataStore = mapper.readValue(file, DataStore.class);
        } catch (IOException e) {
            System.err.println("Error loading data, starting fresh: " + e.getMessage());
            dataStore = new DataStore();
        }
    }

    private void saveToFile() {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), dataStore);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    // ========== CSV Migration ==========

    private void importCsvOnce() {
        // Only migrate if JSON file is empty but CSV files exist
        if (!dataStore.students.isEmpty() && !dataStore.courses.isEmpty()) {
            return; // Already has data
        }

        importStudentsFromCsv();
        importCoursesFromCsv();
        importEnrollmentsFromCsv();

        if (!dataStore.students.isEmpty() || !dataStore.courses.isEmpty()) {
            saveToFile(); // Save migrated data
            System.out.println("Successfully migrated from CSV to JSON format");
        }
    }

    private void importStudentsFromCsv() {
        File csvFile = new File("students.csv");
        if (!csvFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        Student student = new Student(parts[0].trim(), parts[1].trim(), parts[2].trim());
                        dataStore.students.put(student.bannerId(), student);
                    }
                } catch (Exception e) {
                    System.err.println("Skipping invalid student line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error importing students from CSV: " + e.getMessage());
        }
    }

    private void importCoursesFromCsv() {
        File csvFile = new File("courses.csv");
        if (!csvFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        Course course = new Course(parts[0].trim(), parts[1].trim(), Integer.parseInt(parts[2].trim()));
                        dataStore.courses.put(course.code(), course);
                    }
                } catch (Exception e) {
                    System.err.println("Skipping invalid course line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error importing courses from CSV: " + e.getMessage());
        }
    }

    private void importEnrollmentsFromCsv() {
        System.out.println("Note: Enrollment data starts fresh. Re-enroll students as needed.");
    }
}