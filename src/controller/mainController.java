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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.input.KeyCode;
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

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
    private MenuItem editSale;


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
    private MenuItem createClass;

    @FXML
    private MenuItem viewAllClasses;

    @FXML
    private MenuItem delleteStudentContextMenu;

    @FXML
    private MenuItem manageStudents;

    @FXML
    private MenuItem salesOverview;

    @FXML
    private TextField searchBox;

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
    private MenuItem editStage;

    @FXML
    private  MenuItem assessment;

    @FXML
    private MenuItem report;

    @FXML
    private MenuItem grade;

    @FXML
    private MenuItem dateTime;

    @FXML
    private MenuItem create_staff;

    @FXML
    private MenuItem view_staff;

    @FXML
    private MenuItem studentBills;

    @FXML
    private Button schoolFees;

    @FXML
    private Label trialLabel;

    public static User user;
    private ObservableList<Student> data = FXCollections.observableArrayList();
    private FilteredList<Student> filteredData = new FilteredList <> (data, e ->true);
    private SortedList<Student> sortedList = new SortedList<>(filteredData);
    private StudentDao studentDao =new StudentDao();
    TermDao termDao= new TermDao();
    private Initializer initializer = Initializer.getInitializerInstance();
    private Notification notification =Notification.getNotificationInstance();
    private MyProgressIndicator myProgressIndicator = MyProgressIndicator.getMyProgressIndicatorInstance();
    private salesDetailsFormController salesDetailsFormController = new salesDetailsFormController();
    private AppDao appDao = new AppDao();
    private App appSettings =AppDao.getAppSetting();

    public void init(User user){
        mainController.user = user;
        userNameLabel.setText(user.getUsername());
    }

    private void showRegStudentForm() {
        try {
            Parent  root =  FXMLLoader.load(getClass().getResource("/view/student.fxml"));
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
            Scene scene = new Scene(root);
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
        setTableData();
        studentTableView.setVisible(Boolean.TRUE);
        if(data.isEmpty()) {
            getAllStudents();
        }
        studentTableView.setItems(data);
        totalStudents.setText(String.valueOf(data.size()));
        tableInfo.setVisible(true);

    }

    private void setTableData(){
        // if(studentTableView.getItems().isEmpty()||studentTableView.getItems().size()<data.size()){
        nameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().toString()));

        classCol.setCellValueFactory(param -> {
            Stage stage = param.getValue().getStage();
            return new SimpleStringProperty(stage.getName());
        });

        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));


        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

        parentCol.setCellValueFactory(param -> {
            com.hub.schoolAid.Parent parent = param.getValue().getParent();

            return new SimpleStringProperty(parent.getname());
        });

        idCol.setCellValueFactory(new PropertyValueFactory<>("Id"));

        payFeeding.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPayFeeding() ? "YES": "NO"));

        studentTableView.setVisible(!studentTableView.isVisible());
    }
    private List<Student> getAllStudents() {
        data.clear();
        try{
            data.addAll(studentDao.getAllStudents());
            return data;
        }catch (HibernateException e){
            notification.notifyError("Sorry! an error occurred while fetching students","Database Error");
        }
      return null;
    }

    private void refresh() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                getAllStudents();
                return null;
            }
        };
        task.setOnRunning(event -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Refreshing records..."));
        task.setOnSucceeded(event -> {
            MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
            populateTableView();
        });
        task.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(task).start();
    }

    private Optional <ButtonType> showWarning(String message, String header) {
        Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.YES,ButtonType.NO);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional <ButtonType> response = alert.showAndWait();

        return response;
    }

    private void toggleTableView() {
        if(studentTableView.isVisible()) {
            studentTableView.setVisible(Boolean.FALSE);
        } else
        studentTableView.setVisible(Boolean.TRUE);
    }

    private void showNewTermForm() {
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

        }
    }

    private void showTerminalReport() {
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/terminalReport.fxml"));
        try {
            root=fxmlLoader.load();
            TerminalReportController controller = fxmlLoader.getController();
            controller.init();
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Terminal Report");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showManageStudentForm() {
        try {
            Parent root;
            FXMLLoader fxmlLoader = new FXMLLoader((getClass().getResource("/view/manageStudents.fxml")));
            root= fxmlLoader.load();
            ManageStudentController manageStudentController = fxmlLoader.getController();
            if(!data.isEmpty()) {
                manageStudentController.init(data);
            } else {
                manageStudentController.init();
            }

            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("Manage Students");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "Error");
        }
    }

    private void showNewStaffForm() {
        try {
            Parent root;
            FXMLLoader fxmlLoader = new FXMLLoader((getClass().getResource("/view/staff_reg.fxml")));
            root = fxmlLoader.load();
            StaffController staffController = fxmlLoader.getController();
            staffController.init();

            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("Create new staff");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setMaximized(false);
            stage.show();
        }catch (Exception e) {
            Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "Error");
        }
    }

    private void showSchoolFees() {
        if(this.data.isEmpty()) {
            getAllStudents();
        }
        try {
            Parent root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/school_fees_dashboard.fxml"));
            root = fxmlLoader.load();
            SchoolFeesFormController controller = fxmlLoader.getController();
            controller.initialize(data);
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("School Fees");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "Error");
        }
    }

    private void showBill() {
        try {
            Parent root;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/showBills.fxml"));
            root = fxmlLoader.load();
            ShowBillController controller = fxmlLoader.getController();
            controller.init();
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("Student Bill");
            stage.setMaximized(true);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        }catch (Exception e) {
            Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "Error");
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utils utils = new Utils();
        todayLabel.setText("Today is: "+ LocalDate.now().getDayOfWeek().toString());
//        Task check =new Task() {
//            @Override
//            protected Object call() throws Exception {
//            Boolean canCheck =true;
//        while (canCheck){
//            if(data != null){
//                Iterator<Student>studentIterator = data.iterator();
//                while(studentIterator.hasNext()) {
//                    Student s = studentIterator.next();
//                    if(s.getAccount().getFeedingFeeCredit() < 0) {
//                        System.out.println(s.toString() + "is owing Feeding Fee");
//                    }
//                    if(!studentIterator.hasNext())
//                        canCheck=false;
//                }
//            }
//        }
//            return null;
//            }
//        };
//        new Thread(check).start();

        //check whether the term has been initialized
       PauseTransition show = new PauseTransition(Duration.seconds(1));
        show.setOnFinished(event -> {
//            termDao =new TermDao();
          try {
              termDao.getCurrentTerm();
             if(!appDao.appCanBoot(appSettings))
                 utils.showTrialForm();
             else {
                 // check if the date is the same
                 LocalDate date = appSettings.getLastOpened();
                 if(date.isBefore(LocalDate.now()) || date.isAfter(LocalDate.now())) {
                     appDao.increaseAppCounter(appSettings);
                 }

                 int trialLeft = appSettings.getMaxCount() - appSettings.getCurrentCount();
                 if(trialLeft < 14) {
                     utils.showTrialForm();
                     trialLabel.setText("You Trial will expire in "+ (trialLeft) +" days");
                     trialLabel.setVisible(true);
                 }
             }

              // update the date
              appSettings.setLastOpened(LocalDate.now());
              AppDao.updateApp(appSettings);
          }catch (NoResultException e){
              showNewTermForm();
          }
        });
        show.play();

        studentTableView.getSelectionModel().setCellSelectionEnabled(false);

        studentTableView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
                if(studentTableView.getItems().size() > 0){
//                    StudentDetailsDao detailsDao =new StudentDetailsDao();
//                    String img= Utils.studentImgPath+detailsDao.getImage(studentTableView.getSelectionModel().getSelectedItem());
//                    String img= Utils.studentImgPath+studentTableView.getSelectionModel().getSelectedItem().getImage();
                    try{
                        if(studentTableView.getSelectionModel().getSelectedItem().getPicture() != null){
//                        URL url= getClass().getResource(studentTableView.getSelectionModel().getSelectedItem().getImage());
                            //                          URL url= getClass().getResource(studentTableView.getSelectionModel().getSelectedItem().getImage());
//                            Image image = new Image(new FileInputStream(getClass().getResourceAsStream(img).toString()));
//                            Image image = new Image(getClass().getResourceAsStream(img));
//
                            imgLabel.setText("");
//                            studentImage.setImage(image);
                            ImageHandler.setImage(studentTableView.getSelectionModel().getSelectedItem().getPicture(), studentImage);

                            studentImage.setVisible(Boolean.TRUE);
                        }else {
                            imgLabel.setText("Image Does Not Exist");
                            studentImage.setVisible(Boolean.FALSE);
                        }
                    }catch (NullPointerException e){
                        imgLabel.setText("Image Does Not Exist");
                        studentImage.setVisible(Boolean.FALSE);
                    }
                }
            }
        });

        studentTableView.setEditable(Boolean.TRUE);

        studentTableView.setRowFactory(param -> {
            final TableRow<Student> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2){
                    //listening for double click
                    if(!studentTableView.getItems().isEmpty()){
                        showStudentDetailsForm(studentTableView.getSelectionModel().getSelectedItem());
                    }
                }
            });
            return row;
        });

        studentTableView.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER){
                if(!studentTableView.getItems().isEmpty()){
                    showStudentDetailsForm(studentTableView.getSelectionModel().getSelectedItem());
                }
            }else if(event.getCode()==KeyCode.DELETE){
                if(!studentTableView.getItems().isEmpty()){
                    deleteStudentData();
                }
            }
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
//            populateTableView();
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
            deleteStudentData();
            return;
        });

        searchBox.setOnKeyReleased(event -> {
            searchTable();
        });

        viewStudentDetails.setOnAction(event -> {
            showStudentDetailsForm(studentTableView.getSelectionModel().getSelectedItem());
        });

        salesOverview.setOnAction(event -> showSalesOverview());

        create_staff.setOnAction(event -> this.showNewStaffForm());

        // activate the search box when the table view is filled with data
        data.addListener(new ListChangeListener<Student>() {
            @Override
            public void onChanged(Change<? extends Student> c) {
                if(!data.isEmpty()) {
                    searchBox.setDisable(false);
                } else searchBox.setDisable(true);
            }
        });

        newPayment.setOnAction(event -> {
            // check the type of payment that the school uses
//            try {
//                Parent  root =  FXMLLoader.load(getClass().getResource("/view/salesDetailsForm.fxml"));
//                Scene scene= new Scene(root);
//                javafx.stage.Stage stage = new javafx.stage.Stage();
//                stage.setScene(scene);
//                stage.initStyle(StageStyle.DECORATED);
//                stage.setMaximized(Boolean.TRUE);
//                stage.setTitle("");
//                stage.initModality(Modality.APPLICATION_MODAL);
//                stage.show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            appSettings = AppDao.getAppSetting() ;
            if (appSettings != null) {
                if (appSettings.getFeedingType() == FeedingType.COUPON) {
                    //show the coupon options here
                    this.showPaymentOption("/view/feeding_form.fxml", FeedingType.COUPON);
                } else {
                    // show form for those who use CASH
                    this.showPaymentOption("/view/salesDetailsForm.fxml", FeedingType.CASH);
                }
            }
        });


        viewAttendance.setOnAction(event -> {
            showViewAttendanceForm();
        });

        manageStudents.setOnAction(e-> showManageStudentForm());

        newSubject.setOnAction(event -> showSubjectForm());

        assessment.setOnAction(event -> showAssessmentForm());

        report.setOnAction(event -> showTerminalReport());

        grade.setOnAction(event -> showGradeForm());

        editSale.setOnAction(event   -> showSalesItemForm());

        logout.setOnAction(event -> Initializer.getInitializerInstance().showLoginForm());

        dateTime.setOnAction(event ->  salesDetailsFormController.showCreateAttendaceForm());

        menuToggle.setOnMouseClicked(e-> {
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

        editStage.setOnAction(event -> this.showEditStageForm());

        schoolFees.setOnAction(event -> showSchoolFees());

        studentBills.setOnAction(event -> showBill());
    }

    private void searchTable() {
        searchBox.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredData.setPredicate( (Predicate<? super  Student>) student ->{
                if( newValue ==null || newValue.isEmpty() ) {
                    return  true;
                }

                String lowerVal = newValue.toLowerCase();
                if(student.getFirstname().toLowerCase().contains(lowerVal)){
                    return  true;
                }else if(student.getLastname().toLowerCase().contains(lowerVal)) {
                    return  true;
                }else if(student.getLastname().toLowerCase().contains(lowerVal)) {
                    return  true;
                }else if (student.toString().toLowerCase().contains(lowerVal));
                return false;
            });
        }));

        sortedList.comparatorProperty().bind(studentTableView.comparatorProperty());
        studentTableView.setItems(sortedList);
    }

    private void deleteStudentData() {
        Optional<ButtonType>  response  = showWarning("You are about to delete a student.\n All records about this student including parent and sales information " +
                "will be cleared.\n Do you want to continue?","Delete Student");

        if(response.isPresent() && response.get() ==ButtonType.YES){
            Optional<ButtonType>response2 =showWarning("You cannot undo this action.\n Do you want to continue?","Delete student");
            if(response2 .isPresent() && response2.get() == ButtonType.YES){
                TextInputDialog inputDialog = new TextInputDialog();
                inputDialog.setHeaderText("Permission Required");
                inputDialog.setContentText("Enter the password for this user to continue");
                Optional<String> result = inputDialog.showAndWait();
                if(result.isPresent() && result.get().equals(LoginFormController.user.getPassword())){
                    try {
                        studentDao.deleteStudent(studentTableView.getSelectionModel().getSelectedItem());
                        notification.notifySuccess("Student records deleted","Success");
                        refresh();
                    }catch (HibernateException e){
                        notification.notifyError("Sorry! an error occurred while fetching students","Database Error");
                        e.printStackTrace();
                    }
                }else Notification.getNotificationInstance().notifyError("Action denied","Error");
            }
        }
    }

    public void showStudentDetailsForm(Student student) {
        Parent root;
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/view/studentDetailsTabPane.fxml"));
        try {
            root=fxmlLoader.load();
            studentDetailsFormController studentDetailsFormController = fxmlLoader.getController();
            studentDetailsFormController.init(student);
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

    private void showViewAttendanceForm(){
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/attendance.fxml"));
        try {
            root=fxmlLoader.load();
            viewAttendanceController viewAttendanceController = fxmlLoader.getController();
            viewAttendanceController.init();
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
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

    private void showSalesItemForm() {
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/salesItemForm.fxml"));
        try {
            root = fxmlLoader.load();
            SalesItemFormController salesItemFormController = fxmlLoader.getController();
            salesItemFormController.init();
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showGradeForm (){
        Parent root;
        FXMLLoader fxmlLoader =new FXMLLoader(getClass().getResource("/view/gradeForm.fxml"));
        try {
            root=fxmlLoader.load();
            Scene scene =new Scene(root);
            javafx.stage.Stage stage =new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("New Grade");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEditStageForm() {
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/manageStage.fxml"));
        try {
            root = fxmlLoader.load();
            ManageStageController controller = fxmlLoader.getController();
            controller.init();
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("Edit Stage");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSalesOverview () {
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/salesSummary.fxml"));
        try {
            root = fxmlLoader.load();
            SalesSummaryController controller = fxmlLoader.getController();
            controller.init(null);
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.setTitle("Sales Overview");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showPaymentOption(String url, FeedingType feedingType) {

        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url));
            root = fxmlLoader.load();

            // check if we need to init a controller
            if (feedingType == FeedingType.COUPON) {
                FeedingFormController controller = fxmlLoader.getController();
                controller.init(this);
            }
            Scene scene= new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.DECORATED);
            stage.setMaximized(Boolean.TRUE);
            stage.setTitle("");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (Exception e) {

        }

//        try {
//            Parent  root =  FXMLLoader.load(getClass().getResource(url));
//            Scene scene= new Scene(root);
//            javafx.stage.Stage stage = new javafx.stage.Stage();
//            stage.setScene(scene);
//            stage.initStyle(StageStyle.DECORATED);
//            stage.setMaximized(Boolean.TRUE);
//            stage.setTitle("");
//            stage.initModality(Modality.WINDOW_MODAL);
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}

