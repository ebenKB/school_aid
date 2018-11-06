package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AssessmentFormController implements Initializable{
    @FXML
    private Pane root;

    @FXML
    private JFXComboBox<Course> subjectCombo;

    @FXML
    private JFXComboBox<Stage> classCombo;

    @FXML
    private TextField searchStd;

    @FXML
    private ProgressIndicator progress;


    @FXML
    private CheckBox selectAll;

    @FXML
    private TableView<Assessment> assmntTableView;

    @FXML
    private TableColumn<Assessment, String> nameCol;

    @FXML
    private TableColumn<Assessment, String> classScore;

    @FXML
    private TableColumn<Assessment, String> examScore;

    @FXML
    private TableColumn<Assessment, String> totalScore;

    @FXML
    private TableColumn<Assessment, String> gradeCol;

    @FXML
    private TableColumn<Assessment, String> remarkCol;

    @FXML
    private Label subjectLabel;

    @FXML
    private TextField totalStudents;

    @FXML
    private JFXListView<Student> stdListView;

    @FXML
    private ProgressBar assessmentIndicator;

    @FXML
    private HBox assmntLabel;

    @FXML
    private Button close;

    @FXML
    private Button saveRecord;

    @FXML
    private HBox changesLabel;

    @FXML
    private Label changesCounter;

    private  AssessmentDao assessmentDao = new AssessmentDao();
    private ObservableList<Assessment> assessments = FXCollections.observableArrayList();
    private ObservableList<Student>students = FXCollections.observableArrayList();
    private final SimpleListProperty listProperty  = new SimpleListProperty(students);
    private ObservableList<Assessment>editedAssessment = FXCollections.observableArrayList();
    private ObservableList<Assessment>savedAssessment = FXCollections.observableArrayList();
    private Boolean hasInit=false;


    private Boolean canSearch(){
        if(classCombo.getSelectionModel().isEmpty() || subjectCombo.getSelectionModel().isEmpty())
            return false;
        return true;
    }

    private Boolean prepareAssessment(){
        StudentDao studentDao = new StudentDao();
        List<Student> studentList =studentDao.getStudentFromClass(classCombo.getSelectionModel().getSelectedItem());
       if(studentList.size()<1)
           return false;

        Assessment assessment ;
        assessments.clear();
        ObservableList<Assessment> newAssessments =FXCollections.observableArrayList();

        //check for all students whether they have assessment for the selected course.
        for (Student student : studentList){
            assessment = assessmentDao.existAssessment(student, subjectCombo.getSelectionModel().getSelectedItem());

            //if the student does not have assessment for the selected course,create the assessment for the course.
            if(assessment==null){
                assessment=new Assessment();
                assessment.setStudent(student);
                assessment.setCourse(subjectCombo.getSelectionModel().getSelectedItem());
                assessment.setClassScore(0.0);
                assessment.setExamScore(0.0);
                newAssessments.add(assessment);
            }
            assessments.add(assessment);
        }
        assessmentDao.createAssessment(newAssessments);
        return true;
    }

    private void initFilds(){
        StageDao stageDao =new StageDao();
        CourseDao courseDao =new CourseDao();
        classCombo.getItems().addAll(stageDao.getGetAllStage());
        subjectCombo.getItems().addAll(courseDao.getAllCourses());
        stdListView.itemsProperty().bind(listProperty);
    }

    private void initAssessmentTable(){
//        assmntTableView.getItems().clear();
        nameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().toString()));

        classScore.setCellFactory(TextFieldTableCell.forTableColumn());
        classScore.setOnEditCommit(event -> {
            if(event.getNewValue().matches("[0-9]+\\.*") && Double.valueOf(event.getNewValue())>=0){
                Assessment newAssess = event.getTableView().getItems().get(event.getTablePosition().getRow());
                newAssess.setClassScore(Double.valueOf(event.getNewValue()));
                setTableViewColumns();

                if(!editedAssessment.contains(newAssess)){
                    if(newAssess.getClassScore()!=Double.valueOf(event.getNewValue())){
                        if((newAssess.getClassScore()+newAssess.getExamScore() <=0) ) {
                            editedAssessment.add(newAssess);
                        }
                    }
                }
            }
        });
        classScore.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getClassScore().toString()));
        examScore.setCellFactory(TextFieldTableCell.forTableColumn());
        examScore.setOnEditCommit(event -> {
            if(event.getNewValue().matches("[0-9]+\\.*") && Double.valueOf(event.getNewValue())>=0){
                Assessment newAssess = event.getTableView().getItems().get(event.getTablePosition().getRow());
                newAssess.setExamScore(Double.valueOf(event.getNewValue()));
                setTableViewColumns();

                if(!editedAssessment.contains(newAssess)){
                    if(newAssess.getExamScore()!=Double.valueOf(event.getNewValue())){
                        editedAssessment.add(newAssess);
                    }
                }
            }
        });

        examScore.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getExamScore().toString()));

        totalScore.setCellValueFactory(param -> {
            Double total = param.getValue().getClassScore() + param.getValue().getExamScore();
            return new SimpleStringProperty(total.toString());
        });


        gradeCol.setCellValueFactory(param -> {
            try {
                return new SimpleStringProperty(String.valueOf(param.getValue().getGrade().getName()));
            }catch (Exception e){
                return new SimpleStringProperty("N/A");
            }
        });

        remarkCol.setCellValueFactory(param -> {
            try {
                return new SimpleStringProperty(param.getValue().getGrade().getRemark());
            }catch (NullPointerException e){
                return new SimpleStringProperty("N/A");
            }
        });
        assmntTableView.setItems(assessments);
        assmntTableView.setVisible(Boolean.TRUE);
        assmntLabel.setVisible(Boolean.TRUE);
        totalStudents.setText(String.valueOf(assessments.size()));

    }
    private void setTableViewColumns(){
        initAssessmentTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Task init = new Task() {
            @Override
            protected Object call() {
                initFilds();
                return null;
            }
        };
        init.setOnRunning(e-> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Initializing..."));
        init.setOnFailed(e->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        init.setOnSucceeded(e->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(init).start();

        classCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->  renderAssessmentData());

        subjectCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            renderAssessmentData();
            subjectLabel.textProperty().bind(new SimpleStringProperty(subjectCombo.getSelectionModel().getSelectedItem().getName()));
        });

        searchStd.setOnKeyReleased((KeyEvent event) -> {
            if(searchStd.getText().trim().length()>0){
                Task getStd=new Task() {
                    @Override
                    protected Object call(){
                        StudentDao studentDao=new StudentDao();
                        List<Student>studentList =studentDao.getStudentByName(searchStd.getText().trim());
                        Platform.runLater(()->{
                            students.clear();
                            students.addAll(studentList);
                        });
                        return null;
                    }
                };
                getStd.setOnRunning(e-> Platform.runLater(()->progress.setVisible(Boolean.TRUE)));
                getStd.setOnSucceeded(e->Platform.runLater(()->progress.setVisible(Boolean.FALSE)));
                getStd.setOnFailed(e->Platform.runLater(()->progress.setVisible(Boolean.FALSE)));
                new Thread(getStd).start();
            }else
                stdListView.getItems().clear();
        });

        stdListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Task getData =new Task() {
                @Override
                protected Object call(){
                    if(newValue!=null){
                        assessments.clear();
                        assessments.addAll(assessmentDao.getAssessment(newValue));
                        setTableViewColumns();
                    }
                    return null;
                }
            };
            getData.setOnRunning(e->{
                assessmentIndicator.progressProperty().bind(getData.progressProperty());
                assessmentIndicator.setVisible(Boolean.TRUE);
            });
            getData.setOnSucceeded(e->{
                assessmentIndicator.setVisible(false);
//                    Platform.runLater(()->assessmentIndicator.setVisible(false));
                getData.cancel();
            });
            getData.setOnFailed(e->{
//                  Platform.runLater(()->assessmentIndicator.setVisible(false));
                assessmentIndicator.setVisible(false);
                getData.cancel();
            });
            new Thread(getData).start();
        });

        saveRecord.setOnAction(event->{
               Task saving =new Task() {
                   @Override
                   protected Object call() throws Exception {
                      saveAssessment();
                       return null;
                   }
               };
               new Thread(saving).start();
               saving.setOnRunning(e->MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Saving Assessment..."));
               saving.setOnSucceeded(e->{
                   MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();

                   Task checking=new Task() {
                       @Override
                       protected Object call() throws Exception {
                           //check if all items were saved...
                           editedAssessment.removeAll(savedAssessment);
                           return null;
                       }
                   };
                   new Thread(checking).start();
                   checking.setOnRunning(e2->MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Finalizing..."));
                   checking.setOnSucceeded(e2->{
                       MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                       Notification.getNotificationInstance().notifySuccess("We have saved your records","Success");
                       hideChangeLable();
                   });
                   checking.setOnFailed(e2->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
               });
               saving.setOnFailed(e2->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        });

        //Bind text properties to observable lists
        //totalStudents.textProperty().bind(new SimpleStringProperty(String.valueOf(students.size())));
//        totalStudents.textProperty().bind(new SimpleStringProperty(String.valueOf(assessments.size())));

//        changesCounter.textProperty().bind(new SimpleStringProperty(String.valueOf(editedAssessment.size())));


        editedAssessment.addListener((ListChangeListener<Assessment>) c -> {
            if(!c.getList().isEmpty())
                showChangeLabel();
        });

        close.setOnAction(e->PDFMaker.getPDFMakerInstance().createReportForAllStudents());
    }

    private void saveAssessment() {
        AssessmentDao assessmentDao =new AssessmentDao();
        savedAssessment.addAll(assessmentDao.updateAssessment(editedAssessment));
//        for (Assessment assessment:editedAssessment){
//            if((assessment.getClassScore()+assessment.getExamScore())<=100){
//                if(assessmentDao.updateAssessment(assessment))
//                    savedAssessment.add(assessment);
//            }else{
//                //show error cannot save assessment with score more than 100
//                Notification.getNotificationInstance().notifyError((assessment.getClassScore()+assessment.getExamScore())+"is greater than 100","Invalid marks");
//            }
//        }
    }

    private void renderAssessmentData() {
        if(canSearch() && !hasInit){
            if(prepareAssessment()){
                setTableViewColumns();
                hasInit=true;
            }else Notification.getNotificationInstance().notifyError("Sorry something went wrong while processing the request.\nPlease try again","Error!");
        }
    }

    private void showChangeLabel(){
        changesCounter.setText(String.valueOf(editedAssessment.size()));
        changesLabel.setVisible(true);
    }

    private void hideChangeLable(){
        changesCounter.setText("");
        changesLabel.setVisible(false);
    }

    public void closeStage(){

    }
}
