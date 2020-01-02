package com.hub.schoolAid;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BillDao {
    private EntityManager em;
    /**
     * creates a bill for all students
     * @return
     */
    public Boolean createBill(Bill bill) throws Exception {
        // create the bill for all
        try {
            // fetch all students from the database
            StudentDao studentDao = new StudentDao();
            List<Student>students = studentDao.getAllStudents(true);
            if(createBill(students, bill)) {
                // update all classes with the new bill

                List<Stage>stages = new ArrayList<>();
                StageDao stageDao = new StageDao();
                stages.addAll(stageDao.getGetAllStage());

                em = HibernateUtil.getEntityManager();
                HibernateUtil.begin();

                // update the stages
//                for(Stage s: stages) {
//                    s.setBill(bill);
//                    s.setFeesToPay((s.getFeesToPay() + bill.getTotalBill()));
//                    em.merge(s);
//                }
                HibernateUtil.commit();
            }
            return true;
        } catch (Exception e) {
            HibernateUtil.rollBack();
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
    private Boolean createBill(List<Student>students, Bill bill) throws Exception {
        try {
            Set<Stage> stageSet = new HashSet<>();
            int entityCount = students.size();
            int batchSize = 25;
            em= HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            List<Student>selectedStudents = new ArrayList<>();

            // update the school fees amount to pay for all students for which the bill is being created for
            for(int i =0; i< entityCount; i++) {
                Student st = students.get(i);
                stageSet.add(st.getStage());

                // check if the student pays school fees
                if(st.getPaySchoolFees()) {
                    // update the student's account with the school fees to pay
                    Double bal = st.getAccount().getSchoolFeesBalance();
                    st.getAccount().setPreviousSchoolFeesBalance(bal);
                    st.getAccount().setSchoolFeesBalance((bal + bill.getTotalBill()));
                    selectedStudents.add(st);
                }
                // check if the batch size has been reached
                if(i > 0 &&  i%batchSize == 0) {
                    // save the records and clear the entity manager
                    em.flush();
                    em.clear();
                }
                em.merge(st);
            }

            bill.setStudents(selectedStudents);
            bill.setCreatedAt(LocalDate.now());
            em.persist(bill);

            HibernateUtil.commit();
            return true;
        } catch (Exception e) {
            HibernateUtil.rollBack();
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if(em == null) {
                em.close();
            }
        }
    }

    /**
     * Create a bill for selected classes
     * @param bill The bill that we want to create
     * @param stages The classes or stages that we want to crete the bill for
     * @return return true if the transaction was successful else return false
     */
    public Boolean createBill (Bill bill, List<Stage>stages) {
        System.out.println("Creating a bill for "+ stages.size());
        try {
            StudentDao studentDao = new StudentDao();
            List<Student>students = new ArrayList<>();

            // find all classes involved in the stage
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            for(Stage stage : stages) {
                students.addAll(studentDao.getStudentFromClass(stage));
                stage.setBill(bill);

//                stage.setFeesToPay((stage.getFeesToPay() + bill.getTotalBill()));
                em.merge(stage);
            }

            // assign the bill to the students
            createBill(students, bill);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }

    public Boolean createBill(Student student) {
        return false;
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

    /**
     * Get all the current bills for a student
     * @param student
     * @return
     */
    public Bill getCurrentBill(Student student) {
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        TermDao termDao = new TermDao();
        Term term = termDao.getCurrentTerm();

        Query query = em.createQuery("from Bill B join B.students S where  S.id = ?1 AND B.academicYear like ?2 AND B.createdFor = ?3");
        query.setParameter(1, student.getId());
        query.setParameter(2, termDao.getCurrentAcademicYear());
        query.setParameter(3, term.getValue());
        List<Bill>bills = query.getResultList();

        // check  if a student will be found for the bill
        for (Bill b: bills) {
            if(b.getStudents().contains(student))
                return b;
        }
        // if no bill was found for the student return null
        return null;
    }

    public List<Bill> getBill(Student student) {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query query = em.createQuery("from Bill B join students S where S.id = ?1");
            query.setParameter(1, student.getId());
            return query.getResultList();
        } catch (Exception e) {
            HibernateUtil.rollBack();
            return null;
        } finally {
            em.close();
        }
    }

    public List<Bill> getBill() {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.getEntityManager();
            Query query = em.createQuery("from Bill B order by B.createdAt desc");
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public void addStudentToBill(Bill bill, Student student) {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query query = em.createQuery("from Bill B join B.students S where B.id =?1 and S.id = ?2");
            query.setParameter(1, bill.getId());
            query.setParameter(2, student.getId());
            Object existing = query.getSingleResult();
            if(existing == null) {
                List<Student>list = bill.getStudents();
                list.add(student);
                bill.setStudents(list);
                em.merge(bill);
            }
            HibernateUtil.commit();
        } catch (Exception e) {
            HibernateUtil.rollBack();
        } finally {
            HibernateUtil.close();
            em.close();
        }
    }

    public void removeStudentFromBill(Bill bill, Student student) {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();

            // get all the students related to the bill
            List<Student>students = bill.getStudents();

            // remove the student from the bill
            students.remove(student);

            // add the update set of student to the bill
            bill.setStudents(students);

            // save the changes to the database
            em.merge(bill);

        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            HibernateUtil.close();
            em.close();
        }
    }

    private Bill takeBillFromStudent(Bill bill) {
        try {
            List<Student>students = bill.getStudents();

            // removes the bill from every student
            bill.setStudents(null);
        }catch (Exception e) {

        }
        return null;
    }

    public void deleteBill(Boolean isSoft, Bill bill) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            List<Student>students = bill.getStudents();
            Query query = em.createQuery("from Bill B where B.id = ?1");
            query.setParameter(1, bill.getId());
            Bill newBill = (Bill) query.getSingleResult();
            newBill.setDeleted(true); // turn the bill off
            em.merge(newBill);

            StudentDao studentDao = new StudentDao();
            // reverse the bill amount from the student's amount to pay
            // PERFORM A BATCH UPDATE
            int batchSize = students.size();
            for (int i=0; i <= students.size(); i++) {
                Double newAmount = (students.get(i).getAccount().getSchoolFeesBalance() - bill.getTotalBill());
                System.out.println("Deducting from student account" + newAmount);
                studentDao.updateSchoolFee(students.get(i), newAmount);
                em.merge(students.get(i));
                if(i > 0 && i%batchSize == 0) {
                    // persist the records and clear the entity manager
                    em.flush();
                    em.clear();
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("an error occurred while deleting the item...");
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param newBill the bill that contains the new changes
     * @return return true if the update was successful else return false
     */
    public Boolean updateBill(Bill newBill,Double oldTotal) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            Bill bill = em.find(Bill.class, newBill.getId());
            bill = newBill;
            em.merge(bill);
            // update the account for the students
            for(Student s: newBill.getStudents()) {
                // subtract the old balance from the student account
                double feeToPay = s.getAccount().getSchoolFeesBalance();

                // reverse the previous balance from the total amount for the student to pay
                double newFee = feeToPay + oldTotal;

                // add the new fee to the total amount for the student to pay
                newFee+= newBill.getTotalBill();

                // add the new account to the student account
                Query query = em.createQuery("update StudentAccount  A set schoolFeesBalance=?1 where A.id =?2");
                query.setParameter(1, newFee);
                query.setParameter(2, s.getAccount().getId());
                query.executeUpdate();

                // check whether there is any class associated with the bill and update the amount
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        }finally {
            em.close();
        }
    }
}
