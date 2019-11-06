package controller;
import com.hub.schoolAid.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class TrialController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private Label trialLabel;

    @FXML
    private Button continueTrial;

    @FXML
    private Pane separator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        continueTrial.setOnAction(event -> Utils.dispose(event));
    }
}
