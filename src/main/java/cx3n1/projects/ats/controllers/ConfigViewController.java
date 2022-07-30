package cx3n1.projects.ats.controllers;

import cx3n1.projects.ats.ATSSettings;
import cx3n1.projects.ats.ATSWatchman;
import cx3n1.projects.ats.utilities.Alerts;
import cx3n1.projects.ats.utilities.Utils;
import cx3n1.projects.ats.data.Preset;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalTimePicker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Properties;
import java.util.ResourceBundle;

public class ConfigViewController implements Initializable {

    public LocalTimePicker tpkr_shutdown_time;

    public Slider sld_warning_time;
    public TextField txtf_warning_time;

    public TextField txtf_name;

    public CheckBox chck_monday;
    public CheckBox chck_tuesday;
    public CheckBox chck_wednesday;
    public CheckBox chck_thursday;
    public CheckBox chck_friday;
    public CheckBox chck_saturday;
    public CheckBox chck_sunday;

    public Button btn_save;
    public Button btn_cancel;

    /**
     * initialize config view window at the start
     * this includes filling in fields which displays info about currently configured preset
     * (for which config window was open)
     * and making slider work with some "observer pattern magic"
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sld_warning_time.valueProperty().addListener((observableValue, number, t1) -> onSliding());

        txtf_name.setText(ATSSettings.getCurrentlyEditedPresetName());

        try {
            fillConfigWindowFromPreset(Utils.Paths.getPresetFilePath(ATSSettings.CURRENTLY_EDITED_PRESET_NAME));
        } catch (Exception e) {
            Alerts.error("Couldn't load preset");
            e.printStackTrace();
            closeThisWindow();
        }
    }

    public void onClickSaveButton(ActionEvent actionEvent) {
        boolean confirmed = Alerts.confirm("Are you sure you want to save?","You cannot undo changes after you save!");

        if(!confirmed) return;

        if(txtf_name.getText() == null || txtf_name.getText().isEmpty()){
            Alerts.error("Preset name cannot be empty");
            return;
        }

        String presetPath = Utils.Paths.getPresetFilePath(txtf_name.getText()).toString();

        try {
            Utils.Paths.createFileIfItDoesNotExists(presetPath);
        } catch (IOException e) {
            Alerts.error("Couldn't create preset!");
            e.printStackTrace();
            return;
        }

        try (FileOutputStream os = new FileOutputStream(presetPath)) {
            fillInPropertiesFromConfigWindow(os);
        } catch (IOException e) {
            Alerts.error("Couldn't find/open/modify preset!");
            e.printStackTrace();
            return;
        }

        if(!txtf_name.getText().equals(ATSSettings.CURRENTLY_EDITED_PRESET_NAME)){
            try {
                Files.delete(Utils.Paths.getPresetFilePath(ATSSettings.CURRENTLY_EDITED_PRESET_NAME));
            } catch (IOException e) {
                Alerts.error("Couldn't delete old preset file!");
                e.printStackTrace();
            }
        }

        if(ATSSettings.CURRENTLY_EDITED_PRESET_NAME.equals(ATSSettings.CURRENTLY_ACTIVE_PRESET_NAME)){
            try {
                ATSSettings.setLoadedPreset(txtf_name.getText());
            } catch (Exception e) {
                Alerts.error("Couldn't reset loaded preset name!");
                e.printStackTrace();
            }
        }

        closeThisWindow();
    }

    public void onClickCancelButton(ActionEvent actionEvent) {
        boolean confirm = Alerts.confirm("Are you sure you want to cancel?","If you cancel all unsaved changes will be lost!");

        if(confirm) closeThisWindow();
    }

    public void onSliding() {
        txtf_warning_time.setText(Long.toString(Math.round(sld_warning_time.getValue())));
    }


    //** Utils **\\
    private void fillConfigWindowFromPreset(Path pathToPreset) throws Exception {
        fillConfigWindowFromPreset(Preset.loadPreset(pathToPreset));
    }
    private void fillConfigWindowFromPreset(Preset preset){
        tpkr_shutdown_time.setLocalTime(preset.getTimeOfShutdown());

        txtf_warning_time.setText(String.valueOf(preset.getWarningTime()));
        sld_warning_time.setValue(preset.getWarningTime());

        chck_monday.setSelected(preset.getDays()[0]);
        chck_tuesday.setSelected(preset.getDays()[1]);
        chck_wednesday.setSelected(preset.getDays()[2]);
        chck_thursday.setSelected(preset.getDays()[3]);
        chck_friday.setSelected(preset.getDays()[4]);
        chck_saturday.setSelected(preset.getDays()[5]);
        chck_sunday.setSelected(preset.getDays()[6]);
    }
    private void fillInPropertiesFromConfigWindow(FileOutputStream os) throws IOException {
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
    private void closeThisWindow() {
        Stage stage = (Stage) btn_save.getScene().getWindow();
        stage.close();
        ATSWatchman.notifyChange();
    }
}
