package controller;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import com.hub.schoolAid.Stage;
import com.hub.schoolAid.StageDao;

import java.net.URL;
import java.util.List;
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
            System.out.print("we are fetching data for the class list table view"+ stage.getName());
        }
        return counter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateClassListTableView();
    }
}
