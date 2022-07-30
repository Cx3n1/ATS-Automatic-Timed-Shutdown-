package cx3n1.projects.ats.data;

import cx3n1.projects.ats.ATSSettings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;

public class Preset {

    private final Path pathToPreset;
    private final LocalTime timeOfShutdown;
    private final int warningTime;
    private final boolean[] days;


    private Preset(Path pathToPreset, LocalTime timeOfShutdown, int warningTime, boolean[] days) {
        this.pathToPreset = pathToPreset;
        this.timeOfShutdown = timeOfShutdown;
        this.warningTime = warningTime;
        this.days = days;
    }


    //*** Preset manipulation ***\\
    public static Preset CreateNewPresetDefault(Path pathToPreset) throws Exception {
        if (!Files.exists(pathToPreset))
            Files.createFile(pathToPreset);

        fillInPropertiesFileWithDefaults(pathToPreset);

        return loadPreset(pathToPreset);
    }

    public static Preset loadPreset(Path pathToPreset) throws Exception {
        if (!Files.exists(pathToPreset))
            throw new FileNotFoundException("File with given path couldn't be found");

        try (FileInputStream fis = new FileInputStream(pathToPreset.toFile())) {
            Properties prop = new Properties();
            prop.load(fis);

            checkIfAllPropertiesArePresentInFile(prop);

            LocalTime timeOfShutdown = loadShutdownTimeFrom(prop);
            int warningTime = loadWarningTimeFrom(prop);
            boolean[] days = loadDaysFrom(prop);

            return new Preset(pathToPreset, timeOfShutdown, warningTime, days);
        }
    }

    public void savePreset() throws IOException {
        savePreset(pathToPreset);
    }

    public void savePreset(Path newPath) throws IOException {
        if (!Files.exists(newPath))
            Files.createFile(newPath);

        try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
            Properties prop = new Properties();

            prop.setProperty("time.shutdown.hour", String.valueOf(timeOfShutdown.getHour()));
            prop.setProperty("time.shutdown.minute", String.valueOf(timeOfShutdown.getMinute()));

            prop.setProperty("time.warning", String.valueOf(warningTime));

            prop.setProperty("day.monday", String.valueOf(days[0]));
            prop.setProperty("day.tuesday", String.valueOf(days[1]));
            prop.setProperty("day.wednesday", String.valueOf(days[2]));
            prop.setProperty("day.thursday", String.valueOf(days[3]));
            prop.setProperty("day.friday", String.valueOf(days[4]));
            prop.setProperty("day.saturday", String.valueOf(days[5]));
            prop.setProperty("day.sunday", String.valueOf(days[6]));

            prop.store(fos, "Preset saved on: " + LocalDateTime.now());
        }
    }


    //*** Other ***\\
    public boolean checkIfTodayIsChecked() {
        return checkIfGivenDayIsChecked(LocalDateTime.now().getDayOfWeek());
    }

    public boolean checkIfGivenDayIsChecked(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> days[0];
            case TUESDAY -> days[1];
            case WEDNESDAY -> days[2];
            case THURSDAY -> days[3];
            case FRIDAY -> days[4];
            case SATURDAY -> days[5];
            case SUNDAY -> days[6];
        };
    }


    //*** Getter/Setters ***\\
    public LocalTime getTimeOfShutdown() {
        return timeOfShutdown;
    }

    /**
     * @return warning time in minutes
     */
    public int getWarningTime() {
        return warningTime;
    }

    public boolean[] getDays() {
        return days;
    }


    //*** Utility ***\\
    private static void fillInPropertiesFileWithDefaults(Path pathToPreset) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(pathToPreset.toFile())) {

            Properties prop = new Properties();

            prop.setProperty("time.shutdown.hour", "22");
            prop.setProperty("time.shutdown.minute", "50");

            prop.setProperty("time.warning", "10");

            prop.setProperty("day.monday", "true");
            prop.setProperty("day.tuesday", "true");
            prop.setProperty("day.wednesday", "true");
            prop.setProperty("day.thursday", "true");
            prop.setProperty("day.friday", "true");
            prop.setProperty("day.saturday", "true");
            prop.setProperty("day.sunday", "true");

            prop.store(fos, "Stored new preset" + LocalTime.now());
        }
    }

    private static void checkIfAllPropertiesArePresentInFile(Properties prop) throws Exception {
        for (String key : ATSSettings.PROPERTY_KEY_NAMES) {
            if (!prop.containsKey(key))
                throw new Exception("Error: property " + key + "is missing");
        }
    }

    private static LocalTime loadShutdownTimeFrom(Properties prop) {
        return LocalTime.of(
                Integer.parseInt(prop.getProperty("time.shutdown.hour", "22")),
                Integer.parseInt(prop.getProperty("time.shutdown.minute", "50")));
    }

    private static int loadWarningTimeFrom(Properties prop) {
        return Integer.parseInt(prop.getProperty("time.warning", "10"));
    }

    private static boolean[] loadDaysFrom(Properties prop) {
        return new boolean[]{
                Boolean.parseBoolean(prop.getProperty("day.monday", "true")),
                Boolean.parseBoolean(prop.getProperty("day.tuesday", "true")),
                Boolean.parseBoolean(prop.getProperty("day.wednesday", "true")),
                Boolean.parseBoolean(prop.getProperty("day.thursday", "true")),
                Boolean.parseBoolean(prop.getProperty("day.friday", "true")),
                Boolean.parseBoolean(prop.getProperty("day.saturday", "true")),
                Boolean.parseBoolean(prop.getProperty("day.sunday", "true")),
        };
    }

}
