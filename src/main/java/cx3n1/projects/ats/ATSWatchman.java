package cx3n1.projects.ats;

import cx3n1.projects.ats.data.Preset;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSWatchman {

    /***
     * file which will store all the info which needs to be saved after closing program
     * such as current preset and default location of resource files
     */
    private static String PROGRAM_DATA_FILE = "Data.properties";

    /***
     * path to directory where all presets are stored
     */
    public static String RESOURCE_DIRECTORY;

    /***
     * name of preset file currently in use
     */
    public static String CURRENT_PRESET_FILE_NAME;

    /***
     * selected preset which is currently can be edited by user
     */
    public static String CURRENTLY_SELECTED_PRESET_NAME;

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

    /***
     * reference to thread that runs separately from gui thread and which runs logic
     */
    public static Thread LOGIC_THREAD;

    /***
     * reference to thread which controls progress bar in main view
     */
    public static Thread PROGRESS_BAR_THREAD;


    public static void initialize() throws Exception {
        loadPropertiesFromDataFile();

        try {
            LOADED_PRESET = Preset.loadPreset(Path.of(RESOURCE_DIRECTORY + "\\" + CURRENT_PRESET_FILE_NAME + ".properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void loadPropertiesFromDataFile() throws Exception {
        if(!Files.exists(Path.of(PROGRAM_DATA_FILE))){
            throw new Exception("FATAL ERROR: Couldn't find Data file!");
        }

        try(FileInputStream fis = new FileInputStream(PROGRAM_DATA_FILE)){
            Properties prop = new Properties();
            prop.load(fis);
            RESOURCE_DIRECTORY = prop.getProperty("RESOURCE_DIRECTORY");
            CURRENT_PRESET_FILE_NAME = prop.getProperty("CURRENT_PRESET_FILE_NAME");
            CURRENTLY_SELECTED_PRESET_NAME = CURRENT_PRESET_FILE_NAME;

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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
