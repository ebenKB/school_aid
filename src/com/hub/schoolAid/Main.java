package com.hub.schoolAid;

import controller.AppSettingsController;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public class Main extends Application {

    private static MyProgressIndicator myProgressIndicator = MyProgressIndicator.getMyProgressIndicatorInstance();
    boolean connected=false;
    private App appSettings;
    private AppDao appDao = new AppDao();

    @Override
    public void start(Stage primaryStage) {


        Initializer initializer = Initializer.getInitializerInstance();
        //Initialize the system defaults
//        HibernateUtil.initDB();
        Task task = new Task() {

            @Override
            protected Object call() {
               connectToDB();
                return null;
            }

            private void connectToDB() {
                if(HibernateUtil.initDB())
                    connected=true;
            }
        };
        task.setOnRunning(event -> myProgressIndicator.showInitProgress("Setting up system modules...", task));
        task.setOnSucceeded(event -> {
            myProgressIndicator.hideProgress();

            //show the login form here
            if(connected) {
                // Set app defaults if it has not been set
                appSettings = AppDao.getAppSetting();
                AppSettingsController settingsController = new AppSettingsController();
                if (appSettings != null) {
                    if (!appSettings.getHasInit()) {
                        settingsController.showAppSettingsForm();
                    } else {
                        System.out.println("The app has init");
                        initializer.showLoginForm();
                    }
                } else {
                    settingsController.showAppSettingsForm();
                }
//                initializer.showLoginForm();
            } else {
              Alert alert =new Alert(Alert.AlertType.ERROR,"", ButtonType.OK);
              alert.setTitle("Error");
              alert.setHeaderText("Error Connecting to Database");
              alert.setContentText("Could not reach Database server. \nMake sure the server is live and your connection is active.");

              Optional<ButtonType>result =  alert.showAndWait();
              if(result.isPresent()){
                  System.exit(1);
              }
            }
        });

        task.setOnFailed(event -> {
            Alert   alert =  new Alert(Alert.AlertType.INFORMATION,"", ButtonType.OK);
            alert.setHeaderText("Start up error!");
            alert.setContentText("The system encountered an error while starting up.\nPlease close the application and try again.");
            alert.initStyle(StageStyle.UNDECORATED);
            Optional<ButtonType> result =alert.showAndWait();
            if(result.isPresent()&&result.get() == ButtonType.OK){
                System.exit(0);
            }
        });

        Utils.showTaskException(task);
        new Thread(task).start();
    }

    public static void main(String[] args) {
       try {
           launch(args);
       } catch (Exception e) {
           System.out.println("APPLICATION LEVEL EXCEPTION OCCURRED...");
       }
    }
}
