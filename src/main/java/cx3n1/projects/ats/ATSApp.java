package cx3n1.projects.ats;
//Auto Timed Shutdown System

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import cx3n1.projects.ats.utilities.Alerts;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class ATSApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ATSApp.class.getResource("Main-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 370, 390);

        FXTrayIcon trayIcon = new FXTrayIcon(stage);
        trayIcon.addExitItem("Exit", e -> {
            shutdownSequence(stage, trayIcon);
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
        try {
            ATSWatchman.startupSequence();
        } catch (Exception e) {
            Alerts.error("There has been a problem in startup sequence!");
            e.printStackTrace();
            System.exit(1);
        }
        launch();
    }


    //*** Utility ***\\
    private void shutdownSequence(Stage stage, FXTrayIcon trayIcon) {
        Platform.runLater(() -> {
            stage.close();
        });
        try {
            ATSWatchman.shutdownSequence();
        } catch (Exception ex) {
            Alerts.error("Couldn't shutdown ATS!");
            ex.printStackTrace();
        }
        trayIcon.hide();
    }

}

