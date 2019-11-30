package controller;

import com.hub.schoolAid.TransactionLogger;
import com.hub.schoolAid.TransactionLoggerDao;
import com.hub.schoolAid.TransactionType;
import com.hub.schoolAid.Utils;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.sun.org.apache.regexp.internal.RE;
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
    private VBox dateOptions;

    private ObservableList<TransactionLogger>schoolFees = FXCollections.observableArrayList();
    private ObservableList<TransactionLogger>feedingFee = FXCollections.observableArrayList();
    private ObservableList<TransactionLogger>sales = FXCollections.observableArrayList();
    LocalDate logDate = LocalDate.now();

    private TransactionLoggerDao transactionLoggerDao = new TransactionLoggerDao();

    public void init() {
        feedingFee.addAll(transactionLoggerDao.getLog(LocalDate.now(), TransactionType.FEEDING_FEE));
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
        studentCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().toString()));
        populateTableview();
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
               if(newValue == selectDateRadio) {

               }
            }
        });
    }

    private void populateTableview() {
        if(! feedingFee.isEmpty()) {
            typeCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTransactionType().toString()));
            transactionTableview.setItems(feedingFee);
        }
        checkTotal();
    }

    private void checkTotal() {
        Double total = 0.0;
        if(typeToggle.getSelectedToggle() == feedingRadio) {
            for(TransactionLogger t : feedingFee) {
                total+=t.getAmount();
            }
        }
        logsTotal.setText(total.toString());
        logSize.setText(String.valueOf(feedingFee.size()));
    }
}
