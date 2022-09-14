package cx3n1.projects.ats.utilities;

import cx3n1.projects.ats.ATS;
import cx3n1.projects.ats.ATSSettings;
import cx3n1.projects.ats.ATSWatchman;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class Utils {

    public static class Paths{
        public static Path getPresetFilePath(String presetName) {
            return Path.of(ATSSettings.RESOURCE_DIRECTORY_ABS_PATH.toString(), presetName + ".properties");
        }
        public static String getFilenameWithoutExtension(Path filePath) {
            return filePath.getFileName().toString().split("\\.")[0];
        }
        public static String removeFileExtension(String fileName){
            return fileName.split("\\.")[0];
        }
        public static void createFileIfItDoesNotExists(String presetPath) throws IOException {
            if (!Files.exists(Path.of(presetPath))) {
                Files.createFile(Path.of(presetPath));
            }
        }
    }

    public static class WindowCtrl {
        public static void openConfigWindowOf(Path presetToEdit) throws IOException {
            openConfigWindowOf(Paths.getFilenameWithoutExtension(presetToEdit));
        }
        public static void openConfigWindowOf(String presetToEdit) throws IOException {
            ATSSettings.CURRENTLY_EDITED_PRESET_NAME = presetToEdit;

            Parent root = FXMLLoader.load(Objects.requireNonNull(ATS.class.getResource("Config-View.fxml")));

            Scene scene = new Scene(root, 490, 302);
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Config");
            stage.setResizable(false);
            stage.requestFocus();
            stage.toFront();
            stage.setScene(scene);
            stage.show();
        }
    }

    public static void deletePresetFile(String fileName) throws Exception {
        Path filePath = Utils.Paths.getPresetFilePath(fileName);

        if (!Files.exists(filePath))
            throw new Exception("ERROR: Couldn't find preset file!");

        Files.delete(filePath);

        ATSWatchman.notifyChange();
    }
}

