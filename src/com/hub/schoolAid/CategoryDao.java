package com.hub.schoolAid;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class CategoryDao {
    private EntityManager em;


    public Category addCategory(Category category) {
        em = HibernateUtil.getEntityManager();
        try {
            return HibernateUtil.save(Category.class, category);
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Category> getCategory() {
        em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        try {
            return em.createQuery("from Category ").getResultList();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Category getCategory(String name) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            Query query = em.createQuery("from Category C where lower( C.name) like ?1");
            query.setParameter(1, name.toLowerCase().trim());
            Category c = (Category)query.getSingleResult();
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean attachCategory(Stage stage, Category category) {
        try {
            em =HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            category.getStages().add(stage);
            stage.getCategories().add(category);
            em.merge(category);
            em.merge(stage);
            return true;
        }catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * add a list of groups or categories for a student
     * @param stage
     * @param categories
     * @return
     */
    public Boolean attachCategory(Stage stage, List<Category>categories) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            stage.getCategories().addAll(categories);
            em.merge(stage);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    public Boolean attachCategory(Student student, Category category) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            student.setCategory(category);
            em.merge(student);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    public Boolean attachCategory(List<Student>students, Category category) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            int batchSize = 25;
            int entityCount = students.size();

            for(int i = 0; i<entityCount; i++) {
                students.get(i).setCategory(category);
                em.merge(students.get(i));
                // check if we have reached the batch size
                if(i%batchSize ==0) {
                    em.flush();
                    em.clear();
                }
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
