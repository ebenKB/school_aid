package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXTextArea;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javax.rmi.CORBA.Util;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AppSettingsController implements Initializable {
    @FXML
    private AnchorPane primary;

    @FXML
    private TextField nameOfSchool;

    @FXML
    private TextField contactOfSchool;

    @FXML
    private JFXTextArea address;

    @FXML
    private JFXTextArea motto;

    @FXML
    private ComboBox<FeedingType> feeding;

    @FXML
    private ComboBox<String> confirmation;

    @FXML
    private ComboBox<String> notification;

    @FXML
    private Button cancel;

    @FXML
    private Button save;

    @FXML
    private Label errorLabel;

    @FXML
    private Pane root;

    ValidationSupport validator = new ValidationSupport();
    Initializer initializer = Initializer.getInitializerInstance();

    private void setValidation() {
        validator.registerValidator(nameOfSchool, true, Validator.createEmptyValidator("Name is required"));
        validator.registerValidator(contactOfSchool, true, Validator.createEmptyValidator("Contact is required"));
        validator.registerValidator(notification, true, Validator.createEmptyValidator("Please answer Yes or No"));
        validator.registerValidator(confirmation, true, Validator.createEmptyValidator("Please answer Yes or No"));
        validator.registerValidator(feeding, true, Validator.createEmptyValidator("Please select the feeding type"));
    }

    public Boolean validate() {
        if (nameOfSchool.getText().trim().isEmpty()) {
            return false;
        }

        if (!contactOfSchool.getText().trim().isEmpty() && !contactOfSchool.getText().trim().matches("[0-9]+")) {
            return  false;
        }

        if(feeding.getSelectionModel().getSelectedItem() == null) {
            return  false;
        }

        if(notification.getSelectionModel().getSelectedItem() == null) {
            return false;
        }

        if (confirmation.getSelectionModel().getSelectedItem() == null) {
            return false;
        }

        return true;
    }
    void init() {
        confirmation.getItems().addAll("Yes", "No");
        notification.getItems().addAll("Yes", "No");
        feeding.getItems().addAll(FeedingType.CASH, FeedingType.COUPON);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        init();
        setValidation();

        save.setOnAction(event -> {
            if(validator.isInvalid()) {
                // the forms is not valid
                errorLabel.setText("ERROR! Please make sure that all required fields are completed.");
            } else {
                // the forms are valid and we can proceed to save the app settings
                errorLabel.setText("");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
                alert.setTitle("Save settings");
                alert.setHeaderText("Are you sure you want to save the records?");
                alert.initOwner(save.getScene().getWindow());
                Optional<ButtonType>results = alert.showAndWait();
                if(results.isPresent() && results.get() == ButtonType.YES) {
                    Task task = new Task() {
                        @Override
                        protected Object call() throws Exception {
                            createAppSettings();
                            return null;
                        }
                    };
                    task.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Creating app settings..."));
                    task.setOnSucceeded(event1 -> {
                        MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                        ((Node)(event).getSource()).getScene().getWindow().hide();
                        initializer.showLoginForm();
                    });
                    task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                    new Thread(task).start();
                }
            }
        });

        cancel.setOnAction(event -> Utils.closeEvent(event));
    }

    private void createAppSettings() {
        App app = new App();
        app.setName(nameOfSchool.getText().trim());
        app.setContact(contactOfSchool.getText().trim());
        app.setFeedingType(feeding.getSelectionModel().getSelectedItem());
        app.setAddress(address.getText().trim());
        app.setMotto(motto.getText().trim());
        app.setHasInit(true);

        if (notification.getSelectionModel().getSelectedItem().toLowerCase().equals("yes")){
            app.setCanShowPopUp(true);
        } else app.setCanShowPopUp(false);

        if(confirmation.getSelectionModel().getSelectedItem().toLowerCase().equals("yes")){
            app.setCanShowIntroHelp(true);
        } else app.setCanShowIntroHelp(false);



        app.setMaxCount(31); // set max count for trial
        app.setCurrentCount(0);  // set initial count for trial
        AppDao appDao = new AppDao();
        if(appDao.createAppSettings(app));
    }

    public void showAppSettingsForm() {
        javafx.scene.Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/settings.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        }catch (Exception e) {
            Notification.getNotificationInstance().notifyError("An error occured while loading form", "Error");
        }
    }
}
