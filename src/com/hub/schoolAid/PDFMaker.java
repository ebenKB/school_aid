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
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class PDFMaker {
    private static PDFMaker pdfMakerInstance;

    public static PDFMaker getPDFMakerInstance() {
        if(pdfMakerInstance == null) {
            pdfMakerInstance = new PDFMaker();
        }

        return pdfMakerInstance;
    }

    private PDFMaker() {

    }

    Double totalFeeding =0.0;
    int totalAttendance = 0;

//    StudentDao dao = new StudentDao();
//    List<Student> students = dao.getAllStudents();
    AssessmentDao assessmentDao =new AssessmentDao();
    TerminalReportDao terminalReportDao =new TerminalReportDao();
    ObservableList <Assessment> assessmentList = FXCollections.observableArrayList();

    PDDocument pdDocument;
    PDFont font = PDType1Font.HELVETICA_BOLD;
    PDFont regText = PDType1Font.HELVETICA;
    final int headerSize = 12;
    final int contentSize =10;

    // create report for all students
    public  PDDocument createReport() {
        System.out.println("We called the functio to creat report");
        pdDocument = new PDDocument();
        ObservableList <TerminalReport> reports = FXCollections.observableArrayList();
        assessmentList.addAll(assessmentDao.getAssessment());
        reports.addAll(terminalReportDao.getReport());
        this.designReport(reports);

       return pdDocument;
    }

    // create report for selected students
    public PDDocument createReport(ObservableList<TerminalReport> reports) {
        List<Student> students = FXCollections.observableArrayList();
//        pdDocument = new PDDocument();
        for(TerminalReport t: reports ) {
            students.add(t.getStudent());
            System.out.println("adding student to stack"+ t.toString());
        }

        assessmentList.clear();
        // get assessment of the selected students
        assessmentList.addAll(assessmentDao.getAssessment(students));
        this.designReport(reports);
        return pdDocument;
    }

    private void designReport(ObservableList<TerminalReport>reports) {
        try {
            pdDocument = new PDDocument();
            for(TerminalReport report:reports) {
//                if (report.getStudent().getStage().getClassValue() > 0) { // start class condition check
                    //prepare pdf document
                    PDPage pdPage = new PDPage(PDRectangle.A5);
                    PDRectangle mediaBox = pdPage.getMediaBox();

                    //add header text
                    String schName = "THE FATHER'S MERCY SCHOOL";
                    String address = "Location: Sowutuom - Accra, Contact: ";
                    String heading = "TERMINAL REPORT";

                    //student details\
                    String name = "Name: " + report.getStudent().toString().toUpperCase() + "                Class: " + report.getStudent().getStage().getName().toUpperCase();
                    String term = "Term ends: 17th August 2019                Next Term Begins: 3rd September, 2019";
//                  String attendance = "Second Term Report        Attendance: .........out of.........";
                    String attendance = "Second Term Report";
                    float nameGap = ((mediaBox.getWidth()) - (font.getStringWidth(name) / 1000));


                    pdDocument.addPage(pdPage);

                    float margin = 20;
                    float yStartNewPage = pdPage.getMediaBox().getHeight() - margin;
                    float yStart = (yStartNewPage - 105);

                    float titleWidth = font.getStringWidth(schName) / 1000 * headerSize;
                    float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerSize;

                    float startX = (mediaBox.getWidth() - titleWidth) / 2;
                    float startY = mediaBox.getHeight() - (margin + 10) - titleHeight;
                    PDPageContentStream pageContentStream = new PDPageContentStream(pdDocument, pdPage);



//                 pageContentStream.addRect(0,mediaBox.getHeight()-50,mediaBox.getWidth(),(mediaBox.getHeight()-(mediaBox.getHeight()/1000)-50));
//                 pageContentStream.beginText();

                    //show school name
//                 pageContentStream.newLineAtOffset(startX,startY);
//                 pageContentStream.setFont(font,headerSize);
//                 pageContentStream.setLeading(15f);
//                 pageContentStream.showText(schName);
//                 pageContentStream.newLine();
//                 pageContentStream.endText();

                    //show address
//                 pageContentStream.beginText();
//                 pageContentStream.setFont(regText,contentSize);
//                 titleWidth = font.getStringWidth(address)/1000 * contentSize ;
//                 titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * contentSize ;
//
//                 startX = (mediaBox.getWidth() -titleWidth) /2;
//                 startY = mediaBox.getHeight() - (margin +30) - titleHeight;
//
//                 pageContentStream.newLineAtOffset(startX,startY);
//                 pageContentStream.showText(address);
//                 pageContentStream.newLine();
//                 pageContentStream.endText();


                    //show heading
//                 pageContentStream.beginText();
//                 pageContentStream.setFont(font,headerSize);
//                 titleWidth = font.getStringWidth(heading)/1000 * headerSize ;
//                 titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerSize ;
//
//                 startX = (mediaBox.getWidth() - titleWidth) /2;
//                 startY = mediaBox.getHeight() - (margin + 50) - titleHeight;
//                 pageContentStream.newLineAtOffset(startX,startY);
//                 pageContentStream.showText(heading);
//                 pageContentStream.newLine();
//                 pageContentStream.endText();

                    //add student details
                    pageContentStream.beginText();
                    pageContentStream.setFont(regText, contentSize);
                    titleWidth = font.getStringWidth(name) / 1000 * contentSize;
                    titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * contentSize;

                    startX = margin;
                    startY = mediaBox.getHeight() - (margin + 50) - titleHeight;
                    pageContentStream.newLineAtOffset(startX, startY);
                    pageContentStream.showText(name);
                    pageContentStream.newLine();
                    pageContentStream.endText();

                    //add term ends
                    pageContentStream.beginText();
                    pageContentStream.setFont(regText, contentSize);
                    titleWidth = font.getStringWidth(term) / 1000 * contentSize;
                    titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * contentSize;

                    startX = margin;
                    startY = mediaBox.getHeight() - (margin + 70) - titleHeight;
                    pageContentStream.newLineAtOffset(startX, startY);
                    pageContentStream.showText(term);
                    pageContentStream.newLine();
                    pageContentStream.endText();

                    //add attendance
                    pageContentStream.beginText();
                    pageContentStream.setFont(regText, contentSize);
                    titleWidth = font.getStringWidth(attendance) / 1000 * contentSize;
                    titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * contentSize;

                    startX = margin;
                    startY = mediaBox.getHeight() - (margin + 90) - titleHeight;
                    pageContentStream.newLineAtOffset(startX, startY);
                    pageContentStream.showText(attendance);
                    pageContentStream.newLine();
                    pageContentStream.endText();

                    //add the conduct
                    pageContentStream.beginText();
                    String conduct = "Calm and Respectful";
                    startY = mediaBox.getHeight() - (margin + 480) - titleHeight;
                    pageContentStream.newLineAtOffset(startX, startY);
                    pageContentStream.setFont(regText, contentSize);
                    pageContentStream.setLeading(15f);
                    pageContentStream.showText("Pupil's conduct: " + report.getConduct());
                    pageContentStream.newLine();
                    pageContentStream.endText();

                    //Add remark
                    pageContentStream.beginText();
                    String remark = "Good performance, keep it up";
                    startY = mediaBox.getHeight() - (margin + 500) - titleHeight;
                    pageContentStream.newLineAtOffset(startX, startY);
                    pageContentStream.setFont(regText, contentSize);
                    pageContentStream.setLeading(15f);
                    pageContentStream.showText("Head Teacher's remark: " + report.getHeadTracherRemark());
                    pageContentStream.newLine();
                    pageContentStream.endText();

                    //Add newClass
                    pageContentStream.beginText();
                    String newClass = "Promoted To: ................";
                    startY = mediaBox.getHeight() - (margin + 520) - titleHeight;
                    pageContentStream.newLineAtOffset((startX), startY);
                    pageContentStream.setFont(regText, contentSize);
                    pageContentStream.setLeading(15f);
                    pageContentStream.showText(newClass);
                    pageContentStream.newLine();
                    pageContentStream.endText();

                    //Add signature
                    pageContentStream.beginText();
                    String signature = "Headmistress's signature: ..................";
                    startY = mediaBox.getHeight() - (margin + 520) - titleHeight;
                    pageContentStream.newLineAtOffset(startX + 220, startY);
                    pageContentStream.setFont(regText, contentSize);
                    pageContentStream.setLeading(15f);
                    pageContentStream.showText(signature);
                    pageContentStream.newLine();
                    pageContentStream.endText();

                    pageContentStream.close();

                    float tableWidth = pdPage.getMediaBox().getWidth() - (2 * margin);

                    //add table
                    // use ystart to push the table top or down
                    BaseTable baseTable = new BaseTable(yStart-30, yStartNewPage, 20, tableWidth, (margin), pdDocument, pdPage, true, true);

                    //Create Header Row
                    Row<PDPage> headerRow = baseTable.createRow(30f);
                    Cell<PDPage> cell;
                    cell = headerRow.createCell(30, "SUBJECT");
                    cell.setAlign(HorizontalAlignment.CENTER);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFillColor(Color.lightGray);
                    cell.setTextColor(Color.BLACK);

                    cell = headerRow.createCell(14, "CLASS SCORE (30%)");
                    cell.setAlign(HorizontalAlignment.CENTER);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFillColor(Color.lightGray);
                    cell.setTextColor(Color.BLACK);

                    cell = headerRow.createCell(14, "EXAM SCORE (70%)");
                    cell.setAlign(HorizontalAlignment.CENTER);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFillColor(Color.lightGray);
                    cell.setTextColor(Color.BLACK);

                    cell = headerRow.createCell(10, "TOTAL (100%)");
                    cell.setAlign(HorizontalAlignment.CENTER);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFillColor(Color.lightGray);
                    cell.setTextColor(Color.BLACK);

                    cell = headerRow.createCell(12, "GRADE");
                    cell.setAlign(HorizontalAlignment.CENTER);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFillColor(Color.lightGray);
                    cell.setTextColor(Color.BLACK);

                    cell = headerRow.createCell(20, "REMARK");
                    cell.setAlign(HorizontalAlignment.CENTER);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFillColor(Color.lightGray);
                    cell.setTextColor(Color.BLACK);

                    cell.setFillColor(Color.lightGray);
                    cell.setAlign(HorizontalAlignment.CENTER);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setTextColor(Color.BLACK);
                    baseTable.addHeaderRow(headerRow);

                    //search for all the student's assessment
                    List<Assessment> foundAss = searchAssessment(report.getStudent().getId(), assessmentList);
                    /**
                     * make sure that every terminal report has at least 12 table rows.
                     * if the student has less than 12 subjects, we add excess table rows
                     * to cater for the remaining table rows.
                     */
                    if (foundAss.size() < 12) {
                        int diff = 12 - foundAss.size();
                        for (int i = 0; i < diff; i++) {
                            foundAss.add(new Assessment());
                        }
                    }

                    for (Assessment assessment : foundAss) {
                        Row<PDPage> row = baseTable.createRow(24f);

                        cell = row.createCell(30, assessment.getCourse() == null ? " " : assessment.getCourse().getName());
                        cell.setAlign(HorizontalAlignment.LEFT);
                        cell.setValign(VerticalAlignment.MIDDLE);

                        cell = row.createCell(14, assessment.getClassScore() == null ? " " : assessment.getClassScore().toString());
                        cell.setAlign(HorizontalAlignment.CENTER);
                        cell.setValign(VerticalAlignment.MIDDLE);

                        cell = row.createCell(14, assessment.getExamScore() == null ? " " : assessment.getExamScore().toString());
                        cell.setAlign(HorizontalAlignment.CENTER);
                        cell.setValign(VerticalAlignment.MIDDLE);

                        cell = row.createCell(10, (assessment.getExamScore() == null && assessment.getClassScore() == null) ? " " :
                                String.valueOf((assessment.getClassScore() + assessment.getExamScore())));
                        cell.setAlign(HorizontalAlignment.CENTER);
                        cell.setValign(VerticalAlignment.MIDDLE);
                        cell.setTextColor(Color.red);

                        cell = row.createCell(12, assessment.getGrade() == null ? " " : assessment.getGrade().getName());
                        cell.setAlign(HorizontalAlignment.CENTER);
                        cell.setValign(VerticalAlignment.MIDDLE);
                        cell.setTextColor(Color.red);

                        cell = row.createCell(20, assessment.getGrade() == null ? " " : assessment.getGrade().getRemark());
                        cell.setAlign(HorizontalAlignment.LEFT);
                        cell.setValign(VerticalAlignment.MIDDLE);
                        assessmentList.remove(assessment);
                    }
                    baseTable.draw();
                //} // end class condition check
                generateAccountReport(pdDocument,report.getStudent());
            }
        } catch (Exception e) {
//                e.printStackTrace();
        }
    }

    public static void savePDFToLocation(PDDocument pdDocument) {
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
            System.out.println("error while saving file ");
        }


    }

    public static  void createAttendanceReport (LocalDate from,LocalDate to,List<Attendance> attendanceList){

    }

    public  void createBillAndItemList() {
        pdDocument = new PDDocument();
        StudentDao dao = new StudentDao();
        List<Student> students = dao.getAllStudents();
        for(Student st: students) {
            generateAccountReport(pdDocument, st);
        }

        savePDFToLocation(pdDocument);
    }

    public void createBillAndItemList(ObservableList<Student>students) {
        pdDocument = new PDDocument();
        for(Student st: students) {
            generateAccountReport(pdDocument, st);
        }

        savePDFToLocation(pdDocument);
    }

    private HashMap createItemList(Boolean isLower){
        HashMap<String,String> itemList = new HashMap<String,String>();
        if(isLower){
            //Add items
            itemList.put("Toilet Roll", "4 pieces");
            itemList.put("Camel/Dettol Antiseptic", "1 Medium Size");
            itemList.put("Powdered Soap Only", "1 kg");

        }else{
            //Add items
            itemList.put("Toilet Roll", "2 pieces");
            itemList.put("Powdered Soap Only", "500g");
        }
            return itemList;
    }

    private void generateAccountReport(PDDocument pdDocument, Student student) {
//        if(student.getAccount().getFeedingFeeCredit() < 0 | student.getAccount().getFeeToPay() < 0) {
            PDPage pdPage = new PDPage(PDRectangle.A5);
            PDRectangle mediaBox = pdPage.getMediaBox();
            pdDocument.addPage(pdPage);
            PDPageContentStream pageContentStream = null;
            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDFont regText = PDType1Font.HELVETICA;
            final float headerSize = 13;
            final float contentSize =12;
            float margin = 20;

            try {
                pageContentStream = new PDPageContentStream(pdDocument,pdPage);

                //add header text
                String schName = "THE FATHER'S MERCY SCHOOL";
                String address = "Location: Sowutuom - Accra, Contact:";
                String heading = "STUDENT BILL FOR SECOND TERM";

                //student details
                String name = "Name: "+ student.toString().toUpperCase()+"                     Class: "+student.getStage().getName().toUpperCase();

                //show school name
                float titleWidth = font.getStringWidth(schName)/1000 * headerSize ;
                float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerSize ;
                float startX = (mediaBox.getWidth() - titleWidth) /2;
                float startY = mediaBox.getHeight() - (margin + 10) - titleHeight;

                pageContentStream.beginText();
                pageContentStream.newLineAtOffset(startX,startY);
                pageContentStream.setFont(font,headerSize);
                pageContentStream.setLeading(15f);
                pageContentStream.showText(schName);
                pageContentStream.newLine();
                pageContentStream.endText();

                //show heading
                float yStartNewPage = pdPage.getMediaBox().getHeight() - margin;
                float yStart = ( yStartNewPage - 105);

                //show address
                pageContentStream.beginText();
                pageContentStream.setFont(regText,contentSize);
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
                startY = mediaBox.getHeight() - (margin + 60) - titleHeight;
                pageContentStream.newLineAtOffset(startX,startY);
                pageContentStream.showText(heading);
                pageContentStream.newLine();
                pageContentStream.endText();

                //add student details
                pageContentStream.beginText();
                pageContentStream.setFont(regText,contentSize);
                titleWidth = font.getStringWidth(name)/1000 * contentSize ;
                titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * contentSize ;

                startX = margin;
                startY = mediaBox.getHeight() - (margin + 90) - titleHeight;
                pageContentStream.newLineAtOffset(startX,startY);
                pageContentStream.showText(name);
                pageContentStream.newLine();
                pageContentStream.endText();

                pageContentStream.close();


                //add table
                float tableWidth = pdPage.getMediaBox().getWidth() - (2 * margin);
                BaseTable baseTable = new BaseTable(yStart,yStartNewPage,20,tableWidth,(margin),pdDocument,pdPage,true,true);

                //Create Header Row
                Row<PDPage>headerRow = baseTable.createRow(32f);

                float bigSize = 65;
                float smallSize = 35;
                Double total = 0.00;

                Cell <PDPage> cell ;

                cell= headerRow.createCell(bigSize,"ITEM");

                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFillColor(Color.lightGray);
                cell.setTextColor(Color.BLACK);
//                cell.setFont(font);
                cell.setFontSize(13);
                cell.setFont(PDType1Font.HELVETICA_BOLD);

                cell=headerRow.createCell(smallSize,"AMOUNT");
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFillColor(Color.lightGray);
                cell.setTextColor(Color.BLACK);
//                cell.setFont(font);
                cell.setFontSize(13);
                cell.setFont(PDType1Font.HELVETICA_BOLD);

                if( student.getPayFeeding() && student.getAccount().getFeedingFeeCredit() < 0){
                    Row < PDPage > row = baseTable.createRow(30f);
                    cell = row.createCell(bigSize,"Feeding Fee Arrears");
                    cell.setAlign(HorizontalAlignment.LEFT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    cell = row.createCell(smallSize,String.valueOf(student.getAccount().getFeedingFeeCredit() * -1));
                    total+=(student.getAccount().getFeedingFeeCredit() * - 1);
                    System.out.println("This is the total:"+ total);
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);
                }

                //only for students who do not pay school fees
                if(! student.getPaySchoolFees()) {
                    // add maintenance dues
                    Row < PDPage > row2 = baseTable.createRow(30f);
                    cell = row2.createCell(bigSize,"First Aid / Maintenance");
                    cell.setAlign(HorizontalAlignment.LEFT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    cell = row2.createCell(smallSize,"20.00");
                    total+=20.00;
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    // add computer facility fee
                    Row < PDPage > comp = baseTable.createRow(30f);
                    cell = comp.createCell(bigSize,"Computer User Fee");
                    cell.setAlign(HorizontalAlignment.LEFT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    cell = comp.createCell(smallSize,"10.00");
                    total+=10.00;
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);
                }

                if(student.getPaySchoolFees() && student.getAccount().getFeeToPay()< 0) {
                    Row < PDPage > row3 = baseTable.createRow(30f);

                    cell = row3.createCell(bigSize,"School Fees Arrears");
                    cell.setAlign(HorizontalAlignment.LEFT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    cell = row3.createCell(smallSize,String.valueOf((student.getAccount().getFeeToPay())*-1));
                    total+=(student.getAccount().getFeeToPay() * -1);
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);
                }

                if(student.getPaySchoolFees()){
                    Row < PDPage > row4 = baseTable.createRow(30f);

                    cell = row4.createCell(bigSize,"School Fees");
                    cell.setAlign(HorizontalAlignment.LEFT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    cell = row4.createCell(smallSize,String.valueOf((student.getStage().getFeesToPay())));
                    total+=(student.getStage().getFeesToPay());
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);
                }

                Row < PDPage > row5 = baseTable.createRow(30f);
                cell = row5.createCell(bigSize,"Examination Fees");
                cell.setAlign(HorizontalAlignment.LEFT);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFontSize(12);

                if(student.getStage().getClassValue()  <= 2){
                    cell = row5.createCell(smallSize,"10.00");
                    total+=10.00;
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                }else if (student.getStage().getClassValue() <= 7) {
                    cell = row5.createCell(smallSize,"12.00");
                    total+=12.00;
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                }else{
                    cell = row5.createCell(smallSize,"15.00");
                    total+=15.00;
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                }

                Row < PDPage > row6 = baseTable.createRow(30f);

                cell = row6.createCell(bigSize,"TOTAL BILL");
                cell.setAlign(HorizontalAlignment.LEFT);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFontSize(13);
                cell.setFont(PDType1Font.HELVETICA_BOLD);


                cell = row6.createCell(smallSize,"GHS "+ (String.valueOf(total)) );
                cell.setAlign(HorizontalAlignment.RIGHT);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFillColor(Color.decode("#F5F4F4"));
                cell.setFontSize(13);
                cell.setFont(PDType1Font.HELVETICA_BOLD);


               //add empty row
                Row < PDPage > row7 = baseTable.createRow(30f);
                row7.createCell(bigSize,"");
                row7.createCell(smallSize,"");

                //Add item list for the student
                Row < PDPage > row8 = baseTable.createRow(30f);
                cell = row8.createCell(bigSize, "ITEM LIST");
                cell.setAlign(HorizontalAlignment.LEFT);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFillColor(Color.lightGray);
                cell.setTextColor(Color.BLACK);
                cell.setFont(font);
                cell.setFontSize(13);

                cell = row8.createCell(smallSize,"QUANTITY");
                cell.setAlign(HorizontalAlignment.LEFT);
                cell.setValign(VerticalAlignment.MIDDLE);
                cell.setFillColor(Color.lightGray);
                cell.setTextColor(Color.BLACK);
                cell.setFont(font);
                cell.setFontSize(13);

                Set set;
                // create items from creche - kg1
                if(student.getStage().getClassValue() <= 4){
                   set = createItemList(true).entrySet();
               } else {
                    // create item list from kg2 to jhs
                   set = createItemList(false).entrySet();
               }

                Iterator iterator = set.iterator();

                while (iterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry)iterator.next();
                    Row<PDPage> row =baseTable.createRow(30f);
                    cell=row.createCell(bigSize, mapEntry.getKey().toString());
                    cell.setFontSize(12);
                    cell=row.createCell(smallSize, mapEntry.getValue().toString());
                    cell.setFontSize(12);
                }
                baseTable.draw();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public void createA4() {

    }

    public void createAttendanceReport (LocalDate date,List<Attendance> attendanceList){
        PDDocument pdDocument = new PDDocument();
        PDFont font = PDType1Font.HELVETICA_BOLD;
        PDFont regText = PDType1Font.HELVETICA;
        final int headerSize = 12;
        final int contentSize =12;
        totalAttendance =0;

       try {
           //prepare pdf document
           PDPage pdPage = new PDPage(PDRectangle.A4);

           PDRectangle mediaBox = pdPage.getMediaBox();

           //add header text
           String schName = "THE FATHER'S MERCY SCHOOL";
           String address ="Location: Sowutuom - Accra, Contact:";
           String heading = "REPORT ON ATTENDANCE FOR" + " " + this.dateToString(date);

           pdDocument.addPage(pdPage);

           float margin = 20;
           float yStartNewPage = pdPage.getMediaBox().getHeight() - ( margin);
           float yStart = pdPage.getMediaBox().getHeight() - ( margin + 105);

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
           startY = mediaBox.getHeight() - (margin + 30) - titleHeight;

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

           pageContentStream.close();

           //add table
           float tableWidth = pdPage.getMediaBox().getWidth() - (2 * margin);
           BaseTable baseTable = new BaseTable(yStart ,yStartNewPage,20,tableWidth,(margin),pdDocument,pdPage,true,true);

           //Create Header Row
           Row<PDPage>headerRow = baseTable.createRow(15f);
           Cell <PDPage> cell ;
//           cell.setFontSize(12);
           cell= headerRow.createCell(40,"STUDENT");
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);
           cell.setFillColor(Color.lightGray);
           cell.setTextColor(Color.BLACK);

           cell=headerRow.createCell(14,"CLASS");
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);
           cell.setFillColor(Color.lightGray);
           cell.setTextColor(Color.BLACK);

           cell = headerRow.createCell(14,"PAID");
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);
           cell.setFillColor(Color.lightGray);
           cell.setTextColor(Color.BLACK);

           cell=headerRow.createCell(10,"AMOUNT PAID");
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);
           cell.setFillColor(Color.lightGray);
           cell.setTextColor(Color.BLACK);

//           cell = headerRow.createCell(15,"DATE");
//           cell.setAlign(HorizontalAlignment.CENTER);
//           cell.setValign(VerticalAlignment.MIDDLE);
//           cell.setFillColor(Color.lightGray);
//           cell.setTextColor(Color.BLACK);


           cell.setFillColor(Color.lightGray);
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);
           cell.setTextColor(Color.BLACK);
           baseTable.addHeaderRow(headerRow);

           for (Attendance attendance:attendanceList){
               if(attendance.getDate().toString().equals(date.toString())){
                   Row<PDPage> row = baseTable.createRow(10f);
                   cell = row.createCell(40,attendance.getStudent().toString());

                   cell = row.createCell(14,attendance.getStudent().getStage().getName());
                   cell.setAlign(HorizontalAlignment.CENTER);
                   cell.setValign(VerticalAlignment.MIDDLE);

                   cell = row.createCell(14,attendance.getPaidNow()? "YES" : "NO");
                   cell.setAlign(HorizontalAlignment.CENTER);
                   cell.setValign(VerticalAlignment.MIDDLE);


                   if((attendance.getStudent().getPayFeeding() && attendance.getPaidNow())) {
                       cell = row.createCell(10, String.valueOf(attendance.getFeedingFee()));
                       totalFeeding +=attendance.getFeedingFee();

                   }else{
                       cell = row.createCell(10, "0.00");
                   }

                   totalAttendance++;
//                   cell.setAlign(HorizontalAlignment.CENTER);
//                   cell.setValign(VerticalAlignment.MIDDLE);

//                   cell = row.createCell(12,attendance.getDate().toString());
//                   cell.setAlign(HorizontalAlignment.CENTER);
//                   cell.setValign(VerticalAlignment.MIDDLE);
               }
           }

           baseTable.draw();
           pdDocument.addPage(genStatsPage(date,attendanceList,pdDocument));

       }catch (IOException e) {
           e.printStackTrace();
       }
       savePDFToLocation(pdDocument);
    }

    /**
     * this function searches for all the assessment for one student and returns all the assessment for that student
     * @param searchItem student id
     * @param sortedAssessment
     * @return assessment for the student
     */
    private static  List<Assessment> searchAssessment(Long searchItem,List<Assessment> sortedAssessment) {
        System.out.println("------------------------------------------------------------------");
        for (Assessment as:sortedAssessment) {
            System.out.println(as.getStudent().toString()+" ASS ID "+as.getId()+"STU ID: "+as.getStudent().getId());
        }
        System.out.println("------------------------------------------------------------------");

        List<Assessment> found = new ArrayList<>();
        System.out.println("SIZE OF THE SEARCH == " +sortedAssessment.size());
        // loop through the sorted assessments
        for (int i=0; i < sortedAssessment.size(); i++){
            // check if the assessment belongs to the student you are searching
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


    private String dateToString(LocalDate date) {
        return  date.getDayOfWeek().toString().toUpperCase()+", "+date.getDayOfMonth()+"th "+date.getMonth().toString().toUpperCase()+" "+date.getYear();
    }

    private PDPage genStatsPage (LocalDate date, List<Attendance> attendanceList,PDDocument pdDocument) {
        PDPage pdPage = new PDPage(PDRectangle.A4);
        PDRectangle mediaBox = pdPage.getMediaBox();

       try {
           float margin = 20;
           float yStartNewPage = pdPage.getMediaBox().getHeight() - ( margin);
           float yStart = pdPage.getMediaBox().getHeight() - ( margin + 105);

           String header  ="REPORT STATISTIC";
           float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(header)/1000 * 14 ;
           float titleHeight = PDType1Font.HELVETICA_BOLD.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 12 ;

           float startX = (mediaBox.getWidth() -titleWidth) /2;
           float startY = mediaBox.getHeight() - (margin + 10) - titleHeight;
           PDPageContentStream pageContentStream = new PDPageContentStream(pdDocument,pdPage);

           pageContentStream.beginText();

           //show header
           pageContentStream.newLineAtOffset(startX,startY);
           pageContentStream.setFont(PDType1Font.HELVETICA_BOLD,14);
           pageContentStream.setLeading(15f);
           pageContentStream.showText(header);
           pageContentStream.newLine();
           pageContentStream.endText();

           pageContentStream.close();


           float tableWidth = pdPage.getMediaBox().getWidth() - (2 * margin);
           BaseTable baseTable = new BaseTable(yStart,yStartNewPage,20,tableWidth,(margin),pdDocument,pdPage,true,true);

           //Create Header Row
           Row<PDPage>headerRow = baseTable.createRow(15f);
           Cell <PDPage> cell ;
           cell= headerRow.createCell(40,"KEY");
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);
           cell.setFillColor(Color.lightGray);
           cell.setTextColor(Color.BLACK);

           cell=headerRow.createCell(30,"VALUE");
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);
           cell.setFillColor(Color.lightGray);
           cell.setTextColor(Color.BLACK);

           Row<PDPage> row = baseTable.createRow(10f);
           cell = row.createCell(40,"TOTAL FEEDING FEE");
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);

           cell = row.createCell(40,String.valueOf(totalFeeding));
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);

           Row<PDPage> row2 = baseTable.createRow(10f);
           cell = row2.createCell(40,"TOTAL ATTENDANCE");
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);

           cell = row2.createCell(40,String.valueOf(totalAttendance));
           cell.setAlign(HorizontalAlignment.CENTER);
           cell.setValign(VerticalAlignment.MIDDLE);

           baseTable.draw();

       } catch (IOException e) {
           e.printStackTrace();
       }
        return  pdPage;
    }
}
