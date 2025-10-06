package edu.uca.registration.model;

import java.util.Objects;

public class Student {
    private final String bannerId;
    private final String name;
    private final String email;

    public Student(String bannerId, String name, String email) {
        this.bannerId = bannerId;
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getBannerId() { return bannerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(bannerId, student.bannerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bannerId);
    }

    @Override
    public String toString() {
        return bannerId + " " + name + " <" + email + ">";
    }
}
