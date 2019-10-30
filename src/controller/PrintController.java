package controller;

import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import sun.jvm.hotspot.debugger.Page;

import java.net.URL;
import java.util.ResourceBundle;

public class PrintController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane printNode;

    @FXML
    private Button cancel;

    @FXML
    private Button printJob;

    public void createPrintJob(){
        PrinterJob job = PrinterJob.createPrinterJob();
        if(job.showPageSetupDialog(null)){
            System.out.println("This is the printer that we got"+ job.getPrinter().getName());
            if (job.printPage(printNode)) {
                job.printPage(printNode);
                System.out.println("The job has been printed...");
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
    }
}
