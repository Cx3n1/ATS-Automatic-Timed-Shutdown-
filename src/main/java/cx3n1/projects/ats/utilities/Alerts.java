package cx3n1.projects.ats.utilities;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SystemUtils;

import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Alerts {

    public static Runnable alertSound;

    static{
        if(SystemUtils.IS_OS_WINDOWS){
            alertSound = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        }
    }


    public static void error(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("There has been an error!");
        alert.setContentText(text);

        ringAlertSound();

        alert.showAndWait();
    }

    public static boolean confirm(String header, String text){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation requested");
        alert.setHeaderText(header);
        alert.setContentText(text);

        ringAlertSound();

        Optional<ButtonType> buttonType = alert.showAndWait();
        return buttonType.get() == ButtonType.OK;
    }

    public static void info(String header, String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(header);
        alert.setContentText(text);

        ringAlertSound();

        alert.showAndWait();
    }


    //*** Utility ***\\
    private static void ringAlertSound() {
        if(alertSound != null) alertSound.run();
    }
}
