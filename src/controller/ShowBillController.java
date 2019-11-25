package controller;

import com.hub.schoolAid.Notification;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ShowBillController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private Button createBill;

    @FXML
    private Pane separator;

    @FXML
    private TableView<?> billTableview;

    @FXML
    private Button close;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createBill.setOnAction(event -> showCreateBillForm());
    }

    // show the form to create a new bill
    private void showCreateBillForm() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader = new FXMLLoader(getClass().getResource("/view/Bill.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            Notification.getNotificationInstance().notifyError("An error occurred while loading the form", "error!");
            e.printStackTrace();
        }
    }
}
