package com.hub.schoolAid;


import javafx.fxml.LoadException;
import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Iterator;
import java.util.List;

public class StageDao {
//    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//private Session session = HibernateUtil.getSession();
private EntityManager em;

    public List<Stage> getGetAllStage() throws HibernateException {
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            String hql = "FROM  Class C order by C.classValue ASC";
            return em.createQuery(hql).getResultList();
        } catch (Exception e) {
            System.out.println("This is em:"+em);
            HibernateUtil.close();
            System.out.println("Deleted em:" + em);
            return null;
        }finally {
            if(em == null) {
                em.close();
            }
        }
    }

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

    public Boolean addStudent(Stage stage) {
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Stage newStage = em.find(Stage.class,stage.getId());
            newStage.setNum_on_roll((newStage.getNum_on_roll()+1));
            HibernateUtil.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }

    public Boolean updateStage (Stage stage) {
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Stage newStage = em.find(Stage.class, stage.getId());

//            newStage.setFeesToPay(stage.getFeesToPay());

            newStage.setName(stage.getName());
            newStage.setFeeding_fee(stage.getFeeding_fee());
            HibernateUtil.commit();
            return  true;
        } catch (Exception e) {
            return  false;
        } finally {
            HibernateUtil.close();
        }
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

    public Boolean existStage(List<Stage> stages, int value) {
        int left = 0;
        int right = (stages.size() - 1);

        return findClass(stages, left, right, value);
    }

    /***
     *
     * @param stages the stages to search from
     * @param l the left position of the pointer
     * @param r the right position of the pointer
     * @param val the value we are searching for
     * @return
     */
    public Boolean findClass(List<Stage> stages, int l, int r, int val) {
        if (r >= l) {
            int mid = l + (r - l) / 2;

            // check if the element is found at the middle
            if (stages.get(mid).getClassValue() == val) {
                return true;
            }

            if (stages.get(mid).getClassValue() > val) {
                // move to the left
                r = mid - 1;
                return findClass(stages, l, r, val);
            }

            if (stages.get(mid).getClassValue() < val) {
                // move to the right
                l = mid + 1;
                return findClass(stages, l, r, val);
            }
        }
        return false;
    }

    public void syncNumberOnRow() {
        int class_value = 1;
        Boolean hasNext = true;
        while (hasNext) {
            try {
                em=HibernateUtil.getEntityManager();
                HibernateUtil.begin();
                String hql = null;
                Query query;

                // find all students from the current class
                hql = "from students S where S.stage.classValue=?1";
                query=em.createQuery(hql);
                query.setParameter(1, class_value);
                List<Student> studentList = query.getResultList();

                // get the right stage
                query =  em.createQuery("from Class C where C.classValue =?1");
                query.setParameter(1, class_value);
                Stage stage = null;
                try {
                    stage = (Stage) query.getSingleResult();
                } catch (NoResultException e) {
                    break;
                }

                // check if the stage is not empty
                if (stage != null) {
                    // check if the student list empty
                    if(studentList.size() > 0) {
                        // update the number on row
                        stage.setNum_on_roll(studentList.size());
                        class_value++;
                    } else {
                        // no students were found so set the class value to zero
                        stage.setNum_on_roll(0);
                        // there were no records found -- CONSIDER TERMINATING THE LOOP
                        if (class_value > 15 ) {
                            hasNext = false;
                        } else {
                            class_value++;
                        }
                    }
                }
                // commit the transaction to the database
                if(stage != null) {
                    em.merge(stage);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                hasNext = false;
            }
        }
    }

//    private int maxClassValue() {
//        String hql;
//        Query query;
//        em=HibernateUtil.getEntityManager();
//        try {
//            query = em.createQuery("from  students  S order by S.stage.classValue desc ");
//            query.setMaxResults(100);
//            Student s  = (Student) query.getSingleResult();
//            return s.getStage().getClassValue();
//        } catch (Exception e) {
//            e.printStackTrace();
//            HibernateUtil.close();
//            return 0;
//        }
//    }
}
