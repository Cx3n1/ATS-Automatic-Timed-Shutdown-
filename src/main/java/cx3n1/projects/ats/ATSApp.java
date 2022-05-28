package cx3n1.projects.ats;
//Auto Timed Shutdown System

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.io.IOException;


public class ATSApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ATSApp.class.getResource("Main-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 370, 388);

        stage.setTitle("ATS");
        stage.setResizable(false);
        stage.requestFocus();
        stage.toFront();

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        initialiseEverything();
        launch();
    }

    private static void initialiseEverything() {
        try {
            ATSWatchman.initialize();
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fatal Error!");
            alert.setHeaderText("Fatal Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            System.exit(1);
        }
        ATSWatchman.LOGIC_THREAD = new Thread(() -> {
            try {
                ATSLogic.mainLogic();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ATSWatchman.LOGIC_THREAD.start();
    }
}

