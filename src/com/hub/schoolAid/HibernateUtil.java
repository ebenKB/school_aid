package com.hub.schoolAid;

import com.github.cliftonlabs.json_simple.JsonObject;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import jdk.nashorn.internal.parser.JSONParser;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


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
//            readFile();
            // read database properties from the file
            Map map = new HashMap();
//            map.put("javax.persistence.jdbc.user", "");
//            map.put("javax.persistence.jdbc.password", "thishubkbs*");
            factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
            new UserDao().createDefaultAdmin();
            return true;
        }catch (Exception e){
            Alert alert =new Alert(Alert.AlertType.ERROR,"", ButtonType.OK);
            alert.setTitle("Error");
            alert.setHeaderText("Error Connecting to Database");
            alert.setContentText(e.toString());

            return  false;
        }
    }

    private static void readFile(){
       try {
           FileReader fileReader = new FileReader("src/database.properties");
           String line = null;

           // wrap the file reader in a buffered reader
           BufferedReader bufferedReader = new BufferedReader(fileReader);
           while((line = bufferedReader.readLine()) != null) {
               System.out.println("Reading file" + line);
           }

           bufferedReader.close();
       } catch (IOException e) {
            Notification.getNotificationInstance().notifyError("error while reading file", "file error");
       }
    }

    public static  void readFileAsJson() {
       try {
           FileReader fileReader = new FileReader("");

       } catch (IOException e) {

       }
    }

    public static EntityManager getEntityManager(){
//        if(em != null){
//            em.close();
//        }
        em = factory.createEntityManager();
        return em;
    }

    public static void begin() {
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
