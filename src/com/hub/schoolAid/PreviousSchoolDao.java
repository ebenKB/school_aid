package com.hub.schoolAid;

import org.hibernate.type.LongType;

import javax.persistence.EntityManager;

public class PreviousSchoolDao {
    private EntityManager em;

    /**
     *
     * @param previousSchool the previous school that we want to add
     * @param student_id the id of the student that we are adding the previous school for
     * @return return the new previousSchool that was created
     */
    public PreviousSchool addPreviousSchool(PreviousSchool previousSchool, Long student_id) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();

            Student st = em.find(Student.class, student_id);
            previousSchool.setStudent(st);
            return HibernateUtil.save(PreviousSchool.class, previousSchool);
        }catch (Exception e) {
            return null;
        }
    }
}
