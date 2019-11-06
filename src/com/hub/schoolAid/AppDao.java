package com.hub.schoolAid;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class AppDao {

    private static Session session;
    private  static App app =null;
    private  static EntityManager em;

    public static App getAppSetting() {
        if(app ==  null) {
            try {
                System.out.println("From the database");
                em = HibernateUtil.getEntityManager();
                HibernateUtil.begin();
                Query query = em.createQuery("from app ");
                app =(App) query.getSingleResult();
            }catch (Exception e) {
                return null;
            } finally {
                em.close();
            }
        } else System.out.println("Not from the database");
        return app;
    }
//    public static Boolean canShowPopUp(){
//        session = HibernateUtil.getSession();
//        session.beginTransaction();
//        String hql= "FROM APP";
//        Query query = session.createQuery(hql);
//        List <App> list = query.list();
//        if(list !=null){
//            app =list.get(0);
//        }
//        if(app.getCanShowPopUp() ==1)
//            return true;
//
//        return false;
//    }
    public void changeContact() {    // update the contact details for the school

    }


    public Boolean setDefault() {
        try {
            App app = new App();
            app.setCanShowIntroHelp(true);
            app.setCanShowIntroHelp(true);
            app.setFeedingType(FeedingType.COUPON);
            app.setCanShowPopUp(true);
//            app.setFeedingFee(5.0);
            app.setHasInit(true);

            // save the records to the database
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            em.persist(app);
            HibernateUtil.commit();
            return true;
        } catch (Exception e) {
            HibernateUtil.rollBack();
            return false;
        } finally {
            em.close();
        }
    }

    public Boolean createAppSettings(App app) {
        if (app ==null)
            return false;
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            HibernateUtil.save(App.class, app);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean increaseAppCounter(App app){
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            if((app.getCurrentCount() == app.getMaxCount())  || (app.getCurrentCount() > app.getMaxCount())) {
                return false;
            } else {
                app.setCurrentCount((app.getCurrentCount() + 1));
                em.merge(app);
            }
            return true;
        }catch (Exception e){
            HibernateUtil.rollBack();
            return false;
        }
    }

    public Boolean appCanBoot(App app) {
        if(app.getMaxCount() > app.getCurrentCount()) {
            return true;
        }  else return false;
    }
//    public App getAppSettings() {
//        try {
//            em = HibernateUtil.getEntityManager();
//            HibernateUtil.begin();
//            Query query = em.createQuery("from app ");
//            return  (App) query.getSingleResult();
//        }catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            em.close();
//        }
//    }
}
