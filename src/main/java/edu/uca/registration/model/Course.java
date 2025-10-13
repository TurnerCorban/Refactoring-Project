package edu.uca.registration.model;

import java.util.ArrayList;
import java.util.List;

public record Course(String code, String title, int capacity) {
    public static List<String> roster = new ArrayList<>(), waitlist = new ArrayList<>();
}