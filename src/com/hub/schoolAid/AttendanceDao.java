package com.hub.schoolAid;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.List;

public class AttendanceDao  {
//    private Session session;
    private EntityManager em;

    public Boolean moveAttendanceToMasterTable(){
        System.out.print("moving records to master table");
       try{
           AttendanceTemporaryDao attendanceTemporaryDao = new AttendanceTemporaryDao();
           List <AttendanceTemporary> attendanceList = attendanceTemporaryDao.getTempAttendance();
           if(attendanceList !=null){
               for (AttendanceTemporary attendanceTemporary:attendanceList){
                   if(attendanceTemporary.isPresent()){
                       Attendance attendance  =new Attendance();
                       attendance.setStudent(attendanceTemporary.getStudent());
                       attendance.setFeedingFee(attendanceTemporary.getFeedingFee());
                       attendance.setDate(attendanceTemporary.getDate());
                       HibernateUtil.save(Attendance.class,attendance);
                   }
               }
           }
           //now clear the old attendance data
           attendanceTemporaryDao.deleteAllTempAttendance();
           return true;
       }catch (PersistenceException p){
           p.printStackTrace();
           return false;
       }
    }

    public List<Attendance> getAllAttendance(){
     try{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        return em.createQuery("from Attendance order by date DESC ").getResultList();
    }catch (PersistenceException p){
        p.printStackTrace();
        return null;
      }
    }

    public List<Attendance> getStudentAttendance(int s){
        try{
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            return em.createQuery("from Attendance A where A.student.id =? order by date desc ").setParameter(0,s).getResultList();
        }catch (Exception e){
            return null;
        }
    }

    public List <Attendance> getAttendanceByDate(LocalDate date){
        try{
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            return em.createQuery("from Attendance where '"+date+"' like '"+date+"' order by feedingFee desc ").getResultList();
        }catch (Exception e){
            return null;
        }
    }

 //    public List<Attendance> getAttendanceByMonth(LocalDate date){
//        em=HibernateUtil.getEntityManager();
//        HibernateUtil.begin();
//        return (em.createQuery("from Attendance where "))
//    }
}
