package com.hub.schoolAid;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class BillDao {
    private EntityManager em;

    /**
     * creates a bill for all students
     * @return
     */
    public Boolean createBill(Bill bill) throws Exception {
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();

        // create the bill for all
        try {
            // fetch all students from the database
            StudentDao studentDao = new StudentDao();
            List<Student>students = studentDao.getAllStudents(true);
            createBill(students, bill);
            return true;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    /**
     * Before creating a bill, set the date for next term for which you are creating the bill
     * @param students
     * @param bill
     * @return
     * @throws Exception
     */
    public Boolean createBill(List<Student>students, Bill bill) throws Exception {
        try {
            int entityCount = students.size();
            int batchSize = 25;
            em= HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            List<Student>selectedStudents = new ArrayList<>();

            // update the school fees amount to pay for all students for which the bill is being created for
            for(int i =0; i< entityCount; i++) {
                Student st = students.get(i);

                // check if the student pays school fees
                if(st.getPaySchoolFees()) {
                    // update the student's account with the school fees to pay
                    st.getAccount().setFeeToPay(bill.getTotalBill());
                    selectedStudents.add(st);
                }

                // check if the batch size has been reached
                if(i > 0 &&  i %batchSize == 0) {
                    // save the records and clear the entity manager
                    em.flush();
                    em.clear();
                }
                HibernateUtil.save(Bill.class, bill);
                em.merge(st);
            }
            bill.setStudents(selectedStudents);
            HibernateUtil.commit();
            return false;
        } catch (Exception e) {
            HibernateUtil.rollBack();
            throw new Exception(e);
        } finally {
            if(em == null) {
                em.close();
            }
        }
    }

    public Boolean hasBillForNextTerm(Student student) {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();

            TermDao termDao = new TermDao();
            Term term = termDao.getCurrentTerm();
            Query query= em.createQuery("from Bill B where B.createdBy = ?1 AND B.academicYear like ?2");
            query.setParameter(1, term.getValue());
            query.setParameter(2, termDao.getNextAcademicYear());
            Bill bill = (Bill) query.getSingleResult();

            // check if a student is found for the bill
            if(bill.getStudents().contains(student)) {
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        } finally {
            if(em == null) {
                em.close();
            }
        }
    }

    public List<Bill> getCurrentBill() {
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();

        TermDao termDao = new TermDao();
        Term term = termDao.getCurrentTerm();

        Query query = em.createQuery("from Bill B where B.academicYear like?1 AND B.createdFor =?2");
        query.setParameter(1, termDao.getCurrentAcademicYear());
        query.setParameter(2, term.getValue());

        return query.getResultList();
    }

    public Bill getCurrentBill(Student student) {
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        TermDao termDao = new TermDao();
        Term term = termDao.getCurrentTerm();

        Query query = em.createQuery("from Bill B where B.academicYear like ?1 AND B.createdFor =?2");
        List<Bill>bills = query.getResultList();

        // check  if a student will be found for the bill
        for (Bill b: bills) {
            if(b.getStudents().contains(student))
                return b;
        }

        // if no bill was found for the student return null
        return null;
    }
}
