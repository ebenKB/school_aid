package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.util.Pair;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class viewAttendanceController implements Initializable{

    @FXML
    private AnchorPane attendanceForm;

    @FXML
    private AnchorPane leftStack;

    @FXML
    private HBox detailsHbox;

    @FXML
    private JFXButton btnDetails;

    @FXML
    private JFXRadioButton yesterdayAttendance;

    @FXML
    private  JFXRadioButton dateAttendance;

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
    private Label total;

    @FXML
    private Label feedingTotal;

    @FXML
    private FlowPane statsPane;

    @FXML
    private JFXRadioButton classAttendance;

    @FXML
    private JFXComboBox<Stage> classCombo;

    @FXML
    private JFXDatePicker datePicker;

    @FXML
    private JFXButton generatePDF;

    private Double totalFeedingPaid =0.0;
    ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();
    FilteredList<Attendance> filteredList = new FilteredList<>(attendanceList, e -> true);
    SortedList <Attendance> sortedList = new SortedList<>(filteredList);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stdAttendance.setOnKeyReleased(e -> filterAttendanceByText());
        generatePDF.setOnAction(e -> {
            Optional<Pair<LocalDate,LocalDate>> result = showPDFDialog();
           result.ifPresent(pair -> {
                if(pair.getKey() != null){
                    PDFMaker.getPDFMakerInstance().createAttendanceReport(pair.getKey(),attendanceList);
                }else {
                    PDFMaker.createAttendanceReport(pair.getKey(),pair.getValue(),attendanceList);
                }
           });
        });

        //filter records here
        viewToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
        if(newValue == dateAttendance) {
            //set default
            datePicker.setValue(LocalDate.of(LocalDate.now().getYear(),LocalDate.now().getMonth(),(LocalDate.now().getDayOfMonth()-1)));
            datePicker.setOnAction(e ->{
                if(datePicker.getValue() != null){
                    filteredList.setPredicate( (Predicate < ? super  Attendance >) at -> {//
                        if(at.getDate().toString().equals(datePicker.getValue().toString())){//
                            return  true;
                        }
                        return  false;
                    });
                }
            });
            bindTableRecord();
            }else if(newValue == allAttendance) {
                filteredList.setPredicate((Predicate<? super Attendance>) at -> true);
                bindTableRecord();
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

//            stdAttendance.setOnKeyReleased(event -> {
//                if(stdAttendance.getText().trim().length()>0){
//                    StudentDao studentDao =new StudentDao();
//                    List<Student> data = studentDao.getStudentByName(stdAttendance.getText().trim());
//                    stdListView.getItems().clear();
//                    stdListView.getItems().addAll(data);
//                    showListView();
//                }else{
//                    hideListView();
//                }
//                hideStatsPane();
//            });

        stdAttendance.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
               if(oldValue && ! (stdListView.isVisible())){
                   showStatsPane();
               }
            }
        });

//            stdListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
//                @Override
//                public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
//                    if(newValue!=null){
//                        AttendanceDao attendanceDao =new AttendanceDao();
//                        List<Attendance>data = attendanceDao.getStudentAttendance(newValue.getId().intValue());
//                        populateTableView();
//                    }
//                }
//            });
        }

    private void bindTableRecord() {
        sortedList.comparatorProperty().bind(attendanceTableView.comparatorProperty());
        attendanceTableView.setItems(sortedList);
        total.textProperty().bind(Bindings.size(attendanceTableView.getItems()).asString());
    }

    private void filterAttendanceByText() {
        stdAttendance.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate( (Predicate < ? super  Attendance > ) at ->{
                if( newValue == null || newValue.isEmpty() ) {
                    return  true;
                }

                String lowerVal = newValue.toLowerCase();
                return  checkIfStudent(lowerVal, at.getStudent());
            });
        }));

        sortedList.comparatorProperty().bind(attendanceTableView.comparatorProperty());
        attendanceTableView.setItems(sortedList);
    }

    //global search to find a match from table view for students
    static boolean checkIfStudent(String lowerVal, Student student) {
        if(student.getFirstname().toLowerCase().contains(lowerVal)){
            return true;
        }else if(student.getLastname().toLowerCase().contains(lowerVal)){
            return true;
        }else if(student.getOthername().toLowerCase().contains(lowerVal)){
            return true;
        }else if (student.toString().trim().replace(" ","").toLowerCase().contains(lowerVal.trim().replace(" ",""))){
            return true;
        }else if (student.getStage().getName().trim().replace(" ","").toLowerCase().contains(lowerVal.trim().replace(" ",""))){
            return true;
        }
        return false;
    }

    public void init() {
        Task load = new Task() {
            @Override
            protected Object call() throws Exception {
                loadData();
                return null;
            }
        };
        load.setOnRunning(e-> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress(" Loading Records"));
        load.setOnSucceeded(e -> {
            MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
            populateTableView();
        });

        load.setOnFailed(e -> {
            MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
            Notification.getNotificationInstance().notifyError("An error occurred while loading records ...","Error");
        });
        new Thread(load).start();
    }

    private void populateTableView(){

        if(attendanceList !=null){
//            attendanceTableView.getItems().clear();
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
//            attendanceTableView.getItems().addAll(attendanceList);
            attendanceTableView.setItems(attendanceList);
            total.textProperty().bind(Bindings.size(attendanceTableView.getItems()).asString());

            for(Attendance at:attendanceList){
               if(at.getPaidNow()){
                   totalFeedingPaid+=at.getFeedingFee();
               }
            }

            feedingTotal.setText(totalFeedingPaid.toString());
//            total.setText(String.valueOf(attendanceList.size()));
            hideStdList();
        }

        leftStack.setOnMouseClicked(e->{
            hideStdList();
            showStatsPane();
        });

        btnDetails.setOnAction(e->{
            if(detailsHbox.isVisible()){
                detailsHbox.setVisible(false);
            }else{
                detailsHbox.setVisible(true);
            }
        });

    }

    private void loadData(){
        AttendanceDao attendanceDao = new AttendanceDao();
         attendanceList.addAll( attendanceDao.getAllAttendance());
//         populateTableView();
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

    private void toggleStats(){
        if(statsPane.isVisible()){
            statsPane.setVisible(false);
        }else{
            statsPane.setVisible(true);
        }
    }

    private void hideStatsPane(){
       statsPane.setVisible(false);
    }

    private void hideStdList(){
       stdListView.setVisible(false);
    }
    private  void  showStatsPane(){
        statsPane.setVisible(true);
    }

    private Optional<Pair<LocalDate, LocalDate>> showPDFDialog () {
        Dialog<Pair<LocalDate,LocalDate>> dialog  = new Dialog<>();
        dialog.setTitle("GENERATE PDF");

        //add custom button
        ButtonType accept = new ButtonType("Continue");
        dialog.getDialogPane().getButtonTypes().addAll(accept,ButtonType.CANCEL);


        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20,15,10,10));
        gridPane.add(new Label("GENERATE ATTENDANCE REPORT"),0,0);

        RadioButton date = new RadioButton("BY DATE");
        date.setSelected(Boolean.TRUE);
        RadioButton period = new RadioButton("PERIOD");
        ToggleGroup toggleGroup = new ToggleGroup();
        date.setToggleGroup(toggleGroup);
        period.setToggleGroup(toggleGroup);

//        HBox box =new HBox();
//        box.getChildren().addAll(date,period);
//        box.setSpacing(20);
        gridPane.add(date,0,1);
        gridPane.add(period,1,1);

        DatePicker from =new DatePicker(LocalDate.of(LocalDate.now().getYear(),LocalDate.now().getMonth(),LocalDate.now().getDayOfMonth()-1));
        DatePicker to  =new DatePicker();

        VBox vBox =new VBox();
        vBox.getChildren().addAll(new Label("Start Date"),from);
        vBox.setSpacing(10);
        gridPane.add(vBox,0,2);


        VBox vBox2 =new VBox();
        vBox2.getChildren().addAll(new Label("End Date"),to);
        vBox2.setSpacing(10);
        vBox2.setVisible(Boolean.FALSE);
        gridPane.add(vBox2,1,2);

        //add toggle listener
        toggleGroup.selectedToggleProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue  == period) {
                vBox2.setVisible(Boolean.TRUE);
            }else vBox2.setVisible(Boolean.FALSE);
        }));

        dialog.getDialogPane().setContent(gridPane);

        //parse the result
        dialog.setResultConverter(dialogbtn -> {
            if(dialogbtn == accept) {
                return  new Pair<>(from.getValue(),to.getValue());
            }
            return null;
        });

       return  dialog.showAndWait();
    }
    }




