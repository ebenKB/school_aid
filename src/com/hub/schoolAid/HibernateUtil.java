package com.hub.schoolAid;

import javax.persistence.*;


/**
 * Created by HUBKB.S on 11/20/2017.
 */
public class HibernateUtil {
    private static final String PERSISTENCE_UNIT_NAME ="MY_PERSISTENCE";
    private static EntityManagerFactory factory;
    private static EntityManager em;

    public static Boolean initDB() {
        try
        {
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            new UserDao().createDefaultAdmin();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return  false;
        }
    }

    public static EntityManager getEntityManager(){
//        if(em != null){
//            em.close();
//            em = null;
//        }
        em = factory.createEntityManager();
        return em;
    }

    public static void begin(){
        em.getTransaction().begin();
    }

    public static void commit(){
        em.getTransaction().commit();
    }

    public static void rollBack (){em.getTransaction().rollback();}

    public static <T> T save(Class<T> type, Object o){
        getEntityManager();
        begin();
        em.persist(o);
        commit();
        return type.cast(o);
    }

    public static void delete(Object o){
        getEntityManager();
        begin();
        em.remove(o);
        commit();
        em.close();
    }

    public static long update(String query, Object... params){
        begin();
        Query q = em.createQuery(query);
        for (int i = 0; i < params.length; i++) q.setParameter(i + 1, params[i]);
        long i = q.executeUpdate();
        commit();

        return i;
    }
    public static void close(){
        em.close();
    }

    public static void closeDB(){
        factory.close();
    }

    public static <T> T find(Class<T> type, Object obj){
        return em.find(type, obj);
    }
}
