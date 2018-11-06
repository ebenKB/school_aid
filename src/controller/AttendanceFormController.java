package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.hub.schoolAid.AttendanceTemporary;
import com.hub.schoolAid.Notification;
import com.hub.schoolAid.Student;
import com.hub.schoolAid.StudentDao;

import java.net.URL;
import java.util.ResourceBundle;

public class AttendanceFormController implements Initializable{
    private ObservableList<Student> data = FXCollections.observableArrayList();
    private StudentDao studentDao =new StudentDao();
    private Notification notification=Notification.getNotificationInstance();

    @FXML
    private TableView<Student> attendanceTableView;

    @FXML
    private TableColumn<?, ?> studentCol;

    @FXML
    private TableColumn<?, ?> actionCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void lesson() {

        TableColumn<AttendanceTemporary, Boolean> lessonColumn = new TableColumn<>();

        lessonColumn.setCellFactory(cell -> new TableCell<AttendanceTemporary, Boolean>() {

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    return;
                }

                String buttonText = item ? "Check out" : "Check in";
                Button button = new Button(buttonText);
                if(item) {
                    button.setOnAction(e -> {

                    });
                }
                else {
                    button.setOnAction(e -> {

                    });
                }
            }
        });
    }
}
