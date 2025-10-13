package edu.uca.registration.model;

import java.util.Objects;

public record Enrollment(String studentId, String courseCode, Enrollment.Status status,
                         int waitlistPosition) {
    public enum Status {ENROLLED, WAITLISTED}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(courseCode, that.courseCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseCode);
    }
}