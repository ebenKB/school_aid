package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXRadioButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class salesDetailsFormController implements Initializable{

    @FXML
    private AnchorPane salesPane;

    @FXML
    private TextField name;

    @FXML
    private TableView<Sales> salesTableView;

    @FXML
    private TableColumn<Sales, String> nameCol;

    @FXML
    private TableColumn<Sales, String> itemCol;

    @FXML
    private TableColumn<Sales, String> unitPriceCol;

    @FXML
    private TableColumn<Sales, String> qtyCol;

    @FXML
    private TableColumn<Sales, String> totalCol;

    @FXML
    private TableColumn<Sales,String> amntPaidCol;

    @FXML
    private TableColumn<Sales, String> balCol;

    @FXML
    private TableColumn<Sales, String> dateCol;


    @FXML
    private FontAwesomeIconView presentIcon;

    @FXML
    private ListView<Student> studentListView;

    @FXML
    public TableView<AttendanceTemporary> studentTableView;

    @FXML
    private Label infoLabel;

    @FXML
    private Button allSales;

    @FXML
    public JFXRadioButton attendanceRadio;

    @FXML
    public ToggleGroup radioToggle;

    @FXML
    public JFXRadioButton salesRadio;

    @FXML
    private Button loadData;


    @FXML
    private MenuItem newPayment;

    @FXML
    private MenuItem newAttendancePayment;

    @FXML
    public TableColumn<AttendanceTemporary, String> studentNameCol;

    @FXML
    public TableColumn<AttendanceTemporary, String> classNameCol;

    @FXML
    private TableColumn<Student,String> actionCol;

    @FXML
    private TableColumn<AttendanceTemporary, String> owing;

    @FXML
    private TableColumn<AttendanceTemporary, String> feedingType;

    @FXML
    private TableColumn<AttendanceTemporary, String> feeding;

    @FXML
    private TableColumn<AttendanceTemporary, String> isPresentCol;

    @FXML
    private TableColumn<Sales, String> statusCol;

    @FXML
    private Pane headerPane;

    @FXML
    private TableColumn<AttendanceTemporary, String> tagCol;

    @FXML
    private Label presentLbl;

    @FXML
    private Label nameLable;

    @FXML
    private HBox totalFeedingPane;

    @FXML
    private TextField totalFeeding;

    @FXML
    private TextField totalDebt;

    @FXML
    private VBox fieldsBox;

    @FXML
    private CheckBox debtCheck;

    @FXML
    private CheckBox creditCheck;

    @FXML
    private CheckBox feedingCheck;

    @FXML
    private TextField totalCredit;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Hyperlink attendanceWizard;

    @FXML
    private JFXButton viewDetails;

    @FXML
    private JFXDrawer drawer;


    @FXML
    private BarChart<?, ?> chart;

    private Student student =new Student();
    public StudentDao studentDao =new StudentDao();
    private SalesDao salesDao =new SalesDao();
    private TermDao termDao   =new TermDao();
    private Notification notification =Notification.getNotificationInstance();
    private AttendanceDao attendanceDao = new AttendanceDao();
    public AttendanceTemporaryDao attendanceTemporaryDao= new AttendanceTemporaryDao();
    private ObservableList<Sales> salesdata = FXCollections.observableArrayList();
    public ObservableList<Student> studentdata = FXCollections.observableArrayList();
//    public ObservableList<Student> attendanceData = FXCollections.observableArrayList();
    public ObservableList<AttendanceTemporary> attendanceTemporaries = FXCollections.observableArrayList();
    FilteredList<AttendanceTemporary> filteredAtt = new FilteredList<>(attendanceTemporaries,e ->true);
    SortedList<AttendanceTemporary> sortedList = new SortedList<>(filteredAtt);

    private Image successImg = new Image("/assets/success.png");
    ImageView imageView;
    private SimpleIntegerProperty presentCounter= new SimpleIntegerProperty();

    public void populateStudentTable(){
        try {
            tagCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getPayFeeding() ? "YES":"NO"));

            owing.setCellValueFactory(param -> {
                return  new SimpleStringProperty(String.valueOf(param.getValue().getStudent().getAccount().getFeedingFeeCredit()));
            });

//            owing.setCellFactory(column -> new TableCell<AttendanceTemporary, String>() {
//                @Override
//                protected void updateItem(String bal, boolean empty) {
//                    super.updateItem(bal, empty);
//
//                    setText(empty ? "" : getItem().toString());
//                    setGraphic(null);
//
//                    TableRow<AttendanceTemporary> currentRow = getTableRow();
//
//                    if (!isEmpty()  && !currentRow.isEmpty()) {
//                        if(Double.valueOf(bal)<0 )
//                            currentRow.setStyle("-fx-background-color:#ff6a51");
//                        else if(Double.valueOf(bal)>0 ){
//                            currentRow.setStyle("-fx-background-color:#0ecc31");
//                        }
//                        else {
//                            currentRow.setStyle("");
////                            currentRow.setStyle("-fx-background-color:#fff");
//                        }
//                    }else {
//                        currentRow.setStyle("");
//                    }
//                }
//            });

            feedingType.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getFeedingStatus().toString()));

            studentNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getFirstname() +" "+
                    param.getValue().getStudent().getOthername()+ " " + param.getValue().getStudent().getLastname()));

            feeding.setCellValueFactory(param -> {
                if( !param.getValue().getStudent().getPayFeeding())
                    return new SimpleStringProperty("0");
//            else if (param.getValue().getStudent().getAccount().getFeedingFeeCredit()>0 || param.getValue().getStudent().getFeedingStatus().equals(Student.FeedingStatus.PERIODIC))
//                return  new SimpleStringProperty("0");
                return new SimpleStringProperty(param.getValue().getFeedingFee()>0 ? param.getValue().getFeedingFee().toString():param.getValue().getStudent().getStage().getFeeding_fee().toString());

            });

            feeding.setCellFactory(TextFieldTableCell.forTableColumn());
            feeding.setOnEditCommit(event -> {
                Double newAmount;
                if( ! event.getNewValue().isEmpty()){
                    newAmount =Double.valueOf(event.getNewValue());
                }else newAmount = Double.valueOf(event.getOldValue());

                event.getTableView().getItems().get(event.getTablePosition().getRow()).setFeedingFee(newAmount);
                populateStudentTable();
            });

            classNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getStage().getName()));

            isPresentCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().isPresent() ? "PRESENT" : "ABSENT"));

            addButtonToTable(actionCol);
            studentTableView.setItems(attendanceTemporaries);
            applyTableStyle();
            Double total = 0.0;
            Double debt = 0.0;
            Double credit = 0.0;

            //update the present counter
            presentCounter.setValue(0);
            for(AttendanceTemporary at: attendanceTemporaries){
                if(at.isPresent()){
                    presentCounter.set(presentCounter.getValue()+1);
                    if(at.hasPaidNow()){
                        total+=at.getFeedingFee();
                    }
                }

               if( at.getStudent().getAccount().getFeedingFeeCredit() < 0 ) {
                    debt += at.getStudent().getAccount().getFeedingFeeCredit() ;
                    totalDebt.setText( String.valueOf(debt));
               } else {
                    credit += at.getStudent().getAccount().getFeedingFeeCredit();
                    totalCredit.setText(String.valueOf(credit));
               }
            }
            totalFeeding.setText(total.toString());
        }catch (Exception e){
//            e.printStackTrace();
            Notification.getNotificationInstance().notifyError("An error occurred while preparing the table data","Error in Table field");
        }
    }
    /**
     * this method is used to turn the button on and off
     * @param btn the button that we want to hide or show
     * @param index the table row at which the item is located
     */
    private void toggleButton(Button btn, int index){
        if(btn.getGraphic() ==null){
            imageView  = new ImageView(successImg);
//            attendanceData.add(studentTableView.getItems().get(index));
            attendanceTemporaryDao.markPresent(studentTableView.getItems().get(index));
            presentCounter.set(presentCounter.getValue()+1);
            notification.notifySuccess("Student has been marked present","Present");
            btn.setText("");
            imageView.setFitWidth(65);
            imageView.setFitHeight(35);
            btn.setGraphic(imageView);
            btn.setTextFill(Color.GREEN);
        }else {
            presentCounter.set(presentCounter.getValue()-1);
//            attendanceData.remove(studentTableView.getItems().get(index));
            attendanceTemporaryDao.markAbsent(studentTableView.getItems().get(index));
            notification.notifyError("You have marked this student absent","Absent");
            btn.setGraphic(null);
            btn.setText("Check in");
            btn.setTextFill(Color.BLACK);
        }
    }
    private void toggleButtons(Button btn,Button btn2){
        if(btn.isDisabled()){
            btn2.setDisable(!btn.isDisabled());
        }else{
            btn.setDisable(!btn2.isDisabled());
        }
//        if(btn.isDisabled()){
//            btn.setDisable(Boolean.FALSE);
//            btn2.setDisable(Boolean.TRUE);
//        }else if(btn2.isDisabled()){
//            btn2.setDisable(Boolean.FALSE);
//            btn.setDisable(Boolean.TRUE);
//        }
    }
    private void addButtonToTable(TableColumn column) {

        Callback<TableColumn<Student, Void>, TableCell<Student, Void>> cellFactory = new Callback<TableColumn<Student, Void>, TableCell<Student, Void>>() {
            @Override
            public TableCell<Student, Void> call(final TableColumn<Student, Void> param) {
                TableCell<Student, Void> cell = new TableCell<Student, Void>() {

                    //create radio buttons
                    private  RadioButton paid= new RadioButton("PAY TODAY");
                    private  RadioButton payLater = new RadioButton("PAY LATER");
                    private  ToggleGroup payment = new ToggleGroup();
                    private  VBox vBox=new VBox();

                    //create an action button
                    public Button btn  = new Button("Check in");
                    public Button btn2 = new Button("Checkout");
                    public HBox hBox   = new HBox();

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            if(payment.getSelectedToggle()==null){
                                Alert alert =new Alert(Alert.AlertType.INFORMATION,"",ButtonType.OK);
                                alert.setContentText("Indicate whether the student is paying now or later.");
                                alert.setHeaderText("Payment option ?");
                                alert.show();
                            }else {
                                //Creates an attendance temporary and assign it to st
                                Student st = studentTableView.getItems().get(getIndex()).getStudent();
                                AttendanceTemporary at = studentTableView.getItems().get(getIndex());

                                //check if the student is not paying now
                                if(!at.hasPaidNow() && at.getFeedingFee() > at.getStudent().getAccount().getFeedingFeeToPay()){
                                    String hd = "Invalid amount at FEEDING FEE";
                                    String msg = "If the student is paying  later, the feeding fee cannot be greater than what they pay everyday.\n" +
                                            "If you still want to use this amount, Go to View Student > Feeding > and change the value\n" +
                                            "The system will reset the Feeding Fee to Default." ;
                                    showAlertErrorAlert(hd,msg);
                                    at.setFeedingFee(at.getStudent().getAccount().getFeedingFeeToPay());
                                    populateStudentTable();

                                }
                                //If a daily student does not pay for a day
                                else if(!at.hasPaidNow() && at.getStudent().getFeedingStatus().equals(Student.FeedingStatus.DAILY)){
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION,"",ButtonType.OK,ButtonType.CANCEL);
                                    alert.setTitle("Payment Info");
                                    alert.setHeaderText("Student's payment is"+at.getStudent().getFeedingStatus());
                                    alert.setContentText("If you continue, the student's feeding status will change\n and the student's account will be debited.");
                                    Optional<ButtonType>result = alert.showAndWait();
                                    if(result.isPresent() && result.get() == ButtonType.OK){
                                        confirmAttendance();
                                    }
                                }
                                //when a daily student pays more than they should pay for one day
                                else  if((at.hasPaidNow()) && (at.getStudent().getFeedingStatus().equals(Student.FeedingStatus.DAILY)) &&
                                        (at.getFeedingFee() > at.getStudent().getAccount().getFeedingFeeToPay()) ){
                                    Alert alert =new Alert(Alert.AlertType.INFORMATION,"",ButtonType.YES,ButtonType.CANCEL);
                                    alert.setTitle("Payment");
                                    alert.setHeaderText("PAYMENT IN EXCESS");
                                    alert.setContentText("The student is paying more than they should pay.\n Are you sure you want to continue?");
                                    Optional<ButtonType>result = alert.showAndWait();
                                    if(result.isPresent() && result.get() ==ButtonType.YES){
                                        confirmAttendance();
                                    }
                                }

                                else if(at.getStudent().getFeedingStatus().equals(Student.FeedingStatus.DAILY)){
                                    confirmAttendance();
                                }

                                else if(((at.getStudent().getFeedingStatus().equals(Student.FeedingStatus.PERIODIC)) ||
                                        (at.getStudent().getFeedingStatus().equals(Student.FeedingStatus.SEMI_PERIODIC)))&&
                                        (at.getFeedingFee() <= at.getStudent().getAccount().getFeedingFeeToPay())){

                                    //check if the student has balance
                                    if(st.getAccount().getFeedingFeeCredit()<=0 && !at.hasPaidNow()){ //--added && !at.hasPaidNow()
                                        Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.YES,ButtonType.NO);
                                        alert.setHeaderText("NO BALANCE");
                                        alert.setContentText("This student does not have any balance in their Account." +
                                                "\nIf you continue, the student will owe"+" "+(at.getStudent().getAccount().getFeedingFeeCredit()-at.getFeedingFee())+"\nDo you still want to mark the student present?");
                                        Optional<ButtonType>result = alert.showAndWait();
                                        if(result.isPresent() && result.get() ==ButtonType.NO)
                                            return;
                                        else confirmAttendance();
                                    }else{
                                        //added alert condition
                                        Alert alert =new Alert(Alert.AlertType.WARNING,"",ButtonType.YES,ButtonType.CANCEL);
                                        alert.setHeaderText("Confirm Receipt of Payment");
                                        alert.setContentText("This student pays "+st.getFeedingStatus()+"\nAre you sure he is paying "+at.getFeedingFee()+" for today?\n" +
                                                "If you continue the amount will be added to today's feeding fee\nDo you want to continue?");
                                        Optional<ButtonType>result =alert.showAndWait();
                                        if(result.isPresent() && result.get()==ButtonType.YES)
                                            confirmAttendance();
                                    }
                                }else if (((at.getStudent().getFeedingStatus().equals(Student.FeedingStatus.PERIODIC)) ||
                                        (at.getStudent().getFeedingStatus().equals(Student.FeedingStatus.SEMI_PERIODIC)))&&
                                        (at.getFeedingFee() > at.getStudent().getAccount().getFeedingFeeToPay())){
                                    Alert alert =new Alert(Alert.AlertType.WARNING,"",ButtonType.CANCEL,ButtonType.YES);
                                    alert.setTitle("Payment");
                                    alert.setHeaderText("Confirm Receipt of Payment ");
                                    alert.setContentText("Are you sure the student is paying"+" "+at.getFeedingFee()+" "+"cedis today?\n" +
                                            "If you continue the money will be added to the feeding fee for today.\nAre you sure you have received the money?");

                                    Optional<ButtonType>result =alert.showAndWait();
                                    if(result.isPresent() && result.get() == ButtonType.YES){
                                        confirmAttendance();
                                    }
                                }
                                else showAlertErrorAlert("ERROR!!","Something might have gone wrong. Please return to attendance sheet.");
                            }
                        });

                        btn2.setOnAction(event -> {
                            presentCounter.set(presentCounter.getValue()-1);
                            attendanceTemporaryDao.markAbsent(studentTableView.getItems().get(getIndex()));
                            notification.notifyError("You have marked this student absent","Absent");
                            toggleButtons(btn,btn2);
                            populateStudentTable();
                        });

                        payment.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                            AttendanceTemporary at = studentTableView.getItems().get(getIndex());
                            if(!at.isPresent()){
                                if(newValue==paid){
                                    at.setPaidNow(Boolean.TRUE);
                                }else at.setPaidNow(Boolean.FALSE);
                            }
                        });

//                        style the radio

                        paid.setOnAction(e->{
                            AttendanceTemporary at = studentTableView.getItems().get(getIndex());
                            at.setPaidNow(Boolean.TRUE);
                        });

                        payLater.setOnAction(e->{
                            AttendanceTemporary at = studentTableView.getItems().get(getIndex());
                            at.setPaidNow(Boolean.FALSE);
                        });
                    }

                    private void confirmAttendance() {
                        attendanceTemporaryDao.markPresent(studentTableView.getItems().get(getIndex()));
                        presentCounter.set(presentCounter.getValue()+1);
                        notification.notifySuccess("Student has been marked present","Present");
                        toggleButtons(btn,btn2);
                        populateStudentTable();
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            //initialize the button properties
                            hBox.getChildren().clear();
                            vBox.getChildren().clear();
                            paid.setToggleGroup(payment);
                            payLater.setToggleGroup(payment);
                            vBox.getChildren().addAll(paid,payLater);
                            vBox.setSpacing(10);
                            vBox.setId("vBoxAttendance");

                            hBox.getChildren().addAll(vBox,btn,btn2);
                            hBox.setSpacing(10);
                            hBox.setAlignment(Pos.CENTER);

                            btn.setTextFill(Color.GREEN);
                            btn2.setTextFill(Color.RED);

                            if(studentTableView.getItems().get(getIndex()).isPresent()){
                                btn.setDisable(Boolean.TRUE);
                                btn2.setDisable(Boolean.FALSE);
                            }else{
                                btn2.setDisable(Boolean.TRUE);
                                btn.setDisable(Boolean.FALSE);
                            }

                            //check if the person pays periodic or semi-periodic

                            if((studentTableView.getItems().get(getIndex()).isPresent()) && (studentTableView.getItems().get(getIndex()).hasPaidNow()))
                                payment.selectToggle(paid);
                            else if((studentTableView.getItems().get(getIndex()).isPresent()) && (!studentTableView.getItems().get(getIndex()).hasPaidNow()))
                                payment.selectToggle(payLater);
                            else if((studentTableView.getItems().get(getIndex()).getStudent().getFeedingStatus().equals(Student.FeedingStatus.PERIODIC)) ||
                                    (studentTableView.getItems().get(getIndex()).getStudent().getFeedingStatus().equals(Student.FeedingStatus.SEMI_PERIODIC))){
                                payment.selectToggle(payLater);
                            }else if(!studentTableView.getItems().get(getIndex()).isPresent() && studentTableView.getItems().get(getIndex()).getStudent().getFeedingStatus().equals(Student.FeedingStatus.DAILY)){
                                payLater.setSelected(false);
                                paid.setSelected(false);
                            }
                            setGraphic(hBox);
                        }
                    }
                };
                return cell;
            }
        };
        column.setCellFactory(cellFactory);
    }

    private void showAlertErrorAlert(String hd, String msg) {
        Alert alert =new Alert(Alert.AlertType.ERROR,"",ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText(hd);
        alert.setContentText(msg);
        alert.show();
    }

    private void populateSalesTable(){
        infoLabel.setText("");
        if(! salesdata.isEmpty()){
            nameLable.setText(salesdata.get(0).getStudent().toString());
            nameCol.setCellValueFactory(param -> {
                Sales sales = param.getValue();
                return new SimpleStringProperty(sales.getStudent().toString());
            });
            itemCol.setCellValueFactory(param -> {
                Sales sales = param.getValue();
                return new SimpleStringProperty(sales.getItem().getName());
            });

            unitPriceCol.setCellValueFactory(param -> {
                Sales sales =param.getValue();
                return new SimpleStringProperty(sales.getItem().getCost().toString());
            });

            qtyCol.setCellValueFactory(param -> {
                Sales sales  = param.getValue();
                return new SimpleStringProperty(String.valueOf(sales.getItem().getQty()).toString());
            });

            amntPaidCol.setCellValueFactory(param -> {
//              if(param.getValue().getAmountPaid()==param.getValue().getTotalcost())
//                  amntPaidCol.setStyle("-fx-background-color:green");
//              else
//                  amntPaidCol.setStyle("-fx-background-color:red");
                return new SimpleStringProperty(String.valueOf(param.getValue().getAmountPaid()));
            });
            totalCol.setCellValueFactory(param -> {
                Sales sales  = param.getValue();
                return new SimpleStringProperty(sales.getTotalcost().toString());
            });

            balCol.setCellValueFactory(param -> {
                Sales sales  = param.getValue();
                Double bal = (sales.getTotalcost() - sales.getAmountPaid() * -1);
                return new SimpleStringProperty(bal.toString());
            });

            dateCol.setCellValueFactory(param -> {
                if(param.getValue().getDate() ==null){
                    return new SimpleStringProperty("NOT FOUND");
                }
                return new SimpleStringProperty(param.getValue().getDate().toString());
            });

            statusCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAmountPaid() < param.getValue().getTotalcost()? "OWING" : "PAID"));
            salesTableView.setItems(salesdata);
        }else {
            infoLabel.setText("There is no sale for this student..");
        }
        salesTableView.setVisible(true);
    }


    /**
     * this method shows the form that is used to create a new attendance
     */
    public void showCreateAttendaceForm(){
        javafx.scene.Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/newAttendanceSheetForm.fxml"));
        try {
            root=fxmlLoader.load();
            newAttendanceSheetController controller = fxmlLoader.getController();
            controller.init(this);

            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(3));
        pauseTransition.setOnFinished(event -> {
            //show the form to create a new attendance sheet
            if( ! termDao.getCurrentTerm().getToday().equals(LocalDate.now())){
                showCreateAttendaceForm();
            }
        });
        pauseTransition.play();

        //show the total number of students present
        presentCounter.addListener((observable, oldValue, newValue) -> {
            presentLbl.setText(presentCounter.getValue().toString()+"/"+(attendanceTemporaries.size()));
            presentIcon.setVisible(Boolean.TRUE);
        });

        radioToggle.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            infoLabel.setText("");
            if(newValue==salesRadio){
                showSalesTable();
            }else{
                showAttendanceTable();
                attendanceWizard.setVisible(true);
            }
        });

        salesPane.setOnMouseClicked(event -> {
            studentListView.setVisible(Boolean.FALSE);
        });

        salesTableView.setOnMouseClicked(event -> studentListView.setVisible(Boolean.FALSE));

        name.setOnKeyReleased(event -> {
            activateListView();
        });

        try{
            studentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if(studentListView.getSelectionModel().getSelectedItem() != null){
                    if(radioToggle.getSelectedToggle()==salesRadio){
                        fetchSalesData(studentListView.getSelectionModel().getSelectedItem());

                    }else{
                        System.out.println("you selected from attendance");
                        //show the attendance table
                        String name = studentListView.getSelectionModel().getSelectedItem().getFirstname();
//                                    studentListView.getSelectionModel().getSelectedItem().getOthername()+
//                                    studentListView.getSelectionModel().getSelectedItem().getLastname();

                        attendanceTemporaries.clear();
                        attendanceTemporaries.addAll(attendanceTemporaryDao.getStudentAttendance(name));

                        if(attendanceTemporaries !=null){
//                            studentdata.clear();

                        }
                        studentListView.setVisible(Boolean.FALSE);

//                      toggleTableView(studentTableView,salesTableView);
//                        showAttendanceTable();
                        populateStudentTable();
                        System.out.println("This is the total in the list:"+studentdata.size());
                    }
                }
            });
        }catch (NullPointerException e){
            notification.notifyError("An error occurred.","Unknown");
        }
        loadData.setOnAction(event -> {
            loadData();
//           Task task  = new Task() {
//               @Override
//               protected Object call() throws Exception {
//                   loadData();
//                   return null;
//               }
//           };
//           task.setOnRunning( e -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Preparing records"));
//           task.setOnFailed( e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
//           task.setOnSucceeded(e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
//           new Thread(task).start();
        });

        studentTableView.setEditable(true);

        studentTableView.getItems().addListener(new ListChangeListener<AttendanceTemporary>() {
            @Override
            public void onChanged(Change<? extends AttendanceTemporary> c) {
                Alert alert =new Alert(Alert.AlertType.WARNING,"Something changed",ButtonType.OK);
                alert.show();
            }
        });

        allSales.setOnAction(event -> {
            salesdata.clear();
            salesdata.addAll(salesDao.getAllSalesOfStudent());
            populateSalesTable();
        });

        salesTableView.setRowFactory(param -> {
            final TableRow<Sales> row = new TableRow<>();
            row.setOnMouseClicked(event ->{
                if(event.getClickCount()==2){
                    //listening for double click
                    if(!studentTableView.getItems().isEmpty()){
                        makePayment();
                    }
                }
            });
            return row;
        });

        newPayment.setOnAction(event -> {
            makePayment();
        });

        newAttendancePayment.setOnAction(event -> {
            try{
                AttendanceTemporary attendanceTemporary = studentTableView.getSelectionModel().getSelectedItem();
                if(attendanceTemporary !=null){
                    if(!attendanceTemporary.getStudent().getFeedingStatus().equals(Student.FeedingStatus.DAILY)){
                        makeAttendancePayment(attendanceTemporary);
                        populateStudentTable();
                    }else {
                        //show alert  to change the feeding status
                        Alert alert = new Alert(Alert.AlertType.ERROR,"",ButtonType.OK);
                        alert.setTitle("Invalid Status");
                        alert.setHeaderText("Payment cannot be completed for this student.");
                        alert.setContentText("If you want to continue, you have to change the status of the student to PERIODIC");
                        alert.show();
                    }
                }
            }catch(Exception e){
                Notification.getNotificationInstance().notifyError("You did not select any student","No selection.");
            }
        });

        /**
         * Force attendance sheet  to show.
         * When used can create errors in the attendance table
         */
        attendanceWizard.setOnAction(e->{
            if(termDao.getCurrentTerm().getToday().equals(LocalDate.now())){
                Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.YES,ButtonType.NO);
                alert.setTitle("Confirmation required");
                alert.setHeaderText("Attendance Exist");
                alert.setContentText("There is already attendance for today.\n Showing this form can cause errors in the system.\n" +
                        "Do you still want to continue to show the form?");
                Optional<ButtonType>result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.YES){
                    TextInputDialog inputDialog = new TextInputDialog();
                    inputDialog.setHeaderText("Enter your username to continue");
                    Optional<String>ans = inputDialog.showAndWait();
                    if(ans.isPresent() && ans.get().equals(LoginFormController.user.getUsername()))
                        showCreateAttendaceForm();
                    else Notification.getNotificationInstance().notifyError("Your credentials to not match any.","Access denied");
                }
            }else
                showCreateAttendaceForm();
        });

        sortedList.addListener(new ListChangeListener<AttendanceTemporary>() {
            @Override
            public void onChanged(Change<? extends AttendanceTemporary> c) {
                applyTableStyle();
            }
        });

        viewDetails.setOnAction(e -> {
           toggleFeedingDetails();
        });

        //listen to the details check boxes
        debtCheck.setOnAction(e-> {
            if(debtCheck.isSelected()){
                totalDebt.setVisible(true);
            }else totalDebt.setVisible(false);
        });

        creditCheck.setOnAction(e-> {
            if(creditCheck.isSelected()){
                totalCredit.setVisible(true);
            }else totalCredit.setVisible(false);
        });

        feedingCheck.setOnAction(e-> {
            if(feedingCheck.isSelected()){
                totalFeeding.setVisible(true);
            }else totalFeeding.setVisible(false);
        });
    }

    private void loadData() {
//        studentTableView.getItems().clear();
        attendanceTemporaries.clear();
        attendanceTemporaries.addAll(attendanceTemporaryDao.getTempAttendance());
        if(!attendanceTemporaries.isEmpty()){
            populateStudentTable();
        }else{
            notification.notifyError("No attendance found. Make sure you have created a new attendance sheet","Empty");
            showCreateAttendaceForm();
            showDrawer();
        }
    }

    private void toggleFeedingDetails() {
        if(fieldsBox.isVisible()){
            fieldsBox.setVisible(Boolean.FALSE);

        }else {
            fieldsBox.setVisible(Boolean.TRUE);
        }
    }

    private void applyTableStyle() {
        owing.setCellFactory(column -> new TableCell<AttendanceTemporary, String>() {
            @Override
            protected void updateItem(String bal, boolean empty) {
                super.updateItem(bal, empty);

                setText(empty ? "" : getItem().toString());
                setGraphic(null);

                TableRow<AttendanceTemporary> currentRow = getTableRow();

                if (!isEmpty()  && !currentRow.isEmpty()) {
                    if(Double.valueOf(bal)<0 )
                        currentRow.setStyle("-fx-background-color:#ff6a51");
                    else if(Double.valueOf(bal)>0 ){
                        currentRow.setStyle("-fx-background-color:#0ecc31");
                    }
                    else {
                        currentRow.setStyle("");
//                            currentRow.setStyle("-fx-background-color:#fff");
                    }
                }else {
                    currentRow.setStyle("");
                }
            }
        });

    }

    private void makeAttendancePayment(AttendanceTemporary attendanceTemporary) {
        if(attendanceTemporary != null){
//            TextInputDialog textInputDialog  = new TextInputDialog();
//            textInputDialog.setTitle("Add Feeding Fee");
//            textInputDialog.setHeaderText("Add Feeding Fee");
//            textInputDialog.setContentText("How much is the student paying?");
//
//            TextField text =new TextField();
//            text.setPromptText("who paid?");
//            textInputDialog.getDialogPane().getChildren().add(text);
            Optional <Pair<String,String>> result = showCustomTextInputDiag(attendanceTemporary.getStudent());

            result.ifPresent(pair ->{
                Double amount = Double.valueOf(pair.getKey());
                attendanceTemporary.getStudent().addNewFeedingFeeCredit(amount);
                studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());

                Utils.logPayment(attendanceTemporary.getStudent(),"Feeding Fee Payment for"+" "+
                        attendanceTemporary.getStudent().toString(),pair.getValue(),LocalDate.now(),amount );
            });
//            if(result.isPresent() && result.get() != null) {
//                Double amount = Double.valueOf(result.get());


//            }
        }
    }

//    private void activateListView() {
//        studentListView.getItems().clear();
//        if(name.getText().trim().length()>=1){
//            try
//            {
//                studentdata.clear();
//                studentdata.addAll(studentDao.getStudentByName(name.getText().trim()));
//            }catch (HibernateException e){
//                notification.notifyError("An error occurred while fetching students.\nPlease try again","Error!");
//            }
//            if(!studentdata.isEmpty()){
//                studentListView.setVisible(Boolean.TRUE);
//                studentListView.getItems().addAll(studentdata);
//                studentListView.setCellFactory(param -> new StudentCell());
//                studentdata.clear();
//                salesTableView.getItems().clear();
//            }else{
//                studentListView.setVisible(Boolean.FALSE);
//            }
//        }
//    }

    private void activateListView() {
        name.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredAtt.setPredicate( (Predicate < ? super  AttendanceTemporary>) at ->{
               if( newValue == null || newValue.isEmpty() ) {
                   return  true;
               }

               String lowerVal = newValue.toLowerCase();
                 return  viewAttendanceController.checkIfStudent(lowerVal, at.getStudent());
            });
        }));

        sortedList.comparatorProperty().bind(studentTableView.comparatorProperty());
        studentTableView.setItems(sortedList);

    }

    private void fetchSalesData(Student student) {
        salesdata.clear();
        List<Sales> salesList= salesDao.getAllSalesOfStudent(student);
        if(salesList !=null){
            salesdata.addAll(salesList);
        }
        studentListView.setVisible(Boolean.FALSE);
        showSalesTable();
        populateSalesTable();
    }

    private void makePayment() {
        try{
            TextInputDialog input = new TextInputDialog();
            Sales sales = salesTableView.getSelectionModel().getSelectedItem();
            student= sales.getStudent();
            ImageView imageView =new ImageView();
            Image image =new Image("assets/sale.png");
            imageView.setImage(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(120);
            input.setGraphic(imageView);
            input.setTitle("New Payment");
            input.setContentText("How much is the student paying?");
            String description ="Add Payment for\n"+student.toString()+" "+"["+student.getStage().getName()+"]\n" +
                    "as payment for "+sales.getItem().getName().toUpperCase();
            input.setHeaderText(description);
            Optional<String> result = input.showAndWait();

            if(result.isPresent() && result.get()!=null){
                Alert alert =new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.YES,ButtonType.NO);
                alert.setHeaderText("Are you sure you want to make payment of "+result.get()+"\nfor"+" "+sales.getItem().getName()+"\n");
                Optional<ButtonType>result2 = alert.showAndWait();

                if(result2.isPresent()&& result2.get()==ButtonType.YES){
                    Task payment = new Task() {
                        @Override
                        protected Object call() {
                            salesDao.payForSales(sales,Double.valueOf(result.get()));
                            return null;
                        }
                    };
                    payment.setOnRunning(event -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Updating sales..."));
                    payment.setOnSucceeded(event -> {
                        MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                        Notification.getNotificationInstance().notifySuccess("The record has been updated","success");
                        populateSalesTable();

                        Utils.logPayment(student,description,"",LocalDate.now(),Double.valueOf(result.get()) );
                    });
                    payment.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                    new Thread(payment).start();
                }
            }
        }catch (Exception e){
            //log the error
        }
    }

    private void showSalesTable() {
        studentTableView.setVisible(Boolean.FALSE);
        salesTableView.setVisible(Boolean.TRUE);
        allSales.setVisible(Boolean.TRUE);
        loadData.setVisible(Boolean.FALSE);
        presentLbl.setVisible(Boolean.FALSE);
        totalFeedingPane.setVisible(Boolean.FALSE);
    }
    private void showAttendanceTable(){
        salesTableView.setVisible(Boolean.FALSE);
        studentTableView.setVisible(Boolean.TRUE);
        loadData.setVisible(Boolean.TRUE);
        allSales.setVisible(Boolean.FALSE);
        presentLbl.setVisible(Boolean.TRUE);
        totalFeedingPane.setVisible(Boolean.TRUE);
    }

    private Optional<Pair<String, String>> showCustomTextInputDiag(Student student){
        Dialog<Pair<String,String>> dialog = new Dialog<>();
        dialog.setTitle("Payment");

        //set the buttons
        ButtonType accept = new ButtonType("Make Payment", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(accept, ButtonType.CANCEL);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20,15,10,10));
        gridPane.add(new Label("How much is the student paying? "),0,0);
//        gridPane.add(new Label("Who is making the payment"),2,0);

        Label label = new Label("Who is making the payment");
        RadioButton self = new RadioButton("Student");
        self.setSelected(true);
        RadioButton parent = new RadioButton("Parent");
        RadioButton other = new RadioButton("Other");

        ToggleGroup toggleGroup = new ToggleGroup();

        self.setToggleGroup(toggleGroup);
        parent.setToggleGroup(toggleGroup);
        other.setToggleGroup(toggleGroup);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(self,parent,other);
        hBox.setSpacing(10);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(label,hBox);
        vBox.setSpacing(10);

        gridPane.add(vBox,2,0);

        TextField amount = new TextField();
        amount.setPromptText("Amount to pay");

        TextField paidBy = new TextField();
        paidBy.setText(student.toString());
        paidBy.setPromptText("Paid by");
        gridPane.add(amount,0,1);
        gridPane.add(paidBy,2,1);

        dialog.getDialogPane().setContent(gridPane);

        //Request focus on the first text field
        Platform.runLater(() -> amount.requestFocus());

        //add listener to toggle
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue==self) {
                paidBy.setText(student.toString());
                paidBy.setEditable(false);
            }
            else  if(newValue ==parent ) {
                paidBy.setText(student.getParent().getname());
                paidBy.setEditable(false);
            }
            else if(newValue ==other) {
                paidBy.setText("");
                paidBy.setEditable(true);
            }
        });

        //convert the results to amount and paid-by pair when ok is clicked
        dialog.setResultConverter(dialogbtn ->{
            if(dialogbtn == accept) {
                return  new Pair<>(amount.getText(),paidBy.getText());
            }
            return null;
        });
//        Optional<Pair<String,String>> result = dialog.showAndWait();
//        return result;
        return dialog.showAndWait();
    }

    public  void showDrawer(){
        drawer.setTranslateX(300);
        drawer.setVisible(true);
        drawer.open();
    }

    public void hideDrawer(){

    }
}
