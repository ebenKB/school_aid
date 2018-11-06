package com.hub.schoolAid;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class CourseDao {
    private EntityManager em;
    public void  createNewCourse(Stage stage,List<Course> courses){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Stage newStage =em.find(Stage.class,stage.getId());
        stage.setCourse(courses);
        for(Course course:courses)
            em.persist(course);

        HibernateUtil.commit();
        HibernateUtil.close();
    }

    public void  createNewCourse(List<Stage> stages,List<Course> courses){
        em =HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        for(Course course:courses)
            em.persist(course);

        for(Stage s:stages){
            Stage newStage=em.find(Stage.class,s.getId());
//          newStage.setCourse(courses);
            newStage.getCourse().addAll(courses);
        }

        HibernateUtil.commit();
        HibernateUtil.close();
    }

    public  List<Course> getAllCourses(){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        return  em.createQuery("from Course C order by C.name asc ").getResultList();
    }
}
