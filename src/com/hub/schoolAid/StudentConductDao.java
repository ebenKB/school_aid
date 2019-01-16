package com.hub.schoolAid;

import javax.persistence.EntityManager;
import java.util.List;

public class StudentConductDao {
    private EntityManager em;

    public List<StudentConduct> getConduct (){
       try {
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           return em.createQuery("from StudentConduct S order by S.conduct asc ").getResultList();
       }catch (Exception e){
           return null;
       }
    }

    public StudentConduct addConduct(String conduct){
      try{
          if(!isExisting(conduct)){
              StudentConduct newConduct = new StudentConduct();
              newConduct.setConduct(conduct);
              return HibernateUtil.save(StudentConduct.class,newConduct);
          }else {
              return null;
          }
      }catch (Exception e){
          return null;
      }
    }

    public Boolean isExisting (String remark) {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            StudentConduct c = (StudentConduct) em.createQuery("from StudentConduct  C where lower(C.conduct)  like '%"+remark.toLowerCase()+"%' ").getSingleResult();
            if(c != null)
                return  true;
            return false;
        }catch (Exception e) {
            return false;
        }finally {
            if(em==null){
                em.close();
            }
        }
    }
}
