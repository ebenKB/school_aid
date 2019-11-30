package com.hub.schoolAid;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;

public class TransactionLoggerDao {
    private static TransactionLoggerDao transactionLoggerDaoInstance;
    private EntityManager em;
    public static  TransactionLoggerDao getTransactionLoggerDaoInstance(){
        if(transactionLoggerDaoInstance== null) {
            transactionLoggerDaoInstance = new TransactionLoggerDao();
        }
        return transactionLoggerDaoInstance;
    }

    public Boolean logTransaction(TransactionLogger transaction){
        try {
            HibernateUtil.save(TransactionLogger.class,transaction);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean LogTransaction(Student student, String transactionBy, String description, Double transactionAmount, TransactionType type, Long transId) {
       TransactionLogger transaction = new TransactionLogger();
       transaction.setDate(LocalDate.now());
       transaction.setAmount(transactionAmount);
       transaction.setTransactionId(transId);
       transaction.setDescription(description);
       transaction.setPaidBy(transactionBy);
       transaction.setTransactionType(type);
       transaction.setStudent(student);

       System.out.println("This is the student who is doing the transaction"+ student.getId());

       // add a receipt number to to the transaction
        Receipt receipt = new Receipt();
        transaction.setReceipt(receipt);

        try {
            HibernateUtil.save(TransactionLogger.class,transaction);
            return true;
        } catch (Exception e){
            return false;
        } finally {
            HibernateUtil.close();
        }
    }

    /**
     *
     * @param type The type of the transaction
     * @param student_id the transaction id
     * @return
     */
    public List<TransactionLogger> getLog(TransactionType type, Long student_id) {
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
           Query query = em.createQuery("from TransactionLogger T where T.transactionType = ?1 and T.student.id =?2 and T.status !=?3 order by T.date desc ");
           query.setParameter(1, type);
           query.setParameter(2, student_id);
           query.setParameter(3, 0);
           List<TransactionLogger>transactions = query.getResultList();
           System.out.println("We got some transactions" + transactions.size());
           return transactions;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
//            em.close();
//            HibernateUtil.close();
        }
    }

    /**
     *
     * @param type The type of the transaction
     * @return
     */
    public List<TransactionLogger> getLog(TransactionType type) {
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query query = em.createQuery("from TransactionLogger T where T.transactionType = ?1 and T.status !=?2 order by T.date desc ");
            query.setParameter(1, type);
            query.setParameter(2, 0);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
        finally {
            HibernateUtil.close();
        }
    }

    public List<TransactionLogger>getLog(Student student) {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query query = em.createQuery("from TransactionLogger  T where T.student.id =?1 and T.status !=?2 order by T.transactionType asc");
            query.setParameter(1, student.getId());
            query.setParameter(2, 0);
            return query.getResultList();
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
            em.clear();
        }
    }

    public List<TransactionLogger> getLog(LocalDate date, TransactionType type) {
        try {
            em =HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query query = em.createQuery("from TransactionLogger T where T.date = ?1 and T.transactionType =?2 and T.status !=?3 ");
            query.setParameter(1, date);
            query.setParameter(2, type);
            query.setParameter(3, 0);
            List<TransactionLogger> transactions =  query.getResultList();
            System.out.print("we are returning to the calling function");
            return transactions;

        }catch (Exception e) {
            System.out.print("in the error block");
        } finally {
            em.close();
            System.out.print("In the finally block -- CLOSING THE TRANSACTION");
        }
        return null;
    }

    public List<TransactionLogger> getLog(LocalDate from, LocalDate to, TransactionType type) {
        System.out.println("This is the date: From: "+ from+ "To: " + to);
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Query query = em.createQuery("from TransactionLogger T where T.date >= ?1 and T.date <= ?2 and T.transactionType =?3 and T.status !=?4 order by T.date desc ");
            query.setParameter(1, from);
            query.setParameter(2, to);
            query.setParameter(3, type);
            query.setParameter(4, 0);
            List<TransactionLogger>transactions = query.getResultList();
            return transactions;
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
            System.out.print("We are in the FINALLY BLOCK");
        }
    }
}
