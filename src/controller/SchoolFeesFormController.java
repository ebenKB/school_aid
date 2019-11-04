package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

//    private List<Student>students = new ArrayList<>();
    private ObservableList<Student>students = FXCollections.observableArrayList();
    private StudentDao studentDao;

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
            System.out.println("We want to fetch records here..");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        studentTableview.getItems().addListener(new ListChangeListener<Student>() {
            @Override
            public void onChanged(Change<? extends Student> c) {
                totalRecords.setText(String.valueOf(studentTableview.getItems().size()));
            }
        });

        studentTableview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
                if(newValue != null && newValue != oldValue) {
                    if(newValue.getPicture() != null) {
                        imageLabel.setVisible(false);
//                        studentImageview.setVisible(true);
                        ImageHandler.setImage(newValue.getPicture(), studentImageview);

                        // show the details of the student
                        setDetails(newValue);
                    } else {
                        imageLabel.setVisible(true);
//                        studentImageview.setVisible(false);
                        studentImageview.setImage(null);
                    }
                }
            }
        });
    }
}


