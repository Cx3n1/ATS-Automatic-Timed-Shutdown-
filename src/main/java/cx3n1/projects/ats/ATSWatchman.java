package cx3n1.projects.ats;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSWatchman /*implements Initializable*/ {

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
    public static String[] PROPERTY_KEY_NAMES;

    /***
     * reference to thread which is waiting till time is right for shutdown
     */
    public static Thread WAITER_THREAD;

    /***
     * thread that runs separately from gui thread and which runs logic
     */
    public static Thread LOGIC_THREAD;


    /*@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        RESOURCE_DIRECTORY = "D:\\OneDrives\\OneDrive - ens.tsu.edu.ge\\Programming stuff\\Java\\MainProjects\\ATS\\src\\main\\resources\\cx3n1\\projects\\ats";
        CURRENT_PRESET_FILE_NAME = "Default";
        CURRENTLY_SELECTED_PRESET_NAME = "Default";
        PROPERTY_KEY_NAMES = new String[]{"time.shutdown.hour", "time.shutdown.minute", "time.warning", "day.monday", "day.tuesday", "day.wednesday", "day.thursday", "day.friday", "day.saturday", "day.sunday"};


        WAITER_THREAD = null;
        LOGIC_THREAD = null;

        //TODO: Remove this temporary solution
        try {
            LOADED_PRESET = Preset.CreateNewPreset(Path.of(RESOURCE_DIRECTORY + "\\" + CURRENT_PRESET_FILE_NAME + ".properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void initialize() {
        RESOURCE_DIRECTORY = "C:\\programing\\Projects\\ATS\\src\\main\\resources\\cx3n1\\projects\\ats";
        CURRENT_PRESET_FILE_NAME = "Default";
        CURRENTLY_SELECTED_PRESET_NAME = "Default";
        PROPERTY_KEY_NAMES = new String[]{"time.shutdown.hour", "time.shutdown.minute", "time.warning", "day.monday", "day.tuesday", "day.wednesday", "day.thursday", "day.friday", "day.saturday", "day.sunday"};


        WAITER_THREAD = null;
        LOGIC_THREAD = null;

        //TODO: Remove this temporary solution
        try {
            LOADED_PRESET = Preset.CreateNewPresetDefault(Path.of(RESOURCE_DIRECTORY + "\\" + CURRENT_PRESET_FILE_NAME + ".properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
