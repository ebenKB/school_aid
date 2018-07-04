package com.hub.schoolAid;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.List;

public class TermDao {
    Term term =new Term();

    private Session session;
    EntityManager em;

    public Boolean createTerm(Term term) throws HibernateException {
       try {
          HibernateUtil.save(Term.class,term);
          return true;

       }catch (PersistenceException e){
           return false;
       }
    }

    public void updateTerm(Term oldTerm, Term newTerm){
//        session = HibernateUtil.getSession();
        session.beginTransaction();
        oldTerm =newTerm;
        session.update(oldTerm);
        session.getTransaction().commit();
    }

    public List<Term> getTerm() throws NoResultException{
       em=HibernateUtil.getEntityManager();
       HibernateUtil.begin();
       return em.createQuery("from Term ").getResultList();
    }

    public Term getTerm(String name){
//        session = HibernateUtil.getSession();
        session.beginTransaction();
        String hql ="FROM Term WHERE name = '"+name+"' ";
        Query query = session.createQuery(hql);
        List<Term> terms = query.list();
        Term t = null;
        for(Term term:terms){
            t=term;
        }
        return t;
    }
    public Term getCurrentTerm() throws NoResultException{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
//        try{
            return (Term) em.createQuery("from Term where status =  1 ").getSingleResult();
//        }catch ()
    }

    public LocalDate getCurrentDate(){
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            return  ((LocalDate) em.createQuery("SELECT today FROM Term where status=1").getSingleResult());
        }catch (NoResultException e){
            System.out.print("an error occurred while fetching the current date");
            return null;
        }
    }

    public Boolean updateCurrentDate(LocalDate date){
       try{
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           Term t= getCurrentTerm();
           t.setToday(date);
           em.persist(t);
//           System.out.print("this is the term that we just found:id= "+ t.getId());
//           em.createQuery("update Term  set today ='"+date+"' where id = 2 ");
           em.getTransaction().commit();
//           HibernateUtil.commit();
           return true;
       }catch (Exception e){
           return false;
       }
    }
}
