package edu.uca.registration.model;

import java.util.Objects;

public class Enrollment {
    public enum Status { ENROLLED, WAITLISTED }

    private final String studentId;
    private final String courseCode;
    private final Status status;
    private final int waitlistPosition;

    public Enrollment(String studentId, String courseCode, Status status, int waitlistPosition) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.status = status;
        this.waitlistPosition = waitlistPosition;
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public Status getStatus() { return status; }
    public int getWaitlistPosition() { return waitlistPosition; }

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