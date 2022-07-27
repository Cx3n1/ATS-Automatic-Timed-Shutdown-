package cx3n1.projects.ats.controllers;

import cx3n1.projects.ats.ATSSettings;
import cx3n1.projects.ats.ATSWatchman;
import cx3n1.projects.ats.utilities.Alerts;
import cx3n1.projects.ats.utilities.Utils;
import cx3n1.projects.ats.data.Preset;
import cx3n1.projects.ats.interfaces.Updatable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainViewController implements Initializable, Updatable {

    public ProgressBar prgb_tts_progress;
    public TextField txtf_tts_hours;
    public TextField txtf_tts_minutes;
    public TextField txtf_current_preset;
    public TextField txtf_current_time_zone;
    public ListView<String> lstv_available_presets = new ListView<>();
    public ObservableList<String> listContent = FXCollections.observableArrayList();

    public Button btn_set_preset;
    public Button btn_add_new;
    public Button btn_delete;
    public Button btn_edit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lstv_available_presets.setItems(listContent);

        try {
            updateParameters();
        } catch (IOException e) {
            Alerts.error("Couldn't reload preset list!");
            e.printStackTrace();
        }

        ATSWatchman.addListener(this);

        //TODO: progress bar thingie work this out later
        //startProgressBar();
    }

    //TODO: figure out progress bar
    /*private void startProgressBar() {
        ATSWatchman.PROGRESS_BAR_THREAD = new Thread(getProgressbarUpdaterTask());
        ATSWatchman.PROGRESS_BAR_THREAD.setDaemon(true);
        ATSWatchman.PROGRESS_BAR_THREAD.start();
    }*/

    public void onClickReload(ActionEvent actionEvent) {
        try {
            updateParameters();
        } catch (IOException e) {
            Alerts.error("Couldn't reload preset list!");
            e.printStackTrace();
        }
    }

    public void onClickSetPreset(ActionEvent actionEvent) {
        String selectedPreset = getSelectedItem();

        if(selectedPreset == null){
            Alerts.error("Please select preset you want to set!");
            return;
        }

        ATSSettings.setLoadedPreset(selectedPreset);
        try {
            updateParameters();
        } catch (IOException e) {
            Alerts.error("Couldn't reload preset list!");
            e.printStackTrace();
        }
    }

    public void onClickAddNew(ActionEvent actionEvent) {
        Path pathToPresetFile;

        try {
            pathToPresetFile = createUniquePresetFile();
        } catch (IOException e) {
            Alerts.error("Couldn't create new preset");
            e.printStackTrace();
            return;
        }

        try {
            Preset.CreateNewPresetDefault(pathToPresetFile);
        } catch (Exception e) {
            Alerts.error("Couldn't create new preset");
            e.printStackTrace();
            return;
        }

        try {
            Utils.WindowCtrl.openConfigWindowOf(pathToPresetFile);
        } catch (IOException e) {
            Alerts.error("Couldn't edit new preset");
            e.printStackTrace();
        }

        ATSWatchman.notifyChange();
    }

    public void onClickDelete(ActionEvent actionEvent) {
        String selectedPreset = getSelectedItem();

        if(selectedPreset == null){
            Alerts.error("Please select preset you want to delete!");
            return;
        }

        if(isPresetDefaultOrCurrent(selectedPreset)){
            Alerts.error("Deleting current or Default presets is not allowed!");
            return;
        }

        try {
            removePreset(selectedPreset);
        } catch (Exception e) {
            Alerts.error("Couldn't delete selected preset!");
            e.printStackTrace();
        }

    }

    public void onClickEdit(ActionEvent actionEvent) {
        String selectedPreset = getSelectedItem();

        if(selectedPreset == null || selectedPreset.isEmpty()){
            Alerts.error("Please select preset you want to edit!");
            return;
        }

        if(selectedPreset.equals("Default")){
            Alerts.error("Editing Default presets is not allowed!");
            return;
        }

        try {
            Utils.WindowCtrl.openConfigWindowOf(selectedPreset);
        } catch (IOException e) {
            Alerts.error("Couldn't edit config!");
            e.printStackTrace();
        }
    }

    public void onClickSetTimeZone(ActionEvent actionEvent) {
        String zone_offset = "+0";

        try {
            zone_offset = txtf_current_time_zone.getText();
            ATSSettings.changeTimeZone(zone_offset);
        } catch (DateTimeException e) {
            Alerts.error("Please select valid time zone: from -18:00 to +18:00 in format +/-hh:mm or +/-hh!\n(+/- means you write either - or +, e.g. => +04:30, -10:01, +00, -04 etc.)");
            e.printStackTrace();
            txtf_current_time_zone.setText(String.valueOf(ATSSettings.ZONE_OFFSET));
            return;
        } catch (Exception e) {
            Alerts.error("Couldn't save data into data file!");
            e.printStackTrace();
            txtf_current_time_zone.setText(String.valueOf(ATSSettings.ZONE_OFFSET));
            return;
        }

        Alerts.info("Time Zone saved!", "You have successfully set new time zone.");
    }

    @Override
    public void update() {
        LocalTime timeOfShutdown = ATSSettings.getLoadedShutdownTime();

        txtf_tts_hours.setText(String.valueOf(timeOfShutdown.getHour()));
        txtf_tts_minutes.setText(String.valueOf(timeOfShutdown.getMinute()));

        try {
            updateParameters();
        } catch (IOException e) {
            Alerts.error("Couldn't reload preset list!");
            e.printStackTrace();
        }
    }


    //*** Utility ***\\
    //TODO: Progress bar - work in progress (no pun intended)
    /*public Task<Void> getProgressbarUpdaterTask() {
        return new Task<Void>() {
            @Override
            public Void call() {
                Platform.runLater(() -> {
                    int hourOfShutdown = ATSWatchman.LOADED_PRESET.getTimeOfShutdown().getHour();
                    int minuteOfShutdown = ATSWatchman.LOADED_PRESET.getTimeOfShutdown().getMinute();
                    int secondOfShutdown = ATSWatchman.LOADED_PRESET.getTimeOfShutdown().getSecond();

                    LocalTime startTime = LocalTime.now();
                    LocalTime endTime = LocalTime.of(hourOfShutdown, minuteOfShutdown, secondOfShutdown);

                    int secondsBetween = endTime.toSecondOfDay() - startTime.toSecondOfDay();

                    while (!ATSWatchman.THREADS_SHUTDOWN_COMMAND) {
                        txtf_tts_hours.setText(String.valueOf(hourOfShutdown - LocalTime.now().getHour()));
                        txtf_tts_minutes.setText(String.valueOf(minuteOfShutdown - LocalTime.now().getMinute()));

                        prgb_tts_progress.setProgress((endTime.toSecondOfDay() + LocalTime.now().toSecondOfDay())/((double) secondsBetween));

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return null;
            }

        };
    }*/

    private String getSelectedItem() {
        return lstv_available_presets.getSelectionModel().getSelectedItem();
    }

    private Path createUniquePresetFile() throws IOException {
        long i = 0;

        while (Files.exists(Utils.Paths.getPresetFilePath("New Preset " + i))){
            i++;
        }

        return Files.createFile(Utils.Paths.getPresetFilePath("New Preset " + i));
    }

    private void removePreset(String givenPreset) throws Exception {
        listContent.remove(givenPreset);
        Utils.deletePresetFile(givenPreset);
    }

    public void updateParameters() throws IOException {
        txtf_current_preset.setText(ATSSettings.CURRENTLY_ACTIVE_PRESET_NAME);
        txtf_current_time_zone.setText(String.valueOf(ATSSettings.ZONE_OFFSET));
        loadPresetList();
    }

    private void loadPresetList() throws IOException {
        if (!Files.exists(ATSSettings.RESOURCE_DIRECTORY_ABS_PATH)) {
            throw new FileNotFoundException();
        }

        listContent.clear();

        for (String fileName : Objects.requireNonNull(ATSSettings.RESOURCE_DIRECTORY_ABS_PATH.toFile().list())) {
            String presetName = Utils.Paths.removeFileExtension(fileName);
            listContent.add(presetName);
        }
    }

    private boolean isPresetDefaultOrCurrent(String givenPreset) {
        return givenPreset.equals(txtf_current_preset.getText()) || givenPreset.equals("Default");
        //this statically written Default seems like bad practice
    }
}
