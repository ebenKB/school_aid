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
import javafx.scene.layout.VBox;
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

    @FXML
    private Label infoLabel;

    @FXML
    private VBox feesVBox;

    @FXML
    private RadioButton payFees;

    @FXML
    private ToggleGroup fees;

    @FXML
    private RadioButton noFees;

    private ObservableList<Student>data = FXCollections.observableArrayList();
    private StudentDao studentDao = new StudentDao();
    private SalesDao salesDao = new SalesDao();
    private StageDao stageDao = new StageDao();
    private List<Stage> list  = null;
            MyProgressIndicator progressIndicator = MyProgressIndicator.getMyProgressIndicatorInstance();
            Notification notification = Notification.getNotificationInstance();

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
        if(list == null){
            list = stageDao.getGetAllStage();
        }
        for(Stage s:list){
            classCombo.getItems().add(s);
        }
    }

    private void customizeCombobox(){
        // Define rendering of the list of values in ComboBox drop down.
        CustomCombo.getInstance().overrideCombo(classCombo);

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

                   if(newValue == everyone){
                       feesVBox.setVisible(Boolean.TRUE);
                   }else {
                       feesVBox.setVisible(Boolean.FALSE);
                   }
               }
           }
       });

        saleParentPane.setOnMouseClicked(event -> {
            studentListView.setVisible(Boolean.FALSE);
        });

        //change implementation to filter a list view
        studentName.setOnKeyReleased(event -> {
            data.clear();
            studentListView.setVisible(Boolean.FALSE);
            if(studentName.getText().trim().length()>=1){
                data.addAll(studentDao.getStudentByName(studentName.getText().trim()));
            }
        });

        data.addListener((ListChangeListener<Student>) c -> populateStudentListView(data,studentListView));

        studentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue !=null){
                studentName.setText(newValue.getFirstname() + " " +newValue.getLastname() + " - "+newValue.getStage().getName());
            }
        });

        unitCost.setOnKeyReleased(e-> showCost());

        qty.setOnKeyReleased(e-> showCost());

        save.setOnAction(event -> {
            if(isValid() && itemName.getText().length()>2){
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

                    item.setQty(Integer.parseInt(qty.getText().trim()));
                    sale.setTotalcost(item.getCost() * item.getQty());

                    Task task =  new Task() {
                        @Override
                        protected Object call() {

                            if(saleType.getSelectedToggle() ==individual){
                                salesDao.createSale(sale,item, studentListView.getSelectionModel().getSelectedItem());

                            }else if(saleType.getSelectedToggle()==someClass){
                                salesDao.sellToClass(sale,item,classCombo.getSelectionModel().getSelectedItem());

                            }else if(saleType.getSelectedToggle()==everyone){
                                if(payFees.isSelected()) {
                                    salesDao.sellToAllStudents(sale,item,true);
                                }else if(noFees.isSelected()){
                                    salesDao.sellToAllStudents(sale, item, false);
                                }
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
            }else{
                //show message - fields are empty
                showError();
            }
        });
    }

    private void showError(){
        infoLabel.setText("Fields are empty or you have entered an invalid character.");
    }

    private void clearError(){
        infoLabel.setText("");
    }
    private void showCost() {
        if(isValid()){
           clearError();
            Double sum = Double.valueOf(unitCost.getText().trim()) * Integer.parseInt(qty.getText().trim());
            total.setText(sum.toString());
        }else {
            total.clear();
            if(unitCost.getText().trim().length()>0 && qty.getText().trim().length()>0)
                showError();
        }
    }

    public Boolean isValid(){
        if(everyone.isSelected() && fees.getSelectedToggle() == null)
            return false;

        if(qty.getText().trim().length()>0 && unitCost.getText().trim().length()>0)
            if(((unitCost.getText().trim().length()>0 && unitCost.getText().trim().matches("\\d+\\.{1}\\d+"))||(unitCost.getText().trim().matches("\\d+")))
                    &&((qty.getText().trim().matches("\\d+"))))
                 return  true;
        return false;
    }

    private void toggleFeeOption() {
        feesVBox.setVisible(!feesVBox.isVisible());
    }
}
