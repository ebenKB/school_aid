package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class viewAttendanceController implements Initializable{

    @FXML
    private AnchorPane attendanceForm;

    @FXML
    private JFXRadioButton todayAttendance;

    @FXML
    private ToggleGroup viewToggle;

    @FXML
    private JFXRadioButton allAttendance;

    @FXML
    private TextField stdAttendance;

    @FXML
    private TableView<Attendance> attendanceTableView;

    @FXML
    private TableColumn<Attendance, String> stdCol;

    @FXML
    private TableColumn<Attendance, String> presentCol;

    @FXML
    private TableColumn<Attendance, String> feedingCol;

    @FXML
    private TableColumn<Attendance, String> dateCol;

    @FXML
    private TableColumn<Attendance, String> stageCol;

    @FXML
    private ListView<Student> stdListView;

    @FXML
    private TextField total;

    @FXML
    private JFXRadioButton classAttendance;

    @FXML
    private JFXComboBox<Stage> classCombo;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            viewToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                    if(newValue==allAttendance){
                        hideClassDetails();
                        hideListView();
                        AttendanceDao dao = new AttendanceDao();
                        Task getdata =new Task() {
                            @Override
                            protected Object call() {
                                List <Attendance> data =dao.getAllAttendance();
                                populateTableView(data);
                                return null;
                            }
                        };
                        getdata.setOnRunning(event -> {
                            MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Fetching attendance data. Please wait...");
                        });
                        getdata.setOnSucceeded(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                        getdata.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                        new Thread(getdata).start();
                    }else if(newValue ==todayAttendance) {
                        hideClassDetails();
                        hideListView();
                        AttendanceDao dao = new AttendanceDao();
                        Task getdata =new Task() {
                            @Override
                            protected Object call() {
                                List <Attendance> data =dao.getAttendanceByDate(LocalDate.now());
                                populateTableView(data);
                                return null;
                            }
                        };
                        getdata.setOnRunning(event -> {
                            MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Fetching attendance data. Please wait...");
                        });
                        getdata.setOnSucceeded(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                        getdata.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                        new Thread(getdata).start();
                    }else if(newValue ==classAttendance){
                        if(classCombo.getItems().isEmpty()){
                            StageDao stageDao =new StageDao();
                            CustomCombo.getInstance().overrideCombo(classCombo);
                            classCombo.getItems().addAll(stageDao.getGetAllStage());
                            classCombo.setVisible(Boolean.TRUE);
                        }else showClassDetails();
                    }
                }
            });

            classCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stage>() {
                @Override
                public void changed(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
                    if(newValue !=null){
                        StudentDao studentDao = new StudentDao();
                        stdListView.getItems().addAll(studentDao.getStudentFromClass(newValue));
                    }
                }
            });

            stdAttendance.setOnKeyReleased(event -> {
                if(stdAttendance.getText().trim().length()>0){
                    StudentDao studentDao =new StudentDao();
                    List<Student> data = studentDao.getStudentByName(stdAttendance.getText().trim());
                    stdListView.getItems().clear();
                    stdListView.getItems().addAll(data);
                    showListView();
                }else{
                    hideListView();
                }
            });

            stdListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
                @Override
                public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
                    if(newValue!=null){
                        AttendanceDao attendanceDao =new AttendanceDao();
                        List<Attendance>data = attendanceDao.getStudentAttendance(newValue.getId().intValue());
                        populateTableView(data);
                    }
                }
            });
        }

    public void init(List<Attendance> attendances) {
            populateTableView(attendances);
    }

    private void populateTableView(List<Attendance>data){
            attendanceTableView.getItems().clear();
        stdCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Attendance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Attendance, String> param) {
                return new SimpleStringProperty(param.getValue().getStudent().toString());
            }
        });

        presentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Attendance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Attendance, String> param) {
                return new SimpleStringProperty("YES");
            }
        });

        feedingCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Attendance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Attendance, String> param) {
                return new SimpleStringProperty(String.valueOf(param.getValue().getFeedingFee()));
            }
        });

        dateCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Attendance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Attendance, String> param) {

                return new SimpleStringProperty(String.valueOf(param.getValue().getDate().toString()));
            }
        });

        stageCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Attendance, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Attendance, String> param) {
                return new SimpleStringProperty(param.getValue().getStudent().getStage().getName());
            }
        });
        attendanceTableView.getItems().addAll(data);
        total.setText(String.valueOf(data.size()));
    }
private void hideClassDetails(){
     classCombo.setVisible(Boolean.FALSE);
}

private void showClassDetails(){
     classCombo.setVisible(Boolean.TRUE);
}

private void hideListView(){
     stdListView.setVisible(Boolean.FALSE);
     stdAttendance.clear();
}

private void showListView(){
     stdListView.setVisible(Boolean.TRUE);
}
}


