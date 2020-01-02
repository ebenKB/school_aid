package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class classListController implements Initializable{
    @FXML
    private TableColumn<Stage, Integer> idCol;

    @FXML
    private TableColumn<Stage, String> classNameCol;

    @FXML
    private TableColumn<Stage, String> numOnRollCol;

    @FXML
    private TableColumn<Stage, Double> feesCol;

    @FXML
    private TableColumn<Stage, Double> feedingCol;

    @FXML
    private MenuItem autoUpdateClassSize;

    @FXML
    private JFXButton close;

    @FXML
    private TextField totalNumOnRoll;

    @FXML
    private TableView<Stage> classListTableView;

    private ObservableList<Stage> data = FXCollections.observableArrayList();

    private StageDao stageDao = new StageDao();

    /**
     * takes a stage or class and puts its data in a table
     * @param
     */
    private void populateClassListTableView(){
//        if( data.isEmpty()){
//            return;
//        }

        totalNumOnRoll.setText(String.valueOf(getClassListData()));
        idCol.setCellValueFactory(new PropertyValueFactory<Stage,Integer>("id"));
        classNameCol.setCellValueFactory(new PropertyValueFactory<Stage, String>("name"));
        numOnRollCol.setCellValueFactory(new PropertyValueFactory<Stage, String>("num_on_roll"));
        feesCol.setCellValueFactory(new PropertyValueFactory<Stage,Double>("feesToPay"));
        feedingCol.setCellValueFactory(new PropertyValueFactory<Stage,Double>("feeding_fee"));
        classListTableView.setItems(data);

    }

    private int getClassListData(){
        //fetch data from the database and pass it to the observable list ; data
        List<Stage> list = stageDao.getGetAllStage();
        int counter=0;

        for(Stage stage:list){
            data.add(stage);
            counter+=stage.getNum_on_roll();
        }
        return counter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateClassListTableView();
       close.setOnAction(e->{
           Utils.closeEvent(e);
       });

       autoUpdateClassSize.setOnAction(event -> {
           Alert alert = new Alert(Alert.AlertType.INFORMATION, "",ButtonType.OK, ButtonType.CANCEL);
           alert.setTitle("Synchronize Class size");
           alert.setContentText("Use this tool if you feel there is an error in the class size. \n It will check the various classes and update the number on row.\n If you want to continue to press OK");
           alert.initOwner(close.getScene().getWindow());
           Optional<ButtonType> result = alert.showAndWait();
           if(result.isPresent() && result.get() == ButtonType.OK) {
               Task task = new Task() {
                   @Override
                   protected Object call() throws Exception {
                       StageDao stageDao = new StageDao();
                       stageDao.syncNumberOnRow();
                       return null;
                   }
               };
               task.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Synchronizing class size"));
               task.setOnSucceeded(event1 -> {
                   MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                   Notification.getNotificationInstance().notifySuccess("Class size has been updated automatically", "Success");
               });
               task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
               new Thread(task).start();
           }
       });
    }
}
