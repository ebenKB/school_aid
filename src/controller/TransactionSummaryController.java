package controller;


import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXTabPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.rmi.CORBA.Util;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionSummaryController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private Label studentName;

    @FXML
    private Label totalFees;

    @FXML
    private Button close;

    @FXML
    private ScrollPane scrollpane;

    @FXML
    private VBox summaryBox;

    @FXML
    private JFXTabPane stdDetailsTabPane;

    @FXML
    private Tab schFeeTab;

    @FXML
    private TableView<TransactionLogger> feesLogTableView;

    @FXML
    private TableColumn<TransactionLogger, String> feeDate;

    @FXML
    private TableColumn<TransactionLogger, String> feeAmount;

    @FXML
    private TableColumn<TransactionLogger, String> feeBy;

    @FXML
    private Tab feedingTab;

    @FXML
    private TableView<TransactionLogger> feedingLogsTableview;

    @FXML
    private TableColumn<TransactionLogger, String> feedingDate;

    @FXML
    private TableColumn<TransactionLogger, String> feedingAmount;

    @FXML
    private TableColumn<TransactionLogger, String> feedingBy;

    @FXML
    private TableColumn<TransactionLogger, String> feedingNewBal;

    @FXML
    private TableColumn<TransactionLogger, String> feedingPreBal;

    @FXML
    private TableColumn<TransactionLogger, String> feeNewBal;

    @FXML
    private TableColumn<TransactionLogger, String> feePreBal;

    @FXML
    private TableColumn<TransactionLogger, String> salesPreBal;

    @FXML
    private TableColumn<TransactionLogger, String> salesNewBal;

    @FXML
    private Tab salesTab;

    @FXML
    private TableView<TransactionLogger> salesLogsTableview;

    @FXML
    private TableColumn<TransactionLogger, String> saleDate;

    @FXML
    private TableColumn<TransactionLogger, String> saleAmount;

    @FXML
    private TableColumn<TransactionLogger, String> saleBy;

    @FXML
    private Label grossTotal;

    @FXML
    private Label schFeebalance;

    @FXML
    private Label feedingBalance;

    @FXML
    private Label totalFeeding;

    @FXML
    private Label paysFees;

    @FXML
    private Pane separator;

    @FXML
    private Button printStatement;

    Double schoolFeesTotal = null;
    Double schoolFeesBalance = null;
    Double feedingBal=null;
    Double feedingTotal = null;

    // set global variables for the logs
    private ObservableList<TransactionLogger> fees = FXCollections.observableArrayList();
    private ObservableList<TransactionLogger>feedingLogs = FXCollections.observableArrayList();
    private ObservableList<TransactionLogger>salesLogs = FXCollections.observableArrayList();
    private Student student;
    TransactionLoggerDao tlDao;

    public void init(Student student){
        this.student = student;
        Double bal = student.getAccount().getSchoolFeesBalance();
        studentName.setText(student.toString());
        if (student.getPaySchoolFees()) {
            if(bal != 0) {
                totalFees.setText(String.valueOf(bal * -1));

            } else if (bal==0) {
                totalFees.setText(String.valueOf(bal));
            }

            tlDao = new TransactionLoggerDao();
            fees.addAll(tlDao.getLog(TransactionType.SCHOOL_FEES, this.student.getId()));
            Double gross = 0.00;

            for (TransactionLogger logger : fees) {
                gross+=logger.getAmount();
            }

            populateFeesLogTable();
            grossTotal.setText(gross.toString());
            schFeebalance.setText((String.valueOf((student.getAccount().getSchoolFeesBalance()))));
        } else {
            paysFees.setText("Does not pay school fees");
        }
    }

    private void populateFeesLogTable() {
        if(fees.size() > 0) {
            feeDate.setCellValueFactory(param -> new SimpleStringProperty(Utils.formatDate(param.getValue().getDate(), true)));
            feeAmount.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAmount().toString()));
            feeBy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaidBy()));
            feePreBal.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getBal_before_payment().toString()));
            feeNewBal.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getBal_after_payment().toString()));
            feesLogTableView.setItems(fees);
        }
    }

    private void populateFeedingLogsTableview() {
        if(feedingLogs.size() > 0) {
            feedingDate.setCellValueFactory(param -> new SimpleStringProperty(Utils.formatDate(param.getValue().getDate(), true)));
            feedingAmount.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAmount().toString()));
            feedingBy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaidBy()));
            feedingPreBal.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getBal_before_payment().toString()));
            feedingNewBal.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getBal_after_payment().toString()));
            feedingLogsTableview.setItems(feedingLogs);
        }
    }


    private void populateSalesLogsTableview() {
        if(salesLogs.size() > 0) {
            saleDate.setCellValueFactory(param -> new SimpleStringProperty(Utils.formatDate(param.getValue().getDate(), true)));
            saleAmount.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAmount().toString()));
            saleBy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaidBy()));
            salesPreBal.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getBal_before_payment().toString()));
            salesNewBal.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getBal_after_payment().toString()));
            salesLogsTableview.setItems(salesLogs);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stdDetailsTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue < ? extends Tab > observable, Tab oldValue, Tab newValue) {
                if(newValue != null && newValue == feedingTab ){
                    try {
                        // check if the feeding logs is empty
                        if(feedingLogs.isEmpty()) {
                            // get the feeding logs
                            List<TransactionLogger> logs = tlDao.getLog(TransactionType.FEEDING_FEE, student.getId());
                            feedingLogs.addAll(logs);

                            if(feedingBal == null || feedingTotal == null) {
                                feedingBal =0.0;
                                feedingTotal = 0.0;

                                for(TransactionLogger l : logs) {
                                    feedingTotal+=l.getAmount();
                                }

                                totalFeeding.setText(String.valueOf(feedingTotal));
                                feedingBalance.setText(String.valueOf(student.getAccount().getFeedingFeeCredit()));
                            }
                        }
                        // check if the table has not been populated
                        if(feedingLogsTableview.getItems().isEmpty()) {
                            // populate the table view
                            populateFeedingLogsTableview();
                        }
                    } catch (Exception e) {
                        Notification.getNotificationInstance().notifyError("Something went wrong", "error");
                    }
                } else if(newValue != null && newValue == salesTab) {
                    populateSalesLogsTableview();
                }
             }

        });

        //print statement
        printStatement.setOnAction(event -> {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    // generate and pdf for the transaction logs
                    PDFMaker.savePDFToLocation(PDFMaker.getPDFMakerInstance().generateStatement(student));
                    return null;
                }
            };
            task.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Preparing account statement..."));
            task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            task.setOnSucceeded(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            new Thread(task).start();
        });

        close.setOnAction(event -> Utils.closeEvent(event));
    }
}
