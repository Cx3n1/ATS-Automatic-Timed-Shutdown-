package cx3n1.projects.ats;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ATSController {
    @FXML
    private Button btn_Understand;

    @FXML
    protected void onUnderstandButtonClick() {
        Stage stage = (Stage) btn_Understand.getScene().getWindow();
        stage.close();
    }
}