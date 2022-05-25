package cx3n1.projects.ats;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import jfxtras.scene.control.LocalTimePicker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Properties;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {


    public LocalTimePicker tpkr_shutdown_time;

    public Slider sld_warning_time;
    public TextField txtf_warning_time;

    public CheckBox chck_monday;
    public CheckBox chck_tuesday;
    public CheckBox chck_wednesday;
    public CheckBox chck_thursday;
    public CheckBox chck_friday;
    public CheckBox chck_saturday;
    public CheckBox chck_sunday;

    public TextField txtf_tts_hours;
    public TextField txtf_tts_minutes;
    public TextField txtf_tts_seconds;
    public ProgressBar prgb_tts_progress;

    public Button btn_save;

    //TODO: implement this too
    public Button btn_new_preset;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sld_warning_time.valueProperty().addListener((observableValue, number, t1) -> onSliding());
    }

    public void onClickSaveButton(ActionEvent actionEvent) {

        String presetPath = ATSWatchman.RESOURCE_DIRECTORY + "\\" + ATSWatchman.CURRENTLY_SELECTED_PRESET + ".properties";

        if (fileDoesNotExistsAndCouldNotBeCreated(presetPath)) return;

        try (FileOutputStream os = new FileOutputStream(presetPath)) {
            fillInProperties(os);
        } catch (IOException e) {
            errorAlert("Couldn't find/open/modify preset!");
            e.printStackTrace();
            return;
        }

        //exit out of config window

        confirmAlert("your preset has been saved!");
    }

    public void onSliding() {
        txtf_warning_time.setText(Long.toString(Math.round(sld_warning_time.getValue())));
    }


    //** Utils **\\
    private void fillInProperties(FileOutputStream os) throws IOException {
        Properties prop = new Properties();

        prop.setProperty("time.shutdown.hour", Integer.toString(tpkr_shutdown_time.getLocalTime().getHour()));
        prop.setProperty("time.shutdown.minute", Integer.toString(tpkr_shutdown_time.getLocalTime().getMinute()));

        prop.setProperty("time.warning", txtf_warning_time.getText());

        prop.setProperty("day.monday", String.valueOf(chck_monday.isSelected()));
        prop.setProperty("day.tuesday", String.valueOf(chck_tuesday.isSelected()));
        prop.setProperty("day.wednesday", String.valueOf(chck_wednesday.isSelected()));
        prop.setProperty("day.thursday", String.valueOf(chck_thursday.isSelected()));
        prop.setProperty("day.friday", String.valueOf(chck_friday.isSelected()));
        prop.setProperty("day.saturday", String.valueOf(chck_saturday.isSelected()));
        prop.setProperty("day.sunday", String.valueOf(chck_sunday.isSelected()));

        prop.store(os, "Stored new preset" + LocalTime.now());
    }

    private boolean fileDoesNotExistsAndCouldNotBeCreated(String presetPath) {
        if(!Files.exists(Path.of(presetPath))) {
            try {
                Files.createFile(Path.of(presetPath));
            } catch (IOException e) {
                errorAlert("Couldn't create preset!");
                e.printStackTrace();
                return true;
            }
        }
        return false;
    }

    public static void errorAlert(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("There has been an error");
        alert.setContentText(text);

        alert.showAndWait();
    }

    public static void confirmAlert(String text){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Preset Saved");
        alert.setHeaderText("Congratulations!");
        alert.setContentText(text);

        alert.showAndWait();
    }

}
