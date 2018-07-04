package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class sale implements Initializable{

    @FXML
    private AnchorPane saleParentPane;

    @FXML
    private JFXRadioButton everyone;

    @FXML
    private ToggleGroup saleType;

    @FXML
    private JFXRadioButton someClass;

    @FXML
    private JFXRadioButton individual;

    @FXML
    private ComboBox<Stage> classCombo;

    @FXML
    private TextField itemName;

    @FXML
    private TextField unitCost;

    @FXML
    private TextField qty;

    @FXML
    private TextField total;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXButton save;

    @FXML
    private ListView<Student> studentListView;

    @FXML
    private TextField studentName;

    private ObservableList<Student>data = FXCollections.observableArrayList();
    private StudentDao studentDao = new StudentDao();
    private SalesDao salesDao =new SalesDao();
    private StageDao stageDao = new StageDao();
            MyProgressIndicator progressIndicator = MyProgressIndicator.getMyProgressIndicatorInstance();
            Notification notification = Notification.getNotificationInstance();

//    private boolean isValidSale(Sales sale){
////        if(sale.getStudent()!= null  && sale.getItem().getCost()>0.0 &&sale.getItem().getQty()>0){
//         return true;
//        }
//        return false;
//    }

    public void populateStudentListView(ObservableList<Student> data,ListView<Student> studentListView){
        studentListView.getItems().clear();
        studentListView.getItems().addAll(data);
        studentListView.setVisible(Boolean.TRUE);
        studentListView.setCellFactory(new Callback<ListView<Student>, ListCell<Student>>() {
            @Override
            public ListCell<Student> call(ListView<Student> param) {
                return new StudentCell();
            }
        });
    }

    private void populateCombo(){
        customizeCombobox();
        StageDao stageDao = new StageDao();
        List<Stage> list =stageDao.getGetAllStage();
        for(Stage s:list){
            classCombo.getItems().add(s);
        }
    }

    private void customizeCombobox(){
        // Define rendering of the list of values in ComboBox drop down.
        classCombo.setCellFactory((comboBox) -> {
            return new ListCell<Stage>() {
                @Override
                protected void updateItem(Stage item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
        });

// Define rendering of selected value shown in ComboBox.
        classCombo.setConverter(new StringConverter<Stage>() {
            @Override
            public String toString(Stage stage) {
                if (stage == null) {
                    return null;
                } else {
                    return stage.getName();
                }
            }

            @Override
            public Stage fromString(String stageString) {
                return null; // No conversion fromString needed.
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

       saleType.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
           @Override
           public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
               if(newValue !=null){
                   if(newValue ==individual){
                       studentName.setVisible(Boolean.TRUE);
                   }else {
                       studentName.setVisible(Boolean.FALSE);
                   }

                   if(newValue==someClass){
                       populateCombo();
                       classCombo.setVisible(Boolean.TRUE);
                   }else {
                       classCombo.setVisible(Boolean.FALSE);
                   }
               }
           }
       });

        saleParentPane.setOnMouseClicked(event -> {
            studentListView.setVisible(Boolean.FALSE);
        });

        studentName.setOnKeyReleased(event -> {
            data.clear();
//            studentListView.getItems().clear();
            studentListView.setVisible(Boolean.FALSE);
            if(studentName.getText().trim().length()>=1){
                data.addAll(studentDao.getStudentByName(studentName.getText().trim()));
            }
        });

        data.addListener(new ListChangeListener <Student>() {
            @Override
            public void onChanged(Change<? extends Student> c) {
                populateStudentListView(data,studentListView);
            }
        });

        studentListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student>() {
            @Override
            public void changed(ObservableValue<? extends Student> observable, Student oldValue, Student newValue) {
                if(newValue !=null){
                    studentName.setText(newValue.getFirstname() + " " +newValue.getLastname() + " - "+newValue.getStage().getName());
                }
            }
        });

        save.setOnAction(event -> {
            if(saleType.getSelectedToggle() ==null){
                Alert alert=new Alert( Alert.AlertType.WARNING,"",ButtonType.OK);
                alert.setHeaderText("You have not selected a sale type.\nPlease select who you are selling the item to.");
                alert.setTitle("Empty selection");
                alert.show();
            }else {
                //create a new sale
                Sales sale = new Sales();
                Item item =new Item();
                item.setCost(Double.valueOf(unitCost.getText().trim()));
                item.setName(itemName.getText().trim());

//                sale.setItem(item);
                item.setQty(Integer.parseInt(qty.getText().trim()));
                sale.setTotalcost(item.getCost() * item.getQty());

//                Alert alert=new Alert( Alert.AlertType.WARNING,"",ButtonType.OK,ButtonType.CANCEL);
//                alert.setHeaderText("You have not selected a sale type.\nPlease select who you are selling the item to.");
//                alert.setTitle("Empty selection");
//                Optional<ButtonType> result= alert.showAndWait();

                Task task =  new Task() {
                    @Override
                    protected Object call() {

                            if(saleType.getSelectedToggle() ==individual){
                                salesDao.createSale(sale,item, studentListView.getSelectionModel().getSelectedItem());
//                                if(salesDao.createSale(sale,studentListView.getSelectionModel().getSelectedItem())){
////                                    notification.notifySuccess("A new sale has been created","success");
//                                }
                            }else if(saleType.getSelectedToggle()==someClass){
                                salesDao.sellToClass(sale,item,classCombo.getSelectionModel().getSelectedItem());
//                                if(salesDao.sellToClass(sale,item,classCombo.getSelectionModel().getSelectedItem())){
//                                    notification.notifySuccess("Sales created for"+classCombo.getSelectionModel().getSelectedItem().getName(),"Success");
//                                }
                            }else if(saleType.getSelectedToggle()==everyone){
                                salesDao.sellToAllStudents(sale,item);
//                                if(salesDao.sellToAllStudents(sale,item))
//                                    notification.notifySuccess("Sales created for everyone","success");
                            }
                            //show confirmation message

                        return null;
                    }
                };
                task.setOnRunning(e -> progressIndicator.showActionProgress("Preparing sales") );
                task.setOnSucceeded(e -> {
                    progressIndicator.hideProgress();
                    notification.notifySuccess("A new sale has been created","success");
                });

                task.setOnFailed(e->{
                    notification.notifyError("Sorry an error occurred while creating sales","Fatal error");
                    task.cancel();
                    progressIndicator.hideProgress();
                });
                new Thread(task).start();
            }
        });
    }
}
