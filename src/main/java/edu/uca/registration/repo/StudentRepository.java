package edu.uca.registration.repo;

import edu.uca.registration.records.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Optional<Student> findById(String bannerId);
    List<Student> findAllStudents();
    Student save(Student student);
    boolean deleteStudent(String bannerId);
    boolean existsStudent(String bannerId);

}
