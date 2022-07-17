package cx3n1.projects.ats;

import cx3n1.projects.ats.data.Preset;
import cx3n1.projects.ats.interfaces.Updatable;
import cx3n1.projects.ats.jobs.DayResetJob;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSWatchman {

    /***
     * file which will store all the info which needs to be saved after closing program
     * such as current preset and default location of resource files
     */
    private static final String PROGRAM_DATA_FILE = "Data.properties";

    /***
     * time zone offset of user
     */
    public static ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(4);

    /***
     * path to directory where all presets are stored
     */
    public static Path RESOURCE_DIRECTORY_ABS_PATH;

    /***
     * selected preset which is currently can be edited by user
     */
    public static String CURRENTLY_ACTIVE_PRESET_NAME;

    /***
     * preset name which will be edited if user presses edit button
     */
    public static String CURRENTLY_EDITED_PRESET_NAME;

    /***
     * currently loaded preset
     */
    public static Preset LOADED_PRESET;

    /***
     * keys which should be defined in preset file for it to be valid
     */
    public static List<String> PROPERTY_KEY_NAMES;

    /***
     * reference to thread which is waiting till time is right for shutdown
     */
    public static Thread WAITER_THREAD;

    //TODO: i think it's possible to use one scheduler and use multiple jobs, cancel/reinitialize jobs when needed
    /***
     * scheduler for shutdown sequence
     */
    private static Scheduler SHUTDOWN_SCHEDULER;

    /***
     * scheduler for day reset sequence
     */
    private static Scheduler DAY_RESET_SCHEDULER;

    /***
     * reference to thread which controls progress bar in main view
     */
    public static Thread PROGRESS_BAR_THREAD;

    /***
     * reference to thread which re-initializes main logic every end of the day
     */
    public static Thread DAY_RESET_THREAD;

    @Deprecated
    /***
     * notifies threads that they should shut down
     */
    public static Boolean THREADS_SHUTDOWN_COMMAND = false;

    /***
     * list of listeners which will be updated when appropriate functions are called
     */
    private static final List<Updatable> LISTENER_LIST = new ArrayList<>();

    public static void initialiseEverything() throws Exception {
        initializeWatchmanSettings();
        ATSLogic.mainLogic();
    }


    private static void loadPropertiesFromDataFile() throws Exception {
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

        notifyListeners();
    }


    public static void changeLoadedPreset(String newPresetName) {
        try {
            LOADED_PRESET = Preset.loadPreset(Path.of(RESOURCE_DIRECTORY_ABS_PATH + "\\" + newPresetName + ".properties"));
            CURRENTLY_ACTIVE_PRESET_NAME = newPresetName;

            //TODO: needs alteration or deletion decide when working on progress bar
            //killThreads();

            ATSLogic.mainLogic();

            saveData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyListeners();
    }

    public static void changeTimeZone(String zoneID) throws Exception {
        ZONE_OFFSET = ZoneOffset.of(zoneID);
        saveData();
    }

    public static void deletePresetFile(String fileName) throws Exception {
        Path filePath = Path.of(RESOURCE_DIRECTORY_ABS_PATH + "/" + fileName + ".properties");

        if (!Files.exists(filePath))
            throw new Exception("ERROR: Couldn't find preset file!");

        Files.delete(filePath);

        notifyListeners();
    }

    public static Path getPresetFilePath(String presetName) {
        return Path.of(RESOURCE_DIRECTORY_ABS_PATH + "\\" + presetName + ".properties");
    }

    public static void openConfigWindow(String presetToEdit) throws IOException {
        CURRENTLY_EDITED_PRESET_NAME = presetToEdit;

        Parent root = FXMLLoader.load(Objects.requireNonNull(ATSWatchman.class.getResource("Config-View.fxml")));

        Scene scene = new Scene(root, 490, 302);
        Stage stage = new Stage();

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Config");
        stage.setResizable(false);
        stage.requestFocus();
        stage.toFront();
        stage.setScene(scene);
        stage.show();
    }

    public static void openConfigWindow(Path presetToEdit) throws IOException {
        openConfigWindow(getFilenameWithoutExtension(presetToEdit));
    }

    public static void addListener(Updatable listener) {
        LISTENER_LIST.add(listener);
        notifyListeners();
    }

    public static void removeListener(Updatable listener) {
        LISTENER_LIST.remove(listener);
        notifyListeners();
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

    public static void shutdownSequence() throws Exception {
        saveData();
    }

    //*** Utility ***\\
    private static String getFilenameWithoutExtension(Path presetToEdit) {
        return presetToEdit.getFileName().toString().split("\\.")[0];
    }

    private static void initializeWatchmanSettings() throws Exception {
        loadPropertiesFromDataFile();

        LOADED_PRESET = Preset.loadPreset(getPresetFilePath(CURRENTLY_ACTIVE_PRESET_NAME));

        initialiseDayResetScheduler();

        notifyListeners();
    }

    private static void initialiseDayResetScheduler() throws SchedulerException {
        if (DAY_RESET_SCHEDULER != null)
            DAY_RESET_SCHEDULER.shutdown();
        DAY_RESET_SCHEDULER = StdSchedulerFactory.getDefaultScheduler();
        DAY_RESET_SCHEDULER.start();

        JobDetail job = newJob(DayResetJob.class)
                .withIdentity("dayResetter")
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("resetTrigger")
                .withSchedule(dailyAtHourAndMinute(0, 0))
                .build();

        DAY_RESET_SCHEDULER.scheduleJob(job, trigger);
    }

    private static void saveData() throws Exception {
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

    //TODO: needs alteration or deletion decide when working on progress bar
    /*private static void killThreads() throws SchedulerException {
        long timeout = 10000L;
        long start = LocalTime.now().toSecondOfDay();
        THREADS_SHUTDOWN_COMMAND = true;
        //TODO: do this better?
        while (start - LocalTime.now().toSecondOfDay() < timeout &&
                (WAITER_THREAD == null || WAITER_THREAD.isAlive()) &&
                (PROGRESS_BAR_THREAD == null || PROGRESS_BAR_THREAD.isAlive())
        ) {
            //TODO: wait till they die or timeout happens
        }
        THREADS_SHUTDOWN_COMMAND = false;

        if (SHUTDOWN_SCHEDULER != null)
            SHUTDOWN_SCHEDULER.shutdown();
    }*/

    private static void notifyListeners() {
        for (Updatable updatable : LISTENER_LIST) {
            updatable.update();
        }
    }


}
