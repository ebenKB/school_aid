package com.hub.schoolAid;

import be.quodlibet.boxable.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PDFMaker {
    private static PDFMaker pdfMakerInstance = new PDFMaker();

    public static PDFMaker getPDFMakerInstance() {
        return pdfMakerInstance;
    }

    private PDFMaker() {

    }

    public static void  createReportForAllStudents(){

        PDDocument pdDocument = new PDDocument();
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDFont regText = PDType1Font.HELVETICA;
        final int headerSize = 12;
        final int contentSize =12;
        StudentDao dao = new StudentDao();
        List<Student> students = dao.getAllStudents();
        AssessmentDao assessmentDao =new AssessmentDao();
        ObservableList<Assessment> assessmentList = FXCollections.observableArrayList();
        assessmentList.addAll(assessmentDao.getAssessment());

        try {
             for(Student s:students){
                 System.out.println("searching for studnt "+s.toString());
//                 List<Assessment>assessments =assessmentDao.getAssessment(s);

                 //prepare pdf document
                 PDPage pdPage = new PDPage(PDRectangle.A5);
                 PDRectangle mediaBox = pdPage.getMediaBox();

                 //add header text
                 String schName = "THE FATHER'S MERCY SCHOOL";
                 String address ="Location: Sowutuom - Accra, Contact: 0275900513/ 0000000000";
                 String heading = "TERMINAL REPORT";

                 //student details
                 String name = "Name: "+ s.toString().toUpperCase()+"                     Class: "+s.getStage().getName().toUpperCase();
                 float nameGap = ((mediaBox.getWidth()) - (font.getStringWidth(name)/1000));

                 pdDocument.addPage(pdPage);

                 float margin = 20;
                 float yStartNewPage = pdPage.getMediaBox().getHeight() - ( margin + 105);
                 float yStart = ( yStartNewPage );

                 float titleWidth = font.getStringWidth(schName)/1000 * headerSize ;
                 float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerSize ;

                 float startX = (mediaBox.getWidth() -titleWidth) /2;
                 float startY = mediaBox.getHeight() - (margin + 10) - titleHeight;
                 PDPageContentStream pageContentStream = new PDPageContentStream(pdDocument,pdPage);

                 pageContentStream.beginText();
                 //show school name
                 pageContentStream.newLineAtOffset(startX,startY);
                 pageContentStream.setFont(font,headerSize);
                 pageContentStream.setLeading(15f);
                 pageContentStream.showText(schName);
                 pageContentStream.newLine();
                 pageContentStream.endText();

                 //show address
                 pageContentStream.beginText();
                 pageContentStream.setFont(font,contentSize);
                 titleWidth = font.getStringWidth(address)/1000 * contentSize ;
                 titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * contentSize ;

                 startX = (mediaBox.getWidth() -titleWidth) /2;
                 startY = mediaBox.getHeight() - (margin +30) - titleHeight;

                 pageContentStream.newLineAtOffset(startX,startY);
                 pageContentStream.showText(address);
                 pageContentStream.newLine();
                 pageContentStream.endText();


                 //show heading
                 pageContentStream.beginText();
                 pageContentStream.setFont(font,headerSize);
                 titleWidth = font.getStringWidth(heading)/1000 * headerSize ;
                 titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerSize ;

                 startX = (mediaBox.getWidth() - titleWidth) /2;
                 startY = mediaBox.getHeight() - (margin + 50) - titleHeight;
                 pageContentStream.newLineAtOffset(startX,startY);
                 pageContentStream.showText(heading);
                 pageContentStream.newLine();
                 pageContentStream.endText();

                 //add student details
                 pageContentStream.beginText();
                 pageContentStream.setFont(font,contentSize);
                 titleWidth = font.getStringWidth(name)/1000 * contentSize ;
                 titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * contentSize ;

                 startX = (mediaBox.getWidth() - titleWidth) / 2;
                 startY = mediaBox.getHeight() - (margin + 80) - titleHeight;
                 pageContentStream.newLineAtOffset(startX,startY);
                 pageContentStream.showText(name);
                 pageContentStream.newLine();
                 pageContentStream.endText();

                 pageContentStream.close();

                 float tableWidth = pdPage.getMediaBox().getWidth() - (2 * margin);

                 //add table
                 BaseTable baseTable = new BaseTable(yStart,yStartNewPage,20,tableWidth,(margin),pdDocument,pdPage,true,true);

                 //Create Header Row
                 Row<PDPage>headerRow = baseTable.createRow(15f);
                 Cell <PDPage> cell ;
                 cell= headerRow.createCell(30,"SUBJECT");
                 cell.setAlign(HorizontalAlignment.CENTER);
                 cell.setValign(VerticalAlignment.MIDDLE);
                 cell.setFillColor(Color.lightGray);
                 cell.setTextColor(Color.WHITE);

                 cell=headerRow.createCell(14,"CLASS SCORE (30%)");
                 cell.setAlign(HorizontalAlignment.CENTER);
                 cell.setValign(VerticalAlignment.MIDDLE);
                 cell.setFillColor(Color.lightGray);
                 cell.setTextColor(Color.WHITE);

                 cell = headerRow.createCell(14,"EXAM SCORE (70%)");
                 cell.setAlign(HorizontalAlignment.CENTER);
                 cell.setValign(VerticalAlignment.MIDDLE);
                 cell.setFillColor(Color.lightGray);
                 cell.setTextColor(Color.WHITE);

                 cell=headerRow.createCell(10,"TOTAL (100%)");
                 cell.setAlign(HorizontalAlignment.CENTER);
                 cell.setValign(VerticalAlignment.MIDDLE);
                 cell.setFillColor(Color.lightGray);
                 cell.setTextColor(Color.WHITE);

                 cell = headerRow.createCell(12,"GRADE");
                 cell.setAlign(HorizontalAlignment.CENTER);
                 cell.setValign(VerticalAlignment.MIDDLE);
                 cell.setFillColor(Color.lightGray);
                 cell.setTextColor(Color.WHITE);

                 cell = headerRow.createCell(20,"REMARK");
                 cell.setAlign(HorizontalAlignment.CENTER);
                 cell.setValign(VerticalAlignment.MIDDLE);
                 cell.setFillColor(Color.lightGray);
                 cell.setTextColor(Color.WHITE);

//                 cell.setFont(PDType1Font.HELVETICA);
                 cell.setFillColor(Color.lightGray);
//                 cell.setFontBold(PDType1Font.HELVETICA_BOLD);
                 cell.setAlign(HorizontalAlignment.CENTER);
                 cell.setValign(VerticalAlignment.MIDDLE);
                 cell.setTextColor(Color.WHITE);
                 baseTable.addHeaderRow(headerRow);


               //search for all the student's assessment
                List<Assessment>foundAss= searchAssessment(s.getId(),assessmentList);

                 for (Assessment assessment:foundAss){
                     Row<PDPage> row = baseTable.createRow(10f);
                     cell = row.createCell(30,assessment.getCourse().getName());

                     cell = row.createCell(14,assessment.getClassScore().toString());
                     cell.setAlign(HorizontalAlignment.CENTER);
                     cell.setValign(VerticalAlignment.MIDDLE);

                     cell = row.createCell(14,String.valueOf(assessment.getExamScore()));
                     cell.setAlign(HorizontalAlignment.CENTER);
                     cell.setValign(VerticalAlignment.MIDDLE);

                     cell = row.createCell(10,String.valueOf((assessment.getClassScore() + assessment.getExamScore())));
                     cell.setAlign(HorizontalAlignment.CENTER);
                     cell.setValign(VerticalAlignment.MIDDLE);

                     cell = row.createCell(12,assessment.getGrade().getName());
                     cell.setAlign(HorizontalAlignment.CENTER);
                     cell.setValign(VerticalAlignment.MIDDLE);

                     cell = row.createCell(20,assessment.getGrade().getRemark());
                     assessmentList.remove(assessment);
                 }
                 baseTable.draw();
             }
            } catch (Exception e) {
                e.printStackTrace();
            }

        savePDFToLocation(pdDocument);
    }

    private static void savePDFToLocation(PDDocument pdDocument) {
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

    public static  void createAttendanceReport (LocalDate from,LocalDate to,List<Attendance> attendanceList){

    }

    public static  void createAttendanceReport (LocalDate from,List<Attendance> attendanceList){

    }

    private static  List<Assessment> searchAssessment(Long searchItem,List<Assessment> sortedAssessment){
        System.out.println("------------------------------------------------------------------");
        for (Assessment as:sortedAssessment) {
            System.out.println(as.getStudent().toString()+" ASS ID "+as.getId()+"STU ID: "+as.getStudent().getId());
        }
        System.out.println("------------------------------------------------------------------");

        List<Assessment> found = new ArrayList<>();
        System.out.println("SIZE OF THE SEARCH == " +sortedAssessment.size());
        for (int i=0; i < sortedAssessment.size(); i++){
            if(sortedAssessment.get(i).getStudent().getId().compareTo(searchItem)== 0){
                Assessment assessment = sortedAssessment.get(i);
                System.out.println(sortedAssessment.get(i).getStudent().getId() +" == "+searchItem+"at index: "+i +"SIZE ="+sortedAssessment.size());
                found.add(assessment);
            }else {
                System.out.println(sortedAssessment.get(i).getStudent().getId() +" is not equal to "+searchItem+" at index :"+i+" SIZE = "+sortedAssessment.size());
            }
        }

        System.out.println("*******************************************************************RETURNING");
        for(Assessment a:found){
            System.out.println(a.getStudent().toString());
        }
        System.out.println("*******************************************************************END RETURN");

        return found;
    }
}
