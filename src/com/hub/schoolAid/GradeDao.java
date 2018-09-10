package com.hub.schoolAid;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class GradeDao {
    private EntityManager em;

    private void saveGrade(Grade grade){
//        em=HibernateUtil.getEntityManager();
//        HibernateUtil.begin();
//        em.persist(grade);
//        HibernateUtil.close();
          HibernateUtil.save(Grade.class,grade);
          System.out.println("we saved a grade..");
    }

    private void saveGrade(List <Grade> grade){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.persist(grade);
        HibernateUtil.close();
    }

    public Boolean createGrade(String name){
        try {
            Grade grade = new Grade(name);
            saveGrade(grade);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public Boolean createGrade(Grade grade){
        try {
             saveGrade(grade);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean createGrade(List<Grade> grade){
        try {
            saveGrade(grade);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Grade getGrade(double mark){
        System.out.println("we are searching for a grade....");
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Query q = em.createQuery("from Grade  G where  ?1 >=G.minMark  and ?2 <= G.maxMark ");
//      70    74   80  >=     <=
        q.setParameter(1,mark);
        q.setParameter(2,mark);
        Grade grade =(Grade) q.getSingleResult();
        System.out.println("we got a grade" + grade.getName()+" "+grade.getRemark());
        return  grade;
    }
}
