package com.hub.schoolAid;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class GuardianDao {
    private EntityManager em;
    public Guardian isExistingGuardian(Guardian guardian) throws Exception {
        try {
            String hql ;
            Query query;
            em=HibernateUtil.getEntityManager();
            hql="from Guardian G where G.fullname like ?1 and  G.contact like ?2";
            query = em.createQuery(hql);
            query.setParameter(1, guardian.getFullname());
            query.setParameter(2, guardian.getContact());

            Guardian g =(Guardian)query.getSingleResult();
            return g;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (em == null) {
                em.close();
            }
        }
    }
}
