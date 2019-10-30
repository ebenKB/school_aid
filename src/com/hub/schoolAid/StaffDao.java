package com.hub.schoolAid;

import javax.persistence.EntityManager;

public class StaffDao {
    private EntityManager em;

    public Boolean createStaff(Staff staff) {
        try {
            em= HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            em.persist(staff);
            HibernateUtil.commit();
            return true;
        } catch (Exception e) {
            return  false;
        }
    }
}

