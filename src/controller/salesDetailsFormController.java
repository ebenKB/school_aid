package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXRadioButton;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.hibernate.HibernateException;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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
    private TableColumn<?, ?> dateCol;

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
    private TableColumn<AttendanceTemporary, String> feedingType;

    @FXML
    private TableColumn<AttendanceTemporary, String> feeding;

    @FXML
    private TableColumn<AttendanceTemporary, String> isPresentCol;

    @FXML
    private TableColumn<AttendanceTemporary, String> statusCol;

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

    private Student student =new Student();
    public StudentDao studentDao =new StudentDao();
    private SalesDao salesDao =new SalesDao();
    private TermDao termDao   =new TermDao();
    private Notification notification =Notification.getNotificationInstance();
    private AttendanceDao attendanceDao = new AttendanceDao();
    public AttendanceTemporaryDao attendanceTemporaryDao= new AttendanceTemporaryDao();
    private ObservableList<Sales> salesdata = FXCollections.observableArrayList();
    public ObservableList<Student> studentdata = FXCollections.observableArrayList();
    public ObservableList<Student> attendanceData = FXCollections.observableArrayList();
    public ObservableList<AttendanceTemporary> attendanceTemporaries = FXCollections.observableArrayList();
    private Image successImg = new Image("/assets/success.png");
    ImageView imageView;
    private SimpleIntegerProperty presentCounter= new SimpleIntegerProperty();
//    private int absentCounter = 0;


    public void populateStudentTable(){

        tagCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttendanceTemporary, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AttendanceTemporary, String> param) {
                return new SimpleStringProperty(param.getValue().getStudent().getPayFeeding() ? "YES":"NO");
            }
        });

        feedingType.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttendanceTemporary, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AttendanceTemporary, String> param) {
                if(param.getValue().getStudent().getFeedingStatus()== Student.FeedingStatus.DAILY)
                    param.getTableColumn().setStyle("-fx-background-color:blue");
                return new SimpleStringProperty(param.getValue().getStudent().getFeedingStatus().toString());
            }
        });
        studentNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttendanceTemporary, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AttendanceTemporary, String> param) {
                return new SimpleStringProperty(param.getValue().getStudent().getFirstname() +" "+
                        param.getValue().getStudent().getOthername()+ " " + param.getValue().getStudent().getLastname());
            }
        });

        feeding.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttendanceTemporary, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AttendanceTemporary, String> param) {
                if( !param.getValue().getStudent().getPayFeeding())
                    return new SimpleStringProperty("0");
                return new SimpleStringProperty(param.getValue().getFeedingFee()>0 ? param.getValue().getFeedingFee().toString():param.getValue().getStudent().getStage().getFeeding_fee().toString());
//                String buttonText = item ? "Check out" : "Check in";
            }
        });

        feeding.setCellFactory(TextFieldTableCell.forTableColumn());
        feeding.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AttendanceTemporary, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<AttendanceTemporary, String> event) {
                Student st =event.getRowValue().getStudent();
                if(st.getFeedingStatus() == Student.FeedingStatus.DAILY && (Double.valueOf(event.getNewValue()) > st.getAccount().getFeedingFeeToPay())){
                    WindowsSounds.playWindowsSound();
                    populateStudentTable();
                    return;
                }
                    event.getTableView().getItems().get(event.getTablePosition().getRow()).setFeedingFee(Double.valueOf(event.getNewValue()));
                    populateStudentTable();
            }
        });
        statusCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttendanceTemporary, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AttendanceTemporary, String> param) {
//                return new SimpleStringProperty(String.valueOf(param.getValue().getStudent().getStage().getFeeding_fee()));
                return new SimpleStringProperty(param.getValue().isPresent() ? "PAID" :"NOT PAID");
            }
        });

        classNameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttendanceTemporary, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AttendanceTemporary, String> param) {
                return new SimpleStringProperty(param.getValue().getStudent().getStage().getName());
            }
        });

        isPresentCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AttendanceTemporary, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<AttendanceTemporary, String> param) {
                return new SimpleStringProperty(param.getValue().isPresent() ? "PRESENT" : "ABSENT");
            }
        });

        addButtonToTable(actionCol);
        studentTableView.setItems(attendanceTemporaries);
        Double total = 0.0;
        //update the present counter
        presentCounter.setValue(0);
        for(AttendanceTemporary at: attendanceTemporaries){
            if(at.isPresent()){
                presentCounter.set(presentCounter.getValue()+1);
                total+=at.getFeedingFee();
            }
        }
        totalFeeding.setText(total.toString());
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
            btn.setDisable(Boolean.FALSE);
            btn2.setDisable(Boolean.TRUE);
        }else if(btn2.isDisabled()){
            btn2.setDisable(Boolean.FALSE);
            btn.setDisable(Boolean.TRUE);
        }
    }
    private void addButtonToTable(TableColumn column) {

        Callback<TableColumn<Student, Void>, TableCell<Student, Void>> cellFactory = new Callback<TableColumn<Student, Void>, TableCell<Student, Void>>() {
            @Override
            public TableCell<Student, Void> call(final TableColumn<Student, Void> param) {
                TableCell<Student, Void> cell = new TableCell<Student, Void>() {

                    //create an action button
                    public Button btn  = new Button("Check in");
                    public Button btn2 = new Button("Checkout");
                    public HBox hBox   = new HBox();

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Student st = studentTableView.getItems().get(getIndex()).getStudent();
//                          toggleButton(btn,getIndex());
                            //check if the student has balance
                            if(st.getFeedingStatus() != Student.FeedingStatus.DAILY){
                                if(st.getAccount().getFeedingFeeCredit()<=0){
                                    Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.YES,ButtonType.NO);
                                    alert.setHeaderText("NO BALANCE");
                                    alert.setContentText("This student does not have any balance in their Account\nDo you still want to mark the student present?");
                                    Optional<ButtonType>result = alert.showAndWait();
                                    if(result.isPresent() && result.get() ==ButtonType.NO)
                                        return;
                                }
                            }
                            attendanceTemporaryDao.markPresent(studentTableView.getItems().get(getIndex()));
                            presentCounter.set(presentCounter.getValue()+1);
                            notification.notifySuccess("Student has been marked present","Present");
                            toggleButtons(btn,btn2);
                            populateStudentTable();
//                            studentTableView.getItems().set(getIndex(),attendanceTemporaryDao.getAttendance(studentTableView.getItems().get(getIndex()).getId()));
                        });

                        btn2.setOnAction(event -> {
                            presentCounter.set(presentCounter.getValue()-1);
                            attendanceTemporaryDao.markAbsent(studentTableView.getItems().get(getIndex()));
                            notification.notifyError("You have marked this student absent","Absent");
                            toggleButtons(btn,btn2);
                            populateStudentTable();
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            //initialize the button properties
                            hBox.getChildren().clear();
                            hBox.getChildren().addAll(btn,btn2);
                            btn.setTextFill(Color.GREEN);
                            btn2.setTextFill(Color.RED);

                            if(studentTableView.getItems().get(getIndex()).isPresent()){
                                btn.setDisable(Boolean.TRUE);
                            }else{
                               btn2.setDisable(Boolean.TRUE);
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

    private void populateSalesTable(){
        infoLabel.setText("");
        if(! salesdata.isEmpty()){
//            nameCol.setVisible(Boolean.FALSE);
            nameLable.setText(salesdata.get(0).getStudent().toString());
           nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
               @Override
               public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                   Sales sales = param.getValue();
                   return new SimpleStringProperty(sales.getStudent().toString());
               }
           });
           itemCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
               @Override
               public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                   Sales sales = param.getValue();
                   return new SimpleStringProperty(sales.getItem().getName());
               }
           });

          unitPriceCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
              @Override
              public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                Sales sales =param.getValue();
                  return new SimpleStringProperty(sales.getItem().getCost().toString());
              }
          });

          qtyCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
              @Override
              public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                Sales sales  = param.getValue();
                  return new SimpleStringProperty(String.valueOf(sales.getItem().getQty()).toString());
              }
          });

          amntPaidCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
              @Override
              public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                  if(param.getValue().getAmountPaid()==param.getValue().getTotalcost())
                      amntPaidCol.setStyle("-fx-background-color:green");
                  else
                      amntPaidCol.setStyle("-fx-background-color:red");
                  return new SimpleStringProperty(String.valueOf(param.getValue().getAmountPaid()));
              }
          });
          totalCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
              @Override
              public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                  Sales sales  = param.getValue();
                  return new SimpleStringProperty(sales.getTotalcost().toString());
              }
          });

          balCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
              @Override
              public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                  Sales sales  = param.getValue();
                  Double bal = sales.getItem().getCost() - sales.getAmountPaid();
                  return new SimpleStringProperty(bal.toString());
              }
          });
           salesTableView.setItems(salesdata);
//           salesdata.clear();
       }else {
            infoLabel.setText("There is no sale for this student..");
        }
    }

    /**
     * this method takes two table views, and then shows the first and hides the second
     * @param tableView1 the table view that you want to see
     * @param tableView2 the table view that you want to hide.
     */

//    private void toggleTableView(TableView tableView1,TableView tableView2){
//        if(!tableView1 .isVisible()){
//            tableView1.setVisible(Boolean.TRUE);
//            tableView2.setVisible(Boolean.FALSE);
//        }
//    }

    /**
     * this method switches between two the sales table view and the student table view
     */
    private void toggleTableView(){
        if (salesTableView.isVisible()){
            salesTableView.setVisible(false);
            studentTableView.setVisible(Boolean.TRUE);
            loadData.setVisible(Boolean.TRUE);
            allSales.setVisible(Boolean.FALSE);
        }else{
            studentTableView.setVisible(Boolean.FALSE);
            salesTableView.setVisible(Boolean.TRUE);
            loadData.setVisible(Boolean.FALSE);
            allSales.setVisible(Boolean.TRUE);
        }
    }

    /**
     * this method shows the form that is used to create a new attendance
     */
    public void showCreateAttendaceForm(){
        javafx.scene.Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/newAttendanceSheetForm.fxml"));
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

    public void initStudentTable(){

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
        presentCounter.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               presentLbl.setText(presentCounter.getValue().toString());
            }
        });

        radioToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
           @Override
           public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
//               toggleTableView();
               if(newValue==salesRadio){
                   showSalesTable();
               }else{
                   showAttendanceTable();
               }
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
            studentListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
                @Override
                public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
                    if(studentListView.getSelectionModel().getSelectedItem() !=null){
                        if(radioToggle.getSelectedToggle()==salesRadio){
                          fetchSalesData(studentListView.getSelectionModel().getSelectedItem());
                        }else{
                            //show the attendance table
                            String name = studentListView.getSelectionModel().getSelectedItem().getFirstname();
//                                    studentListView.getSelectionModel().getSelectedItem().getOthername()+
//                                    studentListView.getSelectionModel().getSelectedItem().getLastname();

                            List<Student> students = studentDao.getStudentByName(name);
                            if(students !=null){
                                studentdata.addAll(students);
                            }
                            studentListView.setVisible(Boolean.FALSE);
//                          toggleTableView(studentTableView,salesTableView);
                            showAttendanceTable();
                            populateStudentTable();
                        }
                    }
                }
            });
        }catch (NullPointerException e){
           notification.notifyError("An error occurred.","Unknown");
        }
        loadData.setOnAction(event -> {
            studentTableView.getItems().clear();
            attendanceTemporaries.clear();
            attendanceTemporaries.addAll(attendanceTemporaryDao.getTempAttendance());
            if(!attendanceTemporaries.isEmpty()){
                populateStudentTable();
            }else{
                notification.notifyError("No attendance found. Make sure you have created a new attendance sheet","Empty");
                showCreateAttendaceForm();
            }
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

                        System.out.print("We got a double count...");
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
            salesRadio.setSelected(Boolean.TRUE);
//            name.setText(studentTableView.getSelectionModel().getSelectedItem().getStudent().getFirstname());
        });
    }

    private void activateListView() {
        studentListView.getItems().clear();
        if(name.getText().trim().length()>=1){
            try
            {
                studentdata.clear();
                studentdata.addAll(studentDao.getStudentByName(name.getText().trim()));
            }catch (HibernateException e){
                notification.notifyError("An error occurred while fetching students.\nPlease try again","Error!");
            }
            if(!studentdata.isEmpty()){
                studentListView.setVisible(Boolean.TRUE);
                studentListView.getItems().addAll(studentdata);
                studentListView.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {
                    @Override
                    public ListCell<Student> call(ListView<Student> param) {
                        return new StudentCell();
                    }
                });
                studentdata.clear();
                salesTableView.getItems().clear();
            }else{
                studentListView.setVisible(Boolean.FALSE);
            }
        }
    }

    private void fetchSalesData(Student student) {
        salesdata.clear();
//        List<Sales> salesList= salesDao.getAllSalesOfStudent(studentListView.getSelectionModel().getSelectedItem());
        List<Sales> salesList= salesDao.getAllSalesOfStudent(student);
        if(salesList !=null){
            salesdata.addAll(salesList);
        }
        studentListView.setVisible(Boolean.FALSE);
        showSalesTable();
        populateSalesTable();
    }

    private void makePayment() {
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
        input.setHeaderText("Add Payment for\n"+student.getFirstname()+ " "+student.getLastname()+" "+student.getLastname()+" "+"["+student.getStage().getName()+"]\n" +
                "as payment for "+sales.getItem().getName().toUpperCase());
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
                });
                payment.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                new Thread(payment).start();
            }
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
}
