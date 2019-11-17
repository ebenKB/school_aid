package controller;
import com.hub.schoolAid.App;
import com.hub.schoolAid.AppDao;
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
    private Button close;

    @FXML
    private Pane separator;

    private AppDao appDao;
//    private App app = new App();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // disable the button
        appDao = new AppDao();
        if(!appDao.appCanBoot(AppDao.getAppSetting())) {
            continueTrial.setDisable(true);
            trialLabel.setText("Your trial period has ended");
        }
        continueTrial.setOnAction(event -> {
            Utils.dispose(event);
        });

        close.setOnAction(event -> {
            Utils.closeEvent(event);
            System.exit(0);
        });
    }
}
