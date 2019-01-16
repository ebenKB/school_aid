package com.hub.schoolAid;

import javax.persistence.EntityManager;
import java.util.List;

public class RemarkDao {
    private EntityManager em;

    public List<Remark> getRemark (){
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            return em.createQuery("from Remark  R order by  R.remark asc").getResultList();
        }catch (Exception e){
            return null;
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
       }
    }

    public Boolean isExisting (String remark){
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Remark r = (Remark) em.createQuery("from Remark R where lower(R.remark) like '%"+remark.toLowerCase()+"%' ").getSingleResult();
            if(r != null)
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
