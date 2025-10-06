package edu.uca.registration.utility;

public class Config {
    public static String getDataFilePath() {
        return System.getProperty("data.file", "registration_data.json");
    }

    public static boolean isDemoMode() {
        String demoProperty = System.getProperty("demo.mode");
        if (demoProperty == null) {
            return "true".equalsIgnoreCase(demoProperty);
        }

        for (String arg : java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if ("--demo".equals(arg)) {
                return true;
            }
        }

        return false;
    }
}
