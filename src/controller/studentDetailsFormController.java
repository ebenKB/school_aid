package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.io.FileUtils;
import org.controlsfx.control.ToggleSwitch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class studentDetailsFormController implements Initializable{

    @FXML
    private JFXTabPane stdDetailsTabPane;

    @FXML
    private Tab profileTable;

    @FXML
    private JFXComboBox<Student.FeedingStatus> paymentMode;

    @FXML
    private Tab parentTab;

    @FXML
    private Tab accountTab;

    @FXML
    private  Tab feedingTab;

    @FXML
    private ImageView studentImage;

    @FXML
    private Hyperlink changeImage;

    @FXML
    private JFXTextField fname;

    @FXML
    private JFXTextField surname;

    @FXML
    private JFXTextField oname;

    @FXML
    private JFXTextField stage;

    @FXML
    private JFXDatePicker dob;

    @FXML
    private JFXTextField regDate;

    @FXML
    private JFXTextField phone;

    @FXML
    private JFXButton close;

    @FXML
    private Button btnEditFees;

    @FXML
    private Button save;

    @FXML
    private Button editFees;

    @FXML
    private CheckBox btnEditable;

    @FXML
    private Label infoLable;

    @FXML
    private Label imgInfoLabel;

    @FXML
    private HBox changesLabel;

    @FXML
    private Label changesCounter;

    @FXML
    private FontAwesomeIconView padlock;

    @FXML
    private CheckBox editFeeding;

    @FXML
    private JFXTextField parentName;

    @FXML
    private JFXTextField parentOccupation;

    @FXML
    private JFXTextField parentPhone;

    @FXML
    private JFXTextField parentAdd;

    @FXML
    private JFXTextArea parentLandMark;

    @FXML
    private TextField saleItems;

    @FXML
    private ToggleSwitch feedingToggle;

    @FXML
    private ToggleSwitch schFeesToggle;

    @FXML
    private TextField amountPaid;

    @FXML
    private TextField balance;

    @FXML
    private TextField salesBalance;

    @FXML
    private JFXListView<String> salesListView;

    @FXML
    private CheckBox checkListView;

    @FXML
    private Pane indicator;

    @FXML
    private TextField feesBalance;

    @FXML
    private TextField  feedingFeeCredit;

    @FXML
    private JFXTextField feedingAcc;

    @FXML
    private Hyperlink schFeesSummary;


    //mainController mainController;
    private Student student;
    private Student newStudent;
    private int counter=0;
    private int parentChanges= 0;
    URI path;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        studentImage.setImage(new Image(student.getImage().replace("/","'\'")));
        btnEditable.setOnMouseClicked(event -> {
            if(btnEditable.isSelected()){
                newStudent=new Student();
                newStudent.setAccount(student.getAccount());
                newStudent.setParent(student.getParent());
                padlock.setVisible(Boolean.FALSE);
                fname.setEditable(Boolean.TRUE);
                surname.setEditable(Boolean.TRUE);
                oname.setEditable(Boolean.TRUE);
                dob.setEditable(Boolean.TRUE);
                phone.setEditable(Boolean.TRUE);
                parentName.setEditable(Boolean.TRUE);
                parentPhone.setEditable(Boolean.TRUE);
                parentOccupation.setEditable(Boolean.TRUE);
                parentLandMark.setEditable(Boolean.TRUE);
                parentAdd.setEditable(Boolean.TRUE);
                editFeeding.setVisible(Boolean.TRUE);
                changeImage.setVisible(Boolean.TRUE);
                paymentMode.setDisable(false);
                infoLable.setText("In Editing Mode.");
                infoLable.setTextFill(Color.valueOf("#3CCC13"));
            }else{
                newStudent=null;
                padlock.setVisible(Boolean.TRUE);
                fname.setEditable(Boolean.FALSE);
                surname.setEditable(Boolean.FALSE);
                oname.setEditable(Boolean.FALSE);
                dob.setEditable(Boolean.FALSE);
                phone.setEditable(Boolean.FALSE);
                parentName.setEditable(Boolean.FALSE);
                parentPhone.setEditable(Boolean.FALSE);
                parentOccupation.setEditable(Boolean.FALSE);
                parentLandMark.setEditable(Boolean.FALSE);
                parentAdd.setEditable(Boolean.FALSE);
                editFeeding.setVisible(Boolean.FALSE);
                changeImage.setVisible(Boolean.FALSE);
                paymentMode.setDisable(true);
                infoLable.setText("Editing is Locked.");
                infoLable.setTextFill(Color.valueOf("#fc0303"));
            }
        });

        editFeeding.setOnAction(e->{
            if(editFeeding.isSelected()){
               Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.YES,ButtonType.NO);
               alert.setTitle("Enable editing");
               alert.setHeaderText("You are about to change the feeding fee for this student.\n" +
                       "If you change the feeding fee, the student will be charged with the new amount.\n" +
                       "Are you sure you want to continue ?");
               Optional<ButtonType>result =alert.showAndWait();
               if(result.isPresent() &&  result.get()==ButtonType.YES)
                   feedingAcc.setEditable(true);
               else editFeeding.setSelected(false);

            }else feedingAcc.setEditable(false);
        });

        fname.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
               if(!newValue){
                   if(! fname.getText().trim().equals(student.getFirstname())){
                       changed=true;
                        if(numChanges==0){
                            numChanges=1;
                            updateChangeCounter(numChanges);
                            newStudent.setFirstname(fname.getText().trim());
                        }
                       showChangesLabel();
                   }else {
                       if(changed){
                           revertChanges(numChanges);
                           newStudent.setFirstname(null);
                           changed=false;
                           showChangesLabel();
                           numChanges=0;
                       }
                   }
               }else{
                   fname.setOnKeyTyped(event -> {
                       if(!btnEditable.isSelected()){
                           notifyEditLock();
                       }
                   });
               }
            }
        });

        surname.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    if(! surname.getText().trim().equals(student.getLastname())){
                        changed=true;
                        if(numChanges==0){
                            numChanges=1;
                            updateChangeCounter(numChanges);
                            newStudent.setLastname(surname.getText().trim());
                        }
                        showChangesLabel();
                    }else {
                        if(changed){
                            revertChanges(numChanges);
                            newStudent.setLastname(null);
                            changed=false;
                            showChangesLabel();
                            numChanges=0;
                        }
                    }
                }else{
                    surname.setOnKeyTyped(event -> {
                        if(!btnEditable.isSelected()){
                            notifyEditLock();
                        }
                    });
                }
            }
        });

        oname.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    if(! oname.getText().trim().equals(student.getOthername())){
                        changed=true;
                        if(numChanges==0){
                            numChanges=1;
                            updateChangeCounter(numChanges);
                            newStudent.setOthername(oname.getText().trim());
                        }
                        showChangesLabel();
                    }else {
                        if(changed){
                            revertChanges(numChanges);
                            newStudent.setOthername(null);
                            changed=false;
                            showChangesLabel();
                            numChanges=0;
                        }
                    }
                }else{
                    oname.setOnKeyTyped(event -> {
                        if(!btnEditable.isSelected()) {
                            notifyEditLock();
                        }
                    });
                }
            }
        });

        dob.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    if(! dob.getValue().equals(student.getDob())){
                        changed=true;
                        if(numChanges==0){
                            numChanges=1;
                            updateChangeCounter(numChanges);
                            newStudent.setDob(dob.getValue());
                        }
                        showChangesLabel();
                    }else {
                        if(changed){
                            revertChanges(numChanges);
                            newStudent.setDob(null);
                            changed=false;
                            showChangesLabel();
                            numChanges=0;
                        }
                    }
                }else{
                    dob.setOnKeyTyped(event -> {
                        if(!btnEditable.isSelected()){
                            notifyEditLock();
                        }
                    });
                }
            }
        });


//        feedingAcc.focusedProperty().addListener(new ChangeListener<Boolean>() {
//            Boolean changed=false;
//            int numChanges=0;
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//               if(feedingAcc.isEditable()){
//                   if(!newValue){
//                       if(! feedingAcc.getText().trim().equals(student.getAccount().getFeedingFeeToPay())){
//                           changed=true;
//                           if(numChanges==0){
//                               numChanges=1;
//                               updateChangeCounter(numChanges);
//                               newStudent.getAccount().setFeedingFeeToPay(Double.valueOf(feedingAcc.getText().trim()));
//                           }
//                           showChangesLabel();
//                       }else {
//                           if(changed){
//                               revertChanges(numChanges);
//                               newStudent.getAccount().setFeedingFeeToPay(0.0);
//                               changed=false;
//                               showChangesLabel();
//                               numChanges=0;
//                           }
//                       }
//                   }else{
//                       feedingAcc.setOnKeyTyped(event -> {
//                           if(!btnEditable.isSelected()){
//                               notifyEditLock();
//                           }
//                       });
//                   }
//               }
//            }
//        });

        feedingAcc.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                  if(!newValue && (newStudent !=null) && (newStudent.getAccount()!=null) && (editFeeding.isSelected())){

                      if(Double.valueOf(feedingAcc.getText().trim())  != student.getAccount().getFeedingFeeToPay() ){
                          changed=true;
                          if(numChanges==0){
                              numChanges=1;
                              updateChangeCounter(numChanges);
                              newStudent.getAccount().setFeedingFeeToPay(Double.valueOf(feedingAcc.getText().trim()));
                          }
                          showChangesLabel();
                      }else {
                          if(changed){
                              revertChanges(numChanges);
                              newStudent.getAccount().setFeedingFeeToPay(0.0);
                              changed=false;
                              showChangesLabel();
                              numChanges=0;
                          }
                      }
                  }else{
                      feedingAcc.setOnKeyTyped(event -> {
                          if(!btnEditable.isSelected() || editFeeding.isSelected()){
                              notifyEditLock();
                          }
                      });
                  }
            }
        });

//        paymentMode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Student.FeedingStatus>() {
//            Boolean changed=false;
//            int numChanges=0;
//
//            @Override
//            public void changed(ObservableValue<? extends Student.FeedingStatus> observable, Student.FeedingStatus oldValue, Student.FeedingStatus newValue) {
//
//            }
//        });
       paymentMode.focusedProperty().addListener(new ChangeListener<Boolean>() {
           Boolean changed=false;
           int numChanges=0;
           @Override
           public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
               if(!newValue){
                   if(! paymentMode.getSelectionModel().getSelectedItem().equals(student.getFeedingStatus())){
                       changed=true;
                       if(numChanges==0){
                           numChanges=1;
                           updateChangeCounter(numChanges);
                           newStudent.setFeedingStatus(paymentMode.getSelectionModel().getSelectedItem());
                       }
                       showChangesLabel();
                   }else {
                       if(changed){
                           revertChanges(numChanges);
                           newStudent.setFeedingStatus(null);
                           changed=false;
                           showChangesLabel();
                           numChanges=0;
                       }
                   }
               }else{
                   oname.setOnKeyTyped(event -> {
                       if(!btnEditable.isSelected()){
                           notifyEditLock();
                       }
                   });
               }
           }
       });
        phone.setOnKeyTyped(event -> {
            promptEditNotAllowed();
        });

        stage.setOnKeyTyped(event -> {
            promptEditNotAllowed();
        });

        regDate.setOnKeyTyped(event -> {
            promptEditNotAllowed();
        });

        //listen to changes to the child's parent
        parentName.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;

            private void allowParentChanges() {
                if(numChanges==0){
                    numChanges=1;
                    parentChanges +=1;
                    updateChangeCounter(numChanges);
                    newStudent.getParent().setname(parentName.getText().trim());
                }
                showChangesLabel();
            }
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
               try{
                   if(!parentName.getText().trim().equals(student.getParent().getname())){
                       changed=true;
                       allowParentChanges();
                   }else {
                       if(changed){
                           revertChanges(numChanges);
                           newStudent.getParent().setname(null);
                           changed=false;
                           showChangesLabel();
                           numChanges=0;
                           parentChanges-=1;
                       }
                   }
               }catch (NullPointerException n){
                  allowParentChanges();
               }
                }else{
                    parentName.setOnKeyTyped(event -> {
                        if(!btnEditable.isSelected()){
                            notifyEditLock();
                        }
                    });
                }
            }
        });

        parentOccupation.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    if(! parentOccupation.getText().trim().equals(student.getParent().getOccupation())){
                        changed=true;
                        parentChanges+=1;
                        if(numChanges==0){
                            numChanges=1;
                            updateChangeCounter(numChanges);
                            newStudent.getParent().setOccupation(parentOccupation.getText().trim());
                        }
                        showChangesLabel();
                    }else {
                        if(changed){
                            revertChanges(numChanges);
                            newStudent.getParent().setOccupation(null);
                            changed=false;
                            showChangesLabel();
                            numChanges=0;
                            parentChanges-=1;
                        }
                    }
                }else{
                    parentOccupation.setOnKeyTyped(event -> {
                        if(!btnEditable.isSelected()){
                            notifyEditLock();
                        }
                    });
                }
            }
        });

        parentPhone.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    if(! parentPhone.getText().trim().equals(student.getParent().getTelephone())){
                        changed=true;
                        parentChanges+=1;
                        if(numChanges==0){
                            numChanges=1;
                            updateChangeCounter(numChanges);
                            newStudent.getParent().setTelephone(parentPhone.getText().trim());
                        }
                        showChangesLabel();
                    }else {
                        if(changed){
                            revertChanges(numChanges);
                            newStudent.getParent().setTelephone(null);
                            changed=false;
                            showChangesLabel();
                            numChanges=0;
                            parentChanges-=1;
                        }
                    }
                }else{
                    parentPhone.setOnKeyTyped(event -> {
                        if(!btnEditable.isSelected()){
                            notifyEditLock();
                        }
                    });
                }
            }
        });

        parentAdd.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
               try{
                   if(!newValue){
                       if(! parentAdd.getText().trim().equals(student.getParent().getAddress())){
                           changed=true;
                           parentChanges+=1;
                           if(numChanges==0){
                               numChanges=1;
                               updateChangeCounter(numChanges);
                               newStudent.getParent().getAddress().setHomeAddress(parentAdd.getText().trim());
                           }
                           showChangesLabel();
                       }else {
                           if(changed){
                               revertChanges(numChanges);
                               newStudent.getParent().getAddress().setHomeAddress(null);
                               changed=false;
                               showChangesLabel();
                               numChanges=0;
                               parentChanges-=1;
                           }
                       }
                   }else{
                       parentAdd.setOnKeyTyped(event -> {
                           if(!btnEditable.isSelected()){
                               notifyEditLock();
                           }
                       });
                   }
               }catch (Exception e){

               }
            }
        });

        parentLandMark.focusedProperty().addListener(new ChangeListener<Boolean>() {
            Boolean changed=false;
            int numChanges=0;
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!newValue){
                    if(! parentLandMark.getText().trim().equals(student.getParent().getAddress().getLandmark())){
                        changed=true;
                        parentChanges+=1;
                        if(numChanges==0){
                            numChanges=1;
                            updateChangeCounter(numChanges);
                            newStudent.getParent().getAddress().setLandmark(parentLandMark.getText().trim());
                        }
                        showChangesLabel();
                    }else {
                        if(changed){
                            revertChanges(numChanges);
                            newStudent.getParent().getAddress().setLandmark(null);
                            changed=false;
                            showChangesLabel();
                            numChanges=0;
                            parentChanges-=1;
                        }
                    }
                }else{
                    parentName.setOnKeyTyped(event -> {
                        if(!btnEditable.isSelected()){
                            notifyEditLock();
                        }
                    });
                }
            }
        });

        feedingToggle.setOnMouseClicked(event -> {
           if(btnEditable.isSelected()){
               feedingToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
                   @Override
                   public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                       if(!feedingToggle.isSelected()){
                           Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.YES,ButtonType.NO);
                           alert.setHeaderText("Turn Feeding Fee Off");
                           alert.setContentText("If you turn feeding fee off, the student will not be charged for feeding fee.\n" +
                                   "Are you sure you want to turn this option off?");
                           Optional<ButtonType>result =  alert.showAndWait();
                           if(result.isPresent() && result.get() == ButtonType.YES){
                               TextInputDialog inputDialog = new TextInputDialog();
                               inputDialog.setTitle("Authentication");
                               inputDialog.setHeaderText("Enter your password to continue...");
                               inputDialog.setContentText("To confirm this is you, we need to confirm your password");
                               Optional<String> input = inputDialog.showAndWait();
                               if(input.isPresent() && input.get() !=null){
                                   if(input.get().equals(LoginFormController.user.getPassword())){
                                       feedingToggle.setSelected(Boolean.FALSE);
                                       student.setPayFeeding(false);
//                                           Notification.getNotificationInstance().notifySuccess("");
                                        updateFeedingFee();
                                   }else{
                                       Notification.getNotificationInstance().notifyError("Wrong User password","Authentication Failed");
                                   }
                               }
                           }

                       } else {
                           if(!student.getPayFeeding()){
                               Alert alert = new Alert(Alert.AlertType.WARNING,"",ButtonType.NO,ButtonType.YES);
                               alert.setTitle("Pay Feeding Fee");
                               alert.setHeaderText("Turn on Feeding Fee");
                               alert.setContentText("This student does not pay feeding. \nIf you turn this option on, the student will start paying feeding fee.\n" +
                                       "Are you sure you want to Turn this option ON?");
                               Optional<ButtonType>result= alert.showAndWait();
                               if(result.isPresent() && result.get() ==ButtonType.YES){
                                   feedingToggle.setSelected(Boolean.TRUE);
                                   student.setPayFeeding(Boolean.TRUE);
                                    updateFeedingFee();
                               }
                           }
                       }
                   }
               });
           }else {
               notifyEditLock();
               //revert change to the toggle
               if(feedingToggle.isSelected()){
                   feedingToggle.setSelected(false);
               }else
                   feedingToggle.setSelected(true);
           }
        });

        close.setOnAction(event -> {
            if(counter>0){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"There are unsaved changes.\n Are you sure you want to close the form?", ButtonType.YES,ButtonType.NO);
                alert.setHeaderText(counter+" "+"unsaved Changes");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.YES){
                    ((Node)(event).getSource()).getScene().getWindow();
                }
            }
        });

        save.setOnAction(event -> {
            System.out.println("we want to save");
            Task newRecs  =  new Task() {
                @Override
                protected Object call() {
                    prepareRecordsToSave();
                    StudentDao studentDao=new StudentDao();
                    studentDao.updateStudentRecord(student);

                    return null;
                }
            };
            newRecs.setOnRunning(e -> {
                MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Preparing records to save...");
            });

            newRecs.setOnSucceeded(e ->{
                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
                Notification.getNotificationInstance().notifySuccess("The record has been updated","Success!");
//                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
//                Task saveRecs = new Task() {
//                    @Override
//                    protected Object call() {
//                        StudentDao studentDao=new StudentDao();
//                        if(parentChanges>0){
//                            studentDao.updateStudentRecord(student,student.getParent());
//                        }else{
//                            studentDao.updateStudentRecord(student);
//                        }
//
//                        return true;
//                    }
//                };
//                saveRecs.setOnRunning(e2 ->{
//                    MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Updating student records");
//                });
//
//                saveRecs.setOnSucceeded(e2 ->{
//                    MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
//                    Notification.getNotificationInstance().notifySuccess("The record has been updated","Success!");
//                    refreshFields();
//                    setStudentTabDetails();
//                    setPrentTabDetails();
//                    setAccountTabDetails();
//                });
//
//                saveRecs.setOnFailed(e2->{
//                    MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
//                });
//                new Thread(saveRecs).start();
            });

            newRecs.setOnFailed(e->{
                Notification.getNotificationInstance().notifyError("Error while saving records","Error!");
                MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
            });
            new Thread(newRecs).start();
        });

        stdDetailsTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if(newValue ==parentTab){
                    try {
                        if(parentName.getText().trim().isEmpty() && parentPhone.getText().trim().isEmpty()){
                            //populate the parent fields
                            setPrentTabDetails();
                        }
                    }catch (NullPointerException n){
                        //do nothing...
                    }
                }else if(newValue==accountTab){
                  if(balance.getText().trim().isEmpty() || amountPaid.getText().trim().isEmpty()){
                    setAccountTabDetails();
                  }
                }else if(newValue==feedingTab){
                    prepareFeedingRecords();
                }
            }
        });

        close.setOnAction(e-> {
            Utils.closeEvent(e);
        });

        changeImage.setOnAction(e-> {
            Alert alert =new Alert(Alert.AlertType.CONFIRMATION,"",ButtonType.YES,ButtonType.NO);
            alert.setTitle("Change Image");
            alert.setHeaderText("Your are about to change the image for"+" "+student.getFirstname()+"\nAre you sure you want to continue ?");
            Optional<ButtonType>result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.YES){
                URI path = ImageHandler.getImagePath();
                if(path != null) {
                    try {
                        byte [] imageBytes = ImageHandler.changeToBLOB(path);
                        student.setPicture(imageBytes);
                        studentImage.setImage(new Image(path.toString()));
                        updateChangeCounter(1);
                        showChangesLabel();
                    } catch (IOException ex) {
//                        ex.printStackTrace();
                        Notification.getNotificationInstance().notifyError("Error processing image", "Error");
                    }
                }


//                path=ImageHandler.openImageFile(studentImage);
//                if(path !=null){
////                    counter ++;
////                    showChangesLabel();
//                    //save the image for the student.
//                    Task saveImage =new Task() {
//                        @Override
//                        protected Object call() throws Exception {
//                           updateStudentImage();
//                            return null;
//                        }
//                    };
//                    saveImage.setOnRunning(event-> {
//                        MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Saving student image");
//
//                    });
//                    saveImage.setOnSucceeded(event -> {
//                        MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
//                        Notification.getNotificationInstance().notifySuccess("Image has been saved.","Success");
//                    });
//
//                    saveImage.setOnFailed(event -> {
//                        MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress();
//                        Notification.getNotificationInstance().notifyError("A fatal error occurred while saving the image","Error");
//                        Utils.showTaskException(saveImage);
//                    });
//                    new Thread(saveImage).start();
//                }
            }
        });

        editFees.setOnAction(event -> this.showEditFeesForm());

        schFeesSummary.setOnAction(event -> Utils.showSummaryForm(student));
    }

    private void updateStudentImage() {
        StudentDetails details =new StudentDetails();
        ImageHandler imageHandler =new ImageHandler();
        imageHandler.setStudentImage(details,path);
        StudentDetailsDao detailsDao =new StudentDetailsDao();
        detailsDao.updateDetilsIfExist(student,details);
    }

    private void updateFeedingFee() {
        Task update=new Task() {
            @Override
            protected Object call() {
                StudentDao studentDao = new StudentDao();
                studentDao.updateStudentRecord(student);
                return null;
            }
        };
        update.setOnRunning(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().showActionProgress("Updating student..."));
        update.setOnSucceeded(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        update.setOnFailed(event1 -> MyProgressIndicator.getMyProgressIndicatorInstance().hideProgress());
        new Thread(update).start();
    }

    private void prepareRecordsToSave() {
        //set the new changes to the student obj
        if(newStudent.getFirstname()!=null)
            student.setFirstname(newStudent.getFirstname());
        if(newStudent.getLastname() !=null)
            student.setLastname(newStudent.getLastname());
        if(newStudent.getOthername() !=null)
            student.setOthername(newStudent.getOthername());

        student.setFeedingStatus(newStudent.getFeedingStatus());
        //check if there is a new Image
 //        ImageHandler imageHandler =new ImageHandler();
//        imageHandler.setStudentImage(student,path);

        //check if the parent's records changed
        if(parentChanges > 0){
            if(newStudent.getParent().getname() != null)
                 student.getParent().setname(newStudent.getParent().getname());
            if(newStudent.getParent().getTelephone() !=null)
                student.getParent().setTelephone(parentPhone.getText().trim());
            if(newStudent.getParent().getOccupation() !=null)
                 student.getParent().setOccupation(newStudent.getParent().getOccupation());
            if(newStudent.getParent().getAddress().getHomeAddress()!=null)
                student.getParent().getAddress().setHomeAddress(newStudent.getParent().getAddress().getHomeAddress());
            if(newStudent.getParent().getAddress().getLandmark()!=null)
                student.getParent().getAddress().setLandmark(newStudent.getParent().getAddress().getLandmark());
        }
    }

    private void refreshFields(){
        counter=0;
        parentChanges=0;
        setStudentTabDetails();
        setPrentTabDetails();
        showChangesLabel();
//      setAccountTabDetails();
    }

    // set the details on the student tab
    private void setStudentTabDetails(){
        try{
            if (student.getPicture() != null) {
                ImageHandler.setImage(student.getPicture(), studentImage);
            }
        }catch (Exception e){
            imgInfoLabel.setText("Image not found");
        }

        if(student != null) {
            //populate student details view
            fname.setText(student.getFirstname());
            surname.setText(student.getLastname());
            oname.setText(student.getOthername());
            stage.setText(student.getStage().getName());
            regDate.setText(student.getReg_date().toString());
            dob.setValue(student.getDob());
            phone.setText(student.getParent().getTelephone());
        }
    }

    // set the details on the parent tab pane
    private void setPrentTabDetails() {
        parentName.setText(student.getParent().getname());
        parentPhone.setText(student.getParent().getTelephone());
        parentOccupation.setText(student.getParent().getOccupation());
        parentAdd.setText(student.getParent().getAddress().getHomeAddress());
        parentLandMark.setText(student.getParent().getAddress().getLandmark());
    }

    // set the details on the account tab pane
    private void setAccountTabDetails(){
        SalesDao salesDao = new SalesDao();
        int total = 0;
        Double payment = 0.0;
        Double bal = 0.0;
        Double salesBal = 0.0;
        ObservableList <String> data= FXCollections.observableArrayList();
        List<Sales> sales = salesDao.getAllSalesOfStudent(student);

        if(sales.size() > 0){
            for(Sales s: sales){
                total++;
                payment+=s.getAmountPaid();
                salesBal+=((s.getTotalcost() - s.getAmountPaid())* -1);
                data.add(s.getItem().getName()+"\t"+ "("+s.getItem().getQty()+"piece(s)"+ "*"+s.getItem().getCost()+"="+s.getTotalcost()+")");
            }
            salesBalance.setText(String.valueOf(salesBal));
        }
        checkListView.setOnAction(event -> {
            if(checkListView.isSelected()){
                if( salesListView.getItems().isEmpty()){
                    salesListView.getItems().addAll(data);
                }else {
                    salesListView.setVisible(Boolean.TRUE);
                }
            }else{
                salesListView.setVisible(Boolean.FALSE);
            }
        });

        //set the text fields
        saleItems.setText(String.valueOf(total));
        amountPaid.setText(String.valueOf(payment));
        bal+=student.getAccount().getFeeToPay();

        if(student.getAccount().getFeedingFeeCredit() < 0)
            bal+=(student.getAccount().getFeedingFeeCredit());
//             bal+=(student.getAccount().getFeedingFeeCredit() * -1);

        bal+= salesBal;  // sum up the balance
        balance.setText(String.valueOf(bal));
        feesBalance.setText(String.valueOf(student.getAccount().getFeeToPay()));
        feedingFeeCredit.setText(String.valueOf(student.getAccount().getFeedingFeeCredit()));

        if(bal < 0) {
            //Change the indicator background to red else change to green;
            indicator.setStyle("-fx-background-color: red");
        } else {
            indicator.setStyle("-fx-background-color: green");
        }

        if(total > 0) {
            checkListView.setVisible(Boolean.TRUE);
        }
    }

    private void prepareFeedingRecords() {
        if(student.getPayFeeding()){
            feedingToggle.setSelected(Boolean.TRUE);
        } else feedingToggle.setSelected(Boolean.FALSE);

        if(student.getPaySchoolFees()) {
            schFeesToggle.setSelected(Boolean.TRUE);
        } else schFeesToggle.setSelected(false);

        feedingAcc.setText(String.valueOf(student.getAccount().getFeedingFeeToPay()));

        if(paymentMode.getItems().isEmpty()){
            paymentMode.getItems().add(Student.FeedingStatus.DAILY);
            paymentMode.getItems().add(Student.FeedingStatus.WEEKLY);
            paymentMode.getItems().add(Student.FeedingStatus.MONTHLY);
            paymentMode.getItems().add(Student.FeedingStatus.TERMLY);
            paymentMode.getItems().add(Student.FeedingStatus.PERIODIC);
            paymentMode.getItems().add(Student.FeedingStatus.SEMI_PERIODIC);
        }
        paymentMode.getSelectionModel().select(student.getFeedingStatus());
    }

    public void setStudent(Student student) {
        this.student = student;
        setStudentTabDetails();
    }

    private void showChangesLabel() {
        if(counter>0) {
            changesCounter.setText(String.valueOf(counter));
            changesLabel.setVisible(Boolean.TRUE);
            save.setDisable(Boolean.FALSE);
        }else {
            save.setDisable(Boolean.TRUE);
            changesLabel.setVisible(Boolean.FALSE);
        }
    }

    private void updateChangeCounter(int i){
        counter +=i;
    }
    private void revertChanges(int num){
        counter -=num;
    }

    private void notifyEditLock(){
        WindowsSounds.playWindowsSound();
        infoLable.setText("Please check 'Editable' to continue.");
        infoLable.setTextFill(Color.valueOf("#ffcd05"));
    }

    public void init(Student student)  {
        setStudent(student);
    }

    private void promptEditNotAllowed() {
        WindowsSounds.playWindowsSound();
        infoLable.setText("Editing is not enabled for this field.");
        infoLable.setTextFill(Color.valueOf("#ffcd05"));
    }

    private void showEditFeesForm() {
        javafx.scene.Parent root;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/editFees.fxml"));
        try {
            root = fxmlLoader.load();
            EditFeesController editFeesController = fxmlLoader.getController();
            editFeesController.init(student);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (Exception e) {
            return;
        }
    }

    private void showImagePreview() {
        javafx.scene.Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/previewImage.fxml"));
            root = fxmlLoader.load();
            PreviewImageController controller = new PreviewImageController();
            controller.initialize(this.student.getPicture(), this.student);
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void showSummaryForm() {
//        javafx.scene.Parent root;
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/schFeesSummary.fxml"));
//        try {
//            root = fxmlLoader.load();
//            SchFeesSummaryController controller = fxmlLoader.getController();
//            controller.init(student);
//            Scene scene = new Scene(root);
//            Stage stage = new Stage();
//            stage.setScene(scene);
//            stage.initModality(Modality.APPLICATION_MODAL);
//            stage.initStyle(StageStyle.UTILITY);
//            stage.show();
//        } catch (Exception e) {
//            return;
//        }
//    }

    private void showGeneralForm(String url) {

    }
}
