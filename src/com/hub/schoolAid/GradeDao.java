package com.hub.schoolAid;

import javax.persistence.EntityManager;
import java.util.List;

public class GradeDao {
    private EntityManager em;

    private void saveGrade(Grade grade){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.persist(grade);
        HibernateUtil.close();
    }

    private void saveGrade(List <Grade> grade){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.persist(grade);
        HibernateUtil.close();
    }

    public Boolean createGrade(char name){
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
        return null;
    }
}
