package edu.uca.registration.utility;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern BANNER_ID_PATTERN =
            Pattern.compile("^B\\d{3}$");
    private static final Pattern COURSE_CODE_PATTERN =
            Pattern.compile("^[A-Z]{4}\\d{4}$");

    public static void validateBannerId(String bannerId) {
        if (bannerId == null || bannerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Banner ID cannot be empty");
        }
        if (!BANNER_ID_PATTERN.matcher(bannerId).matches()) {
            throw new IllegalArgumentException("Banner ID must be in format BXXX (e.g., B001)");
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.trim().length() < 2) {
            throw new IllegalArgumentException("Name must be at least 2 characters long");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static void validateCourseCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (!COURSE_CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("Course code must be in format XXXX0000 (e.g., CSCI4490)");
        }
    }

    public static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Course title cannot be empty");
        }
        if (title.trim().length() < 3) {
            throw new IllegalArgumentException("Course title must be at least 3 characters long");
        }
    }

    public static void validateCapacity(int capacity) {
        if (capacity < 1 || capacity > 500) {
            throw new IllegalArgumentException("Capacity must be between 1 and 500");
        }
    }
}