package controller;

import com.hub.schoolAid.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

public class UpdateBillController implements Initializable {
    @FXML
    private TextField tuitionFee;

    @FXML
    private TableView<BillItem> billItemTableview;

    @FXML
    private TableColumn<BillItem, String> itemNameCol;

    @FXML
    private TableColumn<BillItem, String> itemAmount;

    @FXML
    private TextField grossTotal;

    @FXML
    private VBox actionsContainer;

    @FXML
    private Button btnUpdate;

    @FXML
    private VBox updatesLabel;

    @FXML
    private Label changeCounter;

    @FXML
    private Button btnRemove;

    @FXML
    private Button save;

    private Bill bill;
    private Bill updatedBill;
    Double previousTotal = 0.0;
    Boolean hasUpdated = false;

    private ObservableList<BillItem>billItems = FXCollections.observableArrayList();
    private Set<BillItem> updatedItems = new HashSet<BillItem>();
    private int num_of_changes =0;

    public void initBill(Bill newBill) {
        this.bill = newBill;
        billItems.addAll(newBill.getBill_items());
        tuitionFee.setText(bill.getTuitionFee().toString());
        grossTotal.setText(bill.getTotalBill().toString());
        if(bill.getBill_items().size() > 0) {
            populateBillItemTable();
        }
    }

    private void populateBillItemTable() {
        itemNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getItem().getName()));
        itemAmount.setCellValueFactory(param -> {
            if (param.getValue().getCost() < 0) { // check if the value is negative and change it as such
                return  new SimpleStringProperty(String.valueOf((param.getValue().getCost() * -1)));
            } else {
                return new SimpleStringProperty(String.valueOf(param.getValue().getCost()));
            }
        });
        checkBillTotal(bill);
        billItemTableview.setItems(billItems);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        billItemTableview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BillItem>() {
            @Override
            public void changed(ObservableValue<? extends BillItem> observable, BillItem oldValue, BillItem newValue) {
                if(newValue != null) {
                    actionsContainer.setVisible(true);
                } else {
                    actionsContainer.setVisible(false);
                }
            }
        });

        btnUpdate.setOnAction(event -> {
            BillItem billItem = billItemTableview.getSelectionModel().getSelectedItem();
            // ask the user for the new amount for the bill
            TextInputDialog inputDialog = new TextInputDialog(String.valueOf(billItem.getCost()));
            inputDialog.setHeaderText("What is the new amount?");
            inputDialog.setContentText("Please enter the new amount for the bill");
            Optional<String> input = inputDialog.showAndWait();

            Boolean status = Pattern.matches("\\d+","23.0" );

            // make sure the input is valid
            if(input.isPresent() && input.get().trim().length() > 0 ) {
                try {
                    Double amount = Double.valueOf(input.get());
                    if(amount < 0) {  // change the amount to negative
                        billItem.setCost(amount * -1);
                    } else {
                        billItem.setCost(amount);
                    }

                    // check if the item has already been updated
                    if(updatedItems.size() > 0) {
                        Iterator<BillItem> iterator = updatedItems.iterator();
                        BillItem existing = iterator.next();
                        // check if the bill we are about to save is already updated
                        if(existing.getId() == billItem.getId()) {
                            updatedItems.remove(existing);
                            updatedItems.add(billItem);
                        } else {
                            updatedItems.add(billItem);
                            num_of_changes++;
                        }
                    } else {
                        updatedItems.add(billItem);
                        num_of_changes++;
                    }
                    changeCounter.setText(String.valueOf(num_of_changes));
                    showChangeLabel();
                    billItemTableview.refresh();

                    if(updatedBill== null){
                        updatedBill = bill;
                    }
                    checkBillTotal(updatedBill);

                    Notification.getNotificationInstance().notifySuccess("Bill item has been updated", "Success");
                } catch (NumberFormatException e) {
                    Notification.getNotificationInstance().notifyError("Please enter only numbers", "Wrong input");
                }
            } else {
                Notification.getNotificationInstance().notifyError("Invalid input. Please check and try again", "Input error");
            }
        });

        save.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Confirm save");
            alert.setHeaderText("Are you sure you want to save the changes?");
            alert.setContentText("Select YES to continue or NO to cancel");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.YES) {
                updatedBill = bill;
                // get the new items from the table view and set the records
                int size = bill.getBill_items().size();
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        BillDao billDao = new BillDao();
                        // get the new items from the table view and set the records
                        for (int i=0; i< size; i++) {
                            if(bill.getBill_items().get(i).getId() == billItemTableview.getItems().get(i).getId()) {
                                updatedBill.getBill_items().get(i).setCost(billItemTableview.getItems().get(i).getCost());
                            }
                        }
                        checkBillTotal(updatedBill);
                        billDao.updateBill(updatedBill,previousTotal);
                        return null;
                    }
                };
                task.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Updating bill"));
                task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                task.setOnSucceeded(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                new Thread(task).start();
            }
        });
    }

    private void checkBillTotal(Bill bill) {
        Double total = 0.0;
        for (BillItem i: bill.getBill_items()) {
            if(i.getCost() < 0) {
                total+= (i.getCost()*-1);
            } else {
                total+=i.getCost();
            }
        }
        total+=bill.getTuitionFee();
        grossTotal.setText(String.valueOf(total));
        bill.setTotalBill(total * -1);
        if(!hasUpdated) {
            previousTotal = (total);
            hasUpdated=true;
        }
    }

    private void showChangeLabel() {
        updatesLabel.setVisible(true);
    }

    private void hideChangeLabel() {
        updatesLabel.setVisible(false);
    }
}
