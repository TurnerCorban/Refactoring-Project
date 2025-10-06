package edu.uca.registration.repo.implementation;

import edu.uca.registration.model.*;
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
        importCsvOnce(); // Check if we need to migrate from old CSV format
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
        dataStore.students.put(student.getBannerId(), student);
        saveToFile();
        return student;
    }

    @Override
    public boolean deleteStudent(String bannerId) {
        // Also remove related enrollments
        dataStore.enrollments.entrySet().removeIf(entry ->
                entry.getValue().getStudentId().equals(bannerId));

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
        dataStore.courses.put(course.getCode(), course);
        saveToFile();
        return course;
    }

    @Override
    public boolean deleteCourse(String code) {
        // Also remove related enrollments
        dataStore.enrollments.entrySet().removeIf(entry ->
                entry.getValue().getCourseCode().equals(code));

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
            if (enrollment.getStudentId().equals(studentId)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    @Override
    public List<Enrollment> findByCourseCode(String courseCode) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : dataStore.enrollments.values()) {
            if (enrollment.getCourseCode().equals(courseCode)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    @Override
    public List<Enrollment> findWaitlistedByCourse(String courseCode) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment enrollment : dataStore.enrollments.values()) {
            if (enrollment.getCourseCode().equals(courseCode) &&
                    enrollment.getStatus() == Enrollment.Status.WAITLISTED) {
                result.add(enrollment);
            }
        }
        // Sort by waitlist position
        result.sort(Comparator.comparingInt(Enrollment::getWaitlistPosition));
        return result;
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        dataStore.enrollments.put(
                getEnrollmentKey(enrollment.getStudentId(), enrollment.getCourseCode()),
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
    public int countEnrolledInCourse(String courseCode) {
        return (int) dataStore.enrollments.values().stream()
                .filter(e -> e.getCourseCode().equals(courseCode) &&
                        e.getStatus() == Enrollment.Status.ENROLLED)
                .count();
    }

    @Override
    public int countWaitlistedInCourse(String courseCode) {
        return (int) dataStore.enrollments.values().stream()
                .filter(e -> e.getCourseCode().equals(courseCode) &&
                        e.getStatus() == Enrollment.Status.WAITLISTED)
                .count();
    }

    // ========== File Operations ==========

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            dataStore = new DataStore();
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
                        dataStore.students.put(student.getBannerId(), student);
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
                        dataStore.courses.put(course.getCode(), course);
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
        // This would need to reconstruct enrollments from the original logic
        // For now, we'll start with fresh enrollments
        System.out.println("Note: Enrollment data starts fresh. Re-enroll students as needed.");
    }
}