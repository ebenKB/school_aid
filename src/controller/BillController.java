package controller;

import com.hub.schoolAid.Stage;
import com.hub.schoolAid.StageDao;
import com.hub.schoolAid.Student;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class BillController implements Initializable {
    @FXML
    private TextField tuitionFee;

    @FXML
    private AnchorPane darkPane;

    @FXML
    private Button cancel;

    @FXML
    private Button saveBill;

    @FXML
    private TextField totalBill;

    @FXML
    private Button addNewItem;

    @FXML
    private TableView<?> itemsTableview;

    @FXML
    private TableColumn<?, ?> itemCol;

    @FXML
    private TableColumn<?, ?> costCol;

    @FXML
    private AnchorPane addItemPane;

    @FXML
    private TextField itemAmount;

    @FXML
    private Button canceltem;

    @FXML
    private Button saveItem;

    @FXML
    private TextField itemName;

    @FXML
    private JFXRadioButton allRadio;

    @FXML
    private JFXRadioButton classRadio;

    @FXML
    private CheckComboBox<Stage> classCombo;

    @FXML
    private JFXRadioButton studentRadio;

    @FXML
    private ListView<?> studentListview;

    @FXML
    private ToggleGroup studentToggle;

    @FXML
    private ProgressIndicator stageProgess;

    private ObservableList<Stage>stages = FXCollections.observableArrayList();
    private ObservableList<Student>students = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addNewItem.setOnAction(event -> addItemPane.setVisible(!addItemPane.isVisible()));

        studentToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                // check the type of toggle selected
                if(newValue == classRadio) {
                    // check  if the class list is empty
                    if(stages.isEmpty()) {
                        stageProgess.setVisible(true);
                        // fetch the stages
                        StageDao stageDao = new StageDao();
                        stages.addAll(stageDao.getGetAllStage());

                        // hide the progess bar
                        stageProgess.setVisible(false);
                    } else if(newValue == studentRadio) {
                        // show the student list view
                        studentListview.setVisible(true);

                        // check if it is not populated
                    } else if(newValue == allRadio) {
                        // create bill for all students
                    }
                }
            }
        });
    }
}
