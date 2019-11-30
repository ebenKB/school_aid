package com.hub.schoolAid;

import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class ItemDao {
    EntityManager em;
//    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//private Session session = HibernateUtil.getSession();

//    public Boolean addNewItem(Item item) throws  HibernateException{
//        //session =sessionFactory.getCurrentSession();
//        session.beginTransaction();
//        try {
//            session.save(item);
//            return true;
//        }catch (HibernateException e){
//            return false;
//        }
//    }

//    public List<Item> getItem(String name){
//        String hql  = "FROM Item as E WHERE E.name = '"+name+"' ";
//        Query query = session.createQuery(hql);
//        List <Item> item = query.list();
//        return item;
//    }

    public Boolean updateItem(Item item){
        //session = sessionFactory.getCurrentSession();
//        session.beginTransaction();
        try{
//            session.update(item);
//            session.getTransaction().commit();
            return true;
        }catch (HibernateException h){
            return false;
        }
    }

    public Boolean deleteItem(Item item){
        //session =sessionFactory.getCurrentSession();
//        session.beginTransaction();
        try {

            return true;
        }catch (HibernateException e){
            return false;
        }
    }

    public Item createItem(Item item) {
        try {
            // check if the item already exists
            Item existing = this.getItem(item.getName());
            System.out.print("this is the item are about to create : "+ item.toString());
            if(existing != null) {
                System.out.print("The bill item already exists");
                return existing;
            }
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            return HibernateUtil.save(Item.class, item);
        } catch (Exception e) {
            return null;
        }
    }

    public Item getItem(String name) {
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query query = em.createQuery("from Item I where I.name like ?1");
            query.setParameter(1, name);
            return (Item) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }   finally {
            em.close();
        }
    }

    public List<Item> getItem() {
        try {
            em= HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query query  = em.createQuery("from Item ");
            return query.getResultList();
        }catch (Exception e) {
            return null;
        }
    }
}
