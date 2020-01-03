package controller;

import com.hub.schoolAid.Category;
import com.hub.schoolAid.CategoryDao;
import com.hub.schoolAid.Notification;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoryController implements Initializable {

    @FXML
    private TextField groupName;

    @FXML
    private Button cancel;

    @FXML
    private Button save;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        save.setOnAction(event -> {
            if(groupName.getText().trim().length() > 0) {
                Category category = new Category();
                category.setName(groupName.getText().trim());
                CategoryDao categoryDao = new CategoryDao();
                Category newCat = categoryDao.addCategory(category);
                if(newCat != null) {
                    Notification.getNotificationInstance().notifySuccess("Add a new Category", "Success");
                } else {
                    Notification.getNotificationInstance().notifyError("An error occurred while adding the category", "error!");
                }
            } else {
                Notification.getNotificationInstance().notifyError("Invalid group name", "Error");
            }
        });
    }
}
