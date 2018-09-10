package com.hub.schoolAid;

/**
 * Created by HUBKB.S on 1/6/2018.
 */

import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class StudentDao {
    //private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//    private Session session;
    private static EntityManager em;

    //methods to interface with the database
    public Boolean addNewStudent(Student student) {
        try{
            student.getStage().setNum_on_roll(student.getStage().getNum_on_roll()+1);
            student.setAge(student.calcAge(student.getDob()));
            student.getAccount().setFeeToPay(((student.getStage().getFeesToPay())*-1));
            student.getAccount().setFeedingFeeToPay(student.getStage().getFeeding_fee());
            HibernateUtil.commit();
            student.setPayFeeding(true);
            student.setReg_date(LocalDate.now());
            HibernateUtil.save(Student.class,student);
            HibernateUtil.close();
        }catch (Exception e){
//            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     *
     * @param new_student the student that we want to update
     * @return true if the update was successful
     */
    public Boolean updateStudentRecord(Student new_student) throws HibernateException{
        setStdRecsToUpdate(new_student);
        HibernateUtil.commit();
        return true;
    }
    public Boolean updateStudentRecord(Student new_student,Parent parent){
        setStdRecsToUpdate(new_student);
        setParentRecsToUpdate(new_student,parent);
        HibernateUtil.commit();
        return true;
    }
    private void setStdRecsToUpdate(Student new_student){
        em=HibernateUtil.getEntityManager();
        Student newStd = em.find(Student.class,new_student.getId());
        HibernateUtil.begin();
        newStd.setFirstname(new_student.getFirstname());
        newStd.setLastname(new_student.getLastname());
        newStd.setOthername(new_student.getOthername());
        newStd.setPayFeeding(new_student.getPayFeeding());
        newStd.setFeedingStatus(new_student.getFeedingStatus());
        newStd.getAccount().setFeedingFeeToPay(new_student.getAccount().getFeedingFeeToPay());
        newStd.setImage(new_student.getImage());
        newStd.setDob(new_student.getDob());
        newStd.setGender(new_student.getGender());
        newStd.getParent().setname(new_student.getParent().getname());
    }

    private void setParentRecsToUpdate(Student newStd, Parent parent){
        newStd.getParent().setname(parent.getname());
        newStd.getParent().setTelephone(parent.getTelephone());
    }

    public List <Student> getStudent (Student student){
        System.out.println("this is the student"+student.toString());
        String hql = "FROM students S WHERE S.firstname like ? and S.lastname like ? and  S.othername like ? and S.stage.name like ?";
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();

        List <Student> data = em.createQuery(hql).setParameter(0,student.getFirstname())
                .setParameter(1,student.getLastname())
                .setParameter(2,student.getOthername())
                .setParameter(3,student.getStage().getName())
                .getResultList();
      for(Student s : data){
          System.out.println("this is a student found"+s.toString());
      }
        return  data;
    }
    /**
     *
     * @param id the id of the student that we want to get from the database
     * @return a list of the student
     */
    public Student getStudent(String id) throws HibernateException{
          em=HibernateUtil.getEntityManager();
          HibernateUtil.begin();
          return  (Student)em.createQuery("from  students where +id+ like '"+id+"' ").getSingleResult();
    }

    public List <Student> getStudentByName(String name) throws HibernateException{
          String hql = "FROM students S WHERE S.firstname like '"+name+"%' OR S.lastname like '"+name+"%' OR S.othername like '"+name+"%' ";
          em=HibernateUtil.getEntityManager();
          HibernateUtil.begin();

          List<Student> list =em.createQuery(hql).getResultList();
          return list;
    }

    public List<Student> getStudentFromClass(Stage newStage) throws HibernateException{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
//        List<Student> results = em.createQuery("FROM students WHERE +class_id+ like '"+ stage.getId() +"'").getResultList();
        List<Student> results = em.createQuery("FROM students S WHERE S.stage.id =? ").setParameter(0,newStage.getId()).getResultList();
        return results;
    }
    /**
     *
     * @param stage the name of the class to get students from
     * @return the list of students obtained,
     *          null if no student is found
     */
    public List<Student> getStudentFromClass(String stage)throws HibernateException{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        String hql = "FROM students S WHERE S.class = '"+ stage +"' ";
        List results = em.createQuery(hql).getResultList();
        return results;
    }

    public List<Student> getAllStudents()throws HibernateException{
        List<Student> studentList;
        try{
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            studentList = em.createQuery("from students order by firstname asc ").getResultList();
            return studentList;
        }catch (HibernateException e){
            e.printStackTrace();
            return null;
        }
        finally {
           em.close();
        }
    }
    /**
     *
     * @param student the student to remove from the database
     * @return true if the delete operation was successful
     */
    public Boolean deleteStudent(Student student)throws HibernateException
    {
        em=HibernateUtil.getEntityManager();
        Student s = em.find(Student.class,student.getId());
        s.getStage().setNum_on_roll(s.getStage().getNum_on_roll()-1);

        HibernateUtil.begin();
        em.remove(s);
        HibernateUtil.commit();
        HibernateUtil.close();
       return true;
    }
    /**
     * delete and then their parent from the db
     * promote a particular student to the next class
     * @param student
     */
    public void promoteStudent(Student student) throws HibernateException{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        Student s = em.find(Student.class,student.getId());
        Stage newStage = (Stage) em.createQuery("from Class S where  S.classValue =?").setParameter(0,student.getStage().getClassValue()+1).getSingleResult();
        s.setStage(newStage);
        HibernateUtil.commit();
        HibernateUtil.close();
    }
    /**
     * promote all students in a particular class to the next class
     * @param stage
     */
    public void promoteStudent(Stage stage)throws HibernateException{
        em = HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        List<Student> students= em.createQuery("from students S where S.stage.classValue=?").setParameter(0,stage.getClassValue()).getResultList();
        for (Student s:students)
            promoteStudent(s);
        HibernateUtil.close();
    }

    /**
     *  promotes every student in the school to the next class
     */
    public void promoteStudent()throws HibernateException{
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        List<Student> students = em.createQuery("from students ").getResultList();
        for(Student s:students)
            promoteStudent(s);
        HibernateUtil.close();
    }

    public Boolean updateAccount(StudentAccount account){
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            StudentAccount studentAccount=em.find(StudentAccount.class,account.getId());
            studentAccount.setFeedingFeeCredit(account.getFeedingFeeCredit());
            studentAccount.setFeeToPay(account.getFeeToPay());
            HibernateUtil.commit();
            HibernateUtil.close();
        }catch (Exception e){
            return  false;
        }
        return true;
    }
}
