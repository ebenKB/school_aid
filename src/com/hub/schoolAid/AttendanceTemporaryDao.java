package com.hub.schoolAid;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.List;

public class AttendanceTemporaryDao {

    private EntityManager em;
    private ObservableList<AttendanceTemporary> attendanceList= FXCollections.observableArrayList();
    /**
     * This method saves students to a temporary attendance table
     * the data is then moved to the master table after the close of the day
     * @param student
     * @return
     * @throws HibernateException
     */
    public Boolean checkStudenIn(Student student)throws HibernateException{
        AttendanceTemporary attendance =new AttendanceTemporary();
        attendance.setStudent(student);
        attendance.setDate(LocalDate.now());
        attendance.setPresent(Boolean.FALSE);
        if(student.getPayFeeding())
            attendance.setFeedingFee(student.getAccount().getFeedingFeeToPay());
//          attendance.setFeedingFee(student.getStage().getFeeding_fee());
        else attendance.setFeedingFee(0.0);
        HibernateUtil.save(AttendanceTemporary.class,attendance);
        return true;
    }

    public  void markPresent(AttendanceTemporary attendanceTemporary){
        attendanceTemporary.setPresent(Boolean.TRUE);
        if(attendanceTemporary.getStudent().getPayFeeding()){
            if(attendanceTemporary.getStudent().getFeedingStatus()!= Student.FeedingStatus.DAILY){
                if(attendanceTemporary.getFeedingFee()>attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
                    System.out.print("this is the actual amount:"+attendanceTemporary.getFeedingFee());
                    attendanceTemporary.getStudent().addNewFeedingFeeCredit((attendanceTemporary.getFeedingFee()-attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()));
                    attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
                }else {
                    attendanceTemporary.getStudent().debitFeedingAccount(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
                }
                StudentDao studentDao = new StudentDao();
                studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
            }

        }else{
            attendanceTemporary.setFeedingFee(0.0);
        }
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.merge(attendanceTemporary);
        HibernateUtil.commit();
    }

    public  Boolean markAbsent(AttendanceTemporary attendanceTemporary){
        attendanceTemporary.setPresent(Boolean.FALSE);
//        attendanceTemporary.setFeedingFee(0.0);
       if(attendanceTemporary.getStudent().getFeedingStatus()!= Student.FeedingStatus.DAILY) {
           attendanceTemporary.getStudent().addNewFeedingFeeCredit(attendanceTemporary.getFeedingFee());
           StudentDao studentDao = new StudentDao();
           studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
       }
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.merge(attendanceTemporary);
        HibernateUtil.commit();
        return true;
    }
    public List <AttendanceTemporary> getTempAttendance() {
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        return em.createQuery(" from AttendanceTemporary").getResultList();
    }

    public List<AttendanceTemporary> getAbsentees(){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        return em.createQuery(" from AttendanceTemporary A where A.present=false ").getResultList();
    }

    public Boolean deleteAllTempAttendance(){
       try{
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           System.out.print("trying to delete....");
           List<AttendanceTemporary> list =getTempAttendance();
           for(AttendanceTemporary a:list){
               em.remove(a);
               System.out.print("removed an object");
           }
           HibernateUtil.commit();
           return  true;
       }catch (PersistenceException p){
           System.out.print("Error in Attendance temporary dao");
           p.printStackTrace();
           return false;
       }
    }

    public AttendanceTemporary getAttendance(int id) {
        try{
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            return (AttendanceTemporary) em.createQuery("from AttendanceTemporary where  +id+ like '"+id+"' ").getSingleResult();
        }catch (PersistenceException p){
            p.printStackTrace();
            return null;
        }

    }

    public void removeStudent(Student student){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.createQuery("delete from AttendanceTemporary  where '+student_id+' = '+student.id+' ").executeUpdate();
    }
}
