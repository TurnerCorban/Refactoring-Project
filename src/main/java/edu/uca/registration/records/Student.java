package edu.uca.registration.records;

import java.util.Objects;

public record Student(String id, String name, String email) {
    @Override
    public String toString() { return id + " " + name + " <" + email + ">"; }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}