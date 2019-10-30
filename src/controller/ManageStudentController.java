package controller;

import com.hub.schoolAid.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ManageStudentController implements Initializable {
    @FXML
    private AnchorPane root;


    @FXML
    private Pane optionsPane;

    @FXML
    private ListView<Stage> classListView;

    @FXML
    private TableView<Student> studentTableView;

    @FXML
    private TableColumn<?, ?> checkStudents;

    @FXML
    private TableColumn<Student, String> stdNameCol;

    @FXML
    private TableColumn<Student, String> stage;

    @FXML
    private Button promoteStudent;

    @FXML
    private Button demoteStudent;

    @FXML
    private Button setNewClass;

    @FXML
    private Button cancelUpdate;

    @FXML
    private Button updateClass;

    @FXML
    private Button cancel;

    private CheckBox checkBox = new CheckBox();
    private CheckBox selectAll = new CheckBox();
    private StudentDao studentDao = new StudentDao();
    private ObservableList<Student> students = FXCollections.observableArrayList();
    private ObservableList<Student> selectedStudents = FXCollections.observableArrayList();
    private ObservableList<Stage> stages = FXCollections.observableArrayList();
    private Boolean hasEnabledFields= false;

    public void init() {
        // add a check box to select all
        checkStudents.setGraphic(selectAll);
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                // fetch students
                students.addAll(studentDao.getAllStudents());
                // add checkbox to the column header
//                checkStudents.setGraphic(selectAll);
                populateTable();
                return null;
            }
        };
        task.setOnRunning(event -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Initializing..."));
        task.setOnSucceeded(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        task.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(task).start();
    }

    public void init(ObservableList<Student> data){
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                // add a check box to select all
                checkStudents.setGraphic(selectAll);
                students.addAll(data);
                populateTable();
                return null;
            }
        };
        task.setOnRunning(event -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Initializing..."));
        task.setOnSucceeded(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        task.setOnFailed(event -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(task).start();
    }

    private void populateTable() {
        if(students.size() > 0) {
            stdNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().toString()));
            stage.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStage().getName()));
//        classCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getStage().toString()));
//        conductCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        conductCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getConduct()));
//        remarkCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        remarkCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getHeadTracherRemark()));
            addCheckBoxToTable(checkStudents);
            studentTableView.setItems(students);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        promoteStudent.setOnAction(event -> {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    if(students.size() > 0) { // check if there are selected students to promote
                        if(students.size() == 1) {
                            studentDao.promoteStudent(students.get(0));
                        } else {
                            studentDao.updateStudentStage(selectedStudents, true);
                        }
                    }
                    return null;
                }
            };
            task.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Updating student records"));
            task.setOnSucceeded(event1 -> {
                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                Notification.getNotificationInstance().notifySuccess("Students have been promoted", "Success");
            });
            task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            new Thread(task).start();
        });

        demoteStudent.setOnAction(event -> {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                if(students.size() == 1) {
                    studentDao.demoteStudent(students.get(0));
                } else {
                    studentDao.updateStudentStage(students, false);
                }
                return null;
                }
            };
            task.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Updating records"));
            task.setOnSucceeded(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            task.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
            new Thread(task).start();
        });

        setNewClass.setOnAction(event -> {
            optionsPane.setVisible(true);
            if (selectedStudents.size() > 0) {
                // check if the stages are empty
                if (stages.isEmpty()) {
                    System.out.println("We are fetching data");
                    StageDao stageDao = new StageDao();
                    stages.addAll(stageDao.getGetAllStage());
                }

                //populate the combo box with all the stages
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        classListView.setItems(stages);
                        classListView.setCellFactory(new Callback<ListView<Stage>, ListCell<Stage>>() {
                            @Override
                            public ListCell<Stage> call(ListView<Stage> param) {
                                return new StageCell();
                            }
                        });
                        return null;
                    }
                };
                new Thread(task).start();
            } else {
                Notification.getNotificationInstance().notifyError("You have not selected any classes yet", "Error");
            }
        });

        updateClass.setOnAction(event -> {
            if (selectedStudents.size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "",ButtonType.CANCEL, ButtonType.YES);
                alert.setTitle("Empty selection");
                alert.setContentText("Do you really want to move "+ selectedStudents.size()+ "student(s) to class "+ classListView.getSelectionModel().getSelectedItems() );
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.YES) {
                    studentDao.updateStudentStage(selectedStudents, classListView.getSelectionModel().getSelectedItem());
                }
            }
        });

        cancelUpdate.setOnAction(event -> optionsPane.setVisible(false));

        // when a class is selected
      classListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stage>() {
          @Override
          public void changed(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
              if(newValue !=  null) {
                  // enable the save button for the selected item
                  updateClass.setDisable(false);
              }
          }
      });

        selectAll.setOnAction(event -> {
            if (selectAll.isSelected()) {
                selectAll();
            } else {
                unSelectAll();
            }
        });

        cancel.setOnAction(event -> Utils.closeEvent(event));

        // check when a student is select with a check box
        selectedStudents.addListener(new ListChangeListener<Student>() {
            @Override
            public void onChanged(Change<? extends Student> c) {
                if(selectedStudents.size() > 0 && !hasEnabledFields) {
                    // enable all buttons
                    demoteStudent.setDisable(false);
                    promoteStudent.setDisable(false);
                    setNewClass.setDisable(false);

                    // turn on the flag to show that this action has been performed
                    hasEnabledFields = true;
                } else if(selectedStudents.size() == 0) {
                    // turn off the flag
                    demoteStudent.setDisable(true);
                    promoteStudent.setDisable(true);
                    setNewClass.setDisable(true);
                    hasEnabledFields=false;
                }
            }
        });
//
//        classListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Stage>() {
//            @Override
//            public void onChanged(Change<? extends Stage> c) {
//                System.out.println("We have selected some items");
//            }
//        });
    }

    private void selectAll () {
        for (Student st: students) {
            st.setSelected(true);
            selectedStudents.add(st);
        }
        studentTableView.refresh();
    }

    private void unSelectAll() {
        for (Student st: students) {
            st.setSelected(false);
            selectedStudents.remove(st);
        }
        studentTableView.refresh();
    }

    private void addCheckBoxToTable(TableColumn column) {
        Callback<TableColumn<Student, Void>, TableCell<Student, Void>> cellFactory = new Callback<TableColumn<Student, Void>, TableCell<Student, Void>>() {
            @Override
            public TableCell<Student, Void> call(final TableColumn<Student, Void> param) {
                TableCell<Student, Void> cell = new TableCell<Student, Void>() {
                    CheckBox check = new CheckBox("");
                    {
                        // get the row index and update the selected property field
                        check.setOnAction(e -> {
                            Student t = studentTableView.getItems().get(getIndex());
                            if(check.isSelected()) {
                                t.setSelected(true);
                                selectedStudents.add(t);
                            } else {
                                t.setSelected(false);
                                selectedStudents.remove(t);
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
}
