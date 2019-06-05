package com.hub.schoolAid;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Created by HUBKB.S on 11/18/2017.
 */
@Entity(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//  @GenericGenerator(name="sequence_stu",strategy = "model.Student_Id_Generator")
//  @GeneratedValue(generator = "sequence_stu")
    @Column (name = "student_id")
    private Long Id;

    //other attributes of the student
    private String firstname;
    private String lastname;
    private String othername;
    private  int age;
    private String gender;
    private Boolean payFeeding;
    private Boolean paySchoolFees;
    public enum FeedingStatus{DAILY,WEEKLY,MONTHLY,TERMLY,PERIODIC,SEMI_PERIODIC}
    private LocalDate reg_date;
    private LocalDate dob;

    //constructors
    public Student(){

    }

    public Student(Student student){
       this.setFirstname(student.getFirstname());
       this.setStage(student.getStage());
    }

    @OneToOne(orphanRemoval = true,cascade = CascadeType.ALL)
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private Stage stage;

    @OneToOne(orphanRemoval = true,cascade = CascadeType.ALL)
    @JoinColumn(name = "student_acc")
    private StudentAccount account;

    @Enumerated(EnumType.STRING)
    private FeedingStatus feedingStatus = FeedingStatus.DAILY;

    //a student has several attendance
    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Attendance> attendance;

    @OneToMany(mappedBy = "student",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set <AttendanceTemporary> attendanceTemporary;

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

    public Long getUserId() {
        return Id;
    }

    public void setUserId(Long userId) {
        this.Id = userId;
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
       this.getAccount().setFeedingFeeCredit(this.getAccount().getFeedingFeeCredit()-amnt);
       return  true;
    }

    public Boolean addNewFeedingFeeCredit(Double amnt){
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
//            System.out.println("The encoded image byte array is shown below.Please use this in your webpage image tag.\n"+encodedImage);
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
