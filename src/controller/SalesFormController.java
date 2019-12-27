package controller;

import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import org.controlsfx.control.CheckComboBox;

public class SalesFormController {
    @FXML
    private JFXRadioButton allStudentsRadio;

    @FXML
    private ToggleGroup optionsToggle;

    @FXML
    private JFXRadioButton payFeesRadio;

    @FXML
    private JFXRadioButton doNotPayFeesRadio;

    @FXML
    private JFXRadioButton selectClassesRadio;

    @FXML
    private CheckComboBox<?> classesCombo;

    @FXML
    private TextField itemName;

    @FXML
    private TextField itemCost;

    @FXML
    private TextField quantity;

    @FXML
    private TextField totalCost;

    @FXML
    private Button addOrder;

    @FXML
    private Button clear;

    @FXML
    private ListView<?> orderListview;

    @FXML
    private Button checkout;

    @FXML
    private Button cancel;
}
