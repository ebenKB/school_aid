package controller;

import com.hub.schoolAid.MyProgressIndicator;
import com.hub.schoolAid.PDFMaker;
import com.hub.schoolAid.Stage;
import com.hub.schoolAid.Utils;
import com.jfoenix.controls.JFXRadioButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClassOptionsController implements Initializable {
    @FXML
    private JFXRadioButton allClasses;

    @FXML
    private ToggleGroup classToggle;

    @FXML
    private JFXRadioButton selectClasses;

    @FXML
    private CheckComboBox<Stage> classesComobo;

    @FXML
    private Button cancel;

    @FXML
    private Button save;

    @FXML
    private Pane root;

    private List<Stage>stages = new ArrayList<>();
    private List<Stage>selectedStages = new ArrayList<>();

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public void init(List<Stage>stages) {
        setStages(stages);
        classesComobo.getItems().addAll(stages);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        classToggle.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue == selectClasses) {
                    classesComobo.setVisible(true);

                    // clear all selection
                    if(selectedStages != null){
                        selectedStages.clear();
                    }
//                    classesComobo.getCheckModel().getCheckedItems().clear();
                    classesComobo.getCheckModel().clearChecks();
                } else {
                    classesComobo.setVisible(false);

                    // select all classes
                    selectedStages = stages;
                }
            }
        });

        classesComobo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Stage>() {
            @Override
            public void onChanged(Change<? extends Stage> c) {
                selectedStages = classesComobo.getCheckModel().getCheckedItems();
            }
        });

        save.setOnAction(event -> {
            if (isValidSelection()) {
                try {
                    Task task = new Task() {
                        @Override
                        protected Object call() throws Exception {
                            PDFMaker.savePDFToLocation(PDFMaker.getPDFMakerInstance().createAttendanceReport(selectedStages));
                            return null;
                        }
                    };
                    task.setOnRunning(event1 ->  MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Preparing report"));
                    task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                    task.setOnSucceeded(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                    new Thread(task).start();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                    alert.setTitle("Error");
                    alert.setHeaderText("An error occurred while preparing the report.\n Please try again.");
                    alert.show();
                }
            } else {
                showSelectionError();
            }
         });
        cancel.setOnAction(event -> Utils.closeEvent(event));
    }

    private void showSelectionError(){
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        alert.setTitle("Error");
        alert.setHeaderText("Please select a class to continue");
        alert.setContentText("If you want to generate the report, you need to select a class");
        alert.show();
    }

    private Boolean isValidSelection(){
        if(classToggle.getSelectedToggle() == null)
            return false;

        if(classToggle.getSelectedToggle() == allClasses)
            return true;

        if(classToggle.getSelectedToggle() == selectClasses) {
            if(selectedStages==null || selectedStages.isEmpty())
                return false;
            else return true;
        }
        return false;
    }
}
