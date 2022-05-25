package cx3n1.projects.ats;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSWatchman {

    /***
     * path to directory where all presets are stored
     */
    public static String RESOURCE_DIRECTORY = "D:\\OneDrives\\OneDrive - ens.tsu.edu.ge\\Programming stuff\\Java\\MainProjects\\ATS\\src\\main\\resources\\cx3n1\\projects\\ats";

    /***
     * preset which is currently in use
     */
    public static String CURRENT_PRESET = "Default";

    /***
     * selected preset which is currently can be edited by user
     */
    public static String CURRENTLY_SELECTED_PRESET = "Default";

    public static String[] PROPERTY_KEY_NAMES = {"time.shutdown.hour", "time.shutdown.minute", "time.warning",
            "day.monday", "day.tuesday", "day.wednesday", "day.thursday", "day.friday", "day.saturday", "day.sunday"};

}
