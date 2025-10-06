package edu.uca.registration.records;

import java.util.ArrayList;
import java.util.List;

public record Course(String code, String title, int capacity) {
    public static List<String> roster = new ArrayList<>(), waitlist = new ArrayList<>();
}