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
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckListView;

import java.net.URL;
import java.util.Optional;
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
    private TableView<BillItem> itemsTableview;

    @FXML
    private TableColumn<BillItem, String> itemCol;

    @FXML
    private TableColumn<BillItem, String> costCol;

    @FXML
    private AnchorPane addItemPane;

    @FXML
    private TextField itemAmount;

    @FXML
    private Button canceltem;

    @FXML
    private Button saveItem;

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
    private ComboBox<Item> itemsCombo;

    @FXML
    private ToggleGroup studentToggle;

    @FXML
    private Hyperlink addItem;

    @FXML
    private TextField startYear;

    @FXML
    private TextField endYear;

    @FXML
    private ComboBox<Term> termsCombo;


    @FXML
    private ProgressIndicator stageProgess = new ProgressIndicator();

    private ObservableList<Stage>stages = FXCollections.observableArrayList();
    private ObservableList<Stage>selectedStages = FXCollections.observableArrayList();
    private ObservableList<Student>students = FXCollections.observableArrayList();
    private ObservableList<Student>selectedStudents= FXCollections.observableArrayList();
    private ObservableList<BillItem> billItems = FXCollections.observableArrayList();
    private ObservableList<Item>items = FXCollections.observableArrayList();
    private ObservableList<Term>terms = FXCollections.observableArrayList();
    private TermDao termDao = new TermDao();
    private JFXRadioButton radioButton;

    Bill bill ;
    BillDao billDao = new BillDao();
    ItemDao itemDao = new ItemDao();
    Item selectedItem = null;

    private String nameOfItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTerm();
        addNewItem.setOnAction(event -> {
            addItemPane.setVisible(!addItemPane.isVisible());

            // load the items in the combo box
            if(items.isEmpty()) {
                items.addAll(itemDao.getItem());
            }

            itemsCombo.setItems(items);
        });

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

        billItems.addListener(new ListChangeListener<BillItem>() {
            @Override
            public void onChanged(Change<? extends BillItem> c) {
                checkBillTotal();
            }
        });

        itemsCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
            @Override
            public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
                if(newValue != null) {
                    selectedItem = newValue;
                }
            }
        });
        classCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Stage>() {
            @Override
            public void onChanged(Change<? extends Stage> c) {
                selectedStages = classCombo.getCheckModel().getCheckedItems();
            }
        });

        saveItem.setOnAction(event -> {
            if(createBillItem()) {
                Notification.getNotificationInstance().notifySuccess("Bill item added", "success");
                clearBillFields();
            }
        });

        saveBill.setOnAction(event -> {
            if(confirmCreateAction()) {
                // check the selected option
                if(studentToggle.getSelectedToggle() != null) {
                    if(studentToggle.getSelectedToggle() == allRadio) {
                        // create bill for every student
                        try {
                            bill = prepareBill();
                            if(isValid(bill)){
                                billDao.createBill(bill);
                            } else {
                                Notification.getNotificationInstance().notifyError("Bill is not valid", "Error");
                            }
                        } catch (Exception e) {
                            Notification.getNotificationInstance().notifyError("Sorry an error occurred", "Error");
                        }
                    } else if(studentToggle.getSelectedToggle() == classRadio) {
                        // create bill for a class
                        bill = prepareBill();
                        if(isValid(bill)) {
                            billDao.createBill(bill, selectedStages);
                        }
                    } else if (studentToggle.getSelectedToggle() == studentToggle) {
                        // create a bill for selected students
                    }
                } else Notification.getNotificationInstance().notifyError("Select an option to create the bill for", "Error");
            }
        });

        addItem.setOnAction(event -> {
            TextInputDialog inputDialog = new TextInputDialog();
            inputDialog.setHeaderText("You are about to create a new item for a bill");
            inputDialog.setTitle("Add new item");
            inputDialog.setContentText("Add the name of the item that you want to create");
            Optional<String> result = inputDialog.showAndWait();
            if(result.isPresent() && result.get().trim().length() > 0) {
                nameOfItem = result.get();
                // save the item to the database
                ItemDao itemDao = new ItemDao();
                Item item = new Item();
                item.setName(nameOfItem);
                Item newItem = itemDao.createItem(item);
                if(newItem != null) {
                    items.add(newItem);

                    // set the item as the current selection
                    itemsCombo.getSelectionModel().select(newItem);
                    selectedItem = newItem;
                }
            }
        });

        cancel.setOnAction(event -> Utils.closeEvent(event));
    }

    private Bill prepareBill() {
        bill = new Bill();
        if(tuitionFee.getText().trim().length() > 0) {
            bill.setTutitionFee(Double.valueOf(tuitionFee.getText().trim()));
        }
        TermDao termDao = new TermDao();
        Term term = termDao.getCurrentTerm();
        bill.setCreatedBy(term.getValue());
        bill.setCreatedFor(term.getValue() + 1);

        // check if there are other items added to the bill
        if(billItems.size() > 0) {
           bill.setBill_items(billItems);
        }
        bill.setTotalBill(checkBillTotal());
        String year = startYear.getText().trim()+"/"+endYear.getText().trim();

        if(termsCombo.getSelectionModel().getSelectedItem()  != null) {
            bill.setCreatedFor(termsCombo.getSelectionModel().getSelectedItem().getValue());
        }
        bill.setAcademicYear(year);
        return bill;
    }

    private Boolean createBillItem(){
        // check if the fields are valid
        if(itemAmount.getText().trim().length() > 0 && selectedItem != null) {
            BillItem bill_item = new BillItem();
            bill_item.setCost(Double.valueOf(itemAmount.getText().trim()));
            bill_item.setItem(selectedItem);
            billItems.add(bill_item);
            populateBillTableview();
            return  true;
        }
        return  false;
    }

    private void clearBillFields() {
        itemAmount.clear();
    }

    private Double checkBillTotal() {
        Double total = 0.0;
        Double tuition =0.0;

        // check if the tuition fee has been provided
        if(tuitionFee.getText().trim().length() > 0) {
            tuition = Double.valueOf(tuitionFee.getText().trim());
        }

        // get the cost of all the items
        for(BillItem t: billItems) {
            total+= t.getCost();
        }

        // add the tuition fee
        total += tuition;
        totalBill.setText(total.toString());
        return total;
    }

    private void populateBillTableview() {
        itemCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItem().getName()));
        costCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCost().toString()));
        itemsTableview.setItems(billItems);
    }

    public Boolean confirmCreateAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Add new Bill");
        alert.setHeaderText("You are about to create a new bill.");
        alert.setContentText("Are you sure you want to continue? ");
        Optional<ButtonType>result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.YES) {
            return true;
        } else return false;
    }

    private Boolean isValid(Bill bill) {
        if(bill.getTotalBill() == null || bill.getBill_items()== null ||  (bill.getTotalBill() == 0 && bill.getBill_items().isEmpty())) {
            return false;
        }

        if(studentToggle.getSelectedToggle() == classRadio) {
            if(selectedStages.isEmpty()){
                return false;
            }
        }

        if(startYear.getText().trim().length() < 1){
            return false;
        }

        if(endYear.getText().trim().length()< 1) {
            return false;
        }

        if(termsCombo.getSelectionModel().getSelectedItem() == null) {
            return false;
        }

        // validate the start and end to pick only figures

        return true;
    }

    private void setTerm(){
        if(terms.isEmpty()) {
            terms.addAll(termDao.getTerm());
            termsCombo.setItems(terms);
        }
    }
}
