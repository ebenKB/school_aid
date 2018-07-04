package com.hub.schoolAid;

import controller.LoginFormController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;

public class Initializer {

    private static Initializer initializerInstance;
    public static Boolean isLocked =false;
    public static Boolean isLoggedin=false;

    public static Initializer getInitializerInstance(){
        if(initializerInstance ==null){
            initializerInstance=new Initializer();
        }
        return initializerInstance;
    }

    public void showLoginForm(){
        //show the login form
        try {
            System.out.print("Trying to show the login form....");
            Parent root ;
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource("/view/LoginForm.fxml"));
            root=fxmlLoader.load();
            LoginFormController loginFormController = fxmlLoader.getController();
            loginFormController.init();

            //create the form
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
//            stage.setMaximized(Boolean.TRUE);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void haltSystem(){
        PauseTransition delay = new PauseTransition(Duration.seconds(150));
        delay.setOnFinished( event2 -> {
            Initializer.getInitializerInstance().showLoginForm();
            isLocked=true;
        } );
        delay.play();
    }

    public Boolean isValidCurrentDate(){
        TermDao termDao = new TermDao();
        if(termDao.getCurrentDate() == null){
            return false;
        }else if(termDao.getCurrentDate().isAfter(LocalDate.now())){
            return false;
        }
        else if(termDao.getCurrentDate().isBefore(LocalDate.now())){
            return false;
        }
        else return termDao.getCurrentDate() == LocalDate.now();
    }

    public void showCurrentDateForm(){
        try {
            Parent  root =  FXMLLoader.load(getClass().getResource("view/newAttendanceSheetForm.fxml"));
            Scene scene= new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UTILITY);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
