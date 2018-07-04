//package model;
//
//
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.mapping.List;
//
///**
// * Created by HUBKB.S on 11/18/2017.
// */
//public class StudentTest {
//
//    public static void main(String args[]){
//
//        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//        Session session =sessionFactory.getCurrentSession();
//
//        StudentDao studentDao=new StudentDao();
//        Student student=new Student();
//
//        java.util.List <Student> st =studentDao.getStudent("9577017");
//
//        for(Student s:st){
//            student=s;
//            System.out.print("we got a student...");
//            s.toString();
//        }
//
//        Sales sales = new Sales();
//        sales.getItem().setQty(10);
//        session.save(student);
//        sales.setStudent(student);
//        sales.getItem().setCost(10.2);
//        SalesDao salesDao =new SalesDao();
//        salesDao.createSale(sales);
//
//
//
////
////        try {
////            Transaction transaction = session.beginTransaction();
//            student.setFirstname("Ebenezer");
//            student.setLastname("Adjei");
//            student.setOthername("K.B");
//            //student.setUserId(121);
//
//            Parent parent = new Parent();
//            parent.setFirstName("Mad. Mercy");
//            parent.setLastName("Mensah");
//            parent.setTelephone("0548");
//
//            //create a class for the student
//            Stage stage =new Stage();
//            stage.setName("4");
//            stage.setNum_on_roll(1);
//            stage.setClassValue(1);
//
//            //create sales for the student
//            sales = new Sales();
//            sales.setUnitCost(23.2);
//            sales.setQty(5);
//            // student.setSales(sales);
//            sales.setStudent(student);
//
//            //mark attendance for the student
//            Attendance attendance=new Attendance();
//            attendance.setIsPresent(1);
//            attendance.setFeedingFee(3.0);
//            student.setAttendance(attendance);
//
//            student.setStage(stage);
//            student.setParent(parent);
//
//            studentDao.addNewStudent(student);
////            studentDao.selectAllStudents();
//////            student.setFirstname("Angel..");
//////            studentDao.updateStudentRecord(student);
////
////        } catch (Exception e) {
////            e.printStackTrace();
////        }finally {
////  //           session.close();
////        }
//    }
//}
