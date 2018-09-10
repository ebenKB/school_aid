package controller;



import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.hibernate.HibernateException;

import javax.persistence.NoResultException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class mainController implements Initializable{
    @FXML
    private AnchorPane parent;

    @FXML
    private BorderPane borderPane;

    @FXML
    private VBox vbox;

    @FXML
    private FontAwesomeIconView menuToggle;

    @FXML
    private FontAwesomeIconView eye;

    @FXML
    private AnchorPane buttonCotainer;

    @FXML
    private Pane imagePane;

    @FXML
    private AnchorPane menuButtonsPane;

    @FXML
    private AnchorPane tablePane;

    @FXML
    private HBox buttonHBox;

    @FXML
    private Button btnAddNewStudent;

    @FXML
    private Button btnAddNewSale;

    @FXML
    private Button btnPayment;

    @FXML
    private Button btnAttendance1;

    @FXML
    private MenuButton studentMenuButton;

    @FXML
    private MenuBar menubar;

    @FXML
    private Menu fileMenu;

    @FXML
    private MenuItem fileMenuItem;

    @FXML
    private Menu menu;

    @FXML
    private MenuItem refresh;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private TableView<Student> studentTableView;

    @FXML
    private TableColumn<Student, String> nameCol;

    @FXML
    private TableColumn<Student, String> classCol;

    @FXML
    private TableColumn<Student, String> genderCol;

    @FXML
    private TableColumn<Student, String> ageCol;

    @FXML
    private TableColumn<Student, String> payFeeding;

    @FXML
    private TableColumn<Student, String> parentCol;

    @FXML
    private  TableColumn<Student,String> idCol;

    @FXML
    private HBox tableInfo;

    @FXML
    private TextField totalStudents;

    @FXML
    private MenuItem regMenu;

    @FXML
    private MenuItem viewStudent;

    @FXML
    private MenuItem newSale;

    @FXML
    private MenuItem deleteStudent;

    @FXML
    private MenuItem promoteStudent;

    @FXML
    private MenuItem createClass;

    @FXML
    private MenuItem viewAllClasses;

    @FXML
    private MenuItem delleteStudentContextMenu;

    @FXML
    private JFXTextField searchBox;

    @FXML
    private MenuItem viewStudentDetails;

    @FXML
    private MenuItem newPayment;

    @FXML
    private Hyperlink logout;

    @FXML
    private Text userNameLabel;

    @FXML
    private Label todayLabel;

    @FXML
    private Hyperlink changeDate;

    @FXML
    private ImageView studentImage;

    @FXML
    private Label imgLabel;

    @FXML
    private MenuItem viewAttendance;

    @FXML
    private  MenuItem newSubject;

    @FXML
    private  MenuItem assessment;




    public static User user;
    private ObservableList<Student> data= FXCollections.observableArrayList();
    private StudentDao studentDao =new StudentDao();
    TermDao termDao= new TermDao();
    private Initializer initializer = Initializer.getInitializerInstance();
    private Notification notification =Notification.getNotificationInstance();
    private MyProgressIndicator myProgressIndicator = MyProgressIndicator.getMyProgressIndicatorInstance();

    public void init(User user){
        mainController.user = user;
        userNameLabel.setText(user.getUsername());
    }

    private void showRegStudentForm() {
        try {
            Parent  root =  FXMLLoader.load(getClass().getResource("../view/student.fxml"));
            Scene scene= new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showClassListForm(){
        try {
            Parent  root =  FXMLLoader.load(getClass().getResource("/view/classListForm.fxml"));
            Scene scene= new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateTableView(){
//        if(studentTableView.getItems().isEmpty()||studentTableView.getItems().size()<data.size()){
            nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                    return new SimpleStringProperty(param.getValue().getFirstname() +" "+param.getValue().getOthername()+ " "+ param.getValue().getLastname());
                }
            });

            classCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                    Stage stage = param.getValue().getStage();

                    return new SimpleStringProperty(stage.getName());
                }
            });

            genderCol.setCellValueFactory(new PropertyValueFactory<Student,String>("gender"));


            ageCol.setCellValueFactory(new PropertyValueFactory<Student,String>("age"));

            parentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                    com.hub.schoolAid.Parent parent = param.getValue().getParent();

                    return new SimpleStringProperty(parent.getname());
                }
            });

            idCol.setCellValueFactory(new PropertyValueFactory<Student,String >("Id"));

            payFeeding.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Student, String> param) {
                    return new SimpleStringProperty(param.getValue().getPayFeeding() ? "YES": "NO");
                }
            });
            studentTableView.setVisible(Boolean.TRUE);

            getAllStudents();
            studentTableView.setItems(data);
            totalStudents.setText(String.valueOf(data.size()));
            tableInfo.setVisible(true);
//        }else
//            studentTableView.setVisible(Boolean.TRUE);
    }

    private List<Student> getAllStudents(){
        data.clear();
        try{
            data.addAll(studentDao.getAllStudents());
            return data;
        }catch (HibernateException e){
            e.printStackTrace();
            notification.notifyError("Sorry! an error occurred while fetching students","Database Error");
        }
      return null;
    }

    private void refresh(){
       populateTableView();
    }

    private Optional <ButtonType> showWarning(String message, String header){
        Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.YES,ButtonType.NO);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional <ButtonType> response = alert.showAndWait();

        return response;
    }

    private void toggleTableView(){
        if(studentTableView.isVisible()){
            studentTableView.setVisible(Boolean.FALSE);
        }else
        studentTableView.setVisible(Boolean.TRUE);
    }

    private void showNewTermForm(){
        //show a form to create a new term
        try {
            Parent  root =  FXMLLoader.load(getClass().getResource("/view/Term.fxml"));
            Scene scene= new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        todayLabel.setText("Today is: "+ LocalDate.now().getDayOfWeek().toString());
        //check whether the term has been initialized
       PauseTransition show = new PauseTransition(Duration.seconds(5));
        show.setOnFinished(event -> {
//            termDao =new TermDao();

          try{
              termDao.getCurrentTerm();
          }catch (NoResultException e){
              showNewTermForm();
          }
        });
        show.play();

        studentTableView.getSelectionModel().setCellSelectionEnabled(false);

        studentTableView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
                if(studentTableView.getItems().size()>0){
                    String img= Utils.studentImgPath+studentTableView.getSelectionModel().getSelectedItem().getImage();
                    if(img !=null){
                        URL url= getClass().getResource(studentTableView.getSelectionModel().getSelectedItem().getImage());
                        //                          URL url= getClass().getResource(studentTableView.getSelectionModel().getSelectedItem().getImage());
//                            Image image = new Image(new FileInputStream(getClass().getResourceAsStream(img).toString()));
                        Image image = new Image(getClass().getResourceAsStream(img));
//                      System.out.print("This is the file path "+studentTableView.getSelectionModel().getSelectedItem().getImage());
                        imgLabel.setText("");
                        studentImage.setImage(image);
                        studentImage.setVisible(Boolean.TRUE);
                    }else {
                        imgLabel.setText("Image Does Not Exist");
                    }
                }
            }
        });

        studentTableView.setEditable(Boolean.TRUE);

        studentTableView.setRowFactory(param -> {
            final TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event ->{
                if(event.getClickCount()==2){
                    //listening for double click
                    if(!studentTableView.getItems().isEmpty()){
                        showStudentDetailsForm();
                    }
                }
            });
            return row;
        });

        eye.setOnMouseClicked(event -> toggleTableView());

        //show the form to register a new student
        regMenu.setOnAction(event -> {
            showRegStudentForm();
        });

        createClass.setOnAction(event -> {
            try {
                Parent  root =  FXMLLoader.load(getClass().getResource("/view/stageForm.fxml"));
                Scene scene= new Scene(root);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.UTILITY);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        newSale.setOnAction(event -> {
            try {
                Parent  root =  FXMLLoader.load(getClass().getResource("/view/salesForm.fxml"));
                Scene scene= new Scene(root);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.UTILITY);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        refresh.setOnAction(event ->{
            refresh();
        });

        viewStudent.setOnAction(event -> {
            Task task = new Task() {
                @Override
                protected Object call() {
                    populateTableView();
                    return null;
                }
            };
            task.setOnRunning(e -> {
                myProgressIndicator.showActionProgress("Fetching data. Please wait...");
            });
            task.setOnSucceeded(e -> {

                myProgressIndicator.hideProgress();
            });
            task.setOnFailed(e ->{
                myProgressIndicator.hideProgress();
               notification.notifyError("an error occurred while fetching the records","Error!");
            } );

            new Thread(task).start();
        });

        viewAllClasses.setOnAction(event -> {
            showClassListForm();
        });

        delleteStudentContextMenu.setOnAction(event -> {
        Optional<ButtonType>  response  = showWarning("You are about to delete a student.\n All records about this student including parent and sales information " +
                "will be cleared.\n Do you want to continue?","Delete Student");

            if(response.isPresent() && response.get() ==ButtonType.YES){
               Optional<ButtonType>response2 =showWarning("You cannot undo this action.\n Do you want to continue?","Delete student");
               if(response2 .isPresent() && response2.get() == ButtonType.YES){
                   try {
                       studentDao.deleteStudent(studentTableView.getSelectionModel().getSelectedItem());
                       notification.notifySuccess("Student records deleted","Success");
                       refresh();
                   }catch (HibernateException e){
                       notification.notifyError("Sorry! an error occurred while fetching students","Database Error");
                    e.printStackTrace();
                   }
               }
            }
            return;
        });

        searchBox.setOnKeyReleased(event -> {
            data.clear();
            studentTableView.getItems().clear();
            data.addAll(studentDao.getStudentByName(searchBox.getText().trim()));
            populateTableView();
        });

//        viewStudentDetails.setOnAction(event -> {
//            System.out.print("We are passing this student to the controller"+studentTableView.getSelectionModel().getSelectedItem().getFirstname());
//            System.out.print("We are passing this student to the controller--image"+studentTableView.getSelectionModel().getSelectedItem().getImage());
//            Parent root;
//            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/view/studentDetailsForm.fxml"));
//            try {
//                root=fxmlLoader.load();
//                studentDetailsFormController studentDetailsFormController = fxmlLoader.getController();
//
//                studentDetailsFormController.init(studentTableView.getSelectionModel().getSelectedItem());
//                Scene scene = new Scene(root);
//                javafx.stage.Stage stage = new javafx.stage.Stage();
//                stage.setScene(scene);
//                stage.initModality(Modality.APPLICATION_MODAL);
//                stage.initStyle(StageStyle.UNDECORATED);
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        viewStudentDetails.setOnAction(event -> {
            showStudentDetailsForm();
        });

        newPayment.setOnAction(event -> {
            try {
                Parent  root =  FXMLLoader.load(getClass().getResource("/view/salesDetailsForm.fxml"));
                Scene scene= new Scene(root);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.DECORATED);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.setTitle("");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        viewAttendance.setOnAction(event -> {
            AttendanceDao attendanceDao = new AttendanceDao();
            List<Attendance> attendanceList  = attendanceDao.getAttendanceByDate(LocalDate.now());
            showViewAttendanceForm(attendanceList);
        });

        newSubject.setOnAction(event -> showSubjectForm());

        assessment.setOnAction(event -> showAssessmentForm());
        logout.setOnAction(event -> Initializer.getInitializerInstance().showLoginForm());

        menuToggle.setOnMouseClicked(e->{
            if (vbox.isVisible())
                vbox.setVisible(Boolean.FALSE);
            else vbox.setVisible(Boolean.TRUE);
        });

        BooleanProperty mouseMoving = new SimpleBooleanProperty();
        mouseMoving.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean wasMoving, Boolean isMoving) {
                if(isMoving){
                    //do something here
                }else{
                    //lock the system
                    if(!Initializer.isLocked){
                        Initializer.getInitializerInstance().haltSystem();
                        Initializer.isLocked=true;
                    }
                }
            }
        });

//        PauseTransition pause = new PauseTransition(Duration.seconds(5));
//        pause.setOnFinished(event ->{
//            System.out.print("the pause transition is done...");
//            mouseMoving.set(false);
//        });
//
//        parent.setOnMouseMoved(event -> {
//            mouseMoving.set(true);
//            pause.playFromStart();
//        });
//        parent.setOnMouseEntered(event -> {
//            mouseMoving.set(true);
//            pause.playFromStart();
//        });
//
//        parent.setOnMouseExited(event -> {
//            mouseMoving.set(true);
//            pause.playFromStart();
//        });

    }

    private void showStudentDetailsForm() {
        Parent root;
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/view/studentDetailsTabPane.fxml"));
        try {
            root=fxmlLoader.load();
            studentDetailsFormController studentDetailsFormController = fxmlLoader.getController();

            studentDetailsFormController.init(studentTableView.getSelectionModel().getSelectedItem());
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showViewAttendanceForm(List<Attendance> attendances){
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/attendance.fxml"));
        try {
            root=fxmlLoader.load();
            viewAttendanceController viewAttendanceController = fxmlLoader.getController();
            viewAttendanceController.init(attendances);
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            }
        }

    private void showSubjectForm(){
        Parent root;
        FXMLLoader fxmlLoader =new FXMLLoader(getClass().getResource("/view/subject.fxml"));
        try {
            root=fxmlLoader.load();
            Scene scene =new Scene(root);
            javafx.stage.Stage stage =new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("New Subject");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAssessmentForm(){
        Parent root;
        FXMLLoader fxmlLoader =new FXMLLoader(getClass().getResource("/view/assessment.fxml"));
        AssessmentFormController assessmentFormController =fxmlLoader.getController();

        try {
            root=fxmlLoader.load();
            Scene scene =new Scene(root);
            javafx.stage.Stage stage =new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("Assessment Sheet");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

