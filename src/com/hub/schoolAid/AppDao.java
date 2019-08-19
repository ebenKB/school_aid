package com.hub.schoolAid;

import org.hibernate.Session;

public class AppDao {

    private static Session session;
    private  static App app;

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
}
