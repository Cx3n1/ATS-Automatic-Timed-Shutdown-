package cx3n1.projects.ats;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Alerts {
    public static void error(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("There has been an error!");
        alert.setContentText(text);

        alert.showAndWait();
    }

    public static boolean confirm(String header, String text){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation requested");
        alert.setHeaderText(header);
        alert.setContentText(text);

        Optional<ButtonType> buttonType = alert.showAndWait();
        return buttonType.get() == ButtonType.OK;
    }

    public static void info(String header, String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.showAndWait();
    }
}
