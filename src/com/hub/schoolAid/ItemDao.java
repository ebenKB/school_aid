package com.hub.schoolAid;

import org.hibernate.HibernateException;

public class ItemDao {
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
}
