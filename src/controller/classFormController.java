package controller;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import com.hub.schoolAid.Notification;
import com.hub.schoolAid.Stage;
import com.hub.schoolAid.StageDao;

import javax.persistence.PersistenceException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class classFormController implements Initializable{
    @FXML
    private AnchorPane classFormPane;

    @FXML
    private ComboBox<String> className;

    @FXML
    private Button clear;

    @FXML
    private Button save;

    @FXML
    private TextField feeding_fee;

    @FXML
    private TextField fees_to_pay;

    @FXML
    private Pane root;


    private StageDao stageDao = new StageDao();
    private Notification notification =Notification.getNotificationInstance();

    /**
     *
     * @param stage the class whos value we want to find
     * @return 0 if no class value is found
     *          -1 if the stage is empty
     *          otherwise returns the values of the class
     */
    private int getClassValue(String stage){
       if(stage.isEmpty()){
           return -1;
       }
        else if(stage.toLowerCase().equals("creche")){
            return 1;
        }
        else if(stage.toLowerCase().equals("nursery one") ||stage.toLowerCase().equals("nursery 1")){
            return 2;
        }
        else if(stage.toLowerCase().equals("nursery two")){
            return 3;
        }
        else if(stage.toLowerCase().equals("k.g.1") || stage.toLowerCase().equals("kg1") || stage.toLowerCase().equals("kg one")){
            return 4;
        }
        else if(stage.toLowerCase().equals("k.g.2") || stage.toLowerCase().equals("kg2") || stage.toLowerCase().equals("kg two")){
            return 5;
        }
        else if(stage.toLowerCase().equals("one") || stage.toLowerCase().equals("class one")|| stage.toLowerCase().equals("class 1")){
            return 6;
        }
        else if(stage.toLowerCase().equals("two") || stage.toLowerCase().equals("class two")|| stage.toLowerCase().equals("class 2")){
            return 7;
        }
        else if(stage.toLowerCase().equals("three") || stage.toLowerCase().equals("class three")|| stage.toLowerCase().equals("class 3")){
            return 8;
        }
        else if(stage.toLowerCase().equals("four") || stage.toLowerCase().equals("class four")|| stage.toLowerCase().equals("class 4")){
            return 9;
        }
        else if(stage.toLowerCase().equals("five") || stage.toLowerCase().equals("class five")|| stage.toLowerCase().equals("class 5")){
            return 10;
        }
        else if(stage.toLowerCase().equals("six") || stage.toLowerCase().equals("class six")|| stage.toLowerCase().equals("class 6")){
            return 11;
        }
        else if(stage.toLowerCase().equals("form one") ||stage.toLowerCase().equals("form 1") || stage.toLowerCase().equals("jhs one")|| stage.toLowerCase().equals("j.h.s one")||
                stage.toLowerCase().equals("j.h.s 1")||stage.toLowerCase().equals("jhs 1")){
            return 12;
        }
        else if(stage.toLowerCase().equals("form two") || stage.toLowerCase().equals("form 2")|| stage.toLowerCase().equals("jhs two")|| stage.toLowerCase().equals("j.h.s two")||
                stage.toLowerCase().equals("j.h.s 2")||stage.toLowerCase().equals("jhs 2")){
            return 13;
        }

        else if(stage.toLowerCase().equals("form three") || stage.toLowerCase().equals("form 3")|| stage.toLowerCase().equals("jhs three")|| stage.toLowerCase().equals("j.h.s three")||
                stage.toLowerCase().equals("j.h.s 3")||stage.toLowerCase().equals("jhs 3")){
            return 14;
        }
        return 0;
    }

    private Boolean isValidStage(Stage stage){

        return stage.getName() != null && stage.getClassValue() != 0;
    }

    private Boolean isValidInput(){
        if(className.getSelectionModel().getSelectedItem().isEmpty())
            return false;
        if(feeding_fee.getText().isEmpty())
            return false;
        return !fees_to_pay.getText().isEmpty();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        save.setOnAction(event -> {
            if(isValidInput()){
                Stage stage =new Stage();
                stage.setName(className.getSelectionModel().getSelectedItem());
                stage.setClassValue(getClassValue(stage.getName()));
                stage.setNum_on_roll(0);
                stage.setFeeding_fee(Double.valueOf(feeding_fee.getText().trim()));

//                stage.setFeesToPay(Double.valueOf(fees_to_pay.getText().trim()));

                //show an alert to add a new stage
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"", ButtonType.YES,ButtonType.CANCEL);
                alert.setContentText("You have created a new class by name: "+" "+stage.getName());
                alert.setHeaderText("Do you want to save this class?");
                Optional<ButtonType>result = alert.showAndWait();

                if(result.isPresent() && result.get() == ButtonType.YES){
                    //check if the class is already in the system
                    if(stageDao.isExistingStage(stage)){
                        className.setFocusTraversable(Boolean.TRUE);
                        notification.notifyError("Sorry! this class already exits","Duplicate entry");
                    }else {
                       try{
                           stageDao.addNewStage(stage);
                           notification.notifySuccess("You added a new class","Success");
                       }catch (PersistenceException p){
                           notification.notifyError("An error occurred while saving your record","Error!");
                       }
                    }
                }
            }else {
                notification.notifyError("Please complete all fields.","Invalid input");
            }
        });
    }
}
