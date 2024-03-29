package com.hub.schoolAid;

import be.quodlibet.boxable.*;
import com.sun.org.apache.bcel.internal.generic.ARETURN;
import com.sun.org.apache.regexp.internal.RE;
import javafx.application.Platform;
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
import org.apache.pdfbox.printing.PDFPrintable;
import org.hibernate.HibernateException;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class PDFMaker {
    private static PDFMaker pdfMakerInstance;
    private App app = AppDao.getAppSetting();

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
        }

        assessmentList.clear();
        // get assessment of the selected students
        assessmentList.addAll(assessmentDao.getAssessment(students));
        this.designReport(reports);
        return pdDocument;
    }

    private void prepPageWidthHeader(PDPageContentStream pageContentStream, Float margin, Float yStart, PDRectangle mediaBox, String title) {
        try {
            String schName = app.getName().toUpperCase();
            String address = app.getAddress().toUpperCase() + ", "+ app.getContact();

            float titleWidth = font.getStringWidth(schName) / 1000 * headerSize;
            float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerSize;

            float startX = (mediaBox.getWidth() - titleWidth) / 2;
            float startY = mediaBox.getHeight() - (margin + 10) - titleHeight;

//            pageContentStream.setFont(PDType1Font.HELVETICA, 14);
            pageContentStream.setFont(font,headerSize);
            pageContentStream.beginText();
            pageContentStream.newLineAtOffset(startX, startY); // start writing from this line
            pageContentStream.showText(schName);
            pageContentStream.endText();

            // add the address
             pageContentStream.beginText();
             titleWidth = font.getStringWidth(address)/1000 * contentSize ;
             titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * contentSize ;

             startX = (mediaBox.getWidth() -titleWidth) /2;
             startY = mediaBox.getHeight() - (margin +30) - titleHeight;

             pageContentStream.newLineAtOffset(startX,startY);
             pageContentStream.showText(address);
             pageContentStream.newLine();
             pageContentStream.endText();

             // add title
             pageContentStream.beginText();
             pageContentStream.setFont(regText,contentSize);
             titleWidth = font.getStringWidth(title)/1000 * headerSize ;
             titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerSize ;

             startX = (mediaBox.getWidth() - titleWidth) /2;
             startY = mediaBox.getHeight() - (margin + 50) - titleHeight;
             pageContentStream.newLineAtOffset(startX,startY);
             pageContentStream.showText(title);
             pageContentStream.endText();
        } catch (Exception e) {

        }
    }

    private void createCenterText(String text, PDRectangle mediaBox, Float margin, PDPageContentStream pageContentStream, Boolean isBold, Double offset) {
        try {
            float titleWidth = font.getStringWidth(text) / 1000 * headerSize;
            float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * headerSize;
            float startX = (mediaBox.getWidth() - titleWidth) / 2;
            float startY = mediaBox.getHeight() - (margin + 10) - titleHeight;

            // set the text
            if(!isBold) {
                pageContentStream.setFont(regText, contentSize);
            }
            pageContentStream.beginText();
            pageContentStream.newLineAtOffset(startX, (float) (startY - offset) );
            pageContentStream.showText(text);
            pageContentStream.endText();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private void createHeaderRow(Row<PDPage>headerRow, String text, Float width) {
        Cell<PDPage> cell;
        cell = headerRow.createCell(width, text);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFillColor(Color.lightGray);
        cell.setTextColor(Color.BLACK);
    }

    public PDDocument createAttendanceReport(List<com.hub.schoolAid.Stage> stages) throws Exception {

            PDDocument pdDocument = new PDDocument();
//            List<Student>  students = null;
            StudentDao studentDao = new StudentDao();

            // create a new page for every class
            for(com.hub.schoolAid.Stage stage : stages) {
                // check if the class has groups or partitions
                if(stage.getCategories() != null && !stage.getCategories().isEmpty()) {
                    // group the students into categories
                    List<Category>categories = stage.getCategories();
                    for(Category c: categories) {
                        System.out.println("FOUND PARTITION FOR THIS CLASS: "+ stage.getName());
                        pdDocument = createNewPageForFeedingTemplate(pdDocument,stage,studentDao, c);
                    }
                } else {
                    System.out.println("No PARTITION FOR THIS CLASS: "+ stage.getName());
                    // fetch students just by classes
                    pdDocument = createNewPageForFeedingTemplate(pdDocument,stage,studentDao);
                }
            }
            return pdDocument;
    }

    private PDDocument createNewPageForFeedingTemplate(PDDocument pdDocument, com.hub.schoolAid.Stage stage, StudentDao studentDao, Category c) throws  Exception {
        PDPage page = new PDPage(PDRectangle.A4);
        PDRectangle mediaBox = page.getMediaBox();
        pdDocument.addPage(page);

        PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, page);
        List<Student>  students;

        // set page margins
        float margin = 20;
        float yStartNewPage = mediaBox.getHeight() - margin;
        float yStart = (yStartNewPage - 105);

        //Set alignment and add school header
        String title = "FEEDING FEE REPORT FOR "+ stage.toString().toUpperCase()+" "+c.getName();
        prepPageWidthHeader(pdPageContentStream, margin, yStart, mediaBox, title);
        createCenterText("Report Generated on : "+ Utils.formatDate(LocalDate.now(), false), mediaBox, margin, pdPageContentStream, false, 70.0);
        //createCenterText("FEEDING REPORT FOR "+ stage.toString().toUpperCase(), mediaBox, margin, pdPageContentStream );

        try {
            // fetch the records from the database
            students = studentDao.getStudentFromClass(stage, c);
            // create feeding template for students
            createFeedingTemplateForStudents(students, page,pdDocument, margin, yStart, yStartNewPage);
            pdPageContentStream.close();
            return  pdDocument;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private PDDocument createNewPageForFeedingTemplate(PDDocument pdDocument, com.hub.schoolAid.Stage stage, StudentDao studentDao) throws  Exception {
        PDPage page = new PDPage(PDRectangle.A4);
        PDRectangle mediaBox = page.getMediaBox();
        pdDocument.addPage(page);

        PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, page);
        List<Student>  students;

        // set page margins
        float margin = 20;
        float yStartNewPage = mediaBox.getHeight() - margin;
        float yStart = (yStartNewPage - 105);

        //Set alignment and add school header
        String title = "FEEDING FEE REPORT FOR "+ stage.toString().toUpperCase();
        prepPageWidthHeader(pdPageContentStream, margin, yStart, mediaBox, title);
        createCenterText("Report Generated on : "+ Utils.formatDate(LocalDate.now(), false), mediaBox, margin, pdPageContentStream, false, 70.0);
        //createCenterText("FEEDING REPORT FOR "+ stage.toString().toUpperCase(), mediaBox, margin, pdPageContentStream );

        try {
            // fetch the records from the database
            students = studentDao.getStudentFromClass(stage);
            // create feeding template for students
            createFeedingTemplateForStudents(students, page, pdDocument, margin, yStart, yStartNewPage);
            pdPageContentStream.close();
            return pdDocument;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    private void createFeedingTemplateForStudents(List<Student>students, PDPage page,PDDocument pdDocument, float margin, float yStart, float yStartNewPage) throws Exception{
        if (students.size() > 0) {
            // Display the records in a table
            float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
            BaseTable baseTable = new BaseTable(yStart - 30, yStartNewPage, 20, tableWidth, (margin), pdDocument, page, true, true);
            Row<PDPage> headerRow = baseTable.createRow(30f);

            createHeaderRow(headerRow, "STUDENT", (float) 30);
            createHeaderRow(headerRow, "BALANCE", (float) 15);
            createHeaderRow(headerRow, "COUPONS LEFT", (float) 15);
            createHeaderRow(headerRow, "MON", (float) 8);
            createHeaderRow(headerRow, "TUE", (float) 8);
            createHeaderRow(headerRow, "WED", (float) 8);
            createHeaderRow(headerRow, "THUR", (float) 8);
            createHeaderRow(headerRow, "FRI", (float) 8);
            baseTable.addHeaderRow(headerRow);

            // create rows for the table
            for (Student s : students) {
                Row<PDPage> row = baseTable.createRow(24f);
                Cell<PDPage> cell;

                cell = row.createCell(30, s.getFirstname() == null ? " " : s.getFirstname() + " " + s.getLastname() + " " + s.getOthername());
                cell.setAlign(HorizontalAlignment.LEFT);
                cell.setValign(VerticalAlignment.MIDDLE);

                cell = row.createCell(15, String.valueOf(s.getAccount().getFeedingFeeCredit()));
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);

                int coupons;
                Double bal = s.getAccount().getFeedingFeeCredit();
                if(bal<0){
                    coupons = 0;
                } else {
                    coupons = (int) (bal / s.getAccount().getFeedingFeeToPay());
                }

                row.createCell(15, String.valueOf(coupons))
                        .setAlign(HorizontalAlignment.CENTER);
                getEmptyPageCellForFeeding(row);
                getEmptyPageCellForFeeding(row);
                getEmptyPageCellForFeeding(row);
                getEmptyPageCellForFeeding(row);
                getEmptyPageCellForFeeding(row);
            }
            // draw the table
            baseTable.draw();
        }
    }

    public PDDocument generateSchoolFeesReport(ObservableList<Student>students) throws  IOException {
        if(students.isEmpty())
            return null;

        PDDocument pdDocument = new PDDocument();

        // create a page and save all the items on it
        PDPage page = new PDPage(PDRectangle.A4);
        PDRectangle mediaBox = page.getMediaBox();
        pdDocument.addPage(page);

        // set page margins
        float margin = 20;
        float yStartNewPage = mediaBox.getHeight() - margin;
        float yStart = (yStartNewPage - 105);

        try {
            PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, page);

            //Set alignment and add school header
            String title = "SUMMARY OF SCHOOL FEES REPORT";
            prepPageWidthHeader(pdPageContentStream, margin, yStart, mediaBox, title);
            
            createCenterText("Report Generated on : "+ LocalDate.now().toString(), mediaBox, margin, pdPageContentStream, false, 70.0);

            // Display the records in a table
            float tableWidth = ((page.getMediaBox().getWidth() - (2 * margin)));
            BaseTable baseTable = new BaseTable(yStart - 30, yStartNewPage, 20, tableWidth, (margin), pdDocument, page, true, true);
            Row<PDPage> headerRow = baseTable.createRow(30f);

            createHeaderRow(headerRow, "STUDENT", (float) 36);
            createHeaderRow(headerRow, "AMOUNT DUE", (float) 16.6);
            createHeaderRow(headerRow, "AMOUNT PAID", (float) 16.6);
            createHeaderRow(headerRow, "BALANCE", (float) 16.6);
            createHeaderRow(headerRow, "STATUS", (float) 14.6);
            baseTable.addHeaderRow(headerRow);

            // create rows for all the students
            for(Student st : students) {
                Double bal = st.getAccount().getSchoolFeesBalance();
                Row<PDPage> row = baseTable.createRow(24f);
                Cell<PDPage> cell;
                String status;

                if(st.getAccount().getSchoolFeesBalance() == 0) {
                    status = "N/A";
                }else if(bal < 0) {
                    status = "OWING";
                } else if(bal >= 0) {
                    status = "PAID";
                } else status = "N/A";

                row.createCell(36, st.toString());
                row.createCell((float)16.6, st.getAccount().getSchoolFeesBalance() < 0 ? String.valueOf((st.getAccount().getSchoolFeesBalance() * -1)) :
                        String.valueOf(st.getAccount().getSchoolFeesBalance()))
                    .setAlign(HorizontalAlignment.CENTER);
                row.createCell((float) 16.6, String.valueOf(st.getAccount().getSchFeesPaid()))
                    .setAlign(HorizontalAlignment.CENTER);
                row.createCell((float) 16.6, String.valueOf((bal)))
                    .setAlign(HorizontalAlignment.CENTER);
                row.createCell((float) 14.6, status);
            }

            // draw the table
            baseTable.draw();

            // close the stream writer
            pdPageContentStream.close();
        } catch (IOException e) {
            throw new IOException(e);
        }

        // add the school header to the document

        return pdDocument;
    }

    public PDDocument generateStatement(Student student) {
        TransactionLoggerDao transactionLoggerDao = new TransactionLoggerDao();
        List<TransactionLogger>feesLog = transactionLoggerDao.getLog(TransactionType.SCHOOL_FEES, student.getId());
        List<TransactionLogger>feeding = transactionLoggerDao.getLog(TransactionType.FEEDING_FEE, student.getId());
        List<TransactionLogger>sales = transactionLoggerDao.getLog(TransactionType.SALES, student.getId());

        pdDocument = new PDDocument();

        // create a page and save all the items on it
        PDPage page = new PDPage(PDRectangle.A4);
        PDRectangle mediaBox = page.getMediaBox();
        pdDocument.addPage(page);

        // set page margins
        float margin = 20;
        float yStartNewPage = mediaBox.getHeight() - margin;
        float yStart = (yStartNewPage - 105);

        try {
            PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, page);
            String title = "ACCOUNT STATEMENT FOR "+ student.toString()+",   "+student.getStage().getName().toUpperCase();
            prepPageWidthHeader(pdPageContentStream, margin, yStart, mediaBox, title);

            // Display the records in a table
            float tableWidth = ((page.getMediaBox().getWidth() - (2 * margin)));

            // [FIRST TABLE] -- set records for the first table
            BaseTable baseTable = new BaseTable(yStart - 30, yStartNewPage, 20, tableWidth, (margin), pdDocument, page, true, true);
            Double totalPayment=0.0;

            // create rows for the SCHOOL FEES TRANSACTIONS
            totalPayment = logTransactions(feesLog, baseTable, totalPayment);

            // set the summary for all the records
            if(!feesLog.isEmpty()) {
                prepTransactionBal(baseTable, totalPayment, student, TransactionType.SCHOOL_FEES);
            }

            // -- [SECOND TABLE] create rows for the SCHOOL FEEDING FEE TRANSACTIONS
            totalPayment = logTransactions(feeding, baseTable, totalPayment);
            if(!feeding.isEmpty()) {
                prepTransactionBal(baseTable, totalPayment, student, TransactionType.FEEDING_FEE);
            }

            //--[THIRD TABLE] create rows for all sales transactions
            totalPayment = logTransactions(sales, baseTable, totalPayment);
            if(!sales.isEmpty()) {
                prepTransactionBal(baseTable, totalPayment, student, TransactionType.SALES);
            }

            baseTable.draw();

            pdPageContentStream.close();
        } catch (IOException e) {
            Notification.getNotificationInstance().notifyError("Error while generating statement", "error");
        }
        return  pdDocument;
    }

    private void prepTransactionBal(BaseTable baseTable, Double acumulator, Student student, TransactionType transactionType) {
        double amountDue =0.0;

        if(transactionType == TransactionType.SCHOOL_FEES) {
            amountDue = student.getAccount().getSchoolFeesBalance();
            // check the balance
            Row<PDPage>pdPageRow2 = baseTable.createRow(30);
            Cell<PDPage>cell;
            Double bal = student.getAccount().getSchoolFeesBalance();
            cell = pdPageRow2.createCell( 25, "BALANCE");
            cell.setAlign(HorizontalAlignment.LEFT);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFont(font);

            cell = pdPageRow2.createCell(15, bal.toString());
            cell.setAlign(HorizontalAlignment.RIGHT);
            cell.setValign(VerticalAlignment.MIDDLE);
            cell.setFont(font);
        } else if(transactionType == TransactionType.FEEDING_FEE) {
            amountDue = 0.0;
        } else if(transactionType == TransactionType.SALES) {
            amountDue = 0.0;
        }
        Row<PDPage>pdPageRow = baseTable.createRow(30);
        Cell<PDPage>cell;
        cell = pdPageRow.createCell( 25, "TOTAL PAYMENTS");
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(font);

        cell = pdPageRow.createCell(15, acumulator.toString());
        cell.setAlign(HorizontalAlignment.RIGHT);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setFont(font);


    }

    /**
     * Display all the transactions
     * @param logs the transaction logs
     * @param baseTable the table that we are showing the transactions in
     * @param accumulator it adds up the total amount of all the transactions
     * @return the total amount of all the transactions
     */
    private Double logTransactions(List<TransactionLogger> logs, BaseTable baseTable, Double accumulator) {
        if(logs.size() > 0) {
            // SET the table header rows
            Row<PDPage> headerRow = baseTable.createRow(30f);
            createHeaderRow(headerRow, "DATE", (float) 25);
            createHeaderRow(headerRow, "PREVIOUS BAL.", (float) 10);
            createHeaderRow(headerRow, "AMOUNT PAID", (float) 10);
            createHeaderRow(headerRow, "NEW BAL", (float) 10);
            createHeaderRow(headerRow, "DESCRIPTION", (float) 25);
            createHeaderRow(headerRow, "PAID BY", (float) 20);
            baseTable.addHeaderRow(headerRow);
            accumulator = 0.0;

            for (TransactionLogger log : logs) {
                Row<PDPage> row = baseTable.createRow(24);
                Cell<PDPage>cell;
                cell =row.createCell(25, Utils.formatDate(log.getDate(), false));
                cell.setAlign(HorizontalAlignment.LEFT);
                cell.setValign(VerticalAlignment.MIDDLE);

                cell = row.createCell((float)10, log.getBal_before_payment().toString());
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);

                cell = row.createCell((float) 10, log.getAmount().toString());
                cell.setAlign(HorizontalAlignment.RIGHT);
                cell.setValign(VerticalAlignment.MIDDLE);
                accumulator+=log.getAmount();

                cell = row.createCell((float)10, log.getBal_after_payment().toString());
                cell.setAlign(HorizontalAlignment.RIGHT);
                cell.setValign(VerticalAlignment.MIDDLE);

                cell = row.createCell((float) 25, log.getDescription());
                cell.setAlign(HorizontalAlignment.LEFT);
                cell.setValign(VerticalAlignment.MIDDLE);

                cell = row.createCell((float) 20, log.getPaidBy());
                cell.setAlign(HorizontalAlignment.LEFT);
                cell.setValign(VerticalAlignment.MIDDLE);
            }
        }
        return accumulator;
    }

    private Cell<PDPage> getEmptyPageCellForFeeding(Row<PDPage> row) {
        Cell<PDPage> cell;
        cell = row.createCell(8, "");
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setValign(VerticalAlignment.MIDDLE);

        return cell;
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
                    String schName = app.getName().toUpperCase();
//                    String address = "Location: Sowutuom - Accra, Contact: ";
//                    String heading = "TERMINAL REPORT";

                    //student details\
                    String name = "Name: " + report.getStudent().toString().toUpperCase() + "                Class: " + report.getStudent().getStage().getName().toUpperCase();
                    String term = "Term ends: 20th December, 2019                Next Term Begins: 8th January, 2019";
//                  String attendance = "Second Term Report        Attendance: .........out of.........";
                    String attendance = "First Term Report";
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

                    // add main page header
                    prepPageWidthHeader(pageContentStream, margin, yStart, mediaBox, "");

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
                    startY = mediaBox.getHeight() - (margin + 485) - titleHeight;
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
            }
        } catch (Exception e) {
//                e.printStackTrace();
        }
    }

    public static void savePDFToLocation(PDDocument pdDocument) {
        //choose a file location
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                FileChooser fileChooser =new FileChooser();
                fileChooser.setInitialFileName("Report.pdf");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files",".pdf"));
                fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("PDF Files",".pdf"));
                File file  = fileChooser.showSaveDialog(new Stage());
                System.out.println("This is the file"+ file);
                    try {
                        if(file != null) {
                            pdDocument.save(file+".pdf");
                            pdDocument.close();
                        } else {
                            pdDocument.close();
                        }
                    } catch (IOException e) {

                    }
            }
        });
    }

    public static void printPDF(PDDocument pdDocument) {

    }

    public static  void createAttendanceReport (LocalDate from,LocalDate to,List<Attendance> attendanceList){

        // loop through the attendance and compare the dates
        //  the attendance date should be greater than or equal to FROM but less than TO or equal to TO
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

    public PDDocument createBill(Bill bill) {
        if(bill.getStudents().size() > 0) {
            pdDocument = new PDDocument();

            for(Student student: bill.getStudents()) {
                // create a page an save the bill on it
                PDPage page = new PDPage(PDRectangle.A4);
                PDRectangle mediaBox = page.getMediaBox();
                pdDocument.addPage(page);

                // set page margins
                float margin = 20;
                float yStartNewPage = mediaBox.getHeight() - margin;
                float yStart = (yStartNewPage - 105);

                try {
                    PDPageContentStream pageContentStream = new PDPageContentStream(pdDocument, page);
                    String title = "BILL FOR"+ student.toString();
                    prepPageWidthHeader(pageContentStream, margin, yStart, mediaBox, title);
                    pageContentStream.close();

                    // Display the records in a table
                    float tableWidth = ((page.getMediaBox().getWidth() - (2 * margin)));
                    BaseTable baseTable = new BaseTable(yStart - 30, yStartNewPage, 20, tableWidth, (margin), pdDocument, page, true, true);
                    Row<PDPage> headerRow = baseTable.createRow(30f);
                    createHeaderRow(headerRow, "ITEM", (float) 60);
                    createHeaderRow(headerRow, "COST", (float) 40);
                    baseTable.addHeaderRow(headerRow);

                    // CREATE THE ACTUAL ROWS
                    Row<PDPage> row = baseTable.createRow(24f);
                    row.createCell(60, "Tuition Fee");
                    row.createCell(40, bill.getTuitionFee().toString())
                            .setAlign(HorizontalAlignment.CENTER);

                    // display the records in a table
                    for(BillItem billItem : bill.getBill_items()) {
                        row = baseTable.createRow(24f);
                        row.createCell(60, billItem.getItem().getName());
                        row.createCell(40, billItem.getCost().toString())
                            .setAlign(HorizontalAlignment.CENTER);
                    }

                    baseTable.draw();
                } catch (IOException e) {
                    Notification.getNotificationInstance().notifyError("error while creating bill", "error");
                }

            }
        }
        return pdDocument;
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

                if(student.getPaySchoolFees() && student.getAccount().getSchoolFeesBalance() < 0) {
                    Row < PDPage > row3 = baseTable.createRow(30f);

                    cell = row3.createCell(bigSize,"School Fees Arrears");
                    cell.setAlign(HorizontalAlignment.LEFT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    cell = row3.createCell(smallSize,String.valueOf((student.getAccount().getSchoolFeesBalance()) * -1));
                    total+=(student.getAccount().getSchoolFeesBalance() * -1);
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    // add value to total
                    total+=student.getAccount().getSchoolFeesBalance() * -1;
                }

                if(student.getPaySchoolFees()){
                    Row < PDPage > row4 = baseTable.createRow(30f);

                    cell = row4.createCell(bigSize,"School Fees");
                    cell.setAlign(HorizontalAlignment.LEFT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    cell = row4.createCell(smallSize,String.valueOf((student.getStage().getBill().getTotalBill())));
                    total+=(student.getAccount().getSchoolFeesBalance());
                    cell.setAlign(HorizontalAlignment.RIGHT);
                    cell.setValign(VerticalAlignment.MIDDLE);
                    cell.setFontSize(12);

                    // add value to total
                    total+=student.getStage().getBill().getTotalBill();
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
//           String schName = app.getName().toUpperCase();
           String heading = "REPORT ON ATTENDANCE FOR" + " " + this.dateToString(date);

           pdDocument.addPage(pdPage);

           float margin = 20;
           float yStartNewPage = pdPage.getMediaBox().getHeight() - ( margin);
           float yStart = pdPage.getMediaBox().getHeight() - ( margin + 105);

           PDPageContentStream pageContentStream = new PDPageContentStream(pdDocument,pdPage);
           prepPageWidthHeader(pageContentStream, margin, yStart, mediaBox, heading);
//           pageContentStream.close();

           //add table
           float tableWidth = pdPage.getMediaBox().getWidth() - ((2 * margin));
           BaseTable baseTable = new BaseTable(yStart ,yStartNewPage,20,tableWidth,(margin),pdDocument,pdPage,true,true);

           //Create Header Row
           Row<PDPage>headerRow = baseTable.createRow(15f);
           Cell <PDPage> cell ;


           createHeaderRow(headerRow, "STUDENT", (float) 40);
           createHeaderRow(headerRow, "CLASS", (float)14);
           createHeaderRow(headerRow, "PAID", (float)14);
           createHeaderRow(headerRow, "AMOUNT PAID", (float)10);

           baseTable.addHeaderRow(headerRow);

           for (Attendance attendance:attendanceList){
               if(attendance.getDate().toString().equals(date.toString())){
                   Row<PDPage> row = baseTable.createRow(10f);
                   row.createCell(40,attendance.getStudent().toString());

                   cell = row.createCell(14,attendance.getStudent().getStage().getName());
                   cell.setAlign(HorizontalAlignment.CENTER);
                   cell.setValign(VerticalAlignment.MIDDLE);

                   cell = row.createCell(14,attendance.getPaidNow()? "YES" : "NO");
                   cell.setAlign(HorizontalAlignment.CENTER);
                   cell.setValign(VerticalAlignment.MIDDLE);


                   if((attendance.getStudent().getPayFeeding() && attendance.getPaidNow())) {
                       row.createCell(10, String.valueOf(attendance.getFeedingFee()));
                       totalFeeding +=attendance.getFeedingFee();

                   } else{
                       row.createCell(10, "0.00");
                   }

                   totalAttendance++;
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
        for (Assessment as : sortedAssessment) {
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
       }
        return  pdPage;
    }
}
