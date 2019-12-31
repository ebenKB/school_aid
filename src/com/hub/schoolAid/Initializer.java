package com.hub.schoolAid;

import controller.LoginFormController;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Logger;

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
//          stage.setMaximized(Boolean.TRUE);
            stage.show();

            FadeTransition fade =new FadeTransition();
            fade.setDuration(Duration.seconds(2));
            fade.setNode(root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            TranslateTransition translateTransition = new TranslateTransition();
            translateTransition.setDuration(Duration.seconds(2));
            translateTransition.setNode(root);
            translateTransition.setToX(-100);
            translateTransition.setToZ(-80);
            translateTransition.play();

            TranslateTransition translateTransition2 = new TranslateTransition();
            translateTransition2.setDuration(Duration.seconds(3));
            translateTransition2.setNode(root);
            translateTransition2.setToX(0);
            translateTransition2.setToZ(0);
            translateTransition2.play();
        } catch (IOException e) {Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "error");e.printStackTrace();
//            Logger.getLogger("Error with Login","");
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
        LocalDate date = termDao.getCurrentDate(true);
        if(date == null){
            return false;
        }else if(date.isAfter(LocalDate.now())){
            return false;
        }
        else if(date.isBefore(LocalDate.now())){
            return false;
        }
        else return date == LocalDate.now();
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
            Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "error");;
        }
    }
}
