package controller;


import com.hub.schoolAid.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class FeedingFormController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private TextField searchStudent;

    @FXML
    private Button markPresent;

    @FXML
    private Button markAbsent;

    @FXML
    private TableView<AttendanceTemporary> studentTableView;

    @FXML
    private TableColumn<?, ?> checkCol;

    @FXML
    private TableColumn<AttendanceTemporary, String> studentCol;

    @FXML
    private TableColumn<AttendanceTemporary, String> classCol;

    @FXML
    private TableColumn<AttendanceTemporary, String> balanceCol;

    @FXML
    private TableColumn<AttendanceTemporary, String> couponCol;

    @FXML
    private TableColumn<AttendanceTemporary, String> statusCol;

    @FXML
    private TableColumn<AttendanceTemporary, String> attendanceCol;

    @FXML
    private Label totalPresent;

    @FXML
    private CheckBox debitCheck;

    @FXML
    private CheckBox creditCheck;

    @FXML
    private TextField totalDebt;

    @FXML
    private TextField totalCredit;

    @FXML
    private HBox selectionCounter;

    @FXML
    private Label totalSelected;

    @FXML
    private Button detailsButton;

    @FXML
    private HBox detailsHbox;

    @FXML
    private MenuItem refreshMenu;

    @FXML
    private Button printReport;

    @FXML
    private Button close;

    @FXML
    private MenuItem payFeedingMenu;

    @FXML
    private MenuItem paySchoolFeesMenu;

    @FXML
    private ContextMenu contextMenu;

    @FXML
    private MenuItem resetFeedingFeeMenu;

    @FXML
    private MenuItem studentDetailsMenu;

    @FXML
    private Button transactionLogs;

    @FXML
    private Button testPrint;

    private CheckBox selectAll = new CheckBox();

    private ObservableList<Student>students = FXCollections.observableArrayList();
    private StudentDao studentDao;
    private StageDao stageDao = new StageDao();
    private AttendanceTemporaryDao attendanceTemporaryDao;
    public ObservableList<AttendanceTemporary>attendanceTemporaries = FXCollections.observableArrayList();
    private salesDetailsFormController salesDetailsFormController = new salesDetailsFormController();
    private ObservableList<AttendanceTemporary>selectedAttendance = FXCollections.observableArrayList();
    private newAttendanceSheetController attendanceSheetController = new newAttendanceSheetController();
    FilteredList<AttendanceTemporary> filteredAtt = new FilteredList<>(attendanceTemporaries, e ->true);
    SortedList<AttendanceTemporary> sortedList = new SortedList<>(filteredAtt);
    mainController mainController;
    private List<Stage> stages;

    private App app;
    private AppDao appDao;
//    private ObservableList<AttendanceTemporary>attendanceTemporaries ;
    private TermDao termDao;
    private int presentCounter = 0;

//    public ObservableList<AttendanceTemporary> getAttendanceTemporaries() {
//        return attendanceTemporaries;
//    }
//
//    public void setAttendanceTemporaries(ObservableList<AttendanceTemporary> attendanceTemporaries) {
//        this.attendanceTemporaries = attendanceTemporaries;
//    }

    public void init(mainController mainController) {
        this.mainController = mainController;
        studentTableView.getSelectionModel().setCellSelectionEnabled(false);

//        System.out.println("We are initing the controller");
//        termDao = new TermDao();
//        LocalDate today = termDao.getCurrentDate();
//        System.out.println("the date in the system is"+ today.toString());

        // get default app settings
        appDao = new AppDao();
//        app = appDao.getAppSettings();
        checkCol.setGraphic(selectAll);
    }

    public void populateTableview() {
        try {
            if (attendanceTemporaries != null) {
                studentCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getFirstname() +" "+
                        param.getValue().getStudent().getOthername()+ " " + param.getValue().getStudent().getLastname()));

                classCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getStage().getName()));

                attendanceCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().isPresent() ? "PRESENT" : "ABSENT"));

                balanceCol.setCellValueFactory(param -> {
                    return  new SimpleStringProperty(String.valueOf(param.getValue().getStudent().getAccount().getFeedingFeeCredit()));
                });

                couponCol.setCellValueFactory(param -> {
                    // check the number of coupons that the students has
                    int coupons = 0;
                    if (param.getValue().getStudent().getAccount().getFeedingFeeCredit() > 0) {
                        // get the stated amount for feeding fee in the application
                        coupons = ((int) (param.getValue().getStudent().getAccount().getFeedingFeeCredit() / param.getValue().getStudent().getAccount().getFeedingFeeToPay()));

                    }
                    return new SimpleStringProperty(String.valueOf(coupons));
                });

                statusCol.setCellValueFactory(param -> {
                    return new SimpleStringProperty(param.getValue().getStudent().getAccount().getFeedingFeeCredit() < 0 ? "OWING" : "PAID");
                });

                this.addCheckBoxToTable(checkCol);
                studentTableView.setItems(attendanceTemporaries);
                salesDetailsFormController.applyTableStyle(balanceCol);
            } else {
                Notification.getNotificationInstance().notifyError("No records found in table", "Empty records");
            }
            setCounterLabel();
        } catch (Exception e) {
            System.out.println("an error occurred in table view");
        }
    }

    private void fetchRecords() {
        if (attendanceTemporaries.isEmpty()) {
            attendanceTemporaryDao = new AttendanceTemporaryDao();
            attendanceTemporaries.addAll(attendanceTemporaryDao.getTempAttendance());
        }
    }

    private void refreshData() {
        attendanceTemporaries.clear();
        attendanceTemporaryDao = new AttendanceTemporaryDao();
        attendanceTemporaries.addAll(attendanceTemporaryDao.getTempAttendance());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        this.init();
        PauseTransition pauseTransition = new PauseTransition();
        pauseTransition.setDuration(Duration.seconds(1));
        pauseTransition.play();
        pauseTransition.setOnFinished(event -> {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    fetchRecords();
                    return attendanceTemporaries;
                }
            };
            task.setOnRunning(e -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Retrieving records"));
            task.setOnSucceeded(e -> {
                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                populateTableview();
            });
            task.setOnFailed(e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            new Thread(task).start();

            // check whether the current date in the system matches today's date
            attendanceTemporaryDao = new AttendanceTemporaryDao();
            int interval = attendanceTemporaryDao.checkAttendanceInterval();
            if ( interval > 0 && interval < 15) {
                // attendance does not exist
                showCreateAttendanceForm();
            } else {
                // The attendance existing is for yesterday. - CREATE NEW ATTENDANCE FOR TODAY
//                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
//                alert.setTitle("Create new registry");
//                alert.setTitle("You have not created any feeding fee form for today.\n Do you want to create it now?");
//                Optional<ButtonType>results = alert.showAndWait();
//                if(results.isPresent() && results.get() == ButtonType.YES) {
//                    // update the current day in the system
//                    if(termDao.updateCurrentDate(LocalDate.now())) {
//                        Task task = new Task() {
//                            @Override
//                            protected Object call() throws Exception {
//                                attendanceSheetController.createNewAttendanceSheet(null, LocalDate.now());
//                                return null;
//                            }
//                        };
//                        task.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Creating new feeding fee form"));
//                        task.setOnSucceeded((event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress()));
//                        task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
//                        new Thread(task).start();
//                    }
//                }
            }
        });

        studentTableView.setRowFactory(tv -> {
            TableRow<AttendanceTemporary> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty()) {
                    AttendanceTemporary at = row.getItem();
                    at.setSelected(!at.getSelected());
                    if (at.getSelected()) {
                        selectedAttendance.add(at);
                    } else {
                        selectedAttendance.remove(at);
                    }
                    studentTableView.refresh();
                }
            });
            return row;
        });

        selectedAttendance.addListener(new ListChangeListener<AttendanceTemporary>() {
            @Override
            public void onChanged(Change<? extends AttendanceTemporary> c) {
               if (selectedAttendance.size() > 0) {
                   totalSelected.setText(String.valueOf(selectedAttendance.size()));
                   selectionCounter.setVisible(true);

                   // enable the action buttons
                   markPresent.setDisable(false);
                   markAbsent.setDisable(false);
               } else {
                   totalSelected.setText("");
                   selectionCounter.setVisible(false);

                   // Disable the action buttons
                   markAbsent.setDisable(true);
                   markPresent.setDisable(true);
               }
            }
        });

        selectAll.setOnAction(event -> {
            if(selectAll.isSelected()) {
                selectAll();
            } else {
                unselectAll();
            }
        });

        // use this button if you want to mark students present
        markPresent.setOnAction(event -> {
            if (selectedAttendance.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("Check in students");
                alert.setHeaderText("CHECK IN STUDENTS");
                alert.setContentText("If you continue "+selectedAttendance.size()+" students will be checked in.\nDo you want to continue?");
                Optional<ButtonType>results = alert.showAndWait();
                if(results.isPresent() && results.get() == ButtonType.YES) {
                    Task task = new Task() {
                        @Override
                        protected Object call() throws Exception {
                            // the use has confirmed the action
                            AttendanceTemporaryDao attendanceTemporaryDao = new AttendanceTemporaryDao();
                            attendanceTemporaryDao.checkInWithCoupon(selectedAttendance);
                            return null;
                        }
                    };
                    task.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Checking in students..."));
                    task.setOnSucceeded(event1 -> {
                        MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                        studentTableView.refresh();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                resetFields();
                                Notification.getNotificationInstance().notifySuccess("Student(s) marked present", "Present");
                            }
                        });
                    });
                    task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                    new Thread(task).start();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
                alert.setTitle("Empty selection");
                alert.setContentText("You have not selected any student.\nPlease make a selection to continue");
                alert.show();
            }
        });

        // use this button if you want to mark students absent
        markAbsent.setOnAction(event -> {
            if (selectedAttendance.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("Checkout students");
                alert.setHeaderText("MARK ABSENT");
                alert.setContentText("You want to checkout for "+ selectedAttendance.size()+" students\nAre you sure you want to continue?");
                Optional<ButtonType>result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.YES) {
                    Task task = new Task() {
                        @Override
                        protected Object call() throws Exception {
                            AttendanceTemporaryDao attendanceTemporaryDao = new AttendanceTemporaryDao();
                            attendanceTemporaryDao.checkOutWithCoupon(selectedAttendance);
                            studentTableView.refresh();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    setCounterLabel();
                                }
                            });
                            return null;
                        }
                    };
                    task.setOnRunning(e -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Checking out students..."));
                    task.setOnSucceeded(e -> {
                        MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                        Notification.getNotificationInstance().notifyError("Student(s) marked absent", "Absent");
                        resetFields();
                    });
                    task.setOnFailed(e -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
                    new Thread(task).start();
                }
            }
        });

        detailsButton.setOnAction(event -> detailsHbox.setVisible(!detailsHbox.isVisible()));

        debitCheck.setOnAction(event -> totalDebt.setVisible(!totalDebt.isVisible()));
        creditCheck.setOnAction(event ->totalCredit.setVisible(!totalCredit.isVisible()));
        refreshMenu.setOnAction(event -> refreshData());

        searchStudent.setOnKeyReleased(event -> Utils.searchByNaame(searchStudent, filteredAtt,sortedList,studentTableView));

        resetFeedingFeeMenu.setOnAction(event -> {
            Student student = studentTableView.getSelectionModel().getSelectedItem().getStudent();
            if(student != null) {
                salesDetailsFormController.resetFeeding(student);
                studentTableView.refresh();
            }
        });

        studentDetailsMenu.setOnAction(event -> {
            mainController.showStudentDetailsForm(studentTableView.getSelectionModel().getSelectedItem().getStudent());
        });

        payFeedingMenu.setOnAction(event -> {
            try {
                AttendanceTemporary at = studentTableView.getSelectionModel().getSelectedItem();
                payFeedingFee(at);
            } catch (Exception e) {
                // log error here
                Notification.getNotificationInstance().notifyError("An error occurred", "error");
            }
        });

        paySchoolFeesMenu.setOnAction(event -> {
            try {
                Student st = studentTableView.getSelectionModel().getSelectedItem().getStudent();
                salesDetailsFormController.paySchoolFees(st);
                populateTableview();
            } catch (Exception e) {
                // log error here
//                e.printStackTrace();
                Notification.getNotificationInstance().notifyError("Sorry an error occurred", "Error");
            }
        });

        printReport.setOnAction(event -> {
            if(stages == null || stages.isEmpty()) {
                stages = stageDao.getGetAllStage();
            }
//            showGenerateReportForm();
            Utils utils = new Utils();
            utils.showGenerateReportForm(stages);
        });

        close.setOnAction(event -> Utils.closeEvent(event));

        testPrint.setOnAction(event -> {
            showPrintDialogue();
        });

        transactionLogs.setOnAction(event -> Utils.showTransactionLogs());
//        searchStudent.textProperty().addListener(((observable, oldValue, newValue) -> {
//            filteredAtt.setPredicate( (Predicate< ? super  AttendanceTemporary>) at ->{
//                if( newValue == null || newValue.isEmpty() ) {
//                    return  true;
//                }
//
//                String lowerVal = newValue.toLowerCase();
//
//
//                return  Utils.checkIfStudent(lowerVal, at.getStudent());
//            });
//        }));
//        sortedList.comparatorProperty().bind(studentTableView.comparatorProperty());
//        studentTableView.setItems(sortedList);
    }

    private void payFeedingFee(AttendanceTemporary at){
        Student st = at.getStudent();
        Double bal_before_payment = st.getAccount().getFeedingFeeCredit();
        if(st.getPayFeeding()) {
            Optional<Pair<String, String>> result = salesDetailsFormController.showCustomTextInputDiag(st, "Pay Feeding Fees");
            result.ifPresent(pair -> {
                Double amount = Double.valueOf(pair.getKey());
                // take confirmation before adding the fees to the student account
                Optional<ButtonType>confirm = Utils.showConfirmation("Confirm payment of feeding fees", "Payment for " + st.toString(), "Are you sure you want to confirm payment of feeding fees?");
                studentDao = new StudentDao();
                if(confirm.isPresent() && confirm.get() == ButtonType.YES) {
                    if(studentDao.payFeedingFee(amount, st)) {
                        studentTableView.refresh();
                        Notification.getNotificationInstance().notifySuccess("Payment added for " + st.toString(), "Fees paid");
                        // log the transaction
                        Utils.logPayment(st, "Feeding Fees", pair.getValue(), bal_before_payment,st.getAccount().getFeedingFeeCredit(), amount, TransactionType.FEEDING_FEE, at.getId());
                    }
                } else Notification.getNotificationInstance().notifyError("Fees payment cancelled", "Fees not added");
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            alert.setTitle("Error");
            alert.setHeaderText("The student you have selected does not pay feeding fees");
            alert.setContentText("If you want to continue, go to settings and update the student\'s records.");
            alert.show();
        }
    }

//    private void showGenerateReportForm(ObservableList<Stage>stages) {
//        javafx.scene.Parent root;
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/selectClasses.fxml"));
//        try {
//            root=fxmlLoader.load();
//            ClassOptionsController controller = fxmlLoader.getController();
//            controller.init(stages);
//            Scene scene = new Scene(root);
//            javafx.stage.Stage stage = new javafx.stage.Stage();
//            stage.setScene(scene);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.initStyle(StageStyle.UNDECORATED);
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void showPrintDialogue() {
        javafx.scene.Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/attendancePrintNode.fxml"));
        try {
            root=fxmlLoader.load();
            PrintController controller = fxmlLoader.getController();
            controller.init();
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "error");
        }
    }

    private void showCreateAttendanceForm() {
        javafx.scene.Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/newAttendanceSheetForm.fxml"));
        try {
            root=fxmlLoader.load();
            newAttendanceSheetController controller = fxmlLoader.getController();
            controller.intCouponForm(this, FeedingType.CASH);
            Scene scene = new Scene(root);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException e) {
            Notification.getNotificationInstance().notifyError("An error occurred while showing the form", "error");
        }
    }
    private void setCounterLabel() {
        // check total students present and set the label
        presentCounter = 0;
        Double totalDebit =0.0;
        Double credit=0.0;
        int totalSize = attendanceTemporaries.size();
        for (AttendanceTemporary at : attendanceTemporaries) {
            if (at.isPresent()) {
                presentCounter += 1;
            }

            // check whether the student is owing or not
            if(at.getStudent().getAccount().getFeedingFeeCredit()  > 0) {
                credit += at.getStudent().getAccount().getFeedingFeeCredit();
            } else totalDebit += at.getStudent().getAccount().getFeedingFeeCredit();
        }
        totalPresent.setText(String.valueOf(presentCounter) + " / " + totalSize);
        totalCredit.setText(String.valueOf(credit));
        totalDebt.setText(String.valueOf(totalDebit));
    }
    private void addCheckBoxToTable(TableColumn column) {
        Callback<TableColumn<AttendanceTemporary, Void>, TableCell<AttendanceTemporary, Void>> cellFactory = new Callback<TableColumn<AttendanceTemporary, Void>, TableCell<AttendanceTemporary, Void>>() {
            @Override
            public TableCell<AttendanceTemporary, Void> call(final TableColumn<AttendanceTemporary, Void> param) {
                TableCell<AttendanceTemporary, Void> cell = new TableCell<AttendanceTemporary, Void>() {
                    CheckBox check = new CheckBox("");
                    {
                        // get the row index and update the selected property field
                        check.setOnAction(e -> {
                            AttendanceTemporary at = studentTableView.getItems().get(getIndex());
                            if(check.isSelected()) {
                                at.setSelected(true);
                                selectedAttendance.add(at);
                            } else {
                                at.setSelected(false);
                                selectedAttendance.remove(at);
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
private void selectAll() {
    selectedAttendance.clear();
    // select all students in the list
    for (AttendanceTemporary at: studentTableView.getItems()) {
        at.setSelected(true);
        selectedAttendance.add(at);
    }
    studentTableView.refresh();
}

private void unselectAll() {
    selectedAttendance.clear();
    for(AttendanceTemporary at: attendanceTemporaries) {
        at.setSelected(false);
    }
    studentTableView.refresh();
}

private void resetFields() {
        selectAll.setSelected(false);
        unselectAll();
        setCounterLabel();
        studentTableView.refresh();
}

private void checkTotal() {
    Double total = 0.0;
    for(AttendanceTemporary at: attendanceTemporaries) {
        if(at.hasPaidNow()) {
            total+=at.getFeedingFee();
        }
    }
}

//private void activateSearch() {
//    searchStudent.textProperty().addListener(((observable, oldValue, newValue) -> {
//        filteredAtt.setPredicate( (Predicate< ? super  AttendanceTemporary>) at ->{
//            if( newValue == null || newValue.isEmpty() ) {
//                return  true;
//            }
//
//            String lowerVal = newValue.toLowerCase();
//            return  viewAttendanceController.checkIfStudent(lowerVal, at.getStudent());
//        });
//    }));
//    sortedList.comparatorProperty().bind(studentTableView.comparatorProperty());
//    studentTableView.setItems(sortedList);
//}
//    private void applyTableStyle() {
//        attendanceCol.setCellFactory(column -> new TableCell<AttendanceTemporary, String>() {
//            @Override
//            protected void updateItem(String bal, boolean empty) {
//                super.updateItem(bal, empty);
//
//                setText(empty ? "" : getItem().toString());
//                setGraphic(null);
//
//                TableRow<AttendanceTemporary> currentRow = getTableRow();
//
//                if (!isEmpty()  && !currentRow.isEmpty()) {
//                    if(Double.valueOf(bal)<0 )
//                        currentRow.setStyle("-fx-background-color:#ff6a51");
//                    else if(Double.valueOf(bal) > 0 ){
//                        currentRow.setStyle("-fx-background-color:#0ecc31");
//                    }
//                    else {
//                        currentRow.setStyle("");
//                    }
//                } else {
//                    currentRow.setStyle("");
//                }
//            }
//        });
//    }
}
