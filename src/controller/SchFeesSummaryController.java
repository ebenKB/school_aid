package controller;

import com.hub.schoolAid.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class SchFeesSummaryController implements Initializable{

    @FXML
    private AnchorPane root;

    @FXML
    private Label studentName;

    @FXML
    private Label grossTotal;

    @FXML
    private Label totalFees;

    @FXML
    private Label balance;

    @FXML
    private Label paysFees;

    @FXML
    private Button close;

    @FXML
    private ScrollPane scrollpane;


    @FXML
    private VBox summaryBox;

    private Student student;
    private ObservableList<TransactionLogger> logs = FXCollections.observableArrayList();
    private TransactionLoggerDao tlDao;

    public void init(Student student) {
        this.student = student;
        studentName.setText(student.toString());
        if (student.getPaySchoolFees()) {
            totalFees.setText(String.valueOf(student.getAccount().getFeeToPay() * -1));

            tlDao = new TransactionLoggerDao();
            logs.addAll(tlDao.getLog(TransactionType.SCHOOL_FEES, this.student.getId()));
            System.out.println("The logs are: "+ logs.size());
            Double gross = 0.00;

            for (TransactionLogger logger : logs) {
                createLabel(logger.getAmount(),logger.getPaidBy(), logger.getDate());
                gross+=logger.getAmount();
            }

//            for (int i =0; i<20; i++) {
//                createLabel(90.99, LocalDate.now());
//            }
            grossTotal.setText(gross.toString());
            balance.setText((String.valueOf((student.getAccount().getFeeToPay() + gross))));
        } else {
            paysFees.setText("Does not pay school fees");
        }

    }


    private void createLabel(Double tAmount, String paidBy, LocalDate tDate) {
        HBox hBox = new HBox();
        Label date = new Label(Utils.formatDate(tDate));
        date.setMinWidth(80);
        date.setAlignment(Pos.CENTER_LEFT);
        date.setPrefHeight(20);
        date.getStyleClass().add("label-caption");

        Label amount = new Label(tAmount.toString());
        amount.setAlignment(Pos.CENTER_RIGHT);
        amount.setMinWidth(100);
        amount.setPrefHeight(20);
        amount.getStyleClass().add("label-caption");

        Label by = new Label("\t\tpaid by "+ paidBy+"\t\t");
        by.setAlignment(Pos.CENTER_LEFT);
        by.setMinWidth(100);
        by.setPrefHeight(20);
        by.getStyleClass().add("label-caption");


        hBox.getChildren().addAll(date, amount, by);
        hBox.setAlignment(Pos.CENTER_LEFT);

        // set the hbox to the scroll pane
        summaryBox.getChildren().add(hBox);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        close.setOnAction(event -> Utils.closeEvent(event));
    }
}
