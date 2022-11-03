package cx3n1.projects.ats;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import cx3n1.projects.ats.interfaces.StaticObservable;
import cx3n1.projects.ats.utilities.Alerts;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ATSWatchman extends StaticObservable {
    public static void startupSequence() throws Exception {

        ATSSettings.startupSequence();
        //notifyChange() ATSSettings.startupSequence() will notify everybody, so extra notification is useless
        //ATSLogic.mainLogic(); same this is called in ATSSettings.startupSequence()
    }

    public static void shutdownSequence() throws Exception {
        //could add some code here if needed...
        ATSSettings.shutdownSequence();
    }

    public static void shutdownSequenceOnOpenStage(Stage stage, FXTrayIcon trayIcon) {
        Platform.runLater(stage::close);
        try {
            ATSWatchman.shutdownSequence();
        } catch (Exception ex) {
            Alerts.error("Couldn't shutdown ATS!");
            ex.printStackTrace();
        }
        trayIcon.hide();
    }

}
