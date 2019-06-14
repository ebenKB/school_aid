package controller;

import com.hub.schoolAid.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class TerminalReportController implements Initializable {

    @FXML
    private VBox root;

    @FXML
    private JFXTabPane stdDetailsTabPane;

    @FXML
    private Tab terminalReport;

    @FXML
    private TableView<TerminalReport> reportTableView;

    @FXML
    private TableColumn<?, ?> checkStudent;

//    @FXML
//    private CheckBox selectAll;

    @FXML
    private TableColumn<TerminalReport, String> stdNameCol;

    @FXML
    private TableColumn<TerminalReport, String> remarkCol;

    @FXML
    private TableColumn<TerminalReport, String> conductCol;

    @FXML
    private TableColumn<TerminalReport, Integer> attendanceCol;

    @FXML
    private TableColumn<Stage, String> newClassCol;

    @FXML
    private TableColumn<TerminalReport, String> classCol;

    @FXML
    private JFXComboBox<Stage> reportFilter;

    @FXML
    private JFXButton save;

    @FXML
    private JFXButton generatePDF;

    @FXML
    private RadioButton viewAll;

    @FXML
    private ToggleGroup viewCat;

    @FXML
    private RadioButton viewByClass;

    @FXML
    private JFXComboBox<Stage> classCombo;

    @FXML
    private TextField search;

    @FXML
    private  Button createBill;

    private ObservableList<Stage>stages = FXCollections.observableArrayList();
    private ObservableList<Student>selectedStudents=FXCollections.observableArrayList();
    private ObservableList<TerminalReport>terminalReports=FXCollections.observableArrayList();
    private ObservableList<TerminalReport>updateReports = FXCollections.observableArrayList();
    FilteredList<TerminalReport> filteredAtt = new FilteredList<>(terminalReports, e ->true);
    SortedList<TerminalReport> sortedList = new SortedList<>(filteredAtt);
    private StudentDao studentDao;
    private TerminalReportDao terminalReportDao;
    private StageDao stageDao;
    private  int currentReportIndex = -1;
    PDDocument pdDocument;

    public void init() {
        Task init =new Task() {
            @Override
            protected Object call() throws Exception {

                // add checkbox to the column header
                CheckBox selectAll = new CheckBox();
                checkStudent.setGraphic(selectAll);
                selectAll.setOnAction(e -> {
                    if(selectAll.isSelected()) {
                       selectAll();
                    } else unSelectAll();
                });
            refresh();
            if(terminalReports.isEmpty()) {
                terminalReportDao = new TerminalReportDao();
                terminalReportDao.createTerminalReport();
            }
            populateTable();
            return null;
            }
        };
        init.setOnRunning(e -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Initializing..."));
        init.setOnSucceeded(e ->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        init.setOnFailed(e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(init).start();
    }

    public  void updateReportTable(TerminalReport newReport){
        if(currentReportIndex > -1) {
            terminalReports.get(currentReportIndex).setConduct(newReport.getConduct());
            terminalReports.get(currentReportIndex).setHeadTracherRemark(newReport.getHeadTracherRemark());
            populateTable();
        }
    }
    private void refresh() {
        terminalReportDao = new TerminalReportDao();
        stageDao = new StageDao();
        terminalReports.addAll(terminalReportDao.getReport());
        stages.addAll(stageDao.getGetAllStage());
    }

    private void tagAsEdited(TerminalReport report){
        if(updateReports.contains(report))
            return;
        updateReports.add(report);
    }
    private void saveUpdate(){
        terminalReportDao.updateTerminalReport(updateReports);
    }

    private void setClass () {
//        CustomCombo.getInstance().overrideCombo(classCombo);
//        classCombo.getItems().clear();
        classCombo.setItems(stages);
    }

    private void populateTable() {
        stdNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().toString()));
        classCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getStage().toString()));
        conductCol.setCellFactory(TextFieldTableCell.forTableColumn());
        conductCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getConduct()));
        remarkCol.setCellFactory(TextFieldTableCell.forTableColumn());
        remarkCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getHeadTracherRemark()));
        addCheckBoxToTable(checkStudent);

        reportTableView.setItems(terminalReports);
    }

    private void showEditReportForm(TerminalReport report){
        Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/editTerminalReportForm.fxml"));
        try {
            root=fxmlLoader.load();
            EditTerminalReportController controller = fxmlLoader.getController();
            controller.init(report,this);
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Terminal Report");
            stage.show();
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    private void didUpdateRow(TableRow<TerminalReport> row){
        TerminalReport report = row.getItem();
        currentReportIndex = row.getIndex();
        showEditReportForm(report);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        search.setOnKeyReleased((KeyEvent event) -> {
            activateSearch();
        });

        viewByClass.setOnAction(e ->{
            System.out.println("We want to view by class...");
            if(classCombo.getItems().isEmpty()){
                setClass();
            }
        });

        classCombo.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("the class has changed");
            filteredAtt.setPredicate( (Predicate< ? super TerminalReport>) at -> {
                if( newValue == null || newValue.isEmpty() ) {
                    return  true;
                }

                String lowerVal = newValue.getSelectedItem().getName().toLowerCase();
                if(lowerVal.contains(at.getStudent().getStage().getName().toLowerCase()))
                    return true;
                return false;
//                    return  viewAttendanceController.checkIfStudent(lowerVal, at.getStudent());
            });
            sortedList.comparatorProperty().bind(reportTableView.comparatorProperty());
            reportTableView.setItems(sortedList);
        });

        reportTableView.setRowFactory(tv -> {
            TableRow<TerminalReport>row = new TableRow <>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                  didUpdateRow(row);
                }
            });
            return row;
        });



        generatePDF.setOnAction(e -> {
//            for (Student s : selectedStudents) {
//                System.out.println("Students who have been selected"+ s.toString());
//            }

            for(TerminalReport t: sortedList) {
                if(t.isSelected()) {
                    System.out.println("these are the selected records"+ t.getStudent().toString());
                }
            }
            // get all the items in the table and create pdf for them"
//            Task task = new Task() {
//                @Override
//                protected Object call() throws Exception {
//                    pdDocument= PDFMaker.getPDFMakerInstance().createReportForAllStudents();
//                    return null;
//                }
//            };
//            task.setOnRunning(event ->MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Generating Reports. Please wait...."));
//            task.setOnFailed(event-> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
//            task.setOnSucceeded(event -> {
//                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
//                PDFMaker.savePDFToLocation(pdDocument);
//            });
//            new Thread(task).start();
        });

        createBill.setOnAction(event -> PDFMaker.getPDFMakerInstance().createBillAndItemList());



    }

    private void activateSearch() {
        // search the table for records that match the text in the search box
        search.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredAtt.setPredicate( (Predicate< ? super TerminalReport>) at -> {
                if( newValue == null || newValue.isEmpty() ) {
                    return  true;
                }

                String lowerVal = newValue.toLowerCase();
                return  viewAttendanceController.checkIfStudent(lowerVal, at.getStudent());
            });
        }));
        sortedList.comparatorProperty().bind(reportTableView.comparatorProperty());
        reportTableView.setItems(sortedList);
    }

    private void selectAll () {
       ObservableList<TerminalReport> reports = reportTableView.getItems();
        for(TerminalReport t: reports) {
            t.setSelected(true);
        }
        setDataToTable(reportTableView, reports);
    }

    private void unSelectAll() {
        ObservableList<TerminalReport> reports = reportTableView.getItems();
        for(TerminalReport t: reports) {
            t.setSelected(false);
        }
        setDataToTable(reportTableView, reports);
    }

    private void setDataToTable(TableView t, ObservableList data) {
//        t.setItems(data);
        t.refresh();
    }

    private void addCheckBoxToTable(TableColumn column) {

        Callback<TableColumn<TerminalReport, Void>, TableCell<TerminalReport, Void>> cellFactory = new Callback<TableColumn<TerminalReport, Void>, TableCell<TerminalReport, Void>>() {
            @Override
            public TableCell<TerminalReport, Void> call(final TableColumn<TerminalReport, Void> param) {
                TableCell<TerminalReport, Void> cell = new TableCell<TerminalReport, Void>() {

                    //create a checkbox for selecting or deselecting students
                    public CheckBox check = new CheckBox("");

                    {
                        // get the row index and update the selected property field
                        check.setOnAction(e -> {
                            TerminalReport t = reportTableView.getItems().get(getIndex());
                            if(check.isSelected()) {
                                t.setSelected(true);

                            } else {
                                t.setSelected(false);
                            };
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if(empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(check);
                            // check if the check box has to be activated or not
                            if(param.getTableView().getItems().get(getIndex()).isSelected()) {
                                check.setSelected(true);
                            } else {
                                check.setSelected(false);
                            }
                        };
                    }
                };
                return cell;
            }
        };
        column.setCellFactory(cellFactory);
    }
}

