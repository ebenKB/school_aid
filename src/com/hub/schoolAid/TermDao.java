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
    private static LocalDate currentDate;

    private Session session;
    static EntityManager em;

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
       try{
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           return em.createQuery("from Term ").getResultList();
       } catch (Exception e) {
            return  null;
       }finally {
           em.close();
       }
    }

    public Term getTerm(String name){
//        session = HibernateUtil.getSession();
        session.beginTransaction();
        String hql ="FROM Term WHERE name = '"+name+"' ";
        Query query = session.createQuery(hql);
        List <Term> terms = query.list();
        Term t = null;

        for(Term term:terms){
            t=term;
        }
        return t;
    }

    public Term getCurrentTerm() throws NoResultException {
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        try{
            return (Term) em.createQuery("from Term where status =  1 ").getSingleResult();
        }catch (Exception e){
            HibernateUtil.close();
            return  null;
        }finally {
//            em.close();
        }
    }

    public static LocalDate getCurrentDate(Boolean shouldRefresh){
        if (currentDate == null || shouldRefresh) {
            try {
                em=HibernateUtil.getEntityManager();
                HibernateUtil.begin();
                currentDate = ((LocalDate) em.createQuery("SELECT today FROM Term where status=1").getSingleResult());
            }catch (Exception e){
                em.close();
                return null;
            }finally {
                if(em == null) {
                    em.close();
                    HibernateUtil.close();
                }
            }
        }
        return currentDate;
    }

    public Boolean updateCurrentDate(LocalDate date){
       try{
           Term t= getCurrentTerm();
           t.setToday(date);
           HibernateUtil.commit();
           return true;
       }catch (Exception e){
           return false;
       }
    }

    public static Boolean hasTermEnded(){
        if (getCurrentDate(true).compareTo(LocalDate.now()) >= 0){
            return false;
        } else return true;
    }

    public void createNextTerm() {
        Term term = this.getCurrentTerm();

        // find the next term from the system
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        javax.persistence.Query query = em.createQuery("from Term T where T.value = ?1");
        query.setParameter(1, (term.getValue() + 1));

        Term newTerm = (Term) query.getSingleResult();
        newTerm.setValue(getNextTermvalue(term.getValue()));

        // we do not know the start and end dates
        newTerm.setStart_date(null);
        newTerm.setEnd_date(null);
    }

    public void createNextTerm(LocalDate start, LocalDate end) {
        Term term = this.getCurrentTerm();

        // find the next term from the system
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        javax.persistence.Query query = em.createQuery("from Term T where T.value = ?1");
        query.setParameter(1, (term.getValue() + 1));

        Term newTerm = (Term) query.getSingleResult();
        newTerm.setValue(getNextTermvalue(term.getValue()));

        // we do not know the start and end dates
        newTerm.setStart_date(start);
        newTerm.setEnd_date(end);
    }

    /**
     *
     * @param val The value for that very house
     * @return
     */
    private int getNextTermvalue(int val) {
        if(val == 1 || val == 2)
            return (val+1);

        else return 1;
    }

    public String getNextAcademicYear() {
        String year;
        Term term = this.getCurrentTerm();
        LocalDate start = term.getEnd_date();
        LocalDate end = term.getEnd_date();
        if (term.getValue() == 1 ||  term.getValue() == 2) {
            year = start.getYear() + "/"+ end.getYear();
            return  year;
        } else if(term.getValue() == 3) {
            year = (start.getYear()+1) + "/"+ (end.getYear()+1);
            return  year;
        } return null;
    }

    public String getCurrentAcademicYear() {
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Term term = this.getCurrentTerm();
        LocalDate start = term.getStart();
        LocalDate end = term.getEnd_date();

        return start+"/"+end;
    }
}
