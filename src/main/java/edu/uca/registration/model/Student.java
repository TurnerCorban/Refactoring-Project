package edu.uca.registration.model;

import java.util.Objects;

public record Student(String bannerId, String name, String email) {
    @Override
    public String toString() { return bannerId + " " + name + " <" + email + ">"; }
    @Override
    public int hashCode() {
        return Objects.hash(bannerId);
    }
}