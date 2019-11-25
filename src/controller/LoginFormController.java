package controller;

import com.hub.schoolAid.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class LoginFormController implements Initializable{

    @FXML
    private AnchorPane loginPane;

    @FXML
    private JFXButton close;

    @FXML
    private JFXTextField username;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton login;

    @FXML
    private Hyperlink forgetPass;

    @FXML
    private Hyperlink create;

    @FXML
    private Label sessionLable;


    private Notification notification=Notification.getNotificationInstance();

    public static  User user;
    public mainController mainController;

    public void init(){
        if(Initializer.isLocked){
            sessionLable.setText("Your session has expired. Please Log in to continue.");
        }
    }

    private Boolean validate(){
        if(username.getText().isEmpty()){
            return false;
        }
        return !password.getText().isEmpty();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        login.setOnAction(event -> {
           login(event);
        });

        create.setOnAction(event -> {
                 ((Node)(event).getSource()).getScene().getWindow().hide();
            try {
                Parent  root ;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/UserAccountForm.fxml"));
                root = fxmlLoader.load();
                UserAccountController userAccountController =fxmlLoader.getController();
                userAccountController.init(this);

                //setup the view
                Scene scene= new Scene(root);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.UTILITY);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        close.setOnAction(event -> {
//            ((Node)(event).getSource()).getScene().getWindow().hide();

            if(Initializer.isLoggedin && Initializer.isLocked){
                Alert alert =new Alert(Alert.AlertType.WARNING,"",ButtonType.OK);
                alert.setContentText("You cannot perfom this operation.\nPlease log in to close your session.");
                alert.setHeaderText("Action not allowed");
                alert.show();
                return;
            }
            System.exit(1);
        });
    }

    public void authenticateUser(ActionEvent event,User user){
//        ((Node)(event).getSource()).getScene().getWindow().hide();
         UserDao userDao = new UserDao();
        try{
            User auth =userDao.getUser(user);
            if(auth == null) {
                Notification.getNotificationInstance().notifyError("User not found", "Invalid credentials");
            } else {

            //log the user in
                if( ! Initializer.isLoggedin){
                    Parent parent;
                    FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/view/main.fxml"));
                    try {
                        parent = fxmlLoader.load();
                        mainController controller = fxmlLoader.getController();
                        this.mainController=controller;
                        controller.init(user);

                        Scene scene = new Scene(parent);
                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setScene(scene);
                        stage.setMaximized(Boolean.TRUE);
                        stage.show();

                        //hide the old form
                        ((Node)(event).getSource()).getScene().getWindow().hide();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        Initializer.isLoggedin=true;
                        Initializer.isLocked=false;
                    }
                }
                else{
                    ((Node)(event).getSource()).getScene().getWindow().hide();
                    Initializer.isLocked=false;
                }
            }
        }catch (NoResultException e){
            notification.notifyError("Sorry! we didn't find any user with these credentials.\nPlease create an account","No user found");
        }
    }

    public void login(ActionEvent event) {
        if(validate()){
            user = new User();
            user.setUsername(username.getText().trim());
            user.setPassword(password.getText().trim());

           //find the user
            authenticateUser(event,user);
        }
    }
}
