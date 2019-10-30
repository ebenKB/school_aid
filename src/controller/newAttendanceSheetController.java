package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXDatePicker;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
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

    @FXML
    private RadioButton todayRadio;

    @FXML
    private ToggleGroup attendaceDateRadioGroup;

    @FXML
    private RadioButton customDateRadio;

    private Initializer initializer =Initializer.getInitializerInstance();
    private TermDao termDao =new TermDao();
    private salesDetailsFormController main;
    private FeedingFormController couponController;
    private Notification notification =Notification.getNotificationInstance();
    AttendanceDao attendanceDao =new AttendanceDao();
    private AppDao appDao;

//    private ObservableList students = FXCollections.observableArrayList();

    public void initCashController(salesDetailsFormController controller, FeedingType feedingType){
        if (feedingType.equals(FeedingType.CASH)) {
            this.main = controller;
        }
    }

    public void intCouponForm(FeedingFormController controller, FeedingType feedingType) {
        this.couponController = controller;
    }

    private void populasteStudentTableView(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            LocalDate date = termDao.getCurrentDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
            String text = date.format(formatter);

            dateLabel.setText("PREVIOUS DATE IS :"+" "+text);
        }catch (NullPointerException e){
            //there is no date in the system
        }

        createSheet.setOnAction(event -> {
            if (attendaceDateRadioGroup.getSelectedToggle() != null) {
                if (attendaceDateRadioGroup.getSelectedToggle() == todayRadio) {
                    if (saveToday())
                        saveAttendanceRegister(event, LocalDate.now());
                } else {
                    if(saveCustomDate()) {
                        saveAttendanceRegister(event, datePicker.getValue());
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
                alert.setTitle("Select an option");
                alert.setContentText("You have not selected any option.\n Please tell us whether you are creating the attendance for today or not.");
                alert.show();
            }
//            Task createSheet = new Task() {
//                @Override
//                protected Object call() throws Exception {
    //                    createNewAttendanceSheet(event, LocalDate.now());
                // check if the radio button is not empty
//                if (attendaceDateRadioGroup.getSelectedToggle() != null) {
//                    if (attendaceDateRadioGroup.getSelectedToggle() == todayRadio) {
//                        if (saveToday())
//                            createNewAttendanceSheet(event, LocalDate.now());
//                    } else {
//                        if(saveCustomDate()) {
//                            createNewAttendanceSheet(event, datePicker.getValue());
//                        }
//                    }
//                } else {
//                    Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
//                    alert.setTitle("Select an option");
//                    alert.setContentText("You have not selected any option.\n Please tell us whether you are creating the attendance for today or not.");
//                    alert.show();
//                }
//                    return null;
//                }
//            };
//            createSheet.setOnRunning(e-> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Creating a new Attendance Sheet..."));
//            EventHandler eventHandler = e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
//            createSheet.setOnFailed(eventHandler);
//            createSheet.setOnSucceeded(eventHandler);
//            new Thread(createSheet).start();
        });

        useNewDate.setOnAction(event -> datePicker.setVisible(Boolean.TRUE));

        attendaceDateRadioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == customDateRadio) {
                    datePicker.setVisible(true);
                } else {
                    datePicker.setVisible(false);
                }
            }
        });

        save.setOnAction(event -> {
            saveDate(event);
        });

        useSystemDate.setOnAction(event -> {
            dateLabel.setText("Today is: "+" "+LocalDate.now().toString());
            dateLabel.setTextFill(Color.valueOf("#3CCC13"));
        });

        close.setOnAction(event ->Utils.closeEvent(event));
    }

    public void saveDate(ActionEvent event) {
        try {
            if(datePicker.getValue() != null) {
                if(termDao.updateCurrentDate(datePicker.getValue())){
                    dateLabel.setText(termDao.getCurrentDate().toString());
                    Notification.getNotificationInstance().notifySuccess("Date has been updated successfully","Success");
                }
            }else {
                saveToday();
            }
        }catch (Exception e){
            notification.notifyError("An error occurred while setting the date.","Date Error");
        }
        save.setDisable(Boolean.FALSE);
    }

    private Boolean saveToday() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"", ButtonType.YES,ButtonType.NO);
        alert.setTitle("Change App Date");
        alert.setHeaderText("Use today's date for App");
        alert.setContentText("Are you sure you want to use System date for app settings?");
        Optional<ButtonType>result= alert.showAndWait();
        if(result.isPresent()  && result.get() == ButtonType.YES){
            if( termDao.updateCurrentDate(LocalDate.now())){
                notification.notifySuccess("Date settings have changed successfully"," success");
                return true;
            } else return false;
        } else if(result.isPresent() && result.get() == ButtonType.NO){
            return false;
        }
        return false;
    }

    private  Boolean saveCustomDate() {
        if(datePicker.getValue() != null) {
            if(termDao.updateCurrentDate(datePicker.getValue())){
                dateLabel.setText(termDao.getCurrentDate().toString());
                Notification.getNotificationInstance().notifySuccess("Date has been updated successfully","Success");
                return true;
            } else return false;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
            alert.setTitle("Select Date");
            alert.setContentText("You have not selected any date. \n Use the date picker to select a date");
            alert.show();
            return false;
        }
    }
    private void saveAttendanceRegister(ActionEvent event, LocalDate date) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                createNewAttendanceSheet(event, date);
                return null;
            }
        };
            task.setOnRunning(e-> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Creating a new Attendance Sheet..."));
            EventHandler eventHandler = e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
            task.setOnFailed(eventHandler);
            task.setOnSucceeded(eventHandler);
            new Thread(task).start();
    }
    public void createNewAttendanceSheet(ActionEvent event, LocalDate date) {
        AttendanceTemporaryDao dao = new AttendanceTemporaryDao();
        if(dao.canCreateAttendance()) {
            //move the previous attendance to master table
//            if(attendanceDao.moveAttendanceToMasterTable()) {
//                //create a new attendance for all the students and save them in attendance temp.
//                StudentDao studentDao = new StudentDao();
//                AttendanceTemporaryDao attendanceTemporaryDao = new AttendanceTemporaryDao();
//                List<Student> students = studentDao.getAllStudents();
//                for(Student s: students){
//                    attendanceTemporaryDao.checkStudenIn(s, date);
//                }
//                //show the attendance sheet
//                if (AppDao.getAppSetting().getFeedingType().equals(FeedingType.COUPON)) {
//                    System.out.println("We will show the coupon fee form");
//                    FeedingFormController couponFormController = new FeedingFormController();
//                    couponFormController.attendanceTemporaries.addAll(attendanceTemporaryDao.getTempAttendance());
////                        couponFormController.setAttendanceTemporaries((ObservableList<AttendanceTemporary>) attendanceTemporaryDao.getTempAttendance());
//                    couponFormController.populateTableview();
//
//                } else if (AppDao.getAppSetting().getFeedingType().equals(FeedingType.CASH)) {
//                    System.out.println("We found cash");
//                    main.attendanceTemporaries.addAll(main.attendanceTemporaryDao.getTempAttendance());
//                    main.populateStudentTable();
//                    main.attendanceRadio.setSelected(Boolean.TRUE);
//                }
//
//            } else {
//                notification.notifyError("An error occurred while preparing the records","Error!");
//            }
            Task task = new Task() {
                @Override
                protected Object call() {
                //move the previous attendance to master table
                if(attendanceDao.moveAttendanceToMasterTable()) {
                    //create a new attendance for all the students and save them in attendance temp.
                    StudentDao studentDao = new StudentDao();
                    AttendanceTemporaryDao attendanceTemporaryDao = new AttendanceTemporaryDao();
                    List<Student> students = studentDao.getAllStudents();
                    for(Student s: students){
                        attendanceTemporaryDao.checkStudenIn(s, date);
                    }
                    //show the attendance sheet
                    if (AppDao.getAppSetting().getFeedingType().equals(FeedingType.COUPON)) {
                        couponController.attendanceTemporaries.clear();
                        couponController.attendanceTemporaries.addAll(attendanceTemporaryDao.getTempAttendance());
                        couponController.populateTableview();

                    } else if (AppDao.getAppSetting().getFeedingType().equals(FeedingType.CASH)) {
                        main.attendanceTemporaries.addAll(main.attendanceTemporaryDao.getTempAttendance());
                        main.populateStudentTable();
                        main.attendanceRadio.setSelected(Boolean.TRUE);
                    }

                } else {
                    notification.notifyError("An error occurred while preparing the records","Error!");
                }
                return null;
                }
            };
            task.setOnRunning(e -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Preparing records..."));
            task.setOnSucceeded(e -> {
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
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Please set the date for today."  + "The date in the system is: " +termDao.getCurrentDate(),ButtonType.OK);
                    alert.setHeaderText("Wrong Date");
                    alert.setTitle("Error");
                    alert.show();
                }
            });
        }
    }

    private Boolean canCreateNewAttendance() {
        if(termDao.getCurrentDate().equals(LocalDate.now())) {
            System.out.println("We can create a date for the attendance");
            return true;
        } else {
            Period interval = Period.between(termDao.getCurrentDate(), LocalDate.now());
            int diff= interval.getDays();
            System.out.println("This is the difference between the days"+ diff);
            if(diff > 0 && diff < 6 && interval.getMonths() < 1 && interval.getYears() < 1) {
                System.out.println("The difference is less than 6");
            }
        }
        return false;
    }
}
