package com.hub.schoolAid;

import controller.LoginFormController;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Optional;

public class Utils {
    public static String  studentImgPath = "assets/students/";
    public String  studentImgClassPath   = getClass().getResource( "/students/").toString();

    public static void closeEvent(ActionEvent event){
        Alert alert  =new Alert(Alert.AlertType.CONFIRMATION,"", ButtonType.YES,ButtonType.NO);
        alert.setHeaderText("Are you sure you want to close the form?");
        alert.setTitle("Confirm");
        Optional<ButtonType>result = alert.showAndWait();
        if(result.isPresent() &&result.get() ==ButtonType.YES){
            ((Node)(event).getSource()).getScene().getWindow().hide();
        }
    }

    public static void showTaskException(Task task){
    task.exceptionProperty().addListener((observable,oldValue,newValue) -> {
        if(newValue!=null){
            MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
//            Exception ex = (Exception)newValue;
            Alert alert =new Alert(Alert.AlertType.ERROR,"",ButtonType.OK);
//            alert.setContentText(ex.getMessage());
//            ex.printStackTrace();
        }
    });
    }

    public static void logPayment(Student student, String description, String paidBy, Double amount){
        TransactionLoggerDao.getTransactionLoggerDaoInstance().LogTransaction(student.getId(), paidBy, description, amount);
//        TransactionLogger transactionLogger = new TransactionLogger(student.getId(),description,paidBy,date,amount);
//        TransactionLoggerDao loggerDao = new TransactionLoggerDao();
//        loggerDao.logTransaction(transactionLogger);
    }

    public static Boolean authorizeUser() {
        TextInputDialog textInputDialog  = new TextInputDialog();
        textInputDialog.setTitle("Authorize user");
        textInputDialog.setHeaderText("Confirm You Password");
        textInputDialog.setContentText("You need to provide your password to continue this action.");
        Optional<String> result = textInputDialog.showAndWait();
        if(result.isPresent() && result.get().equals(LoginFormController.user.getPassword()))
            return true;
        return false;
    }
}

