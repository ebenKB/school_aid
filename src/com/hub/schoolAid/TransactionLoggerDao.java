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

    public Boolean LogTransaction(Long trans_id, String transactionBy, String description, Double transactionAmount, TransactionType type) {
       TransactionLogger transaction = new TransactionLogger();
       transaction.setDate(LocalDate.now());
       transaction.setAmount(transactionAmount);
       transaction.setTransactionId(trans_id);
       transaction.setDescription(description);
       transaction.setPaidBy(transactionBy);
       transaction.setTransactionType(type);
       transaction.setStudent_id(trans_id);

       System.out.println("This is the student who is doing the transaction"+ trans_id);

       // add a reciept number to to the transaction
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
           Query query = em.createQuery("from TransactionLogger T where T.transactionType = ?1 and T.Student_id =?2 order by T.date desc ");
           query.setParameter(1, type);
           query.setParameter(2, student_id);
           List<TransactionLogger>transactions = query.getResultList();
           System.out.println("We got some transactions" + transactions.size());
           return transactions;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            em.close();
            HibernateUtil.close();
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
            Query query = em.createQuery("from TransactionLogger T where T.transactionType = ?1 order by T.date desc ");
            query.setParameter(1, type);
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
            Query query = em.createQuery("from TransactionLogger  T where T.Student_id =?1 order by T.transactionType asc");
            query.setParameter(1, student.getId());
            return query.getResultList();
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
            em.clear();
        }
    }
}
