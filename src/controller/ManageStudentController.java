package controller;

import com.hub.schoolAid.Student;
import com.hub.schoolAid.StudentDao;
import com.hub.schoolAid.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class ManageStudentController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private TableView<Student> studentTableView;


    @FXML
    private TableColumn<?, ?> checkStudents;

    @FXML
    private TableColumn<Student, String> stdNameCol;

    @FXML
    private Button promoteStudent;

    @FXML
    private Button cancel;

    private CheckBox checkBox = new CheckBox();
    private CheckBox selectAll = new CheckBox();
    private StudentDao studentDao = new StudentDao();
    private ObservableList<Student> students = FXCollections.observableArrayList();
    private ObservableList<Student> selectedStudents = FXCollections.observableArrayList();

    public void init() {
        // fetch students
        students.addAll(studentDao.getAllStudents());
        // add checkbox to the column header
        checkStudents.setGraphic(selectAll);
        populateTable();
        System.out.print("We are in the init function");
    }

    private void populateTable() {
        if(students.size() > 0) {
            stdNameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().toString()));
//        classCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getStage().toString()));
//        conductCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        conductCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getConduct()));
//        remarkCol.setCellFactory(TextFieldTableCell.forTableColumn());
//        remarkCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getHeadTracherRemark()));
            studentTableView.setItems(students);
            addCheckBoxToTable(checkStudents);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        promoteStudent.setOnAction(event -> {
           if(students.size() > 0) { // check if there are selected students to promote
               if(students.size() == 1) {
                   studentDao.promoteStudent(students.get(0));
               } else {
                   studentDao.promoteStudent(students);
               }
           }
        });

        selectAll.setOnAction(event -> {
            System.out.print("There are some students that we want to promote");
        });
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
                            if (param.getTableView().getItems().get(getIndex()).getSelected()) {
                                check.setSelected(true);
                            } else {
                                check.setSelected(false);
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
