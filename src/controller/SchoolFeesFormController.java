package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.rmi.CORBA.Util;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class SchoolFeesFormController implements  Initializable {

    @FXML
    private AnchorPane darkPane;

    @FXML
    private Pane separator;

    @FXML
    private AnchorPane root;

    @FXML
    private HBox actionsHBox;

    @FXML
    private Button printStatement;

    @FXML
    private Button printReport;

    @FXML
    private TextField searchBox;

    @FXML
    private TableView<Student> studentTableview;

    @FXML
    private TableColumn<Student, String> studentCol;

    @FXML
    private TableColumn<Student, String> classCol;

    @FXML
    private TableColumn<Student, String> balanceCol;

    @FXML
    private TextField totalRecords;

    @FXML
    private JFXButton close;

    @FXML
    private ImageView studentImageview;

    @FXML
    private VBox detailsVBox;

    @FXML
    private Label imageLabel;

    @FXML
    private Hyperlink viewPaymentDetails;

    @FXML
    private JFXCheckBox filter;

    @FXML
    private JFXComboBox<Stage> classCombo;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private TableColumn<Student, String> checkCol;

    @FXML
    private Label totalSelected;


    private ObservableList<Student>students = FXCollections.observableArrayList();
    private ObservableList<Stage>stages= FXCollections.observableArrayList();
    private StudentDao studentDao;
    private CheckBox selectAll = new CheckBox();

    FilteredList<Student> filteredAtt = new FilteredList<>(students, e ->true);
    SortedList<Student> sortedList = new SortedList<>(filteredAtt);
    private ObservableList<Student>selectedStudents = FXCollections.observableArrayList();

    public ObservableList<Student> getStudents() {
        return students;
    }

    public void setStudents(ObservableList<Student> students) {
        this.students = students;
    }

    Label h = new Label("___________________________\n\nTRANSACTION DETAILS\n___________________________\n\n\n");
    Label c = new Label();
    Label ad = new Label();
    Label ap = new Label();
    Label o = new Label();


    void initialize(List<Student>students) {
        if (students != null &&  !students.isEmpty()) {
            this.students.addAll(students);
        } else {
            fetchRecord();
        }
        populateTableview();
    }

    private void fetchRecord() {
        // load records from the database
        studentDao = new StudentDao();
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                students.addAll(studentDao.getAllStudents());
                populateTableview();
                return null;
            }
        };
        task.setOnRunning(e -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Loading records"));
        task.setOnSucceeded(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        task.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(task).start();
    }
    private void populateTableview() {
        studentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new SimpleStringProperty(param.getValue().toString());
            }
        });

        classCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                return new SimpleStringProperty(param.getValue().getStage().toString());
            }
        });

        balanceCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                Student student = param.getValue();
                return new SimpleStringProperty(String.valueOf((student.getAccount().getSchFeesPaid() - student.getAccount().getSchFeesPaid())));
            }
        });

        // add checkbox to all the rows
        Utils.addCheckBoxToTable(checkCol, studentTableview, selectedStudents);

        // check if the data is not empty
        studentTableview.setItems(this.students);
        totalRecords.setText(String.valueOf(studentTableview.getItems().size()));
        detailsVBox.getChildren().addAll(h,c,ad,ap,o);
    }

    private void setDetails(Student student) {
        // clear all the labels before setting the items
        String cur = "GHC";
        String amntDue;
        if (student.getAccount().getFeeToPay() < 0) {
            amntDue = "Total School Fees : "+String.valueOf((student.getAccount().getFeeToPay() * -1 ))+"\n\n";
        } else {
            amntDue = "Total School Fees : "+String.valueOf((student.getAccount().getFeeToPay()))+"\n\n";
        }

        String owing = String.valueOf("Amount owing : "+ (student.getAccount().getFeeToPay() + student.getAccount().getSchFeesPaid()))+"\n\n";

        // set the labels
        h.getStyleClass().add("heading-label");
        c.setText(cur);
        c.getStyleClass().add("white-label");
        ad.setText(amntDue);
        ad.getStyleClass().add("white-label");
        ap.setText("Amount paid : "+ String.valueOf(student.getAccount().getSchFeesPaid())+"\n\n");
        ap.getStyleClass().add("white-label");
        o.setText(owing);
        o.getStyleClass().add("owing-label");
        detailsVBox.getStyleClass().add("details-wrapper");
    }

    private void enableButtons() {
        printReport.setDisable(false);
        printStatement.setDisable(false);
    }

    private void disableFields() {
        printStatement.setDisable(true);
        printReport.setDisable(true);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkCol.setGraphic(selectAll);
//        studentTableview.getItems().addListener(new ListChangeListener<Student>() {
//            @Override
//            public void onChanged(Change<? extends Student> c) {
//                totalRecords.setText(String.valueOf(studentTableview.getItems().size()));
//            }
//        });

        // listen when a new table row is selected
        studentTableview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
                if(newValue != null && newValue != oldValue) {
                    viewPaymentDetails.setVisible(true);

                    if(newValue.getPicture() != null) {
                        imageLabel.setVisible(false);
                        ImageHandler.setImage(newValue.getPicture(), studentImageview);

                        // show the details of the student
                        setDetails(newValue);
                    } else {
                        imageLabel.setVisible(true);
                        studentImageview.setImage(null);
                    }
                }
            }
        });

        // listen when the filter check box is selected
        filter.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    classCombo.setVisible(true);
                    if(stages.isEmpty()) {
                        // populate the stage
                        Task task = new Task() {
                            @Override
                            protected Object call() throws Exception {
                                getStage();
                                return null;
                            }
                        };
                        task.setOnRunning(event -> progress.setVisible(true));
                        task.setOnSucceeded(event -> progress.setVisible(false));
                        task.setOnFailed(event -> progress.setVisible(false));
                        new Thread(task).start();
                    }
                } else {
                    classCombo.setVisible(false);
                    classCombo.getSelectionModel().clearSelection();
                }
            }
        });

        // listen when a new class is selected
        classCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stage>() {
            @Override
            public void changed(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
                if(newValue != null) {
                    Utils.filterStudent(filteredAtt, sortedList, studentTableview, newValue.getName());

                } else {
                    Utils.filterStudent(filteredAtt, sortedList, studentTableview, null);
                }
                Utils.unSelectAll(studentTableview, students, selectedStudents, selectAll);
            }
        });

        // listen to key changes in the search box
        searchBox.setOnKeyReleased(event -> {
            Utils.searchStudentByName(searchBox, filteredAtt, sortedList, studentTableview);
            Utils.unSelectAll(studentTableview, students, selectedStudents, selectAll);
        });

        // show the school fees transaction details for a selected student
        viewPaymentDetails.setOnAction(event -> {
            Student student = studentTableview.getSelectionModel().getSelectedItem();
            if(student != null) {
                Utils.showSummaryForm(student);
            }
        });

        selectAll.setOnAction(event -> {
            if(selectAll.isSelected()) {
                Utils.selectAll(studentTableview,students,selectedStudents);
            } else {
                Utils.unSelectAll(studentTableview, students, selectedStudents, selectAll);
            }
        });

        // listen when new items are added to the list
        selectedStudents.addListener(new ListChangeListener<Student>() {
            @Override
            public void onChanged(Change<? extends Student> c) {
                totalSelected.setText(String.valueOf(selectedStudents.size()));

                // check if there are any items selected
                if(selectedStudents.size() > 0) {
                    enableButtons();
                } else disableFields();
            }
        });

        sortedList.addListener(new ListChangeListener<Student>() {
            @Override
            public void onChanged(Change<? extends Student> c) {
                if(!c.getList().isEmpty()) {
                    totalRecords.setText(String.valueOf(c.getList().size()));
                } else {
                    totalRecords.setText(String.valueOf(students.size()));
                }
            }
        });

        studentTableview.setOnSort(event -> {
            totalRecords.setText(String.valueOf(studentTableview.getItems().size()));
        });

        // select the student when the user double clicks on the row
        studentTableview.setRowFactory(tv -> {
            TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty()) {
                    Student st = row.getItem();
                    st.setSelected(!st.getSelected());
                    if (st.getSelected()) {
                        selectedStudents.add(st);
                    } else {
                        selectedStudents.remove(st);
                    }
                    studentTableview.refresh();
                }
            });
            return row;
        });

        printReport.setOnAction(event -> {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    PDFMaker pdfMaker = PDFMaker.getPDFMakerInstance();
                    PDDocument doc = pdfMaker.generateSchoolFeesReport(selectedStudents);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            PDFMaker.savePDFToLocation(doc);
                        }
                    });
                    return null;
                }
            };
            task.setOnRunning(e -> {
                MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Creating report. Please wait...");
            });
            task.setOnSucceeded(e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            task.setOnFailed(e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            new Thread(task).start();
        });

        printStatement.setOnAction(event -> {
            try {
//                getStage();
                PDFMaker pdfMaker =PDFMaker.getPDFMakerInstance();
                PDDocument doc = pdfMaker.generateSchoolFeesReport(selectedStudents);
                PDFMaker.savePDFToLocation(doc);
            } catch (Exception e) {
                Notification.getNotificationInstance().notifyError("An error occurred while generating the report", "Error");
            }
        });
    }

    private void getStage() {
        if(stages.isEmpty()) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    StageDao stageDao = new StageDao();
                    stages.addAll(stageDao.getGetAllStage());
                    classCombo.setItems(stages);
                    return stages;
                }
            };
            task.setOnRunning(event -> progress.setVisible(true));
            task.setOnSucceeded(event -> progress.setVisible(false));
            task.setOnFailed(event -> progress.setVisible(false));
            new Thread(task).start();
        }
    }
}


