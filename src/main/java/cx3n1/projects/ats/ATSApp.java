package cx3n1.projects.ats;
//Auto Timed Shutdown System

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import cx3n1.projects.ats.utilities.Alerts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class ATSApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            ATSWatchman.startupSequence();
        } catch (Exception e) {
            Alerts.error("There has been a problem in startup sequence!\n" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(ATS.class.getResource("Main-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 370, 390);

        FXTrayIcon trayIcon = new FXTrayIcon(stage);
        trayIcon.addExitItem("Exit", e -> {
            ATSWatchman.shutdownSequenceOnOpenStage(stage, trayIcon);
        });
        trayIcon.show();

        stage.setTitle("ATS");
        stage.setResizable(false);
        stage.requestFocus();
        stage.toFront();

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}

