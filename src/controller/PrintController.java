package controller;

import com.hub.schoolAid.Utils;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import sun.jvm.hotspot.debugger.Page;

import javax.rmi.CORBA.Util;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PrintController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private Button cancel;

    @FXML
    private Button printJob;

    @FXML
    private AnchorPane printNode;

    @FXML
    private Label receiptNo;

    @FXML
    private Label name;

    @FXML
    private Text heading;

    @FXML
    private Label date;

    @FXML
    private TextArea details;

    @FXML
    private Button confirmPrint;

    public void createPrintJob(){
        date.setText(Utils.formatDate(LocalDate.now(), false));
        heading.setText("NEW CROSS ROADS AVR");
        receiptNo.setText("#DF0909ES");
        name.setText("Marie Porter");
        details.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
                "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
                "when an unknown printer took a galley of type and scrambled it to make a type specimen book. " +
                "It has survived not only five centuries, but also the leap into electronic typesetting, " +
                "remaining essentially unchanged. It was popularised in the 1960s with the release of " +
                "Letraset sheets containing Lorem Ipsum passages, and more recently with desktop " +
                "publishing software like Aldus PageMaker including versions of Lorem Ipsum.");
        details.setWrapText(true);

        // add the all the children to the print node
//        printNode.getChildren().addAll(heading, date, receiptNo,name,details);
    }

    private void printJob() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if(job.showPageSetupDialog(null)){
            Printer printer = job.getPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.A5, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
            if (job.printPage(pageLayout,printNode)) {
                System.out.println("The job has been printed...");
                job.endJob();
            } else {
                System.out.println("An error occured while printing the job");
            }
        }
    }

    public void init() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        printJob.setOnAction(event -> {
            System.out.println("we want to print");
            createPrintJob();
        });

        confirmPrint.setOnAction(event -> printJob());

        cancel.setOnAction(event -> Utils.closeEvent(event));
    }
}
