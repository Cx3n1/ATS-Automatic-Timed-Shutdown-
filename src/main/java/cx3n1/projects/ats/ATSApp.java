package cx3n1.projects.ats;
//Auto Timed Shutdown System

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class ATSApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ATSApp.class.getResource("Main-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 370, 390);

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
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        launch();
    }


}

