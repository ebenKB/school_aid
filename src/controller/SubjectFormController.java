package controller;

import com.hub.schoolAid.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.controlsfx.control.CheckListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SubjectFormController implements Initializable{
    @FXML
    private AnchorPane root;

    @FXML
    private ListView<Course> subjectListView;

    @FXML
    private CheckListView<Stage> classChckListView ;

    @FXML
    private TextField subjectName;

    @FXML
    private Button clear;

    @FXML
    private Button add;

    @FXML
    private Button save;

    @FXML
    private Button remove;

    private ObservableList<Stage> stages = FXCollections.observableArrayList();
    private ObservableList<Course>courses =FXCollections.observableArrayList();
    private final SimpleListProperty listProperty  = new SimpleListProperty(courses);

    private List<Stage> getClassData(){
        stages.clear();
        StageDao stageDao =new StageDao();
        return stageDao.getGetAllStage();
    }
    private void poupulateClassListView(){
        classChckListView.getItems().clear();
        stages.addAll(getClassData());
        for(Stage stage:stages){
            classChckListView.getItems().add(stage);
        }
    }

    private void moveSubject(){
        if(subjectName.getText().trim().length()>1){
            Course course =new Course();
            course.setName(subjectName.getText().trim().toUpperCase());
            courses.add(course);
            clearSubjectField();
        }
    }

    private void clearSubjectField(){
        subjectName.clear();
    }
    private void initSubjectListView(){
        subjectListView.itemsProperty().bind(listProperty);
        courses.addAll(courses);
    }

    private Boolean isChecked(){
        if(classChckListView.getCheckModel().isEmpty())
            return false;
        return true;
    }

    private  Boolean createCourses(){
        List <Stage>stages=classChckListView.getCheckModel().getCheckedItems();
        if(isChecked() && !courses.isEmpty()){
            CourseDao courseDao =new CourseDao();
            courseDao.createNewCourse(stages,courses);
            return true;
        }else return false;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSubjectListView();

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                poupulateClassListView();
                return null;
            }
        };
        task.setOnRunning(event -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Fetching data...."));
        task.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        task.setOnSucceeded(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(task).start();

        add.setOnAction(event -> moveSubject());

        subjectName.setOnKeyReleased(event -> {
            if(event.getCode() ==KeyCode.ENTER)
                moveSubject();
        });

        clear.setOnAction(e->{clearSubjectField();});

        remove.setOnAction(e->{
            courses.remove(subjectListView.getSelectionModel().getSelectedItem());
            if(courses.isEmpty())
                remove.setVisible(Boolean.FALSE);
        });

        save.setOnAction(event -> {
            Task save = new Task() {
                @Override
                protected Object call() throws Exception {
                    if(createCourses())
                         return true;
                    return false;
                }
            };

            new Thread(save).start();
            save.setOnSucceeded(e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            save.setOnFailed(e-> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            save.setOnRunning(e->MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Creating Subjects..."));
        });

        subjectListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Course>() {
            @Override
            public void changed(ObservableValue<? extends Course> observable, Course oldValue, Course newValue) {
                if(newValue !=null)
                    remove.setVisible(Boolean.TRUE);
            }
        });

    }
}
