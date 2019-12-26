package com.hub.schoolAid;

import javax.persistence.EntityManager;
import java.util.List;

public class RemarkDao {
    private EntityManager em;

    public List<Remark> getRemark (){
        try {
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            List<Remark> remarks = em.createQuery("from Remark  R order by  R.remark asc").getResultList();
            return  remarks;
        }catch (Exception e){
            return null;
        } finally {
            em.close();
        }
    }

    public Remark addRemark(String remark){
       try {
           if(!isExisting(remark)){
               Remark newRemark = new Remark();
               newRemark.setRemark(remark);
               return HibernateUtil.save(Remark.class,newRemark);
           }else {
               return null;
           }
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           em.close();
       }
    }

    public Boolean isExisting (String remark){
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            Remark r = (Remark) em.createQuery("from Remark R where lower(R.remark) like '%"+remark.toLowerCase()+"%' ").getSingleResult();
            if(r != null)
                return  true;
            return false;
        }catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }
}
