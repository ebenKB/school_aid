package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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

    private Student student;
    URI path;
    Parent parent;
    private StudentDao studentDao;
    private Notification notification=Notification.getNotificationInstance();
    Pattern text = Pattern.compile("");

    /**
     *  this determines whether there is a restriction in the system or not
     *  e.g. when the system is locked, it means the user might have entered something invalid...
     */
    private BooleanProperty isLocked =new SimpleBooleanProperty(true);

    @FXML
    private VBox navStack[]=new VBox[3];

    private int counter=0;

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
    private void validateTextBox(TextField textField,int min){

        if(textField.getText().trim().matches("[a-zA-Z\\s\\$]+")){
            //check if the name is short
            if(textField.getText().trim().length()<=min){
                //the first name is too short

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
        feedingCombo.getItems().addAll(Student.FeedingStatus.DAILY, Student.FeedingStatus.WEEKLY, Student.FeedingStatus.MONTHLY, Student.FeedingStatus.TERMLY);
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
            System.out.print(surname.getText().trim().length()+" ="+surname.getText());
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
                showErrorPane("First name is too short");
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

        if(parentName.getText().trim().matches("[a-zA-Z\\s\\$]+") && parentName.getText().trim().length()<3){
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

        if(path ==null){
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
           validateTextBox(fname,4);
        });

        surname.setOnKeyReleased(event -> {
            validateTextBox(surname,3);
        });

        oname.setOnKeyReleased(event -> {
            validateTextBox(oname,2);
        });

        parentName.setOnKeyReleased(event -> {
            validateTextBox(parentName,4);
        });

        occupation.setOnKeyReleased(event -> validateTextBox(occupation,4));

        address.setOnKeyReleased(event -> validateTextBox(address,4));

        landmark.setOnKeyReleased(event -> validateTextBox(landmark,5));

        allergyRadiobtn.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue ==yes){
                    allergy.setVisible(Boolean.TRUE);
                }else allergy.setVisible(Boolean.FALSE);
            }
        });

        uploadImg.setOnAction(event -> {
            FileChooser fileChooser=new FileChooser();
            javafx.stage.Stage stage = new javafx.stage.Stage();
             try {
                 path=fileChooser.showOpenDialog(stage).toURI();

             }catch (NullPointerException e){
                 notification.notifyError("You didn't select any image","Empty selection");
             }

            if(path==null){
                return;
            }
            System.out.print("this is the image path to set:"+path.toString());
            image.setImage(new Image(path.toString()));

        });

        classCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Stage>() {
            @Override
            public void changed(ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) {
                if(newValue !=null)
                    fees.setText(String.valueOf(newValue.getFeesToPay()));
            }
        });
        //save a new student
        save.setOnAction((ActionEvent event) -> {
            MyProgressIndicator myProgressIndicator = MyProgressIndicator.getMyProgressIndicatorInstance();

            if(validate()){
                Task prepRecords = new Task() {
                    @Override
                    protected Object call() {
                      prepareStudentRecords();
                        return null;
                    }
                };
                prepRecords.setOnRunning(event1 -> {
                    myProgressIndicator.showActionProgress("Preparing student data...");
                });

                prepRecords.setOnSucceeded(e -> {
                    myProgressIndicator.hideProgress();

                    Task save = new Task() {
                        @Override
                        protected Object call() {
                            try {
                                studentDao =new StudentDao();
                                studentDao.addNewStudent(student);
//                                notification.notifySuccess("You added a new student","Success");
                            }catch (Exception e){
                                notification.notifyError("Sorry! an error occurred.","Error");
                            }
                            return null;
                        }

                    };
                    save.setOnRunning(event2 -> {
                        myProgressIndicator.showActionProgress("Saving student...");
                    });
                    save.setOnSucceeded(event2 -> {
                        myProgressIndicator.hideProgress();
                        notification.notifySuccess("You added a new student","Success");
                    });

                    save.setOnFailed(event1 -> {
                        myProgressIndicator.hideProgress();
                    });
                    new Thread(save).start();
                });

                prepRecords.setOnFailed(event1 -> {
                    myProgressIndicator.hideProgress();
                });
                new Thread(prepRecords).start();
            }
        });
    }

    private void prepareStudentRecords() {
        student =  new Student();
        student.setFirstname(fname.getText().trim().toString().toUpperCase());
        student.setLastname(surname.getText().trim().toUpperCase());
        student.setOthername(oname.getText().trim().toUpperCase());
        if(path != null){
            try {
                Path source = Paths.get(path);
//                Path newdir = Paths.get("/assets/students");
                Path newdir = Paths.get(getClass().getResource(Utils.studentImgPath).toURI());
//                String newDir = "/assets/students";
                FileChannel fileChannel = FileChannel.open(source);
                Long imageSize = fileChannel.size();

                //resize bigger image
                if(imageSize <= 250000){
                    Files.copy(source, newdir.resolve(source.getFileName()), REPLACE_EXISTING);
                }else if(imageSize <= 1000000){
                    ImageHandler.resize(source.toString(),newdir.resolve(source.getFileName()).toString() ,0.4);
                }else if (imageSize <= 2000000){
                    ImageHandler.resize(source.toString(),newdir.resolve(source.getFileName()).toString() ,0.25);
                }else if(imageSize <= 5000000){
                    ImageHandler.resize(source.toString(),newdir.resolve(source.getFileName()).toString() ,0.15);
                }else{
                    ImageHandler.resize(source.toString(),newdir.resolve(source.getFileName()).toString() ,150,200);
                }
//                student.setImage(newdir.resolve(source.getFileName()).toString());
//                student.setImage(String.valueOf(newdir.resolve(source.getFileName())));
                student.setImage(String.valueOf(source.getFileName()));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if(dob != null){
            student.setDob(dob.getValue());
            student.setStage(classCombo.getSelectionModel().getSelectedItem());
        }
        student.setGender(getGender(sexRadiobtn));
        student.setFeedingStatus(feedingCombo.getSelectionModel().getSelectedItem());

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

        if(occupation !=null){
            parent.setOccupation(occupation.getText().trim().toUpperCase());
        }
        parent.setAddress(homeaddress);
        student.setParent(parent);

        StudentAccount account =new StudentAccount();
        student.setAccount(account);
    }
}
