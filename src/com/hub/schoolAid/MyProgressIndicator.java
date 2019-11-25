package com.hub.schoolAid;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MyProgressIndicator {

    private static MyProgressIndicator myProgressIndicatorInstance;


    private MyProgressIndicator() {
    }
    //get an instance of notification
    public static MyProgressIndicator getMyProgressIndicatorInstance() {
        if (myProgressIndicatorInstance == null) {
            myProgressIndicatorInstance = new MyProgressIndicator();
        }
        return myProgressIndicatorInstance;
    }

    private  javafx.stage.Stage stage;

    //show progress
    public  void showInitProgress(String message, Task task) {
        stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        AnchorPane root = new AnchorPane();
        root.setId("progressPane");
        //add an external image
        Pane imagePane =new Pane();
        imagePane.setId("imgPane");

        imagePane.setLayoutY(200);
        ImageView imageView =new ImageView(new Image("/assets/mat.png"));
        imagePane.getChildren().add(imageView);
        root.getChildren().add(imagePane);

        //add a progress indicator
        ProgressIndicator progressIndicator  = new ProgressIndicator();
//      progressIndicator.progressProperty().bind(task.progressProperty());
        progressIndicator.setPrefWidth(100);
        progressIndicator.setPrefHeight(100);
        root.getChildren().add(progressIndicator);
        progressIndicator.setLayoutX(240);
        progressIndicator.setLayoutY(150);

        Label label =  new Label();
        label.setId("progressLabel");
        root.getChildren().add(label);
        label.setLayoutX(200);
        label.setLayoutY(280);
        label.setText(message);

        ImageView logo =new ImageView(new Image("/assets/logo.png"));
        logo.setFitWidth(250);
        logo.setFitHeight(100);
        root.getChildren().add(logo);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("/stylesheet/main.css")));
        stage.setWidth(570);
        stage.setHeight(420);
        stage.setScene(scene);
        stage.show();
    }

    public void showActionProgress(String message){
        stage =new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        AnchorPane root = new AnchorPane();
//        root.setId("progressPane");
        root.setId("root");
        //add the progress  indicator
        ProgressIndicator progressIndicator=new ProgressIndicator();
        progressIndicator.setPrefWidth(100);
        progressIndicator.setPrefHeight(100);
        root.getChildren().add(progressIndicator);
        progressIndicator.setLayoutX(240);
        progressIndicator.setLayoutY(150);

        //add a label to show message
        Label label =  new Label();
        label.setId("progressLabel");
        root.getChildren().add(label);
        label.setLayoutX(200);
        label.setLayoutY(280);
        label.setText(message);

        //add a scene
        Scene scene=new Scene(root);
        scene.getStylesheets().add(String.valueOf(getClass().getResource("/stylesheet/main.css")));
        stage.setWidth(570);
        stage.setHeight(420);
        stage.setScene(scene);
        stage.show();
    }

    //hide progress
    public void hideProgress(){
        stage.close();
    }
}
