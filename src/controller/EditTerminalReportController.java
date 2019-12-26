package controller;


import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class EditTerminalReportController implements Initializable{
    @FXML
    private AnchorPane root;

    @FXML
    private Pane infoPane;

    @FXML
    private Label infoLabel;

    @FXML
    private TextArea newConduct;

    @FXML
    private TextArea newRemark;

    @FXML
    private JFXButton updateReport;

    @FXML
    private JFXButton cancel;

    @FXML
    private ListView <StudentConduct> conductListView;

    @FXML ListView <Remark>remarkListView;

    TerminalReport report;
    TerminalReportController terminalReportController;
    private ObservableList<StudentConduct> conducts = FXCollections.observableArrayList();
    private ObservableList<Remark> remarks = FXCollections.observableArrayList();
    FilteredList<StudentConduct> filteredList = new FilteredList<>(conducts, e -> true);
    FilteredList<Remark> filteredRemarks = new FilteredList<>(remarks, e -> true);
    SortedList<StudentConduct> sortedList = new SortedList<>(filteredList);
    TerminalReportDao reportDao = new TerminalReportDao();

    public void init(TerminalReport report,TerminalReportController c){
        this.report = report; // get the report that has to be edited
        this.terminalReportController = c;
        newConduct.setText(report.getConduct());
        newRemark.setText(report.getHeadTracherRemark());
    }

    /**
     * get an instance of terminal report, edit the details of the report and save the update report into the
     * database.
     */
    private void updateRecord (){
        if(isValid()) {
            this.report.setConduct(newConduct.getText().trim());
            this.report.setHeadTracherRemark(newRemark.getText().trim());
            reportDao.updateTerminalReport(this.report);
            terminalReportController.updateReportTable(report);
        }
    }

    private void resetError(){
        infoLabel.setText("");
    }
    private void toggleConductSearchOptions(){
        conductListView.setVisible(!conductListView.isVisible());
    }

    private void hideConductSearch() {
        conductListView.setVisible(Boolean.FALSE);
    }

    private void showConductSearch(){
        conductListView.setVisible(Boolean.TRUE);
    }

    private void hideRemarkSearch(){
        remarkListView.setVisible(Boolean.FALSE);
    }

    private void showRemarkSearch() {
        remarkListView.setVisible(Boolean.TRUE);
    }

    private   void checkIfFoundSearch(Object o,String filter,FilteredList filteredList) {
        if(filter == null || filter.length()==0) {
            filteredList.setPredicate(e -> true);
        }else{
            filteredList.setPredicate(e -> o.toString().contains(filter));
        }
    }


    private Boolean isValid(){
        return (newRemark.getText().trim().length()>0 && newConduct.getText().trim().length()>0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        StudentConductDao studentConductDao =new StudentConductDao();
        RemarkDao remarkDao = new RemarkDao();
        remarks.addAll(remarkDao.getRemark());
        conducts.addAll(studentConductDao.getConduct());

        updateReport.setOnAction(event -> {
            Task update = new Task() {
                @Override
                protected Object call() throws Exception {
                    updateRecord();
                    return null;
                }
            };
            update.setOnRunning(e -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Updating Record"));
            update.setOnSucceeded(e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            update.setOnFailed(e ->{
                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                infoLabel.setText("Please complete all fields");
            });
            new Thread(update).start();
        });

        newConduct.setOnKeyReleased((event) -> {
            resetError();
            if(!conductListView.isVisible()) {
                showConductSearch();
            }
            newConduct.textProperty().addListener(param -> {
                String filter = newConduct.getText().trim();
                checkIfFoundSearch(conducts,filter,filteredList);
                conductListView.setItems(filteredList.sorted());
            });
        });

        newRemark.setOnKeyReleased((event) -> {
            resetError();
            if(!remarkListView.isVisible()) {
                showRemarkSearch();
            }
            newRemark.textProperty().addListener(param -> {
                String filter = newRemark.getText().trim();
                checkIfFoundSearch(remarks,filter,filteredRemarks);
                remarkListView.setItems(filteredRemarks.sorted());
            });
        });

       didUpdateConductSearch();
       didUpdateRemarkSearch();

        cancel.setOnAction(event -> Utils.closeEvent(event));

        root.setOnMouseClicked(event ->{
            hideConductSearch();
            hideRemarkSearch();
        });

        infoPane.setOnMouseClicked(event -> {
            hideConductSearch();
            hideRemarkSearch();
        });
    }

    private void didUpdateConductSearch() {
        conductListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StudentConduct>() {
            @Override
            public void changed(ObservableValue<? extends StudentConduct> observable, StudentConduct oldValue, StudentConduct newValue) {
                if(newValue != null){
                    newConduct.setText(newValue.toString());
                }
            }
        });
    }

    private void didUpdateRemarkSearch() {
        remarkListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Remark>() {
            @Override
            public void changed(ObservableValue<? extends Remark> observable, Remark oldValue, Remark newValue) {
                if(newValue != null){
                    newRemark.setText(newValue.toString());
                }
            }
        });
    }
}
