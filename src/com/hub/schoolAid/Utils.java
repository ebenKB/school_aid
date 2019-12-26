package com.hub.schoolAid;

import controller.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
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
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner( ((Node)(event).getSource()).getScene().getWindow());
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

    /**
     *
     * @param student the student whom the transaction was performed for
     * @param description a brief description about the transaction
     * @param paidBy the person who initiated the transaction / payment e.g someone who pays school fees
     * @param amount the amount of the transaction
     * @param type the type of transaction
     * @param transId a unique id to identify the specific transaction that was performed. e.g student id or attendance id
     */
    public static void logPayment(Student student, String description, String paidBy, Double amount, TransactionType type, Long transId){
        TransactionLoggerDao.getTransactionLoggerDaoInstance().LogTransaction(student, paidBy, description, amount, type, transId);
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
            TransactionSummaryController controller = fxmlLoader.getController();
            controller.init(student);
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMaximized(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Notification.getNotificationInstance().notifyError("Error while showing form", "Form load error");
            return;
        }
    }

    public static void searchByNaame(TextField textField, FilteredList filteredList, SortedList sortedList, TableView tableView) {
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate( (Predicate< ? super  AttendanceTemporary>) at ->{
                if(newValue == null || newValue.isEmpty()) {
                    return  true;
                }
                String lowerVal = newValue.toLowerCase();
                return  checkIfStudent(lowerVal, at.getStudent());
            });
        }));
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    public static void searchStudentByName(TextField textField, FilteredList filteredList, SortedList sortedList, TableView tableView) {
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate( (Predicate< ? super  Student>) st ->{
                if(newValue == null || newValue.isEmpty()) {
                    return  true;
                }
                String lowerVal = newValue.toLowerCase();
                return  checkIfStudent(lowerVal, st);
            });
        }));
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    public static void filterStudent(FilteredList filteredList, SortedList sortedList, TableView<Student> tableView, String newValue) {
        filteredList.setPredicate((Predicate<? super Student>) st -> {
            if(newValue == null) {
                return true;
            } else {
                String lowerVal = newValue.toLowerCase();
                return checkIfStudent(lowerVal, st);
            }
        });

        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedList);
    }

    public static boolean checkIfStudent(String lowerVal, Student student) {
        if(student.getFirstname().toLowerCase().contains(lowerVal)){
            return true;
        } else if(student.getLastname().toLowerCase().contains(lowerVal)){
            return true;
        } else if(student.getOthername().toLowerCase().contains(lowerVal)){
            return true;
        } else if (student.toString().trim().replace(" ","").toLowerCase().contains(lowerVal.trim().replace(" ",""))){
            return true;
        } else if (student.getStage().getName().trim().replace(" ","").toLowerCase().contains(lowerVal.trim().replace(" ",""))){
            return true;
        }
        return false;
    }

    public static void addCheckBoxToTable(TableColumn column, TableView tableView, ObservableList selectedItems) {
        Callback<TableColumn<Student, Void>, TableCell<Student, Void>> cellFactory = new Callback<TableColumn<Student, Void>, TableCell<Student, Void>>() {
            @Override
            public TableCell<Student, Void> call(final TableColumn<Student, Void> param) {
                TableCell<Student, Void> cell = new TableCell<Student, Void>() {
                    CheckBox check = new CheckBox("");
                    {
                        // get the row index and update the selected property field
                        check.setOnAction(e -> {
                            Student t = (Student) tableView.getItems().get(getIndex());
                            if(check.isSelected()) {
                                t.setSelected(true);
                                selectedItems.add(t);
//                                tableView.getSelectionModel().select(getIndex());
                            } else {
                                t.setSelected(false);
                                selectedItems.remove(t);
//                                tableView.getSelectionModel().clearSelection(getIndex());
                            };
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);

                        if(empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(check);
                            // check if the check box has to be activated or not
                            if (param != null) {
                                if (param.getTableView().getItems().get(getIndex()).getSelected()) {
                                    check.setSelected(true);
                                } else {
                                    check.setSelected(false);
                                }
                            }
                        };
                    }
                };
                return cell;
            }
        };
        column.setCellFactory(cellFactory);
    }

    public static void selectAll (TableView<Student> tableView, ObservableList<Student> students, ObservableList<Student>selectedStudents) {
        // clear the selection counter in case it already has some items counted
        selectedStudents.clear();
        for (Student st: tableView.getItems()) {
            st.setSelected(true);
            selectedStudents.add(st);
//            tableView.getSelectionModel().select(st);
        }
        tableView.refresh();
    }

    public static void unSelectAll(TableView tableView, ObservableList<Student> students, ObservableList<Student>selectedStudents, CheckBox checkBox) {
        for (Student st: students) {
            st.setSelected(false);
            selectedStudents.remove(st);
//            tableView.getSelectionModel().clearSelection();
        }
        selectedStudents.clear();
        tableView.refresh();

        // clear the selection from the checkbox that selects the items
        checkBox.setSelected(false);
    }

    public void showGenerateReportForm(List<com.hub.schoolAid.Stage> stages) {
        javafx.scene.Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/selectClasses.fxml"));
        try {
            root=fxmlLoader.load();
            ClassOptionsController controller = fxmlLoader.getController();
            controller.init(stages);
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTrialForm() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/trial.fxml"));
            root= fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setMaximized(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showTransactionLogs() {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Utils.class.getResource("/view/paymentDashboard.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            PaymentDashboardController controller = fxmlLoader.getController();
            controller.init();
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String formatDate(LocalDate date, Boolean isShort) {
        if(date == null)
            return "";

        String newDate = null;

        int day = date.getDayOfMonth();
        String month = date.getMonth().toString();
        String dayName = date.getDayOfWeek().toString();
        int year = date.getYear();
        if(!isShort) {
            newDate = dayName +", " + month+ " " + day+ ", " + year;
        } else {
            char [] mChars = month.toCharArray();
            char [] dChars = dayName.toCharArray();
            // take the first three letters of the month name
            month= String.valueOf(mChars[0])+ String.valueOf(mChars[1])+ String.valueOf(mChars[2])+".";

            // take the first three letters of the day name
            dayName = String.valueOf(dChars[0]) + String.valueOf(dChars[1])+ String.valueOf(dChars[2])+".";

            newDate = dayName+" "+month+ " "+ day+ ", "+ year;
        }
        return newDate;
    }


    public static void dispose(ActionEvent event) {
        ((Node)(event).getSource()).getScene().getWindow().hide();
    }

    public static Window getInitOwner(ActionEvent event){
        return  ((Node)(event).getSource()).getScene().getWindow();
    }

    public static Window getInitOwnerFromMouseEvent(MouseEvent event){
        return  ((Node)(event).getSource()).getScene().getWindow();
    }
 }

