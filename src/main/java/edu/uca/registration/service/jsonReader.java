package edu.uca.registration.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class jsonReader{


    public void readJSON() throws IOException {
        byte[] jsonData = Files.readAllBytes(Paths.get("registration_data.json"));
        ObjectMapper mapper = new ObjectMapper();
    }
}
