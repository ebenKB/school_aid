package controller;

import com.hub.schoolAid.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.util.Optional;

public class ManageStageController {

    @FXML
    private Pane root;

    @FXML
    private ListView<Stage> classListView;

    @FXML
    private TextField className;

    @FXML
    private TextField feesAmount;

    @FXML
    private TextField feedingFee;

    @FXML
    private TextField numOnRoll;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    @FXML
    private FontAwesomeIconView lock;

    private ObservableList<Stage> stages = FXCollections.observableArrayList();
    private Stage selectedStage =null;
    private StageDao stageDao = new StageDao();
    private Boolean isLocked = true;

    public void init() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                stages.addAll(stageDao.getGetAllStage());
                classListView.setItems(stages);
                classListView.setCellFactory(new Callback<ListView<Stage>, ListCell<Stage>>() {
                    @Override
                    public ListCell<Stage> call(ListView<Stage> param) {
                        return new StageCell();
                    }
                });
                return null;
            }
        };
        task.setOnRunning(event -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Loading records"));
        task.setOnSucceeded(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        task.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(task).start();

        // listen to class selection
        classListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stage>() {
            @Override
            public void changed(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
                className.setText(newValue.getName());
                numOnRoll.setText(String.valueOf(newValue.getNum_on_roll()));
                feesAmount.setText(newValue.getFeesToPay().toString());
                feedingFee.setText(newValue.getFeeding_fee().toString());
                selectedStage = newValue;
            }
        });

        lock.setOnMouseClicked(event -> {
            if (isLocked) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("Enable Field");
                alert.setContentText("Do you want to enable fields for editing?");
                Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent() && result.get() ==ButtonType.YES) {
                    // accept confirmation
                    this.enableFields();
                } else this.DisableFields();
            } else {
                this.DisableFields();
            }
        });

        save.setOnAction(event -> {
            Task save = new Task() {
                @Override
                protected Object call() throws Exception {
                    prepareRecord(selectedStage);
                    // update the records
                    stageDao.updateStage(selectedStage);
                    return null;
                }
            };
            save.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("updating records..."));
            save.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            save.setOnSucceeded(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            new Thread(save).start();
        });

        cancel.setOnAction(event -> Utils.closeEvent(event));
    }

    private void enableFields() {
        className.setEditable(true);
        feedingFee.setEditable(true);
        feesAmount.setEditable(true);
    }

    private void DisableFields() {
        className.setEditable(false);
        feedingFee.setEditable(false);
        feesAmount.setEditable(false);
    }

    private void prepareRecord(Stage stage) {
        if (isValid()) {
            stage.setName(className.getText().trim());
            stage.setFeeding_fee(Double.valueOf(feedingFee.getText().trim()));
            stage.setFeesToPay(Double.valueOf(feesAmount.getText().trim()));
        }
    }

    private Boolean isValid() {
        if (className.getText().trim().isEmpty()) {
            return false;
        }

        if (feesAmount.getText().trim().isEmpty()) {
            return false;
        }

        if (feedingFee.getText().trim().isEmpty()) {
            return false;
        }
        return true;
    }
}

