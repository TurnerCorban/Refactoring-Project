package edu.uca.registration.utility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilTest {
    @Test
    public void testValidBannerId() {
        assertDoesNotThrow(() -> ValidationUtil.validateBannerId("B001"));
    }

    @Test
    public void testInvalidBannerId() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateBannerId("InvalidID"));
    }

    @Test
    public void testValidEmail() {
        assertDoesNotThrow(() -> ValidationUtil.validateEmail("test@email.com"));
    }

    @Test
    public void testInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateEmail("invalid-email"));
    }

    @Test
    public void testValidCourseCode() {
        assertDoesNotThrow(() -> ValidationUtil.validateCourseCode("CSCI4430"));
    }

    @Test
    public void testInvalidCourseCode() {
        assertThrows(IllegalArgumentException.class, () -> ValidationUtil.validateCourseCode("InvalidCourseCode"));
    }
}
