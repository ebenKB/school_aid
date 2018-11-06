package com.hub.schoolAid;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class AssessmentDao {
    private EntityManager em;

    private Boolean saveAssessment(Assessment assessment){
       try {
//           GradeDao gradeDao =new GradeDao();
//           assessment.setGrade(gradeDao.getGrade((assessment.getClassScore()+assessment.getExamScore())));
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           em.persist(assessment);
           HibernateUtil.commit();
           return  true;
       }catch (Exception e){
           return false;
       }finally {
           em.close();
       }
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
        }finally {
            em.close();
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
       try {
           em = HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           Query q=em.createQuery("from Assessment A where  A.student.id=?");
           q.setParameter(0,student.getId());
           return q.getResultList();
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }finally {
           em.close();
       }
    }

    public List<Assessment> getAssessment(){
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query q=em.createQuery("from Assessment A order by A.id asc ");
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            em.close();
        }
    }

    public Boolean updateAssessment(Assessment assessment){
          GradeDao gradeDao =new GradeDao();
          Grade grade = gradeDao.getGrade((assessment.getClassScore()+assessment.getExamScore()));
            if(grade!=null){
                assessment.setGrade(grade);
            }

        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Assessment newAssessment = em.find(Assessment.class,assessment.getId());
            newAssessment.setClassScore(assessment.getClassScore());
            newAssessment.setExamScore(assessment.getExamScore());
            newAssessment.setGrade(assessment.getGrade());
            HibernateUtil.commit();
            return  true;
        } catch (Exception e){
            e.printStackTrace();
            return  false;
        }finally {
            em.close();
        }
    }

    public List<Assessment> updateAssessment(List<Assessment> assessments)  {
        GradeDao gradeDao = new GradeDao();
        Grade grade=null;
        Assessment newAssessment =null;
        List <Assessment> updated = new ArrayList<>();

        int totalSize = assessments.size();
        int batchSize = 5 ;

        System.out.println("this is the total: "+totalSize);
        for (Assessment assessment : assessments) {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            System.out.println("looping over: "+assessment.getStudent().toString());

//            if(assessments.indexOf(assessment) > 0 && assessments.indexOf(assessment) % batchSize == 0){
//               em.flush();
//               em.clear();
//            }
            try {
                    //set the grade
                    grade = gradeDao.getGrade((assessment.getClassScore()+assessment.getExamScore()));
                    if(grade!=null){
                    assessment.setGrade(grade);

                    //set the assessment
//                    if (!em.getTransaction().isActive())
//                        HibernateUtil.begin();

                    newAssessment =  em.find(Assessment.class,assessment.getId());
                    newAssessment.setClassScore(assessment.getClassScore());
                    newAssessment.setExamScore(assessment.getExamScore());
                    newAssessment.setGrade(assessment.getGrade());
                    HibernateUtil.commit();
                    updated.add(newAssessment);
                }

            }catch (RuntimeException e) {
                if(em.getTransaction().isActive()) {
                    HibernateUtil.rollBack();
                }

                e.printStackTrace();
                return  updated;

            } finally {
                em.close();
            }
        }
    return updated;
    }
}
