package cx3n1.projects.ats;

import cx3n1.projects.ats.data.Preset;
import cx3n1.projects.ats.jobs.DayResetJob;
import cx3n1.projects.ats.jobs.test;
import cx3n1.projects.ats.utilities.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static javafx.beans.binding.Bindings.createDoubleBinding;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Class to store and manage access to all the important static variables in the project.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSSettings {
    /**
     * file which will store all the info which needs to be saved after closing program
     * such as current preset and default location of resource files
     */
    private static final String PROGRAM_DATA_FILE = "Data.properties";

    /**
     * time zone offset of user (UTC +0 by default)
     */
    public static ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(0);

    /**
     * path to directory where all presets are stored
     */
    public static Path RESOURCE_DIRECTORY_ABS_PATH;

    /**
     * selected preset which is currently can be edited by user
     */
    public static String CURRENTLY_ACTIVE_PRESET_NAME;

    /**
     * preset name which will be edited if user presses edit button
     */
    public static String CURRENTLY_EDITED_PRESET_NAME;

    /**
     * currently loaded preset
     */
    private static Preset LOADED_PRESET;

    /**
     * keys which should be defined in preset file for it to be valid
     */
    public static List<String> PROPERTY_KEY_NAMES;

    //TODO: i think it's possible to use one scheduler and use multiple jobs, cancel/reinitialize jobs when needed
    /**
     * scheduler for shutdown sequence (scheduler waits till time is right and then initiates shutdown sequence)
     */
    private static Scheduler SHUTDOWN_SCHEDULER;

    /**
     * scheduler for day reset sequence (initiates day reset at the start of the day)
     */
    private static Scheduler DAY_RESET_SCHEDULER;


    //***Testing area***\\

    private static Scheduler PROGRESS_BAR_SCHEDULER;

    public static DoubleProperty progress = new SimpleDoubleProperty(0);

    public static void initializeProgressBarScheduler() throws SchedulerException {
        if (PROGRESS_BAR_SCHEDULER != null)
            killShutdownScheduler();
        PROGRESS_BAR_SCHEDULER = StdSchedulerFactory.getDefaultScheduler();
        PROGRESS_BAR_SCHEDULER.start();

        JobDetail job = newJob(test.class)
                .withIdentity("test")
                .build();

        //TODO: test if this works
        Trigger trigger = newTrigger()
                .withIdentity("testTrig")
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(1)
                        .repeatForever())
                .startNow()
                .build();

        PROGRESS_BAR_SCHEDULER.scheduleJob(job, trigger);
    }


    //***End of testing area***\\


    /**
     * reference to thread which controls progress bar in main view
     */
    @Deprecated
    public static Thread PROGRESS_BAR_THREAD;

    /**
     * notifies threads that they should shut down
     */
    @Deprecated
    public static Boolean THREADS_SHUTDOWN_COMMAND = false;


    public static void startupSequence() throws Exception {
        loadSettingsFromData();
        LOADED_PRESET = Preset.loadPreset(Utils.Paths.getPresetFilePath(CURRENTLY_ACTIVE_PRESET_NAME));
        initialiseDayResetScheduler();
        ATSWatchman.notifyChange();
    }


    public static void saveSettingsIntoData() throws Exception {
        if (!Files.exists(Path.of(PROGRAM_DATA_FILE))) {
            throw new Exception("FATAL ERROR: Couldn't find Data file!");
        }

        try (FileOutputStream fos = new FileOutputStream(PROGRAM_DATA_FILE)) {
            Properties prop = new Properties();
            prop.setProperty("RESOURCE_DIRECTORY", RESOURCE_DIRECTORY_ABS_PATH.getFileName().toString());
            prop.setProperty("CURRENTLY_ACTIVE_PRESET_NAME", CURRENTLY_ACTIVE_PRESET_NAME);

            prop.setProperty("CURRENT_TIME_ZONE", ZONE_OFFSET.getId());

            prop.setProperty("PROPERTY_KEY_NAMES_0", PROPERTY_KEY_NAMES.get(0));
            prop.setProperty("PROPERTY_KEY_NAMES_1", PROPERTY_KEY_NAMES.get(1));
            prop.setProperty("PROPERTY_KEY_NAMES_2", PROPERTY_KEY_NAMES.get(2));
            prop.setProperty("PROPERTY_KEY_NAMES_3", PROPERTY_KEY_NAMES.get(3));
            prop.setProperty("PROPERTY_KEY_NAMES_4", PROPERTY_KEY_NAMES.get(4));
            prop.setProperty("PROPERTY_KEY_NAMES_5", PROPERTY_KEY_NAMES.get(5));
            prop.setProperty("PROPERTY_KEY_NAMES_6", PROPERTY_KEY_NAMES.get(6));
            prop.setProperty("PROPERTY_KEY_NAMES_7", PROPERTY_KEY_NAMES.get(7));
            prop.setProperty("PROPERTY_KEY_NAMES_8", PROPERTY_KEY_NAMES.get(8));
            prop.setProperty("PROPERTY_KEY_NAMES_9", PROPERTY_KEY_NAMES.get(9));

            prop.store(fos, "Data last updated on: " + LocalDateTime.now());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setLoadedPreset(String newPresetName) {
        try {
            LOADED_PRESET = Preset.loadPreset(Path.of(RESOURCE_DIRECTORY_ABS_PATH + "\\" + newPresetName + ".properties"));
            CURRENTLY_ACTIVE_PRESET_NAME = newPresetName;

            ATSLogic.mainLogic();

            saveSettingsIntoData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ATSWatchman.notifyChange();
    }

    public static Preset getLoadedPreset(){
        return LOADED_PRESET;
    }

    public static void changeTimeZone(String zoneID) throws Exception {
        ZONE_OFFSET = ZoneOffset.of(zoneID);
        saveSettingsIntoData();
        ATSWatchman.notifyChange();
    }


    public static void initializeShutdownScheduler() throws SchedulerException {
        if (SHUTDOWN_SCHEDULER != null)
            killShutdownScheduler();
        SHUTDOWN_SCHEDULER = StdSchedulerFactory.getDefaultScheduler();
        SHUTDOWN_SCHEDULER.start();
    }

    public static Scheduler getShutdownScheduler() {
        return SHUTDOWN_SCHEDULER;
    }

    public static void killShutdownScheduler() throws SchedulerException {
        SHUTDOWN_SCHEDULER.shutdown();
    }


    /**
     * get warning time written in loaded preset
     * @return warning time in minutes
     */
    public static int getLoadedWarningTime() {
        return LOADED_PRESET.getWarningTime();
    }
    /**
     * get shutdown time written in loaded preset
     * @return shutdown time as a LocalTime
     */
    public static LocalTime getLoadedShutdownTime(){
        return LOADED_PRESET.getTimeOfShutdown();
    }


    //*** Utility ***\\
    private static void loadSettingsFromData() throws Exception {
        if (!Files.exists(Path.of(PROGRAM_DATA_FILE))) {
            throw new Exception("FATAL ERROR: Couldn't find Data file!");
        }

        try (FileInputStream fis = new FileInputStream(PROGRAM_DATA_FILE)) {
            Properties prop = new Properties();
            prop.load(fis);
            RESOURCE_DIRECTORY_ABS_PATH = Paths.get(prop.getProperty("RESOURCE_DIRECTORY")).toAbsolutePath();
            CURRENTLY_ACTIVE_PRESET_NAME = prop.getProperty("CURRENTLY_ACTIVE_PRESET_NAME");

            ZONE_OFFSET = ZoneOffset.of(prop.getProperty("CURRENT_TIME_ZONE"));

            PROPERTY_KEY_NAMES = new ArrayList<>();
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_0"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_1"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_2"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_3"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_4"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_5"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_6"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_7"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_8"));
            PROPERTY_KEY_NAMES.add(prop.getProperty("PROPERTY_KEY_NAMES_9"));
        }
    }
    private static void initialiseDayResetScheduler() throws SchedulerException {
        if (DAY_RESET_SCHEDULER != null)
            DAY_RESET_SCHEDULER.shutdown();
        DAY_RESET_SCHEDULER = StdSchedulerFactory.getDefaultScheduler();
        DAY_RESET_SCHEDULER.start();

        JobDetail job = newJob(DayResetJob.class)
                .withIdentity("dayResetter")
                .build();

        //TODO: test if this works
        Trigger trigger = newTrigger()
                .withIdentity("resetTrigger")
                .withSchedule(dailyAtHourAndMinute(0, 0))
                .build();

        DAY_RESET_SCHEDULER.scheduleJob(job, trigger);
    }

}
