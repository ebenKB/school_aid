package com.hub.schoolAid;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Optional;

public class Utils {
    public static String  studentImgPath = "/assets/students/";
    public String  studentImgClassPath =getClass().getResource( "/assets/students/").toString();

    public static void closeEvent(ActionEvent event){
        Alert alert  =new Alert(Alert.AlertType.CONFIRMATION,"", ButtonType.YES,ButtonType.NO);
        alert.setHeaderText("Are you sure you want to close the form?");
        alert.setTitle("Confirm");
        Optional<ButtonType>result = alert.showAndWait();
        if(result.isPresent() &&result.get() ==ButtonType.YES){
            ((Node)(event).getSource()).getScene().getWindow().hide();
        }
    }
}

