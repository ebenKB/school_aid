package com.hub.schoolAid;


import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by HUBKB.S on 1/6/2018.
 */
public class SalesDao {
//    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//private Session session;
private EntityManager em;
    /**
     *
     * @param sales the sales to make
     * @param student the student to sell the item to
     * @return
     */
    public Boolean createSale( Sales sales,Item item,Student student){
//        List<Student> data=new ArrayList<>();
//        data.add(student);
        try{
            sales.setStudent(student);
            sales.setItem(item);

            em =HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            em.persist(sales);
            Student std =  em.find(Student.class,student.getId());
//            std.getSales().add(sales);
            HibernateUtil.commit();
            return true;
        }catch (HibernateException e){
//            throw e;
            return false;
        }
    }

    //sell an item to all students
    public Boolean sellToAllStudents(Sales sales,Item item)throws  HibernateException{
        try {
            StudentDao studentDao = new StudentDao();
            List<Student> students =studentDao.getAllStudents();
            sellToMany(students,sales,item);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This method sells an item to a particular class
     * @param sales the sales to add
     * @param stage  the class you are selling the item to
     * @return true if the sale was successful
     * @throws HibernateException
     */
    public Boolean sellToClass(Sales  sales,Item item, Stage stage) throws  HibernateException{
        try{
            List<Student> students = new StudentDao().getStudentFromClass(stage);

            //sell the item to the students
            sellToMany(students,sales,item);
            return true;
        }catch (HibernateException e){
            e.printStackTrace();
           return false;
        }
    }

    private void sellToMany(List<Student>students,Sales sales,Item item){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        for(Student s:students){
            Sales newSale= new Sales();
            newSale.setItem(item);
            newSale.setStudent(s);
            newSale.setTotalcost(sales.getTotalcost());
            newSale.setAmountPaid(sales.getAmountPaid());
//            s.getSales().add(newSale);
            em.persist(newSale);
        }
        HibernateUtil.commit();

////        sales.setItem(item);
//        em=HibernateUtil.getEntityManager();
//        HibernateUtil.begin();
////        sales.setStudent(students);
//        em.persist(sales);
//        for(Student s:students){
//            Student st= em.find(Student.class,s.getId());
//            st.getSales().add(sales);
//            sales.setStudent(s);
//        }
//        HibernateUtil.commit();
    }

    /**
     *
     * @param sales the sales to add
     * @param students
     * @return
     */

    /**
     * this method retrieves all the sales of a particular student
     * @param std the student whose sales we want to get
     * @return returns all the sales of that student if any
     */
    public List<Sales> getAllSalesOfStudent(Student std) throws  HibernateException{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
//        String hql = "select s from Sales s JOIN s.student st where st.id =?";
        String hql = "from Sales S where  S.student.id = ?";

        List<Sales> list = em.createQuery(hql).setParameter(0,std.getId()).getResultList();
        return list;
    }

    public List<Sales> getAllSalesOfStudent() throws HibernateException{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
//        String hql = "FROM Sales";
        String hql = "select s from Sales s JOIN s.student st";
        List<Sales> sales= em.createQuery(hql).getResultList();
        return sales;
    }

    public Boolean payForSales(Sales sales, Double amnt){
       if(sales.getAmountPaid()==sales.getTotalcost())
           return false;

        try{
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            Sales s =em.find(Sales.class,sales.getId());
            s.setAmountPaid(s.getAmountPaid() + amnt);
            HibernateUtil.commit();
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
