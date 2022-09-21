package cx3n1.projects.ats;

import cx3n1.projects.ats.data.Preset;
import cx3n1.projects.ats.jobs.DayResetJob;
import cx3n1.projects.ats.utilities.DuplicateCheck;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SystemUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Class to store and manage access to all the important static variables in the project.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSSettings {
    /**
     * safeguard mechanism to not launch program more than once
     * lock one port using server socket which will be checked at launch for availability
     */
    private static int LOCK_PORT = 24000;

    private static void initializeLock() throws Exception{
        DuplicateCheck.launchLockWithExceptions(LOCK_PORT);
    }

    /**
     * path to folder which will contain editable resource files of programs in specific OSes.
     * eg: in windows AppData\ATS folder
     */
    private static String OS_DATA_FOLDER;
    //TODO: add support for more OSes
    private static void initializeDataFolder(){
         if(SystemUtils.IS_OS_WINDOWS){
             //TODO: System.getProperty(LOCALAPPDATA) doesn't work this is temporary solution till i find better way to do it :p
             OS_DATA_FOLDER = System.getProperty("user.home") + "\\AppData\\Local\\ATS\\";

             //C:\Users\Cx3n1\AppData\Local\ATS
         }
    }

    /**
     * file which will store all the info which needs to be saved after closing program
     * such as current preset and default location of resource files
     */
    private static final String PROGRAM_DATA_FILE = "Data.properties";

    private static void loadSettingsFromDataFile() throws Exception {
        System.out.println(OS_DATA_FOLDER + PROGRAM_DATA_FILE);
        //C:\Users\Cx3n1\AppData\Local\ATS
        if (!Files.exists(Path.of(OS_DATA_FOLDER + PROGRAM_DATA_FILE))) {
            throw new Exception("FATAL ERROR: Couldn't find Data file!");
        }

        try (FileInputStream fis = new FileInputStream(OS_DATA_FOLDER + PROGRAM_DATA_FILE)) {
            Properties prop = new Properties();
            prop.load(fis);
            LOCK_PORT = Integer.parseInt(prop.getProperty("LOCK_PORT"));

            RESOURCE_DIRECTORY_ABS_PATH = Paths.get(OS_DATA_FOLDER + prop.getProperty("RESOURCE_DIRECTORY"));
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

    private static void saveSettingsIntoDataFile() throws Exception {
        if (!Files.exists(Path.of(OS_DATA_FOLDER + PROGRAM_DATA_FILE))) {
            throw new Exception("FATAL ERROR: Couldn't find Data file!");
        }

        try (FileOutputStream fos = new FileOutputStream(OS_DATA_FOLDER + PROGRAM_DATA_FILE)) {
            Properties prop = new Properties();
            prop.setProperty("LOCK_PORT", String.valueOf(LOCK_PORT));
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
        }
    }


    /**
     * time zone offset of user (UTC +0 by default)
     */
    public static ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(0);

    public static ZoneOffset getZoneOffset() {
        return ZONE_OFFSET;
    }

    public static void setZoneOffset(String zoneID) throws Exception {
        ZONE_OFFSET = ZoneOffset.of(zoneID);
        saveSettingsIntoDataFile();
        ATSWatchman.notifyChange();
    }


    /**
     * path to directory where all presets are stored
     */
    public static Path RESOURCE_DIRECTORY_ABS_PATH;

    public static Path getResourceDirectoryAbsPath() {
        return RESOURCE_DIRECTORY_ABS_PATH;
    }

    public static void setResourceDirectoryAbsPath(Path absPath) {
        RESOURCE_DIRECTORY_ABS_PATH = absPath;
    }


    /**
     * selected preset which is currently can be edited by user
     */
    public static String CURRENTLY_ACTIVE_PRESET_NAME;

    public static String getCurrentlyActivePresetName() {
        return CURRENTLY_ACTIVE_PRESET_NAME;
    }

    public static void setCurrentlyActivePresetName(String presetName) {
        CURRENTLY_ACTIVE_PRESET_NAME = presetName;
    }


    /**
     * preset name which will be edited if user presses edit button
     */
    public static String CURRENTLY_EDITED_PRESET_NAME;

    public static String getCurrentlyEditedPresetName() {
        return CURRENTLY_EDITED_PRESET_NAME;
    }

    public static void setCurrentlyEditedPresetName(String presetName) {
        CURRENTLY_EDITED_PRESET_NAME = presetName;
    }


    /**
     * currently loaded preset
     */
    private static Preset LOADED_PRESET;

    public static Preset getLoadedPreset() {
        return LOADED_PRESET;
    }

    public static void setLoadedPreset(String newPresetName) throws Exception {
        LOADED_PRESET = Preset.loadPreset(Path.of(RESOURCE_DIRECTORY_ABS_PATH + "\\" + newPresetName + ".properties"));
        CURRENTLY_ACTIVE_PRESET_NAME = newPresetName;

        ATSLogic.mainLogic();
        saveSettingsIntoDataFile();
        ATSWatchman.notifyChange();
    }

    /**
     * Shortcut to get warning time written in loaded preset
     *
     * @return warning time in minutes
     */
    public static int getLoadedWarningTime() {
        return LOADED_PRESET.getWarningTime();
    }

    /**
     * Shortcut get shutdown time written in loaded preset
     *
     * @return shutdown time as a LocalTime
     */
    public static LocalTime getLoadedShutdownTime() {
        return LOADED_PRESET.getTimeOfShutdown();
    }

    /**
     * keys which should be defined in preset file for it to be valid
     */
    public static List<String> PROPERTY_KEY_NAMES;


    /**
     * Reference to main scheduler which is responsible for timed program execution
     * (executing programs on given time/schedule)
     */
    private static Scheduler MAIN_SCHEDULER;

    private static void startupMainScheduler() throws SchedulerException {
        if (MAIN_SCHEDULER != null)
            killMainScheduler();
        MAIN_SCHEDULER = StdSchedulerFactory.getDefaultScheduler();
        MAIN_SCHEDULER.start();
    }

    private static void killMainScheduler() throws SchedulerException {
        if (!MAIN_SCHEDULER.isShutdown())
            MAIN_SCHEDULER.shutdown(false);
    }

    public static void scheduleJobOnScheduler(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        if (!MAIN_SCHEDULER.isShutdown())
            MAIN_SCHEDULER.scheduleJob(jobDetail, trigger);
    }

    public static void removeJobFromScheduler(JobKey jobKey) throws SchedulerException {
        if (!MAIN_SCHEDULER.isShutdown() && MAIN_SCHEDULER.checkExists(jobKey))
            MAIN_SCHEDULER.deleteJob(jobKey);
    }


    //**Other Functions**\\
    public static void startupSequence() throws Exception {
        initializeDataFolder();

        loadSettingsFromDataFile();

        initializeLock();

        startupMainScheduler();

        scheduleDayResetJob();

        setLoadedPreset(CURRENTLY_ACTIVE_PRESET_NAME);
    }

    public static void shutdownSequence() throws Exception {
        killMainScheduler();
        saveSettingsIntoDataFile();
    }


    //*** Utility ***\\
    private static void scheduleDayResetJob() throws SchedulerException {
        JobDetail job = newJob(DayResetJob.class)
                .withIdentity("dayResetter")
                .build();

        //TODO: test if this works
        Trigger trigger = newTrigger()
                .withIdentity("resetTrigger")
                .withSchedule(dailyAtHourAndMinute(0, 0))
                .build();

        scheduleJobOnScheduler(job, trigger);
    }
}
