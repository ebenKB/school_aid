package controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.hub.schoolAid.User;
import com.hub.schoolAid.UserDao;

import java.net.URL;
import java.util.ResourceBundle;

public class UserAccountController implements Initializable{

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField contact;

    @FXML
    private JFXButton create;

    private LoginFormController loginFormController;

    public void init(LoginFormController controller){
        this.loginFormController=controller;
    }

    private Boolean validate(){
        if(username.getText().isEmpty()){
            return false;
        }
        if(password.getText().isEmpty()){
            return false;
        }
        if(contact.getText().isEmpty()){
            return false;
        }
        return contact.getText().trim().matches("[0-9]+");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
      create.setOnAction(event -> {
          if(validate()){
              User user =new User();
              user.setUsername(username.getText().trim());
              user.setPassword(password.getText().trim());
              user.setContact(contact.getText().trim());
              try {
                  UserDao userDao = new UserDao();
                  if(!userDao.isExisting(user.getUsername())){
                      userDao.createUser(user);
                  }else {
                      //there is already a user with the given name. Please try with another name...
                  }

                  System.out.print("we have created a user now...");

                  //log the user in
                  loginFormController.authenticateUser(event,user);

//                  //show the main form
//                  Parent root  = FXMLLoader.load(getClass().getResource("../view/main.fxml"));
//                  Stage stage =new Stage();
//                  stage.setTitle("SCHOOL AID V_1");
//                  Scene scene = new Scene(root);
//                  scene.getStylesheets().add(getClass().getResource("../stylesheet/main.css").toExternalForm());
//                  stage.setScene(scene);
//                  stage.show();
//
//                  Initializer.getInitializerInstance().haltSystem();
//                  System.out.print("The execution is here now..");
              }catch (Exception e){
                  e.printStackTrace();
              }
          }
      });
    }
}
