package cx3n1.projects.ats;
//Auto Timed Shutdown System

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import cx3n1.projects.ats.utilities.Alerts;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;


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
        Scene scene = new Scene(fxmlLoader.load(), 360, 400);
        Image icon = new Image(
                Objects.requireNonNull(
                ATSApp.class.getClassLoader().getResourceAsStream("icon.png")
                ));


        Platform.setImplicitExit(false);

        FXTrayIcon trayIcon = new FXTrayIcon(stage);
        trayIcon.addExitItem("Exit", e -> {
            ATSWatchman.shutdownSequenceOnOpenStage(stage, trayIcon);
        });
        trayIcon.setGraphic(icon); //apparently it needs javafx.swing added to vm options to work
        trayIcon.show();

        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(icon);
        stage.setTitle("Automatic Timed Shutdown");
        stage.setResizable(false);
        stage.requestFocus();
        stage.toFront();

        stage.setScene(scene);
        //stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}

