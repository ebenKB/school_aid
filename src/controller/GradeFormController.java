package controller;

import com.hub.schoolAid.Grade;
import com.hub.schoolAid.GradeDao;
import com.hub.schoolAid.Notification;
import com.hub.schoolAid.Utils;
import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class GradeFormController implements Initializable{
    @FXML
    private AnchorPane root;

    @FXML
    private Pane separator;

    @FXML
    private TextField gradeText;

    @FXML
    private TextField minMark;

    @FXML
    private TextField maxMark;

    @FXML
    private TextField remark;

    @FXML
    private Button close;

    @FXML
    private Button save;

    @FXML
    private FontAwesomeIconView gradeErr;

    @FXML
    private FontAwesomeIconView minErr;

    @FXML
    private FontAwesomeIconView maxErr;

    @FXML
    private FontAwesomeIconView remarkErr;

    @FXML
    private Label infoLabel;

    @FXML
    private JFXProgressBar progressBar;

    private void showError(FontAwesomeIconView icon){
        icon.setVisible(Boolean.TRUE);
    }

    private void showSuccess(){
        //change background colour to success
    }

    private  Boolean validate(){
        Boolean isValid= true;

        if((gradeText.getText().trim().length()<0) || (!gradeText.getText().trim().matches("[a-zA-Z0-9]+"))){
            showError(gradeErr);
            isValid= false;
        }

        if((minMark.getText().trim().length()<0) || (!minMark.getText().trim().matches(("[0-9]+")))){
            showError(minErr);
            isValid=false;
        }

        if((maxMark.getText().trim().length()<0) || (!maxMark.getText().trim().matches(("[0-9]+")))){
            showError(maxErr);
            isValid=false;
        }

        if((remark.getText().trim().length()<0) || (!gradeText.getText().trim().matches(("[a-zA-Z]+")))){
            showError(minErr);
            isValid=false;
        }
        return isValid;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        save.setOnAction(e->{
            Task create = new Task() {
                @Override
                protected Object call() throws Exception {
                    createGrade();
                    return null;
                }
            };
            create.setOnRunning(event -> progressBar.setVisible(true));
            create.setOnSucceeded(event -> {
                progressBar.setVisible(false);
                Notification.getNotificationInstance().notifySuccess("You added a new grade","Success");
                clearFields();
            });
            create.setOnFailed(event -> {
                progressBar.setVisible(false);
                Notification.getNotificationInstance().notifyError("An error occured while adding the grade","Failure");
            });
            new Thread(create).start();
        });

        close.setOnAction(event -> Utils.closeEvent(event));
    }

    private void createGrade(){
        if(validate()){
            Grade grade = new Grade(gradeText.getText().trim(),Double.valueOf(maxMark.getText().trim()),Double.valueOf(minMark.getText().trim()),remark.getText().trim());
            GradeDao gradeDao = new GradeDao();
            gradeDao.createGrade(grade);
        }
    }

    private void  clearFields (){
        minMark.clear();
        maxMark.clear();
        remark.clear();
        gradeText.clear();
    }
}
