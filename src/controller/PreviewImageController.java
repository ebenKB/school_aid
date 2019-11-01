package controller;

import com.hub.schoolAid.ImageHandler;
import com.hub.schoolAid.Student;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class PreviewImageController implements Initializable {

    @FXML
    private AnchorPane root;

    @FXML
    private Label message;

    @FXML
    private ImageView imageView;

    @FXML
    private Button cancel;

    @FXML
    private Button save;

    private Student student;
    byte [] imagebytes;

//    public void init(byte [] imagebytes, Student student) {
//        this.student = student;
//        this.imagebytes = imagebytes;
//    }

    void initialize(byte [] imagebytes, Student student) {
        this.student = student;
        this.imagebytes = imagebytes;
        System.out.println("this is the student we have received"+ this.student.toString());

//        infoLabel.setText("KKKKKKK......");
    }

//    void setFields(){
//        if (this.student != null) {
//            System.out.println("the student is not NULL"+ this.student.toString());
//            this.infoLabel.setText(this.student.toString());
//
//            // set the image to the image view
//            ImageHandler.setImage(imagebytes, imageView);
//        } else {
//            System.out.println("STUDENT IS NULL..."+ this.student.toString());
//        }
//    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
