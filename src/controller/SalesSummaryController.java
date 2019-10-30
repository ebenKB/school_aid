package controller;

import com.hub.schoolAid.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class SalesSummaryController implements Initializable {

    @FXML
    private Pane root;

    @FXML
    private TextField search;

    @FXML
    private ListView<Sales> salesListView;

    @FXML
    private ListView<TransactionLogger> saleDetailsListview;

    @FXML
    private TextField totalPaid;

    @FXML
    private TextField balance;

    @FXML
    private Button close;

    @FXML
    private Label itemCost;

    private  Student student;
    private ObservableList<Sales> sales = FXCollections.observableArrayList();
    private SalesDao salesDao= new SalesDao();
    private ObservableList<TransactionLogger> transactions =  FXCollections.observableArrayList();
    TransactionLoggerDao transactionLoggerDao = new TransactionLoggerDao();

    public void init(Student student) {
        if (student == null) {
            // get all sales records and populate the list sales list view
            sales.addAll(salesDao.getAllSales());
        } else {
            // get all sales records for a particular student and populate the list view
            sales.addAll(salesDao.getAllSalesOfStudent(student));
        }

        salesListView.setItems(sales);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        salesListView.setCellFactory(new Callback<ListView<Sales>, ListCell<Sales>>() {
            @Override
            public ListCell<Sales> call(ListView<Sales> param) {
                return new SaleListCell();
            }
        });

        salesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Sales>() {
            @Override
            public void changed(ObservableValue<? extends Sales> observable, Sales oldValue, Sales newValue) {
                if(newValue != null) {
                    // get all the payments for the selected sale
                    transactions.clear();
                    transactions.addAll(transactionLoggerDao.getLog(TransactionType.SALES,newValue.getId()));

                    saleDetailsListview.setItems(transactions);

                    // populate the fields
                    itemCost.setText(String.valueOf(newValue.getTotalcost()));
                    totalPaid.setText(String.valueOf(newValue.getAmountPaid()));
                    balance.setText(String.valueOf((newValue.getTotalcost() - newValue.getAmountPaid())));
                }
            }
        });

        close.setOnAction(event -> Utils.closeEvent(event));
//        transactions.addListener(new ListChangeListener<TransactionLogger>() {
//            @Override
//            public void onChanged(Change<? extends TransactionLogger> c) {
//                System.out.println("The content has changes"+ c.toString());
////                saleDetailsListview.setItems(transactions);
//                saleDetailsListview.refresh();
//            }
//        });
    }
}

