package com.hub.schoolAid;

import com.sun.org.apache.xpath.internal.operations.Bool;
import controller.LoginFormController;
import controller.SchFeesSummaryController;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;

import javax.imageio.IIOException;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

public class Utils {
    public static String  studentImgPath = "assets/students/";
    public String  studentImgClassPath   = getClass().getResource( "/students/").toString();
    private static LocalDate currentDate;

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

    public static void logPayment(Student student, String description, String paidBy, Double amount, TransactionType type){
        TransactionLoggerDao.getTransactionLoggerDaoInstance().LogTransaction(student.getId(), paidBy, description, amount, type);
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

    public static Optional<ButtonType> showConfirmation(String title, String hd, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(hd);
        alert.setContentText(text);
        return alert.showAndWait();
    }

    public static  void showSummaryForm(Student student) {
        javafx.scene.Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getResource("/view/schFeesSummary.fxml"));
        try {
            root = fxmlLoader.load();
            SchFeesSummaryController controller = fxmlLoader.getController();
            controller.init(student);
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            return;
        }
    }

    public static void searchByNaame(TextField textField, FilteredList filteredList, SortedList sortedList, TableView tableView) {
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate( (Predicate< ? super  AttendanceTemporary>) at ->{
                if( newValue == null || newValue.isEmpty() ) {
                    return  true;
                }
                String lowerVal = newValue.toLowerCase();
                return  checkIfStudent(lowerVal, at.getStudent());
            });
        }));
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    public static boolean checkIfStudent(String lowerVal, Student student) {
        if(student.getFirstname().toLowerCase().contains(lowerVal)){
            return true;
        }else if(student.getLastname().toLowerCase().contains(lowerVal)){
            return true;
        }else if(student.getOthername().toLowerCase().contains(lowerVal)){
            return true;
        }else if (student.toString().trim().replace(" ","").toLowerCase().contains(lowerVal.trim().replace(" ",""))){
            return true;
        }else if (student.getStage().getName().trim().replace(" ","").toLowerCase().contains(lowerVal.trim().replace(" ",""))){
            return true;
        }
        return false;
    }
//    public static LocalDate getLocalDate(){
//        if(currentDate == null) {
//            EntityManager em;
//            TermDao termDao = new TermDao();
//            em = HibernateUtil.getEntityManager();
//            HibernateUtil.begin();
//            currentDate = termDao.
//        }
//    }
 }

