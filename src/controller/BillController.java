package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckListView;

import java.net.URL;
import java.util.ResourceBundle;

public class BillController implements Initializable {
    @FXML
    private TextField tuitionFee;

    @FXML
    private AnchorPane darkPane;

    @FXML
    private Button cancel;

    @FXML
    private Button saveBill;

    @FXML
    private TextField totalBill;

    @FXML
    private Button addNewItem;

    @FXML
    private TableView<Item> itemsTableview;

    @FXML
    private TableColumn<Item, String> itemCol;

    @FXML
    private TableColumn<Item, String> costCol;

    @FXML
    private AnchorPane addItemPane;

    @FXML
    private TextField itemAmount;

    @FXML
    private Button canceltem;

    @FXML
    private Button saveItem;

    @FXML
    private TextField itemName;

    @FXML
    private JFXRadioButton allRadio;

    @FXML
    private JFXRadioButton classRadio;

    @FXML
    private CheckComboBox<Stage> classCombo;

    @FXML
    private JFXRadioButton studentRadio;

    @FXML
    private CheckListView<Student> studentListview;

    @FXML
    private ToggleGroup studentToggle;

    @FXML
    private ProgressIndicator stageProgess = new ProgressIndicator();

    private ObservableList<Stage>stages = FXCollections.observableArrayList();
    private ObservableList<Stage>selectedStages = FXCollections.observableArrayList();
    private ObservableList<Student>students = FXCollections.observableArrayList();
    private ObservableList<Student>selectedStudents= FXCollections.observableArrayList();
    private ObservableList<Item> billItems = FXCollections.observableArrayList();
    Bill bill = new Bill();
    BillDao billDao = new BillDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addNewItem.setOnAction(event -> addItemPane.setVisible(!addItemPane.isVisible()));

        canceltem.setOnAction(event -> addItemPane.setVisible(false));

        studentToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                // check the type of toggle selected
                if(newValue == classRadio) {
                    classCombo.setVisible(true);
                    studentListview.setVisible(false);
                    // check  if the class list is empty
                    if( stages.isEmpty()) {
                        stageProgess.setVisible(true);
                        // fetch the stages
                        StageDao stageDao = new StageDao();
                        stages.addAll(stageDao.getGetAllStage());
                        classCombo.getItems().addAll(stages);

                        // hide the progess bar
                        stageProgess.setVisible(false);
                    }
                } else if(newValue == studentRadio) {
                    // show the student list view
                    studentListview.setVisible(true);
                    classCombo.setVisible(false);

                    // load students
                    if(studentListview.getItems().size() == 0) {
                        StudentDao studentDao = new StudentDao();
                        studentListview.getItems().addAll(studentDao.getAllStudents());
                    }

                    // check if it is not populated
                } else if(newValue == allRadio) {
                    // create bill for all students
                    classCombo.setVisible(false);
                    studentListview.setVisible(false);
                }
            }
        });

        tuitionFee.setOnKeyReleased(event -> checkBillTotal());

        billItems.addListener(new ListChangeListener<Item>() {
            @Override
            public void onChanged(Change<? extends Item> c) {
                checkBillTotal();
            }
        });

        saveItem.setOnAction(event -> {
            if(createBillItem()) {
                Notification.getNotificationInstance().notifySuccess("Bill item added", "success");
                clearBillFields();
            }
        });

        saveBill.setOnAction(event -> {
            // check the selected option
            if(studentToggle.getSelectedToggle() != null) {
                if(studentToggle.getSelectedToggle() == allRadio) {
                    // create bill for every student
                    try {
                        billDao.createBill(bill);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if(studentToggle.getSelectedToggle() == classRadio) {
                    // create bill for a class
                } else if (studentToggle.getSelectedToggle() == studentToggle) {
                    // create a bill for selected students
                }
            }
        });
    }

    private Bill prepareBill() {
        bill.setTutitionFee(Double.valueOf(tuitionFee.getText().trim()));

        // check if there are other items added to the bill
        if(billItems.size() > 0) {
            bill.setItems(billItems);
        }

        return bill;
    }

    private Boolean createBillItem(){
        // check if the fields are valid
        if(itemAmount.getText().trim().length() > 0 && itemName.getText().trim().length() > 0) {
            Item item = new Item();
            item.setCost(Double.valueOf(itemAmount.getText().trim()));
            item.setName(itemName.getText().trim());
            billItems.add(item);
            populateBillTableview();
            return  true;
        }
        return  false;
    }

    private void clearBillFields() {
        itemName.clear();
        itemAmount.clear();
    }

    private Boolean isValidBill() {
        if(tuitionFee.getText().trim().isEmpty()){
            return false;
        }

        return true;
    }

    private void checkBillTotal() {
        Double total =0.0;
        for(Item t: billItems) {
            total +=t.getCost();
        }

        // add the tuition fee
        total += Double.valueOf(tuitionFee.getText().trim());
        bill.setTotalBill(total);
        totalBill.setText(total.toString());
    }

    private void populateBillTableview() {
        itemCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        costCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCost().toString()));
        itemsTableview.setItems(billItems);
    }
}
