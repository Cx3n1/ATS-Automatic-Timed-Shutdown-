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
import java.time.ZoneOffset;
import java.util.*;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSWatchman {

    /**
    * list of listeners which will be updated when appropriate functions are called
    */
    private static final List<Updatable> LISTENER_LIST = new ArrayList<>();


    public static void initialiseEverything() throws Exception {
        ATSSettings.startupSequence();
        notifyListeners();
        ATSLogic.mainLogic();
    }

    public static void notifyChange(){
        notifyListeners();
    }

    public static void addListener(Updatable listener) {
        LISTENER_LIST.add(listener);
        notifyListeners();
    }

    public static void removeListener(Updatable listener) {
        LISTENER_LIST.remove(listener);
        notifyListeners();
    }

    public static void shutdownSequence() throws Exception {

        ATSSettings.saveSettingsIntoData();
    }

    //*** Utility ***\\
    private static void notifyListeners() {
        for (Updatable updatable : LISTENER_LIST) {
            updatable.update();
        }
    }


}
