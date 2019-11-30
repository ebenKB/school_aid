package controller;

import com.hub.schoolAid.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShowBillController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private Button createBill;

    @FXML
    private Pane separator;

    @FXML
    private TableView<Bill> billTableview;

    @FXML
    private TableColumn<Bill, String> stdCol;

    @FXML
    private TableColumn<Bill, String> billAmountCol;

    @FXML
    private TableColumn<Bill, String> academicYearCol;

    @FXML
    private TableColumn<Bill, String> dateCol;

    @FXML
    private TableColumn<Bill, String> termCol;

    @FXML
    private TableColumn<Bill, String> itemsCol;

    @FXML
    private ListView<BillItem> bill_items;

    @FXML
    private Button printBill;

    @FXML
    private Button close;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();
    private ObservableList<BillItem> itemList = FXCollections.observableArrayList();
    private BillDao billDao = new BillDao();

    public void init() {
        if(bills.isEmpty()) {
            // fetch the bills
            bills.addAll(billDao.getBill());

            // show all the bills in the main form
            populateBillsTableview();
        }
    }

    private void populateBillsTableview() {
        stdCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getStudents().size())));
        billAmountCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTotalBill().toString()));
        academicYearCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAcademicYear()));
        termCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getCreatedFor())));
        dateCol.setCellValueFactory(param -> new SimpleStringProperty(Utils.formatDate(param.getValue().getCreatedAt(), true)));
        itemsCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getBill_items().size())));
        billTableview.setItems(bills);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createBill.setOnAction(event -> showCreateBillForm());

        billTableview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Bill>() {
            @Override
            public void changed(ObservableValue<? extends Bill> observable, Bill oldValue, Bill newValue) {
                if(newValue != null) {
                    itemList.clear();
                   itemList.addAll(newValue.getBill_items());
                   bill_items.setItems(itemList);
                }
            }
        });

        printBill.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText("Print Bill");
            alert.setContentText("You are about to print this Bill for students. Are you sure you want to continue?");
            Optional<ButtonType>result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.YES) {
                // print the bill for all student who are connected to the bill
                PDFMaker.savePDFToLocation(PDFMaker.getPDFMakerInstance().createBill(billTableview.getSelectionModel().getSelectedItem()));
            }
        });
        close.setOnAction(event -> Utils.closeEvent(event));
    }

    // show the form to create a new bill
    private void showCreateBillForm() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader = new FXMLLoader(getClass().getResource("/view/Bill.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            Notification.getNotificationInstance().notifyError("An error occurred while loading the form", "error!");
            e.printStackTrace();
        }
    }
}
