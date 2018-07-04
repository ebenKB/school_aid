package com.hub.schoolAid;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.List;

public class AssessmentDao {
    private EntityManager em;

    private void saveAssessment(Assessment assessment){
        System.out.print("we are in the save...."+assessment.getStudent().toString());
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.persist(assessment);
        System.out.print("we have persisted the assessment"+assessment.getStudent().toString());
        HibernateUtil.commit();
//        HibernateUtil.close();
    }

    private void saveAssessment(List<Assessment> assessments){
        for (Assessment  assessment:assessments)
            saveAssessment(assessment);
    }

    public Boolean createAssessment(Assessment assessment){
        try {
            saveAssessment(assessment);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean createAssessment(List <Assessment> assessment){
        System.out.print("we have called the Assessment Dao to do Assessment...");
        try {
            saveAssessment(assessment);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean existAssessment(Stage stage, Course course){
       try{
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           Query q=em.createQuery("from Assessment A where A.course.id = ? and A.student.stage.id=? order by A.student.firstname asc");
           q.setParameter(0,course.getId());
           q.setParameter(1,stage.getId());
           q.getSingleResult();
           return true;
       }catch (NoResultException n){
           return false;
       }
    }

    public Assessment existAssessment(Student student, Course course){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        try{
            Query q  = em.createQuery("from Assessment  A where  A.student.id =? and A.course.id=? ");
            q.setParameter(0,student.getId());
            q.setParameter(1,course.getId());
            return (Assessment) q.getSingleResult();

        }catch (NoResultException e){
            return null;
        }
    }

    public List<Assessment> getAssessment(Stage stage){
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Query q=em.createQuery("from Assessment  A where A.student.stage.id =? order by A.student.stage.classValue asc ");
        q.setParameter(0,stage.getId());
        return q.getResultList();
    }

    public List<Assessment> getAssessment(Stage stage,Course course){
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Query q=em.createQuery("from Assessment  A where A.student.stage.id =? and A.course.id=? order by A.student.stage.classValue asc ");
        q.setParameter(0,stage.getId());
        q.setParameter(1,course.getId());
        return q.getResultList();
    }

    public List<Assessment> getAssessment(Course course){
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Query q=em.createQuery("from Assessment  A where A.course.id=?");
        q.setParameter(0,course.getId());
        return q.getResultList();
    }

    public List<Assessment> getAssessment(Student student){
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Query q=em.createQuery("from Assessment A where  A.student.id=?");
        q.setParameter(0,student.getId());
        return q.getResultList();
    }

    public Boolean updateAssessment(Assessment assessment){
        try{
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Assessment newAssessment = em.find(Assessment.class,assessment.getId());
            newAssessment.setClassScore(assessment.getClassScore());
            newAssessment.setExamScore(assessment.getExamScore());
            newAssessment.setGrade(assessment.getGrade());
            HibernateUtil.commit();
            HibernateUtil.close();
            return  true;
        }catch (Exception e){
            return  false;
        }
    }
}
