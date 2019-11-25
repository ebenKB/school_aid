package com.hub.schoolAid;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class AppDao {

    private static Session session;
    private  static App app =null;
    private  static EntityManager em;

    /**
     *
     * @return returns the current settings for the application
     */
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

    public static Boolean updateApp(App app) {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            em.merge(app);
            HibernateUtil.commit();
            return true;
        } catch (Exception e) {;
            return  false;
        }

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


    /**
     * creates default settins for the application
     * @return
     */
    public Boolean setDefault() {
        try {
            App app = new App();
            app.setCanShowIntroHelp(true);
            app.setCanShowIntroHelp(true);
            app.setFeedingType(FeedingType.COUPON);
            app.setCanShowPopUp(true);
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

    /**
     * creates settings for the application
     * The application uses the settings to determine its state at any point in time
     * if settings are not specifies for the application, it creates it's own default settings
     * @param app the app settings to create
     * @return returns true if the settings was created successfully
     */
    public Boolean createAppSettings(App app) {
        if (app == null)
            return false;
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            HibernateUtil.save(App.class, app);
            return true;
        }catch (Exception e) {
            return false;
        } finally {
            if(em == null){
                em.close();
            }
        }
    }

    /**
     * increases the app counter by 1
     * By default this method expects the instance of the currently running application
     * @param app the app to update.
     * @return returns true if the update was success otherwise false
     */
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
            HibernateUtil.commit();
            return true;
        }catch (Exception e){
            HibernateUtil.rollBack();
            return false;
        }
    }

    /**
     * checks if the application can startup
     * @param app the app that is currently ruunning
     * @return returns true if the application can boot otherwise returns false
     */
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
