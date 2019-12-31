package com.hub.schoolAid;

import javax.persistence.EntityManager;

public class PictureDao {
    private EntityManager em;

    /**
     *
     * @param picture the picture that we want to save
     * @param student_id the id of the student that we are adding the picture
     * @return the new picture that created
     */
    public Picture addNewPicture(Picture picture, Long student_id) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            Student st = em.find(Student.class, student_id);
            picture.setStudent(st);
            return HibernateUtil.save(Picture.class, picture);
        } catch (Exception e) {
            return null;
        }
    }
}
