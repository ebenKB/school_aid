package controller;


import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXTabPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private Label balance;

    @FXML
    private Label paysFees;

    @FXML
    private Pane separator;

    @FXML
    private Button printStatement;

    // set global variables for the logs
    private ObservableList<TransactionLogger> fees = FXCollections.observableArrayList();
    private ObservableList<TransactionLogger>feedingLogs = FXCollections.observableArrayList();
    private ObservableList<TransactionLogger>salesLogs = FXCollections.observableArrayList();
    private Student student;
    TransactionLoggerDao tlDao;

    public void init(Student student){
        this.student = student;
        studentName.setText(student.toString());
        if (student.getPaySchoolFees()) {
            totalFees.setText(String.valueOf(student.getAccount().getFeeToPay() * -1));

            tlDao = new TransactionLoggerDao();
            fees.addAll(tlDao.getLog(TransactionType.SCHOOL_FEES, this.student.getId()));
            System.out.println("The logs are: "+ fees.size());
            Double gross = 0.00;

            for (TransactionLogger logger : fees) {
//                createLabel(logger.getAmount(),logger.getPaidBy(), logger.getDate());
                gross+=logger.getAmount();
            }
            populateFeesLogTable();
            grossTotal.setText(gross.toString());
            balance.setText((String.valueOf((student.getAccount().getFeeToPay() + gross))));
        } else {
            paysFees.setText("Does not pay school fees");
        }
    }

    private void populateFeesLogTable() {
        if(fees.size() > 0) {
            feeDate.setCellValueFactory(param -> new SimpleStringProperty(Utils.formatDate(param.getValue().getDate(), true)));
            feeAmount.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAmount().toString()));
            feeBy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaidBy()));
            feesLogTableView.setItems(fees);
        }
    }

    private void populateFeedingLogsTableview() {
        if(feedingLogs.size() > 0) {
            feedingDate.setCellValueFactory(param -> new SimpleStringProperty(Utils.formatDate(param.getValue().getDate(), true)));
            feedingAmount.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAmount().toString()));
            feedingBy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaidBy()));
            feedingLogsTableview.setItems(feedingLogs);
        }
    }


    private void populateSalesLogsTableview() {
        if(salesLogs.size() > 0) {
            saleDate.setCellValueFactory(param -> new SimpleStringProperty(Utils.formatDate(param.getValue().getDate(), true)));
            saleAmount.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAmount().toString()));
            saleBy.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPaidBy()));
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
                            feedingLogs.addAll(tlDao.getLog(TransactionType.FEEDING_FEE, student.getId()));
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
            // generate and pdf for the transaction logs
            PDFMaker.savePDFToLocation(PDFMaker.getPDFMakerInstance().generateStatement(student));
        });

        close.setOnAction(event -> Utils.closeEvent(event));
    }
}
