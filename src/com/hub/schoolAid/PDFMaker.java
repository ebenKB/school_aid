package com.hub.schoolAid;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFMaker {
    private static PDFMaker pdfMakerInstance = new PDFMaker();

    public static PDFMaker getPDFMakerInstance() {
        return pdfMakerInstance;
    }

    private PDFMaker() {

    }

    public static void  createReport(){
        PDDocument pdDocument = new PDDocument();
            try {
             for(int i=0;i<10;i++){
                 //prepare pdf document
                 PDPage pdPage = new PDPage();
                 pdDocument.addPage(pdPage);
                 PDPageContentStream pageContentStream = new PDPageContentStream(pdDocument,pdPage);
                 pageContentStream.beginText();

                 //add settings
                 PDFont font = PDType1Font.TIMES_ROMAN;
                 pageContentStream.setFont(font,16);
                 pageContentStream.drawString("hello world");
                 pageContentStream.endText();
                 pageContentStream.close();
             }
            } catch (Exception e) {
                e.printStackTrace();
            }

        try{
            //choose a file location
            FileChooser fileChooser =new FileChooser();
            fileChooser.setInitialFileName("Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files",".pdf"));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PDF Files",".pdf"));
            File file  = fileChooser.showSaveDialog(new Stage());
            if(file!=null)
                pdDocument.save(file);
            pdDocument.close();
        }catch (Exception e){
                e.printStackTrace();
        }

    }

    public static void  createReport(List<Assessment> assessment){
        PDDocument pdDocument = new PDDocument();
        for(int i=0;i<=5;i++){
            PDPage pdPage =new PDPage();
            pdDocument.addPage(pdPage);
        }
    }
}
