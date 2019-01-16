package com.hub.schoolAid;


import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import java.util.List;

public class StageDao {
//    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//private Session session = HibernateUtil.getSession();
private EntityManager em;

    public List<Stage> getGetAllStage() throws HibernateException{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        String hql = "FROM  Class C order by C.classValue ASC";
        return em.createQuery(hql).getResultList();
    }

//    public double getFeesToPay(String name){
//        em=HibernateUtil.getEntityManager();
//        HibernateUtil.begin();
//
//    }

    public void addNewStage(Stage stage) {
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.persist(stage);
        HibernateUtil.commit();
        em.close();
    }

    public Boolean isExistingStage(Stage stage){

          String hql = "FROM Class C WHERE C.name like '"+stage.getName() +"' OR C.classValue ='"+ stage.getClassValue() +"'";
//
//        List<Stage> list =  session.createQuery(hql).getResultList();
//
//        if (! list.isEmpty()){
//            for(Stage s:list){
//                System.out.print("we got this class(es) existing..."+s.getName());
//            }
//            return  true;
//        }
//        return false;
//        HibernateUtil.begin();
        em=HibernateUtil.getEntityManager();
        List<Stage> list = em.createQuery(hql).getResultList();
        return !list.isEmpty();

    }

    public Boolean addStudent(Stage stage){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Stage newStage = em.find(Stage.class,stage.getId());
        newStage.setNum_on_roll((newStage.getNum_on_roll()+1));
        HibernateUtil.commit();
        return true;
    }

//    public int getNumberOnRol(Stage stage){
//        StudentDao studentDao = new StudentDao();
//        int counter =0;
//        List list = studentDao.getStudentFromClass(stage);
//        for(Object s:list){
//            counter++;
//        }
//        return counter;
//    }
}
