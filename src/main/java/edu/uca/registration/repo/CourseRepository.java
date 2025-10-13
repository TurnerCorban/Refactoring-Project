package edu.uca.registration.repo;

import edu.uca.registration.model.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findByCode(String code);
    List<Course> findAllCourses();
    Course save(Course course);
    boolean deleteCourse(String code);
    boolean existsCourse(String code);
}
