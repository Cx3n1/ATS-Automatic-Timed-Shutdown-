package cx3n1.projects.ats.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

@Deprecated
public class WarningViewController {
    @FXML
    private Button btn_Understand;

    @FXML
    protected void onUnderstandButtonClick() {
        Stage stage = (Stage) btn_Understand.getScene().getWindow();
        stage.close();
    }
}