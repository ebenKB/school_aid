package controller;

import com.hub.schoolAid.Notification;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ShowClassGroupController implements Initializable {
    @FXML
    private Button newGroup;

    private void showNewGroupForm() {
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/group.fxml"));
        try {
            root=fxmlLoader.load();
            Scene scene =new Scene(root);
            javafx.stage.Stage stage =new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("New Group");
            stage.show();
        } catch (IOException e) {
            Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "error");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newGroup.setOnAction(event -> showNewGroupForm());
    }
}
