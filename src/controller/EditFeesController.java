package controller;

import com.hub.schoolAid.Notification;
import com.hub.schoolAid.Student;
import com.hub.schoolAid.StudentDao;
import com.hub.schoolAid.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditFeesController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private RadioButton payFees;

    @FXML
    private ToggleGroup feesAction;

    @FXML
    private RadioButton resetFees;

    @FXML
    private RadioButton updateFees;

    @FXML
    private ToggleGroup feesAction1;

    @FXML
    private Button close;

    @FXML
    private Button save;

    @FXML
    private TextField amount;

    private Student student;

    public void init(Student s) {
        this.student =s;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        feesAction.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == resetFees) {
                    amount.setVisible(false);
                } else amount.setVisible(true);
            }
        });

        save.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("Confirm Action");
            alert.setHeaderText("Edit School Fees for "+ this.student.toString());
            alert.setContentText("Are you sure you want to continue this action?");
            alert.initModality(Modality.APPLICATION_MODAL);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.YES) {
                if(this.confirmAction()) {
                    Notification.getNotificationInstance().notifySuccess("Fees for "+ student.toString()+ " has been updated", "Success");
                } else Notification.getNotificationInstance().notifyError("Could not update fees for "+ this.student.toString()+ "\nThis action is not allowed for students who do not pay fees.", "Error");
            }
        });

        close.setOnAction(event -> Utils.closeEvent(event));
    }

    private Boolean confirmAction() {
        Boolean status=false;
        if (feesAction.getSelectedToggle() != null) {
            StudentDao studentDao = new StudentDao();

            // check the appropriate action
            if(feesAction.getSelectedToggle() == resetFees) {
                status= studentDao.resetSchoolFees(this.student);
            } else {
                Double amt = Double.valueOf(amount.getText().trim());
                if(amt != null) {
                    if(feesAction.getSelectedToggle() == payFees) {
                        status= studentDao.paySchoolFee(student, amt);
                    } else if(feesAction.getSelectedToggle() == updateFees) {
                        status= studentDao.updateSchoolFee(this.student, amt);
                    }
                }
            }
        }
        return status;
    }
}
