package com.hub.schoolAid;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
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
    public Boolean checkStudenIn(Student student)throws HibernateException {
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
            if(attendanceTemporary.getStudent().getFeedingStatus() != Student.FeedingStatus.DAILY){
                if(attendanceTemporary.getFeedingFee() > attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
                    //the student is paying in excess so save the balance and set the feeding fee to the amount
                    attendanceTemporary.getStudent().addNewFeedingFeeCredit((attendanceTemporary.getFeedingFee()-attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()));
//                   attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
                    attendanceTemporary.setFeedingFee(attendanceTemporary.getFeedingFee());
                }else {
                    //if a periodic or semi periodic student pays for only a day even though they may be owing for previous days --add
                    if(attendanceTemporary.hasPaidNow() && attendanceTemporary.getFeedingFee() == attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
                        attendanceTemporary.setFeedingFee(attendanceTemporary.getFeedingFee());
                    }
                    else attendanceTemporary.getStudent().debitFeedingAccount(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()); //--added else
                }
            }else {
                if( !attendanceTemporary.hasPaidNow()){
                    //mark the student present, set the feeding fee to zero and then debit the student
                    attendanceTemporary.getStudent().debitFeedingAccount(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
                }else if(attendanceTemporary.hasPaidNow()){
                    //check if the student is paying in excess of what they should pay for a day
                    if(attendanceTemporary.getFeedingFee() > attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
                        attendanceTemporary.getStudent().addNewFeedingFeeCredit((attendanceTemporary.getFeedingFee()-attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()));
                        attendanceTemporary.setFeedingFee(attendanceTemporary.getFeedingFee());

                        StudentDao studentDao = new StudentDao();
                        studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());

                        //consider changing the status of the student to periodic
                        if(attendanceTemporary.getStudent().getAccount().getFeedingFeeCredit() > 0){
                            attendanceTemporary.getStudent().setFeedingStatus(Student.FeedingStatus.SEMI_PERIODIC);
                            studentDao.updateStudentRecord(attendanceTemporary.getStudent());
                        }
                    }
                }
            }
            StudentDao studentDao = new StudentDao();
            studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
        }else attendanceTemporary.setFeedingFee(0.0);

        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.merge(attendanceTemporary);
        HibernateUtil.commit();
    }
    public  Boolean markAbsent(AttendanceTemporary attendanceTemporary){
        attendanceTemporary.setPresent(Boolean.FALSE);

        if(attendanceTemporary.getStudent().getFeedingStatus()!= Student.FeedingStatus.DAILY) {
            if(attendanceTemporary.getFeedingFee() > attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
                attendanceTemporary.getStudent().addNewFeedingFeeCredit(
                        (attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()) * -1);
            }else{
                if(attendanceTemporary.getFeedingFee() != attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){   //--add
                    attendanceTemporary.getStudent().addNewFeedingFeeCredit(attendanceTemporary.getFeedingFee());
                } //--add

                //--added this block
                else if( (attendanceTemporary.getFeedingFee() == attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay())&& !attendanceTemporary.hasPaidNow()){
                    attendanceTemporary.getStudent().addNewFeedingFeeCredit(attendanceTemporary.getFeedingFee());
                } //-end add
            }
            attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
            StudentDao studentDao = new StudentDao();
            studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
        }else if((!attendanceTemporary.hasPaidNow())){
            System.out.print("in 1st else marking absent with :"+attendanceTemporary.getFeedingFee());
            attendanceTemporary.getStudent().addNewFeedingFeeCredit(attendanceTemporary.getFeedingFee());
            StudentDao studentDao = new StudentDao();
            studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
        }else if(attendanceTemporary.hasPaidNow() && attendanceTemporary.getFeedingFee() > attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
            System.out.print("in second else marking absent with :"+attendanceTemporary.getFeedingFee());
            attendanceTemporary.getStudent().addNewFeedingFeeCredit( (attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()) * -1);
            attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
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
        return em.createQuery(" from AttendanceTemporary A order by A.student.firstname asc ").getResultList();
    }

    public Boolean checkIfExisForToday(){
        try{
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            String hql = "from AttendanceTemporary at ";
            Query query= em.createQuery(hql);
            query.setFirstResult(1);
            query.setMaxResults(1);
            AttendanceTemporary at =(AttendanceTemporary) query.getSingleResult();

            if(at.getDate().equals(LocalDate.now()))
                return true;
            return false;
        }catch (Exception e){
            return false;
        }
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
            List<AttendanceTemporary> list =getTempAttendance();
            for(AttendanceTemporary a:list){
                em.remove(a);
            }
            HibernateUtil.commit();
            return  true;
        }catch (PersistenceException p){
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

    public  List<AttendanceTemporary> getStudentAttendance(String name){
        try{
            em=HibernateUtil.getEntityManager();
            return  em.createQuery("from AttendanceTemporary  at where LOWER(at.student.firstname) like '%"+name.toLowerCase()+"' or lower( at.student.firstname) like '+"+name.toLowerCase()+"%' " +
                    "or lower(at.student.lastname) like '%"+name.toLowerCase()+"' or lower(at.student.othername) like '%"+name+"' " +
                    "or lower(student.othername) like  '"+name+"' or lower(student.stage.name)  like  '"+name.toLowerCase()+"%'  " +
                    "or lower(student.stage.name)  like '%"+name.toLowerCase()+"' ").getResultList();
        }catch (Exception e){
            return null;
        }finally {
           if(em == null){
               em.close();
               HibernateUtil.close();
           }
        }
    }
    public void removeStudent(Student student){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.createQuery("delete from AttendanceTemporary  where '+student_id+' = '+student.id+' ").executeUpdate();
    }
}
