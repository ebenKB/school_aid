package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import com.hub.schoolAid.Notification;
import com.hub.schoolAid.Term;
import com.hub.schoolAid.TermDao;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TermController implements Initializable {
    @FXML
    private Pane root;

    @FXML
    private ComboBox<String> termCombo;

    @FXML
    private DatePicker start_date;

    @FXML
    private DatePicker end_date;

    @FXML
    private Button cancel;

    @FXML
    private Button save;

    private TermDao termDao =new TermDao();
    private Notification notification = Notification.getNotificationInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        termCombo.getItems().addAll("First Term","Second Term","Third Term");

        save.setOnAction(event -> {
            if(start_date != null && end_date !=null){
                Term term =new Term();
                term.setStatus(1);
                term.setStart(start_date.getValue());
                term.setEnd(end_date.getValue());
                term.setToday(LocalDate.now());
                term.setName(termCombo.getSelectionModel().getSelectedItem());
                if( termDao.createTerm(term)){
                    notification.notifySuccess("You have created a new Term","success");
                }
            }else{
                //show error message
                notification.notifyError("Please make sure you have provided a start date and an end date","Empty field");
            }
        });

        cancel.setOnAction(event -> {
            ((Node)(event).getSource()).getScene().getWindow().hide();
        });
    }
}
