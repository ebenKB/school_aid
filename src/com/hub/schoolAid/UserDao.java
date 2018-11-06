package com.hub.schoolAid;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class UserDao {
//    private UserAccount userAccount = new UserAccount();
    private Session session;
    EntityManager em;

    public User getUser(User user) throws HibernateException {
        try {
            em=HibernateUtil.getEntityManager();

            Query q = em.createQuery("from User  where username like ?1 and password like  ?2");
            q.setParameter(1,user.getUsername());
            q.setParameter(2,user.getPassword());
            return (User) q.getSingleResult();

        }catch (Exception e) {
            return null;
        }
//        finally {
//           if(em != null)
//               em.close();
//        }
    }

    /**
     * @param user The user to create
     * @return returns true if creating the user was successful
     * @throws HibernateException
     */
    public Boolean createUser(User user) throws  HibernateException{
        HibernateUtil.save(User.class,user);
        HibernateUtil.close();
        return true;
    }

    public void createDefaultAdmin(){
      try{
          em=HibernateUtil.getEntityManager();
          HibernateUtil.begin();
          try{
              User user = (User)em.createQuery("from User").getSingleResult();
          }catch (NoResultException e){
              User user= new User();
              user.setUsername("admin");
              user.setPassword("1234");
              user.setIsactive(true);
              em.persist(user);
              HibernateUtil.commit();
              HibernateUtil.close();
          }
      }catch (Exception e){

      }finally {
         if(em != null)
             em.close();
      }
    }
    public Boolean isExisting(String username) throws HibernateException{
        try {
            User user = (User) em.createQuery("From User where username like  '"+username+"' ").getSingleResult();
            return true;
        }catch (NullPointerException n){
            return false;
        }finally {
            em.close();
        }
    }

//    public Boolean updateUser(User olduser,User newUser){
//        session = HibernateUtil.getSession();
//        session.beginTransaction();
//        olduser=newUser;
//        session.update(olduser);
//        session.getTransaction().commit();
//        return true;
//    }
}
