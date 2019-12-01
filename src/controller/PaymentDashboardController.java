package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.tools.corba.se.idl.Util;
import com.sun.tools.corba.se.idl.constExpr.Not;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentDashboardController implements Initializable {

    @FXML
    private Pane separator;

    @FXML
    private ToggleGroup momentToggle;

    @FXML
    private TableView<TransactionLogger> transactionTableview;

    @FXML
    private TableColumn<TransactionLogger, String> typeCol;

    @FXML
    private TableColumn<TransactionLogger, String> amountCol;

    @FXML
    private TableColumn<TransactionLogger, String> transactionBy;

    @FXML
    private TableColumn<TransactionLogger, String> date;

    @FXML
    private TableColumn<TransactionLogger, String> status;

    @FXML
    private TableColumn<TransactionLogger, String>studentCol;

    @FXML
    private JFXRadioButton feedingRadio;

    @FXML
    private ToggleGroup typeToggle;

    @FXML
    private JFXRadioButton feesRadio;

    @FXML
    private JFXRadioButton salesRadio;

    @FXML
    private JFXRadioButton todayRadio;

    @FXML
    private JFXDatePicker fromDate;

    @FXML
    private JFXDatePicker toDate;

    @FXML
    private JFXRadioButton selectDateRadio;

    @FXML
    private Label logsTotal;

    @FXML
    private Label logSize;

    @FXML
    private Button getRecords;

    @FXML
    private VBox dateOptions;

    @FXML
    private Label infoLabel;

    private ObservableList<TransactionLogger>schoolFees = FXCollections.observableArrayList();
    private ObservableList<TransactionLogger>feedingFee = FXCollections.observableArrayList();
    private ObservableList<TransactionLogger>sales = FXCollections.observableArrayList();
    LocalDate logDate = LocalDate.now();
    LocalDate prevFrom= null;
    LocalDate prevTo = null;
    String msgA= "Showing all transactions ";
    String msgB = "for today. "+ Utils.formatDate(LocalDate.now(), false);

    private TransactionLoggerDao transactionLoggerDao = new TransactionLoggerDao();

    public void init() {
        feedingFee.addAll(transactionLoggerDao.getLog(LocalDate.now(), TransactionType.FEEDING_FEE));
        populateTableview(TransactionType.FEEDING_FEE);
        infoLabel.setText(msgA + msgB);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        feedingFee.addListener(new ListChangeListener<TransactionLogger>() {
            @Override
            public void onChanged(Change<? extends TransactionLogger> c) {
                checkTotal();
            }
        });

        schoolFees.addListener(new ListChangeListener<TransactionLogger>() {
            @Override
            public void onChanged(Change<? extends TransactionLogger> c) {
                checkTotal();
            }
        });

        sales.addListener(new ListChangeListener<TransactionLogger>() {
            @Override
            public void onChanged(Change<? extends TransactionLogger> c) {
                checkTotal();
            }
        });

        momentToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue != null && newValue == selectDateRadio){
                    dateOptions.setVisible(true);
                } else {
                    dateOptions.setVisible(false);
                }
            }
        });

//        momentToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
//            @Override
//            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
//               if(newValue == selectDateRadio) {
//                    LocalDate to = toDate.getValue();
//                    LocalDate from = fromDate.getValue();
//
//                    // check the type of data fo fetch
//                   if(typeToggle.getSelectedToggle() != null) {
//                       if(typeToggle.getSelectedToggle() == feedingRadio) {
//                           // fetch feeding fee logs
//                           if(feedingFee.isEmpty()) {
//
//                           }
//                       } else if(typeToggle.getSelectedToggle() == schoolFees) {
//                           // fetch school fees
//
//                       } else  if(typeToggle.getSelectedToggle() == sales) {
//                           // fetch sales records
//                       }
//                   }
//               }
//            }
//        });

        getRecords.setOnAction(event -> {
            // check the type of record to fetch
            if(typeToggle.getSelectedToggle() == feedingRadio) { // fetch records for feeding fee
                fetchData(feedingFee, TransactionType.FEEDING_FEE);
                populateTableview(TransactionType.FEEDING_FEE);
            } else if(typeToggle.getSelectedToggle() == feesRadio) {
                fetchData(schoolFees, TransactionType.SCHOOL_FEES);
                populateTableview(TransactionType.SCHOOL_FEES);
            } else if(typeToggle.getSelectedToggle() == salesRadio) {
                fetchData(sales, TransactionType.SALES);
                populateTableview(TransactionType.SALES);
            }

            // set the information label
            if(momentToggle.getSelectedToggle() == selectDateRadio) {
                infoLabel.setText("Showing transactions sorted from "+ Utils.formatDate(fromDate.getValue(), false)+ " to "+ Utils.formatDate(toDate.getValue(), false));
            } else {
                infoLabel.setText(msgA + msgB);
            }
        });
    }

    private void fetchData(ObservableList<TransactionLogger> container, TransactionType type) {
        if(momentToggle.getSelectedToggle() == selectDateRadio) {
            /**
             * This algorithm checks to make sure that data is not fetched unnecessarily form the database.
             * It fetches records only when the search params have changed
             */
//            if((container.isEmpty()) || (prevFrom == null ) || (prevTo== null) || (prevFrom.compareTo(fromDate.getValue()) != 0) || (prevTo.compareTo(toDate.getValue()) != 0)) {
                // check if the search is date range
                if(fromDate.getValue()!= null && toDate.getValue() != null) {
                    // search by a range
                    List<TransactionLogger> data = transactionLoggerDao.getLog(fromDate.getValue(), toDate.getValue(), type);
                    if(!data.isEmpty()) {
                        container.clear();
                        container.addAll(data);
                    }
                } else if(fromDate.getValue() != null) {
                    // search by only from date
                    List<TransactionLogger> data = transactionLoggerDao.getLog(fromDate.getValue(), type);
                    if(!data.isEmpty()) {
                        container.clear();
                        container.addAll(data);
                    }
                } else {
                    // the date is not valid
                    Notification.getNotificationInstance().notifyError("Please select the right date", "Error");
                }
                prevFrom = fromDate.getValue();
                prevTo = toDate.getValue();
//            }
        } else {
            // fetch records for today
//            if(container.isEmpty()) {
                container.clear();
                container.addAll(transactionLoggerDao.getLog(LocalDate.now(), type));
//            }
        }
    }

    private void populateTableview(TransactionType type) {
        // set the table columns
        studentCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().toString()));
        typeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTransactionType().toString()));
        amountCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getAmount())));
        transactionBy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaidBy()));
        date.setCellValueFactory(param -> new SimpleStringProperty(Utils.formatDate(param.getValue().getDate(), true)));
        status.setCellValueFactory(param -> {
            String msg ="";
            if(param.getValue().getStatus() == 1) {
                msg = "ACTIVE";
            } else if(param.getValue().getStatus() ==0) {
                msg = "DELETED";
            } else if(param.getValue().getStatus() == -1) {
                msg ="UPDATED";
            } else {
                msg = "N/A";
            }
            return new SimpleStringProperty(msg);
        });

        // check the type of date to send to the table view
        if(type == TransactionType.FEEDING_FEE) { // it is feeding fee
            if(! feedingFee.isEmpty()) {
                transactionTableview.setItems(feedingFee);
            } else {
                transactionTableview.getItems().clear();
            }
        } else if(type == TransactionType.SALES) {
            if(!sales.isEmpty()) {
                transactionTableview.setItems(sales);
            } else {
                transactionTableview.getItems().clear();
            }
        } else if (type == TransactionType.SCHOOL_FEES) {
            if(!schoolFees.isEmpty()) {
                transactionTableview.setItems(schoolFees);
            } else {
                transactionTableview.getItems().clear();
            }
        }
        checkTotal();
    }
//    public void fetchLogs(TransactionType type) {
//        if(type  == TransactionType.FEEDING_FEE) {
//            // check if the feeding fee is empty
//            if(feedingFee.isEmpty()) {
//                feedingFee.addAll(transactionLoggerDao.get)
//            }
//        }
//    }

    private void checkTotal() {
        Double total = 0.0;
        if(typeToggle.getSelectedToggle() == feedingRadio) {
            for(TransactionLogger t : feedingFee) {
                total+=t.getAmount();
            }
        } else if(typeToggle.getSelectedToggle() == feesRadio) {
            for(TransactionLogger t : schoolFees) {
                total+=t.getAmount();
            }

        } else if(typeToggle.getSelectedToggle() == sales) {
            for(TransactionLogger t : sales) {
                total+=t.getAmount();
            }
        }

        logsTotal.setText(total.toString());
        logSize.setText(String.valueOf(feedingFee.size()));
    }
}
