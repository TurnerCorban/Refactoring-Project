package edu.uca.registration.repo;

import edu.uca.registration.model.Enrollment;
import java.util.List;

public interface EnrollmentRepository {
    List<Enrollment> findByStudentId(String studentId);
    List<Enrollment> findByCourseCode(String courseCode);
    List<Enrollment> findWaitlistedByCourse(String courseCode);
    Enrollment save(Enrollment enrollment);
    boolean delete(String studentId, String courseCode);
    int countEnrolledInCourse(String coursesCode);
    int countWaitlistedInCourse(String coursesCode);
}
