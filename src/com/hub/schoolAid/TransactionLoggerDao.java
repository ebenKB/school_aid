package com.hub.schoolAid;

import java.time.LocalDate;

public class TransactionLoggerDao {
    private static TransactionLoggerDao transactionLoggerDaoInstance;

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

    public Boolean LogTransaction(Long student_id, String transactionBy, String description, Double transactionAmount, TransactionType type) {
       TransactionLogger transaction = new TransactionLogger();
       transaction.setDate(LocalDate.now());
       transaction.setAmount(transactionAmount);
       transaction.setStudent_id(student_id);
       transaction.setDescription(description);
       transaction.setPaidBy(transactionBy);
       transaction.setTransactionType(type);

        try {
            HibernateUtil.save(TransactionLogger.class,transaction);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
