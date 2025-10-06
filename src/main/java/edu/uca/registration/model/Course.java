package edu.uca.registration.model;

import java.util.Objects;

public class Course {
    private final String code;
    private final String title;
    private final int capacity;

    public Course(String code, String title, int capacity) {
        this.code = code;
        this.title = title;
        this.capacity = capacity;
    }

    // Getters
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCapacity() { return capacity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(code, course.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return code + " " + title + " cap=" + capacity;
    }
}
