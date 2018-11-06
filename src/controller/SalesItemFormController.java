package controller;

import com.hub.schoolAid.MyProgressIndicator;
import com.hub.schoolAid.Sales;
import com.hub.schoolAid.SalesDao;
import com.hub.schoolAid.WindowsSounds;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SalesItemFormController implements Initializable{
    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Sales> salesItemTableView;

    @FXML
    private TableColumn<Sales, String> itemNameCol;

    @FXML
    private TableColumn<Sales, String> unitCostCol;

    @FXML
    private TableColumn<Sales, String> qtyCol;

    @FXML
    private TableColumn<Sales, String> totalCostCol;

    @FXML
    private TableColumn<Sales, String> amountPaidCol;

    @FXML
    private TableColumn<Sales, String> balCol;

    @FXML
    private Button close;

    @FXML
    private Button save;

    private SalesDao salesDao;
    private ObservableList<Sales> salesData = FXCollections.observableArrayList();
    private  ObservableList <Sales> editedSales= FXCollections.observableArrayList();

    private int changeCounter= 0;
    private  String oldValue="";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        salesItemTableView.setEditable(true);
        editedSales.addListener(new ListChangeListener<Sales>() {
            @Override
            public void onChanged(Change<? extends Sales> c) {
                if(changeCounter>0){
                    save.setDisable(false);
                }
            }
        });

        unitCostCol.setCellFactory(TextFieldTableCell.forTableColumn());
        unitCostCol.setOnEditStart(event -> {
            oldValue=event.getOldValue();
        });

        unitCostCol.setOnEditCommit(event -> {
            event.getTableView().getItems().get(event.getTablePosition().getRow()).getItem().setCost(Double.valueOf(event.getNewValue()));
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setTotalcost((Double.valueOf(event.getNewValue()) *
                    Double.valueOf(event.getTableView().getItems().get(event.getTablePosition().getRow()).getItem().getQty())));
            prepareTable();
            if(!editedSales.contains(event.getRowValue())){
                editedSales.add(event.getRowValue());
                changeCounter++;
            }

//           if((Double.valueOf(event.getNewValue())> 0.0)  && (Double.valueOf(event.getNewValue()) != Double.valueOf(oldValue))){
//               event.getTableView().getItems().get(event.getTablePosition().getRow()).getItem().setCost(Double.valueOf(event.getNewValue()));
//               prepareTable();
//               System.out.print("there is a change");
//               changeCounter++;
//           }else{
//               System.out.println("this is the old value:"+oldValue+" "+ "and this is the new value:"+" " +event.getNewValue());
//               WindowsSounds.playWindowsSound();
//               prepareTable();
//               System.out.println("there is no change..");
//           }
        });

        qtyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        qtyCol.setOnEditCommit(event->{
            event.getTableView().getItems().get(event.getTablePosition().getRow()).getItem().setQty(Integer.parseInt(event.getNewValue()));
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setTotalcost((Double.valueOf(event.getNewValue()) *
                    Double.valueOf(event.getTableView().getItems().get(event.getTablePosition().getRow()).getItem().getCost())));
            prepareTable();
            if(!editedSales.contains(event.getRowValue())){
                editedSales.add(event.getRowValue());
                changeCounter++;
            }
        });

        save.setOnAction(event -> {
            Task save = new Task() {
                @Override
                protected Object call() throws Exception {
                    saveEditedSales();
                    return null;
                }
            };
            save.setOnRunning(e-> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Updating records..."));
            save.setOnSucceeded(e->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            save.setOnFailed(e->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            new Thread(save).start();
        });

        close.setOnAction(event -> {

        });
    }

    public void init(){
        salesDao =new SalesDao();
        List<Sales> sales =salesDao.getAllSales();
        salesData.addAll(sales);
        prepareTable();
    }

    private void prepareTable(){
        itemNameCol.setCellValueFactory(param ->{
            return new SimpleStringProperty(param.getValue().getItem().getName());
        });

        unitCostCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getItem().getCost().toString());
        });

        qtyCol.setCellValueFactory(param -> {
            return  new SimpleStringProperty(String.valueOf(param.getValue().getItem().getQty()));
        });

        amountPaidCol.setCellValueFactory(new PropertyValueFactory<Sales,String>("amountPaid"));

        totalCostCol.setCellValueFactory(new PropertyValueFactory<Sales,String>("totalcost"));

        balCol.setCellValueFactory(param -> {
         return new SimpleStringProperty(String.valueOf(param.getValue().getTotalcost() - param.getValue().getAmountPaid()));
        });

//        salesItemTableView.getItems().clear();
        salesItemTableView.setItems(salesData);
    }

    private void saveEditedSales(){
        salesDao=new SalesDao();
        for (Sales sales:editedSales){
            salesDao.updateSale(sales);
            changeCounter --;
        }
    }
}
