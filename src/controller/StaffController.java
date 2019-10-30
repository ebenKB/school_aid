package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.org.apache.xml.internal.security.Init;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StaffController implements Initializable {
    @FXML
    private Pane root;

    @FXML
    private JFXTextField full_name;

    @FXML
    private JFXTextField contact;

    @FXML
    private JFXTextField home_address;

    @FXML
    private JFXTextField landmark;

    @FXML
    private VBox subjects;

    @FXML
    private CheckComboBox<Stage> classesCombo;

    @FXML
    private CheckComboBox<Course> coursesCombo;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXButton save;

    @FXML
    private Label errorLabel;
//
//    private ObservableList<Stage> stages = FXCollections.observableArrayList();
//    private ObservableList<Course>courses = FXCollections.observableArrayList();

    private List<Stage> stages;
    private List<Course>courses;
    private List<Stage> selectedStages;
    private List<Course> selectedCourses;

    ValidationSupport validator = new ValidationSupport();
    public void init() {
        StageDao stageDao = new StageDao();
        stages = stageDao.getGetAllStage();
        CourseDao courseDao = new CourseDao();
        courses = courseDao.getAllCourses();

        // set items to the combo box
        classesCombo.getItems().addAll(stages);
        coursesCombo.getItems().addAll(courses);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set validation rules
        validator.registerValidator(full_name, true, Validator.createEmptyValidator("Name is required"));
//        validator.registerValidator(full_name, Validator.createRegexValidator("Name should follow this format", "\\[a-zA-Z]+/S/i \\", Severity.ERROR ));

//        validator.registerValidator(contact, true, Validator.createEmptyValidator("Provide a contact"));
        coursesCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Course>() {
            @Override
            public void onChanged(Change<? extends Course> c) {
                selectedCourses = coursesCombo.getCheckModel().getCheckedItems();
            }
        });

        classesCombo.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Stage>() {
            @Override
            public void onChanged(Change<? extends Stage> c) {
                selectedStages = classesCombo.getCheckModel().getCheckedItems();
            }
        });

        save.setOnAction(event -> {
            // check if the fields are valid
            if (! validator.isInvalid()) {
                errorLabel.setText("");
                Staff staff = new Staff();
                staff.setFull_name(full_name.getText().trim());
                staff.setContact(contact.getText().trim());

                // check if the address fields are provided
                if (landmark.getText().trim().length() > 0 || home_address.getText().trim().length() > 0) {
                    Address address = new Address();
                    address.setLandmark(landmark.getText().trim());
                    address.setHomeAddress(home_address.getText().trim());
                    staff.setAddress(address);
                }

                // save the staff to the database
                StaffDao staffDao = new StaffDao();
                staffDao.createStaff(staff);

            } else  {
                System.out.println("The fields are not valid");
                errorLabel.setText("Some fields are not valid. Please complete all fields marked X");
            }
        });
    }
}
