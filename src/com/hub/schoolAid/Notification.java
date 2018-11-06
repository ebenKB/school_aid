package com.hub.schoolAid;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Created by HUBKB.S on 5/27/2017.
 */
public class Notification extends  Object{
    private static Notification notificationInstance;

    //get an image for notifications
    private Image successImg = new Image("/assets/success.png");
    private Image cancelImg  = new Image("/assets/cancel.png");

    private Notification(){

    }

    //get an instance of notification
    public static Notification getNotificationInstance(){
        if(notificationInstance==null){
            notificationInstance=new Notification();
        }
            return notificationInstance;
    }

    //notify users of errors
    public void notifyError(String message,String Title){
        Notifications notificationBuilder = Notifications.create();
        notificationBuilder.title(Title);
        notificationBuilder.text(message);
        //get an image for the notification
        notificationBuilder.graphic(new javafx.scene.image.ImageView(cancelImg));
        notificationBuilder.hideAfter(Duration.seconds(10))
               .position(Pos.BOTTOM_RIGHT);
        WindowsSounds.playWindowsSound();
        notificationBuilder.show();

    }

    //notify users of success
    public void notifySuccess(String message, String Title){
        Notifications notificationBuilder = Notifications.create();
        notificationBuilder.title(Title);
        notificationBuilder.text(message);
        //get an image for the notification
        notificationBuilder.graphic(new javafx.scene.image.ImageView(successImg));
        notificationBuilder.hideAfter(Duration.seconds(6.5))
                .position(Pos.BOTTOM_RIGHT);
        WindowsSounds.playWindowsSound();
        notificationBuilder.show();
        WindowsSounds.playWindowsSound();
    }
 }
