package com.hub.schoolAid;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import com.sun.javafx.tools.packager.Log;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;

import javax.persistence.*;
import java.sql.Blob;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Created by HUBKB.S on 11/18/2017.
 */
@Entity(name = "students")
public class Student {
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_sequence")


    @SequenceGenerator(name = "student_sequence", sequenceName = "student_sequence", allocationSize = 1)
    @GeneratedValue(generator = "student_sequence")

//   @GenericGenerator(name="student_sequence",strategy = "model.Student_Id_Generator")
//   @GeneratedValue(generator = "student_sequence")

    @Column (name = "student_id")
    private Long Id;

//    @Lob
//    private byte[] picture;

    @OneToOne(mappedBy = "student", optional = true, cascade = CascadeType.ALL)
    private Picture picture;

    @OneToOne(mappedBy = "student", optional = true, cascade = CascadeType.ALL)
    private PreviousSchool previousSchool;

    //other attributes of the student
    private String firstname;
    private String lastname;
    private String othername;
    private  int age;
    private String gender;
    private Boolean payFeeding;
    private Boolean paySchoolFees;
    private Boolean hasAllergy;
    public enum FeedingStatus{ DAILY, WEEKLY, MONTHLY, TERMLY, PERIODIC, SEMI_PERIODIC }
    private LocalDate reg_date;
    private LocalDate dob;
//    private String previousSchool;
    private boolean isActive; // indicates whether the student is still actively enrolling in the school

    // constructors
    public Student(){
        setSelected(false);
        setDeleted(false);
        setHasAllergy(false);
        setActive(true);
    }

    public Student(Student student){
       this.setFirstname(student.getFirstname());
       this.setStage(student.getStage());
        isSelected = false;
        setDeleted(false);
    }

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    private Parent parent;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Guardian> guardian;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
//    @JoinColumn(name = "class_id")
    private Stage stage;

    @ManyToOne(optional = true)
    @JoinTable(name="student_category")
    private Category category;

    @OneToOne(orphanRemoval = true,cascade = CascadeType.ALL)
    @JoinColumn(name = "student_acc")
    private StudentAccount account;

    @Enumerated(EnumType.STRING)
    private FeedingStatus feedingStatus = FeedingStatus.DAILY;

    //a student has several attendance
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Attendance> attendance;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set <AttendanceTemporary> attendanceTemporary;

    // every student has a terminal report
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true, orphanRemoval=true)
    private TerminalReport terminalReport;

    /**
     * one class or stage can multiple Bills for previous terms and current term
     * When a class is deleted, remove all the bills associated with the class
     */
    @ManyToMany(mappedBy = "students", cascade = CascadeType.REMOVE)
    private List<Bill>bills;

    @ManyToMany(mappedBy = "student", cascade = {CascadeType.PERSIST})
    private List<Allergy>allergies;

    @Transient
    private Boolean isSelected;

    private Boolean isDeleted = false;

    // a student can have many assessments assigned to them
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set <Assessment> assessments;

    //getters and setters

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getOthername() {
        return othername;
    }

    public void setOthername(String othername) {
        this.othername = othername;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getId() {
        return Id;
    }

    public TerminalReport getTerminalReport() {
        return terminalReport;
    }

    public void setTerminalReport(TerminalReport terminalReport) {
        this.terminalReport = terminalReport;
    }

    public Set<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(Set<Assessment> assessments) {
        this.assessments = assessments;
    }

    public PreviousSchool getPreviousSchool() {
        return previousSchool;
    }

    public void setPreviousSchool(PreviousSchool previousSchool) {
        this.previousSchool = previousSchool;
    }

    //    public String getPreviousSchool() {
//        return previousSchool;
//    }
//
//    public void setPreviousSchool(String previousSchool) {
//        this.previousSchool = previousSchool;
//    }

    public Boolean getHasAllergy() {
        return hasAllergy;
    }

    public void setHasAllergy(Boolean hasAllergy) {
        this.hasAllergy = hasAllergy;
    }

    public List<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<Allergy> allergies) {
        this.allergies = allergies;
    }

    //    public List<Sales> getSales() {
//        return sales;
//    }
//
//    public void setSales(List<Sales> sales) {
//        this.sales = sales;
//    }

    public LocalDate getReg_date() {
        return reg_date;
    }

    public void setReg_date(LocalDate reg_date) {
        this.reg_date = reg_date;
    }

    public Boolean getPayFeeding() {
        return payFeeding;
    }

    public void setPayFeeding(Boolean payFeeding) {
        this.payFeeding = payFeeding;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Boolean getPaySchoolFees() {
        return paySchoolFees;
    }

    public void setPaySchoolFees(Boolean paySchoolFees) {
        this.paySchoolFees = paySchoolFees;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    //    public byte[] getPicture() {
//        return picture;
//    }
//
//    public void setPicture(byte[] picture) {
//        this.picture = picture;
//    }

    public List<Guardian> getGuardian() {
        return guardian;
    }

    public void setGuardian(List<Guardian> guardian) {
        this.guardian = guardian;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    //        public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }

//    public StudentImage getImage() {
//        return image;
//    }
//
//    public void setImage(StudentImage image) {
//        this.image = image;
//    }

    public Set<Attendance> getAttendance() {
        return attendance;
    }

    public void setAttendance(Set<Attendance> attendance) {
        this.attendance = attendance;
    }

    public Set<AttendanceTemporary> getAttendanceTemporary() {
        return attendanceTemporary;
    }

    public void setAttendanceTemporary(Set<AttendanceTemporary> attendanceTemporary) {
        this.attendanceTemporary = attendanceTemporary;
    }


    public StudentAccount getAccount() {
        return account;
    }

    public void setAccount(StudentAccount account) {
        this.account = account;
    }

    public FeedingStatus getFeedingStatus() {
        return feedingStatus;
    }

    public void setFeedingStatus(FeedingStatus feedingStatus) {
        this.feedingStatus = feedingStatus;
    }

    public Boolean hasFeedingCredit(){
        return this.getAccount().getFeedingFeeCredit()>0;
    }

    public Boolean debitFeedingAccount(Double amnt){
       this.getAccount().setFeedingFeeCredit(this.getAccount().getFeedingFeeCredit() - amnt);
       return  true;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public Boolean updateFeedingAccount(Double amount) {
        try {
            this.getAccount().setFeedingFeeCredit(this.getAccount().getFeedingFeeCredit() + amount);
            return true;
        } catch (Exception e) {
            return  false;
        }
    }

    public Boolean addNewFeedingFeeCredit(Double amnt) {
       try{
           this.getAccount().setFeedingFeeCredit(this.getAccount().getFeedingFeeCredit() + amnt);
           /**
            * for semi-periodic payment when the balance gets back to zero, hnge to student to daily.
            */
           if(this.getFeedingStatus() == FeedingStatus.SEMI_PERIODIC){
               if(this.getAccount().getFeedingFeeCredit() == 0){
                   this.setFeedingStatus(FeedingStatus.DAILY);
                   StudentDao studentDao =new StudentDao();
                   studentDao.updateStudentRecord(this);
                   studentDao =null;
               }
           }
       }catch (Exception e){
//           e.printStackTrace();
           return false;
       }
       return true;
    }

//    public Boolean resetFeedingFee(Double amount) {
//        try {
//            //we cannot reset the feeding fee for students who do not pay feeding fee unless the new amount is 0.00
////            if(!this.getPayFeeding() && amount != 0)
////                return false;
//            this.getAccount().setFeedingFeeCredit(amount);
//
//            if(this.getFeedingStatus() == FeedingStatus.SEMI_PERIODIC) {
//                if(this.getAccount().getFeedingFeeCredit() == 0) {
//                    this.setFeedingStatus(FeedingStatus.DAILY);
//                }
//            }
//            StudentDao studentDao =new StudentDao();
//            studentDao.updateStudentRecord(this);
//            return true;
//        }catch (Exception e) {
//            return false;
//        }
//    }

//    public String  convertFileContentToBlob(String filePath) {
//        ByteArrayOutputStream baos =null;
//        String encodedImage=null;
//        byte[] res = null;
//
//        try{ BufferedImage image = ImageIO.read(new File(filePath));
//            baos = new ByteArrayOutputStream();
//            ImageIO.write(image, "png", baos);
//            res = baos.toByteArray();
////            encodedImage = Base64.encode(baos.toByteArray());
//            encodedImage = Base64.getEncoder().encode(baos.toByteArray());
//            System.out.println("The encoded image byte array is shown below.Please use this in your web page image tag.\n"+encodedImage);
//        }
//            catch(Exception e) {
//                e.printStackTrace();
//                System.out.println("Some problem has occurred. Please try again");
//            }
//            return encodedImage;
//    }

    public int calcAge(LocalDate date){
       if(date==null)
           return 0;
        int age=0;
        age = LocalDate.now().getYear() - date.getYear();
        if(date.getMonth().getValue() <= LocalDate.now().getMonth().getValue())
            return age;
        else return age-1;
    }

    @Override
    public String toString() {
        return this.getFirstname().toUpperCase()+" "+this.getOthername().toUpperCase()+" "+this.getLastname().toUpperCase();
    }
}
