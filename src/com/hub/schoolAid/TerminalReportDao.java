package com.hub.schoolAid;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.EntityManager;
import java.util.List;

public class TerminalReportDao {
    private EntityManager em;

    public Boolean createTerminalReport(Student student){
       try{
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           TerminalReport terminalReport = new TerminalReport();
           terminalReport.setStudent(student);
           em.persist(terminalReport);
           HibernateUtil.commit();
           return true;
       }catch (Exception e){
           HibernateUtil.rollBack();
           return false;
       }finally {
           em.close();
       }
    }

    public Boolean createTerminalReport(TerminalReport  report) {
       try{
           if(report.getStudent() == null)
               return false;

           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           em.persist(report);
           return  true;
       }catch (Exception e) {
           HibernateUtil.rollBack();
           return  false;
       }finally {
           em.close();
       }
    }

    public Boolean createTerminalReport() {
        int batchSize =25;
        int entityCount=0;
        em = HibernateUtil.getEntityManager();
        StudentDao studentDao = new StudentDao();
        ObservableList<Student> list = FXCollections.observableArrayList();
        list.addAll(studentDao.getAllStudents());
        entityCount=list.size();
        em =HibernateUtil.getEntityManager();
        try {
            HibernateUtil.begin();
            //begin a batch process
            for(int i=0; i < entityCount; i++) {
                //do this in batches of 25
                if(i > 0 && i % batchSize == 0) {
//                    HibernateUtil.commit();
//                    HibernateUtil.begin();
                    em.flush();
                    em.clear();
                }
                TerminalReport terminalReport = new TerminalReport();
                terminalReport.setStudent(list.get(i));
                em.persist(terminalReport);
//                HibernateUtil.commit();  // commnented this out
            }
            HibernateUtil.commit();
            return  true;
        }catch (RuntimeException e) {
            if(HibernateUtil.getEntityManager().getTransaction().isActive()){
                HibernateUtil.getEntityManager().getTransaction().rollback();
            }
            e.printStackTrace();
            return  false;
        }finally {
            em.close();
        }
    }

    public List getReport(){
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            List<TerminalReport> reports  = em.createQuery("from TerminalReport ").getResultList();
            return reports;
        } catch (Exception e){
            e.printStackTrace();
            return  null;
        } finally {
//             em.close();
        }
    }

    public List getReport (Stage stage){
        return null;
    }

    public List getReport (Student student){
        return null;
    }

    public List getReport (List<Student> students){
        return null;
    }


    public Boolean updateTerminalReport(TerminalReport report)  {
        try{
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            TerminalReport newReport = em.find(TerminalReport.class,report.getId());
            if(newReport != null){
                newReport.setConduct(report.getConduct());
                newReport.setHeadTracherRemark(report.getHeadTracherRemark());
                newReport.setPromotedTo(report.getPromotedTo());

                HibernateUtil.commit();
            }
            new StudentConductDao().addConduct(report.getConduct());
            new RemarkDao().addRemark(report.getHeadTracherRemark());
            return true;
        }catch (Exception e) {
            return  false;
        }finally {
            em.close();
        }
    }

    public Boolean updateTerminalReport(List<TerminalReport> reports) {
        int batchSize = 25;
        int entityCount = reports.size();
        TerminalReport newReport = null;
        em=HibernateUtil.getEntityManager();
        try {
            HibernateUtil.begin();
            for (int i=0;i<entityCount;i++){
                if(i>0 && i%batchSize==0){
//                    HibernateUtil.commit();
//                    HibernateUtil.begin();
                    em.flush();
                    em.clear();
                }
                //find report and update
                newReport=em.find(TerminalReport.class,reports.get(i).getId());
                newReport.setHeadTracherRemark(reports.get(i).getHeadTracherRemark());
                newReport.setConduct(reports.get(i).getConduct());

                new StudentConductDao().addConduct(newReport.getConduct());
                new RemarkDao().addRemark(newReport.getHeadTracherRemark());
            }
            HibernateUtil.commit();
            return true;
        }catch (RuntimeException e){
            if(HibernateUtil.getEntityManager().getTransaction().isActive()){
                HibernateUtil.rollBack();
            }
        }finally {
            em.close();
            return false;
        }
    }

    public Boolean deleteTerminalReport (Long id){
        try{
            em= HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            TerminalReport report = em.find(TerminalReport.class,id);
            em.remove(report);
            HibernateUtil.commit();
            return   true;
        }catch (Exception e){
            HibernateUtil.rollBack();
            return false;
        }finally {
            em.close();
        }
    }

    public Boolean deleteTerminalReport (Student student){
        try{
            em= HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            TerminalReport report = em.find(TerminalReport.class,student.getId());
            em.remove(report);
            HibernateUtil.commit();
            return   true;
        }catch (Exception e){
            HibernateUtil.rollBack();
            return false;
        }finally {
            em.close();
        }
    }

    public TerminalReport isExiting (Long studentId) {
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        try {
           return (TerminalReport) em.createQuery("from TerminalReport T where  T.student.id = ?").setParameter(0,studentId).getSingleResult();
        }catch (Exception e){
            return null;
        }finally {
            if(em ==null ){
                em.close();
            }
        }
    }

    public Boolean isUpToDate() {
        //if the total number of report is equal to the total number of students return true else return false;
        return true;
    }

    public void syncReport  () {
        //select report by class
        //select report by class
        //compare the number of student to the number of reports for each class
        //if the class size is greater than the report size, create report for remaining students.
        //or return the students without report
    }
}
