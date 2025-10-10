package edu.uca.registration.utility;

public class Config {
    private static final String datapath = "src/main/java/edu/uca/registration/data/"; //data folder
    private static final String filename = "registration_data.json"; //json save file
    private static final String filepath = datapath + filename;

    public static String getDataFilePath() {
        return System.getProperty("data.file", filepath);
    }
}
