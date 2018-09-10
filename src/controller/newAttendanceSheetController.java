package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXDatePicker;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class newAttendanceSheetController implements Initializable{

    @FXML
    private Label dateLabel;

    @FXML
    private Button useSystemDate;

    @FXML
    private Button useNewDate;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private Button save;

    @FXML
    private Button createSheet;

    @FXML
    private Button close;

    private Initializer initializer =Initializer.getInitializerInstance();
    private TermDao termDao =new TermDao();
    private salesDetailsFormController main;
    private Notification notification =Notification.getNotificationInstance();
    AttendanceDao attendanceDao =new AttendanceDao();

//    private ObservableList students = FXCollections.observableArrayList();

    public void init(salesDetailsFormController controller){
        this.main =controller;
    }

    private void populasteStudentTableView(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LocalDate date = termDao.getCurrentDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
            String text = date.format(formatter);

            dateLabel.setText("SYSTEM DATE IS :"+" "+text);
        }catch (NullPointerException e){
            //there is no date in the system
        }

        createSheet.setOnAction(event -> {
            //create a new attendance sheet
            Task createSheet = new Task() {
                @Override
                protected Object call() throws Exception {
                    createNewAttendanceSheet(event);
                    return null;
                }
            };
            createSheet.setOnRunning(e->MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Creating a new Attendance Sheet..."));
            createSheet.setOnFailed(e->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            createSheet.setOnSucceeded(e->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            new Thread(createSheet).start();
        });
        useNewDate.setOnAction(event -> datePicker.setVisible(Boolean.TRUE));

        save.setOnAction(event -> {
            saveDate(event);
        });

        useSystemDate.setOnAction(event -> {
            dateLabel.setText("Today is: "+" "+LocalDate.now().toString());
            dateLabel.setTextFill(Color.valueOf("#3CCC13"));
        });

        close.setOnAction(event ->Utils.closeEvent(event));
    }

    private void saveDate(ActionEvent event){
        try{
            if(datePicker.getValue() !=null){
                if(termDao.updateCurrentDate(datePicker.getValue())){
                    dateLabel.setText(termDao.getCurrentDate().toString());
                    createNewAttendanceSheet(event);
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"", ButtonType.YES,ButtonType.NO);
                alert.setTitle("Change App Date");
                alert.setHeaderText("Use today's date for App");
                alert.setContentText("Are you sure you want to use System date for app settings?");
                Optional<ButtonType>result= alert.showAndWait();
                if(result.isPresent()  && result.get()==ButtonType.YES){
                    if( termDao.updateCurrentDate(LocalDate.now())){
//                        notification.notifySuccess("Date settings have changed successfully"," success");
                        createNewAttendanceSheet(event);
                    }
                }
            }
        }catch (Exception e){
            notification.notifyError("An error occurred while setting the date.","Date Error");
        }
//        if(datePicker.getValue() !=null){
//            if( termDao.updateCurrentDate(datePicker.getValue())){
//                try{
//                    dateLabel.setText(termDao.getCurrentDate().toString());
//                    notification.notifySuccess("You have set a new date for Attendance","success");
//                }catch (NullPointerException e){
//                    notification.notifyError("There is no current date in the system./nPlease create a new term and try again","No date found");
//                }
//            }
//        }else{
//            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"", ButtonType.YES,ButtonType.NO);
//            alert.setTitle("Change App Date");
//            alert.setHeaderText("Use today's date for App");
//            alert.setContentText("Are you sure you want to use System date for app settings?");
//            Optional<ButtonType>result= alert.showAndWait();
//            if(result.isPresent()  && result.get()==ButtonType.YES){
//                if( termDao.updateCurrentDate(LocalDate.now())){
//                    notification.notifySuccess("Date settings have changed successfully"," success");
//                }
//            }
//        }
        save.setDisable(Boolean.FALSE);
//            ((Node)(event).getSource()).getScene().getWindow().hide();
    }
    private void createNewAttendanceSheet(ActionEvent event){
        if(termDao.getCurrentDate().equals(LocalDate.now())){
            Task task = new Task() {
                @Override
                protected Object call() {
                    //move the previous attendance to master table
                    if(attendanceDao.moveAttendanceToMasterTable()){
                        //create a new attendance for all the students and save them in attendance temp.
                        StudentDao studentDao =new StudentDao();
                        AttendanceTemporaryDao attendanceTemporaryDao =new AttendanceTemporaryDao();
                        List<Student> students = studentDao.getAllStudents();
                        for(Student s: students){
                            attendanceTemporaryDao.checkStudenIn(s);
                        }
                        //show the attendance sheet
                        main.attendanceTemporaries.addAll(main.attendanceTemporaryDao.getTempAttendance());
                        main.populateStudentTable();
                        main.attendanceRadio.setSelected(Boolean.TRUE);

                    }else{
                        notification.notifyError("An error occured while preparing the records","Error!");
                    }
                    return null;
                }
            };
            task.setOnRunning(e -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Preparing records..."));
            task.setOnSucceeded(e ->{
                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                ((Node)(event).getSource()).getScene().getWindow().hide();
            });
            task.setOnFailed(event1 -> {
                notification.notifyError("Sorry!! Something went wrong.","Error while creating attendance sheet");
                task.cancel();
                ((Node)(event).getSource()).getScene().getWindow().hide();
                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
            });
            new Thread(task).start();
        }else {
            notification.notifyError("Please set the date for today.\n" +
                    "The date in the system is:" +termDao.getCurrentDate(),"Wrong date");
        }
    }
}
