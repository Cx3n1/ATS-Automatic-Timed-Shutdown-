package cx3n1.projects.ats.controllers;

import cx3n1.projects.ats.ATSWatchman;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainViewController implements Initializable {

    public ProgressBar prgb_tts_progress;
    public TextField txtf_tts_hours;
    public TextField txtf_tts_minutes;
    public TextField txtf_tts_seconds;

    public TextField txtf_current_preset;
    public ObservableList<String> listContent = FXCollections.observableArrayList();
    public ListView<String> lstv_available_presets = new ListView<>();

    public Button btn_set_preset;
    public Button btn_add_new;
    public Button btn_delete;
    public Button btn_edit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lstv_available_presets.setItems(listContent);
    }

    public void updateParameters() throws IOException {
        txtf_current_preset.setText(ATSWatchman.CURRENT_PRESET_FILE_NAME);

        updatePresetList();
    }

    private void updatePresetList() throws IOException {
        if(!Files.exists(Path.of(ATSWatchman.RESOURCE_DIRECTORY))){
            throw new FileNotFoundException();
        }

        Stream<Path> streamOfResources = Files.list(Path.of(ATSWatchman.RESOURCE_DIRECTORY));
        List<Path> listOfResources = streamOfResources.filter(r -> r.getFileName().toString().endsWith(".properties")).collect(Collectors.toList());

        for (int i = 0; i < listOfResources.size(); i++) {
            String nameOfPreset = String.valueOf(listOfResources.get(i).getFileName()).replace(".properties","");

            if(!listContent.contains(nameOfPreset))
                listContent.add(i, nameOfPreset);
        }
    }

    public void onClickReload(ActionEvent actionEvent) {
        try {
            updateParameters();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
