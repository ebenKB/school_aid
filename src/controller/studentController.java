package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.hibernate.engine.jdbc.BlobProxy;

import java.net.URI;
import java.net.URL;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class studentController implements Initializable{

    @FXML
    private AnchorPane root;

    @FXML
    private JFXTextField surname;

    @FXML
    private JFXTextField fname;

    @FXML
    private JFXTextField oname;

    @FXML
    private ComboBox<Stage> classCombo;

    @FXML
    private ComboBox<Student.FeedingStatus> feedingCombo;

    @FXML
    private ToggleGroup sexRadiobtn;

    @FXML
    private JFXDatePicker dob;

    @FXML
    private RadioButton male;

    @FXML
    private RadioButton female;

    @FXML
    private RadioButton no;

    @FXML
    private ToggleGroup allergyRadiobtn;

    @FXML
    private RadioButton yes;

    @FXML
    private JFXTextArea allergy;

    @FXML
    private JFXTextField parentName;

    @FXML
    private JFXTextField contact;

    @FXML
    private JFXTextField occupation;

    @FXML
    private JFXTextField landmark;

    @FXML
    private ImageView image;

    @FXML
    private Button cancel;

    @FXML
    private Button save;

    @FXML
    private JFXTextField address;

    @FXML
    private ImageView infoImageView;

    @FXML
    private Button uploadImg;

    @FXML
    private Pane imageViewPane;

    @FXML
    private Label infoLabel;

    @FXML
    private TextField fees;

    @FXML
    private RadioButton paysFees;

    @FXML
    private ToggleGroup schoolFeesToggle;

    @FXML
    private RadioButton noFees;

    @FXML
    private RadioButton feedingYes;

    @FXML
    private ToggleGroup radioTogglePayFeeding;

    @FXML
    private RadioButton feedingNo;

    @FXML
    private JFXTextField previousSchool;

    @FXML
    private JFXTextField guardian_1_name;

    @FXML
    private JFXTextField guardian_2_name;

    @FXML
    private JFXTextField guardian_2_contact;

    @FXML
    private JFXTextField guardian_1_contact;

    @FXML
    private JFXTextField relationshipToWard;

    private Student student;
    byte [] studentImage;
//    private StudentDetails details;

    URI path;
    Parent parent;
    private StudentDao studentDao;
    private Notification notification=Notification.getNotificationInstance();
    Pattern text = Pattern.compile("");
    private  List<Student> existing =null;

    /**
     *  this determines whether there is a restriction in the system or not
     *  e.g. when the system is locked, it means the user might have entered something invalid...
     */
    private BooleanProperty isLocked =new SimpleBooleanProperty(true);

    @FXML
    private VBox navStack[]=new VBox[3];

    private Boolean isValidUserDetails(Student student){
        return false;
    }

    private void showErrorPane(String message){
        infoLabel.setText(message);
    }

    /**
     *
     * @param textField the text field to validate
     * @param min the minimum characters required for the text field.
     */
    private void checkTextBoxLength(TextField textField,int min){

        if(textField.getText().trim().matches("[a-zA-Z\\s\\$]+")){
            //check if the name is short
            if(textField.getText().trim().length()< min){

                showErrorPane(textField.getAccessibleText()+" "+"is too short");
                infoLabel.setTextFill(Color.valueOf("#fc0303"));

            }else{
                //the first name is accepted
                showErrorPane(textField.getAccessibleText()+" "+"is valid");
                infoLabel.setTextFill(Color.valueOf("#3CCC13"));
            }
        }
    }
    private void populateCombo(){
        customizeCombobox();
        StageDao stageDao = new StageDao();
        List<Stage> list =stageDao.getGetAllStage();
        for(Stage s:list){
            classCombo.getItems().add(s);
        }
    }

    private void populateFeedingCombo(){
        //populate the feeding type
        feedingCombo.getItems().clear();
        feedingCombo.getItems().addAll(Student.FeedingStatus.DAILY, Student.FeedingStatus.PERIODIC, Student.FeedingStatus.SEMI_PERIODIC);
        feedingCombo.getSelectionModel().select(Student.FeedingStatus.DAILY);
    }
    private void customizeCombobox(){
        CustomCombo.getInstance().overrideCombo(classCombo);
    }

    private Boolean validate(){

        if(surname.getText().trim().isEmpty()){
            showErrorPane("Please provide a sur name");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if(surname.getText().trim().matches("[a-zA-Z\\s\\$]+") && surname.getText().trim().length()<3){
            showErrorPane("Surname name is too short");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if( ! surname.getText().trim().matches("[a-zA-Z\\s\\$]+")){
            showErrorPane("Surname cannot contain figures and special characters");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }
        if(fname.getText().trim().isEmpty()){
            showErrorPane("Please provide a First name");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if(fname.getText().trim().matches("[a-zA-Z+\\s\\$]") && fname.getText().trim().length()<3){
            showErrorPane("First name is too short");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if( ! fname.getText().trim().matches("[a-zA-Z\\s\\$]+")){
            showErrorPane("First name cannot contain figures and special characters");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if(oname.getText().trim().length()>0){
            if(oname.getText().trim().matches("[a-zA-Z+\\s\\$]") && oname.getText().trim().length()<1){
                showErrorPane("Other  name is too short");
                infoLabel.setTextFill(Color.valueOf("#fc0303"));
                return false;
            }

            if( ! oname.getText().trim().matches("[a-zA-Z\\s\\$]+")){
                showErrorPane("Other name cannot contain figures and special characters");
                infoLabel.setTextFill(Color.valueOf("#fc0303"));
                return false;
            }
        }

        if(classCombo.getSelectionModel().getSelectedItem() ==null){
            showErrorPane("Please select a class");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }
        if(sexRadiobtn.getSelectedToggle() ==null){
            showErrorPane("Please select gender");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if(schoolFeesToggle.getSelectedToggle() ==null){
            showErrorPane("Indicate whether the child will pay fees or not.");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }


        if(allergyRadiobtn.getSelectedToggle() ==null){
            showErrorPane("Please indicate whether the student has allergy or not");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }
        if(allergyRadiobtn.getSelectedToggle()==yes && allergy.getText().trim().isEmpty()){
            showErrorPane("Please indicate the allergy the student has");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if(parentName.getText().trim().matches("[a-zA-Z\\s\\$]+") && parentName.getText().trim().length() < 3){
            showErrorPane("First name is too short");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if( ! parentName.getText().matches("[a-zA-Z\\s\\$]+")){
            showErrorPane("Parent name cannot contain figures and special characters");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }
        if(parentName.getText().trim().isEmpty()){
            showErrorPane("Please provide a name for the parent or guardian");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }
        if(contact.getText().trim().isEmpty()){
            showErrorPane("Please provide contact for the parent/guardian");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));
            return false;
        }

        if(relationshipToWard.getText().trim().isEmpty()) {
            showErrorPane("What is the relationship of to the ward");
            infoLabel.setTextFill(Color.valueOf("#fc0303"));

            return false;
        }

        if(path == null){
            Alert alert =new Alert(Alert.AlertType.WARNING,"",ButtonType.NO,ButtonType.YES);
            alert.setHeaderText("No Image selected");
            alert.setContentText("You did not select an image for this student.\nDo you want to continue without am image?");
            Optional<ButtonType>result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.YES;
        }

        return true;
    }

    private Optional<ButtonType> showEmptyFieldAlert(TextField textField){
        Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.OK,ButtonType.CANCEL);
        alert.setContentText("You did not provided any data for "+" "+textField.getId()+"\nDo you want to leave it blank?");
        alert.setHeaderText("Empty field");
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }

    private String getGender(ToggleGroup toggleGroup){
        if(toggleGroup.getSelectedToggle() ==male)
            return "Male";

        else if(toggleGroup.getSelectedToggle() ==female)
            return "Female";

        return "N/A";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //add initial items to the combo box
        populateCombo();
        populateFeedingCombo();

        //clear the info label
//       infoLabel.setText("");

        //validate the major fields on focus
        fname.setOnKeyReleased(event -> {
            checkTextBoxLength(fname,2);
        });

        surname.setOnKeyReleased(event -> {
            checkTextBoxLength(surname,3);
        });

        oname.setOnKeyReleased(event -> {
            checkTextBoxLength(oname,2);
        });

        parentName.setOnKeyReleased(event -> {
            checkTextBoxLength(parentName,3);
        });

        occupation.setOnKeyReleased(event -> checkTextBoxLength(occupation,3));

        address.setOnKeyReleased(event -> checkTextBoxLength(address,3));

        landmark.setOnKeyReleased(event -> checkTextBoxLength(landmark,5));

        allergyRadiobtn.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue ==yes){
                    allergy.setVisible(Boolean.TRUE);
                }else allergy.setVisible(Boolean.FALSE);
            }
        });

        uploadImg.setOnAction(event -> {
            // get the image path
            URI path = ImageHandler.getImagePath();

            // set the image to the image view
            if(path != null) {
                image.setImage(new Image(path.toString()));
            }

            // preparing the image
            try {
                byte[] a = ImageHandler.changeToBLOB(path);
                studentImage = a;

//                student.setPicture(a);
            } catch (Exception e){
                e.printStackTrace();
                System.out.println("an error occurred while converting the file"+ e);
            }
//            FileChooser fileChooser=new FileChooser();
//            javafx.stage.Stage stage = new javafx.stage.Stage();
//             try {
//                 path=fileChooser.showOpenDialog(stage).toURI();
//
//             }catch (NullPointerException e){
//                 notification.notifyError("You didn't select any image","Empty selection");
//             }
//
//            if(path==null){
//                return;
//            }
//            image.setImage(new Image(path.toString()));

        });

        classCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stage>() {
            @Override
            public void changed(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
                if(newValue !=null && newValue.getBill() != null){
//                    fees.setText(String.valueOf(newValue.getFeesToPay()));
                    fees.setText(String.valueOf(newValue.getBill().getTotalBill()));
                }

            }
        });

        //save a new student
        save.setOnAction((ActionEvent event) -> {
            if(validate()){
                FinalizeRecords();
            }
        });
    }

    private Boolean prepareStudentRecords() {
        student =  new Student();
        student.setFirstname(fname.getText().trim().toString().toUpperCase());
        student.setLastname(surname.getText().trim().toUpperCase());
        student.setOthername(oname.getText().trim().toUpperCase());

//        details = new StudentDetails();
//      details.setStudent(student);
//        try{
//            ImageHandler imageHandler =new ImageHandler();
//            imageHandler.setStudentImage(details,path);
//        }catch (Exception e){
//            return false;
//        }

        if(dob != null){
            student.setDob(dob.getValue());
        }

        if(classCombo.getSelectionModel().getSelectedItem() !=null){
            student.setStage(classCombo.getSelectionModel().getSelectedItem());
        }

        // check if the student has a previous school
        if(previousSchool.getText().trim().length() > 0) {
            student.setPreviousSchool(previousSchool.getText().trim());
        }

        // prepare guardian information for the student
        prepStdGuardianInfo(student);

        student.setGender(getGender(sexRadiobtn));
        student.setFeedingStatus(feedingCombo.getSelectionModel().getSelectedItem());

        if(schoolFeesToggle.getSelectedToggle()==paysFees){
            student.setPaySchoolFees(true);
        }else student.setPaySchoolFees(false);

        if(radioTogglePayFeeding.getSelectedToggle() ==feedingYes){
            student.setPayFeeding(true);
        }else if(radioTogglePayFeeding.getSelectedToggle()==feedingNo)
            student.setPayFeeding(false);

        //get the parent's details of the student
        parent=new Parent();
        parent.setname(parentName.getText().toUpperCase());
        parent.setTelephone(contact.getText().trim());

        //set the address of the parent
        Address homeaddress = new Address();
        if(address !=null){
            homeaddress.setLandmark(landmark.getText().toUpperCase());
        }

        if(landmark !=null){
            homeaddress.setLandmark(landmark.getText().trim().toUpperCase());
        }

        if(relationshipToWard.getText().trim().length() > 0) {
            parent.setRelationToWard(relationshipToWard.getText().trim());
        }

        if(occupation !=null){
            parent.setOccupation(occupation.getText().trim().toUpperCase());
        }
        parent.setAddress(homeaddress);
        student.setParent(parent);

        StudentAccount account = new StudentAccount();
        student.setAccount(account);

        // check if the student has an allergy
        if(allergyRadiobtn.getSelectedToggle() == yes) {
            if(allergy.getText().trim().length() > 0) {
                student.setHasAllergy(true);
                Allergy alg = new Allergy();
                alg.setAllergy(allergy.getText().trim());
                List<Allergy>allergies = new ArrayList<>();
                allergies.add(alg);
                student.setAllergies(allergies);

            } else student.setHasAllergy(false);
        } else student.setHasAllergy(false);

        cancel.setOnAction(e->{
            Utils.closeEvent(e);
        });

        // check if there is an image for the student
        if(studentImage != null) {
            student.setPicture(studentImage);
        }

        studentDao=new StudentDao();
        existing=studentDao.getStudent(student);

        if(existing.isEmpty())
            return true;
        return  false;
    }
    private void clearStdField(){
        fname.clear();
        oname.clear();
        surname.clear();
        dob.setValue(null);
        sexRadiobtn.getSelectedToggle().setSelected(false);
        allergyRadiobtn.getSelectedToggle().setSelected(false);
        classCombo.getSelectionModel().clearSelection();
        allergy.clear();
        image.setImage(null);
        classCombo.getSelectionModel().clearSelection();
        studentImage = null;
    }

    private void clearParentField(){
        parentName.clear();
        contact.clear();
        occupation.clear();
        address.clear();
        landmark.clear();
        feedingCombo.getSelectionModel().select(Student.FeedingStatus.DAILY);
    }

    private void prepStdGuardianInfo(Student student) {
        try {
            List<Guardian>guardians = new ArrayList<>();
            // check if the student has a guardian

            Guardian guardian;
            guardian = createGuardian(guardian_1_name, guardian_1_contact);

            if (guardian != null) {
                guardians.add(guardian);
            }

            guardian = createGuardian(guardian_2_name, guardian_2_contact);
            if(guardian != null) {
                guardians.add(guardian);
            }

            // add the guardians to the students
            if(!guardians.isEmpty()) {
                student.setGuardian(guardians);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // a guardian is any person who can stand in for the parent in their absence
    private Guardian createGuardian(TextField name, TextField contact) {
        if(name.getText().trim().length() == 0 && contact.getText().trim().length() == 0)
            return null;

        Guardian guardian = new Guardian();
        // check if there is a name
        if(name.getText().trim().length()>0){
            guardian.setFullname(name.getText().trim());
        }

        // check if there is a contact
        if(contact.getText().trim().length()>0) {
            guardian.setContact(contact.getText().trim());
        }
        return guardian;
    }

//    private  Boolean exists(Student student){
//        Task task =new Task() {
//            @Override
//            protected Object call() throws Exception {
//                studentDao=new StudentDao();
//                if(studentDao.getStudent(student)!=null){
//                    return  true;
//                }
//                return null;
//            }
//        };
//        task.setOnRunning(e->MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Checking if the student exists..."));
//        task.setOnSucceeded(e->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
//        task.setOnFailed(e->MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
//
//        new Thread(task).start();
//
//        return false;
//    }

    // finalize the student records to be saved to the database
    private void FinalizeRecords(){
        Task prepRecords = new Task() {
            @Override
            protected Object call() {
                if(prepareStudentRecords())
                    saveRecords();
                return null;
            }
        };
        prepRecords.setOnRunning(event1 -> {
            MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Preparing student data...");
        });

        prepRecords.setOnSucceeded(e -> {
            MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();

            //check if there are some records already with the same details.
            if(! existing.isEmpty()){
                ButtonType details =new ButtonType("Details");
                Alert alert =new Alert(Alert.AlertType.WARNING,"",details,ButtonType.YES,ButtonType.NO);
                alert.setTitle("Duplicate Entry");
                alert.setHeaderText(existing.size()+" "+"Student(s)found with the same details.");
                alert.setContentText("Do you want to clear the fields and save another student?");
                Optional<ButtonType> result = alert.showAndWait();

                if(result.isPresent() && result.get() == ButtonType.YES)
                    clearStdField();
                else if(result.isPresent() && result.get() ==details){
                    showExistingStudentList();
                }
            }
        });

        prepRecords.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        Utils.showTaskException(prepRecords);
        new Thread(prepRecords).start();
    }

    // If any students are found with similar records, show them here
    private void showExistingStudentList() {
        //create a list view and show the existing records on it
        AnchorPane pane =new AnchorPane();
        pane.setId("root");
        pane.setPrefWidth(370);
        pane.setPrefHeight(500);
        ListView<String> listView =new ListView();

        for (Student s : existing){
            listView.getItems().add(s.toString());

            javafx.stage.Stage stage =new javafx.stage.Stage();
            Button save =new Button("Save Anyway");
            Button cancel =new Button("Cancel");
            Button back = new Button("Go Back");

            HBox hBox =new HBox();
            hBox.getChildren().addAll(back,cancel,save);
            hBox.setSpacing(10.5);

            VBox vBox=new VBox();
            vBox.getChildren().addAll(listView,hBox);
            vBox.setLayoutX(70);
            vBox.setLayoutY(20);
            vBox.setSpacing(15);
            pane.getChildren().add(vBox);
            Scene scene =new Scene(pane);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Existing Records");
            stage.setWidth(400);
            stage.setHeight(500);
            stage.show();

            //make sure the user confirms the save action
            save.setOnAction(event->{
                Alert confirm =new Alert(Alert.AlertType.WARNING,"",ButtonType.CANCEL, ButtonType.YES);
                confirm.setTitle("Warning");
                confirm.setHeaderText("We have found some students with the same details.");
                confirm.setContentText("If you continue to save, it can result in having duplicate entry.\n Are " +
                        "you sure you want to SAVE this student?");

                Optional<ButtonType> confirmSave = confirm.showAndWait();
                if(confirmSave.isPresent() && confirmSave.get() ==ButtonType.YES){
                    saveRecords();
                    stage.close();
                }
            });

            cancel.setOnAction(event -> {
                Alert confirmCancel = new Alert(Alert.AlertType.WARNING,"",ButtonType.NO,ButtonType.YES);
                confirmCancel.setTitle("Clear Fields");
                confirmCancel.setHeaderText("If you cancel, all fields will be cleared.\nClick YES to continue.");
                Optional<ButtonType> confirm = confirmCancel.showAndWait();
                if(confirm.isPresent() && confirm.get() ==ButtonType.YES){
                    stage.close();
                    clearStdField();
                    clearParentField();
                }
            });
            back.setOnAction(event->stage.close());
        }
    }

    private void saveRecords(){
        Task save = new Task() {
            @Override
            protected Object call() {
                try {
                    studentDao = new StudentDao();
                    studentDao.addNewStudent(student);
                }catch (Exception e){
                    notification.notifyError("Sorry! an error occurred.","Error");
                }
                return null;
            }
        };
        save.setOnRunning(event2 -> {
            MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Saving student...");
        });

        save.setOnSucceeded(event2 -> {
            MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
            notification.notifySuccess("You added a new student","Success");
            clearStdField();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.YES,ButtonType.NO);
            alert.setTitle("Clear Fields");
            alert.setHeaderText("Do you want to clear the Parent fields?\nIf you want to save another student with the \n " +
                    "same parent, click NO otherwise click YES.");
            Optional<ButtonType>result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.YES)
                clearParentField();

            Task checkin = new Task() {
                @Override
                protected Object call() throws Exception {
                    TermDao term = new TermDao();
                    if (term.getCurrentDate(true).equals(LocalDate.now())){
                        AttendanceTemporaryDao dao = new AttendanceTemporaryDao();
                        dao.createAttendanceSheet(student, LocalDate.now());
                    }
                    return null;
                }
            };
            new Thread(checkin).start();
        });

        save.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        Utils.showTaskException(save);
        new Thread(save).start();
    }
}
