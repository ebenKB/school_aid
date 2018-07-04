package com.hub.schoolAid;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    private static MyProgressIndicator myProgressIndicator = MyProgressIndicator.getMyProgressIndicatorInstance();

    @Override
    public void start(Stage primaryStage) {
        Initializer initializer = Initializer.getInitializerInstance();
        //Initialize the system defaults
        Task task = new Task() {
            @Override
            protected Object call() {
               try {
                   HibernateUtil.initDB();
               }catch (Exception e){
                   e.printStackTrace();
               }
                return null;
            }
        };
        task.setOnRunning(event -> myProgressIndicator.showInitProgress("Setting up system modules...",task));
        task.setOnSucceeded(event -> {
            myProgressIndicator.hideProgress();

            //show the login form here
            initializer.showLoginForm();
        });
        task.setOnFailed(event -> {
            Alert   alert =  new Alert(Alert.AlertType.INFORMATION,"", ButtonType.OK);
            alert.setHeaderText("Start up error!");
            alert.setContentText("The system encountered an error while starting up.\nPlease close the application and try again.");
            Optional<ButtonType> result =alert.showAndWait();
            if(result.isPresent()&&result.get() == ButtonType.OK){
                System.exit(0);
            }
        });
        new Thread(task).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
