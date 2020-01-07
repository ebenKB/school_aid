package controller;
import com.hub.schoolAid.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.controlsfx.control.CheckListView;
import org.controlsfx.control.IndexedCheckModel;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AssignGroupController implements Initializable {

    @FXML
    private CheckListView<Stage> stageListview;

    @FXML
    private CheckListView<Category> groupListview;

    @FXML
    private Button cancel;

    @FXML
    private Button assign;

    private List<Stage> stageList = FXCollections.observableArrayList();
    private List<Category> categories = FXCollections.observableArrayList();

    public void init() {

    }

    void init(List<Stage>stages) {
        this.stageList = stages;
//        stageList.addAll(stages);
        stageListview.getItems().addAll(stages);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.setOnAction(event -> Utils.closeEvent(event));
        assign.setOnAction(event -> {
            // check if the selections are not empty
            if(stageListview.getCheckModel().getCheckedItems().size() > 0 && groupListview.getCheckModel().getCheckedItems().size() > 0) {
                CategoryDao categoryDao = new CategoryDao();
               if(categoryDao.attachCategory(stageListview.getCheckModel().getCheckedItems(), groupListview.getCheckModel().getCheckedItems())) {
                   Notification.getNotificationInstance().notifySuccess("Groups have been assigned to stages", "Success");
               }
            } else {
                Notification.getNotificationInstance().notifyError("Please make a selection", "invalid options");
            }
        });

        stageListview.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Stage>() {
            @Override
            public void onChanged(Change<? extends Stage> c) {
                if(!c.getList().isEmpty()){
                    populateGroups();
                }
            }
        });
    }

    private void populateGroups() {
        CategoryDao categoryDao = new CategoryDao();
        List<Category>categories = categoryDao.getCategory();
        if(categories != null && categories.size() > 0) {
            this.categories.addAll(categories);
            if(groupListview.getItems().isEmpty()) {
                groupListview.getItems().addAll(this.categories);
            }
        }
    }
}
