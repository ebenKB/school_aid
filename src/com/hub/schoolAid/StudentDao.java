package com.hub.schoolAid;

/**
 * Created by HUBKB.S on 1/6/2018.
 */

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

public class StudentDao {
    //private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
//    private Session session;
    private static EntityManager em;
    private StageDao stageDao;

    //methods to interface with the database
    public Boolean addNewStudent(Student student) {
        try{
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();

            student.setAge(student.calcAge(student.getDob()));

            if(student.getPaySchoolFees()) {
                if(student.getStage().getBill() != null) {
                    student.getAccount().setFeeToPay(((student.getStage().getBill().getTotalBill())));
                }
            } else {
                student.getAccount().setFeeToPay(0.00);
            }
            student.getAccount().setFeedingFeeToPay(student.getStage().getFeeding_fee());
            student.setPayFeeding(true);
            student.setReg_date(LocalDate.now());
            student.setDeleted(false);
            em.persist(student);
//            HibernateUtil.save(Student.class, student);

            // get the bill of the class that the student belongs to and add the student to the bill
            Bill bill = student.getStage().getBill();
            if(bill != null) {
                // ADD THE STUDENT TO THE BILL
//              Query query = em.createQuery("insert into bill_students (bills_id, students_student_id) values()");
                bill.getStudents().add(student);
                em.merge(bill);
            } else {
                System.out.println("Sorry the bill is null");
            }
            em.getTransaction().commit();

            stageDao = new StageDao();
            stageDao.addStudent(student.getStage());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            HibernateUtil.close();
            em.close();
        }
    }

    /**
     *
     * @param new_student the student that we want to update
     * @return true if the update was successful
     */
    public Boolean updateStudentRecord(Student new_student) throws HibernateException {
        try {
            setStdRecsToUpdate(new_student);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean updateStudentRecord(Student new_student,Parent parent){
        setStdRecsToUpdate(new_student);
        setParentRecsToUpdate(new_student,parent);
        HibernateUtil.commit();
        HibernateUtil.close();
        return true;
    }

    private void setStdRecsToUpdate(Student new_student){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.merge(new_student);
        HibernateUtil.commit();

//        Student newStd = em.find(Student.class,new_student.getId());
//        newStd.setFirstname(new_student.getFirstname());
//        newStd.setLastname(new_student.getLastname());
//        newStd.setOthername(new_student.getOthername());
//        newStd.setPayFeeding(new_student.getPayFeeding());
//        newStd.setFeedingStatus(new_student.getFeedingStatus());
//        newStd.getAccount().setFeedingFeeToPay(new_student.getAccount().getFeedingFeeToPay());
//        newStd.setDob(new_student.getDob());
//        newStd.setGender(new_student.getGender());
//        new_student.setReg_date(LocalDate.now());
//        newStd.getParent().setname(new_student.getParent().getname());
    }

    private void setParentRecsToUpdate(Student newStd, Parent parent){
        newStd.getParent().setname(parent.getname());
        newStd.getParent().setTelephone(parent.getTelephone());
    }

    public List <Student> getStudent (Student student) {
        try{
            String hql = "FROM students S WHERE S.firstname like ? and S.lastname like ? and  S.othername like ? and S.stage.name like ?";
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();

            List <Student> data = em.createQuery(hql).setParameter(0,student.getFirstname())
                    .setParameter(1,student.getLastname())
                    .setParameter(2,student.getOthername())
                    .setParameter(3,student.getStage().getName())
                    .getResultList();
            return  data;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        } finally {
            em.close();
        }
    }
    /**
     * @param id the id of the student that we want to get from the database
     * @return a list of the student
     */
    public Student getStudent(String id) throws HibernateException{
        try {
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            return  (Student)em.createQuery("from  students where +id+ like '"+id+"' ").getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List <Student> getStudentByName(String name) {
         try {
             String hql = "FROM students S WHERE S.firstname like '"+name+"%' OR S.lastname like '"+name+"%' OR S.othername like '"+name+"%' ";
             em=HibernateUtil.getEntityManager();
             em.getTransaction().begin();

             List<Student> list =em.createQuery(hql).getResultList();
             return list;
         }catch (HibernateException e){
             e.printStackTrace();
             return null;
         }finally {
             em.close();
         }
    }

    @Transactional
    public List <Student> getStudentByCategory(String cat){
       try{
           String hql = "FROM students S WHERE LOWER(S.firstname) like '"+cat.toLowerCase()+"%' OR LOWER(S.lastname) like '"+cat.toLowerCase()+"%' OR LOWER(S.othername) like '"+cat.toLowerCase()+"%' " +
                   "OR lower(S.stage.name) like '"+cat.toLowerCase()+"%' OR lower(S.stage.name) like '%"+cat.toLowerCase()+"' order by  S.firstname ASC ";
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();

           List<Student> list =em.createQuery(hql).getResultList();
           return list;
       }catch (HibernateException e){
           e.printStackTrace();
           return null;
       }
       finally {
           if(em == null)
               em.close();
       }
    }

    public List<Student> getStudentFromClass(Stage newStage) throws Exception {
        try{
            if(em==null || !em.isOpen()) {
                em=HibernateUtil.getEntityManager();
                em.getTransaction().begin();
            }
            List<Student> results = em.createQuery("FROM students S WHERE S.stage.id =? order by S.firstname asc ").setParameter(0,newStage.getId()).getResultList();
            return results;
        }catch (HibernateException e){
            throw new HibernateException(e);
        } finally {
           if(em == null) {
               em.close();
           }
        }
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
        String hql = "FROM students S WHERE S.class = '"+ stage +"' order S.firstname asc";
        List results = em.createQuery(hql).getResultList();
        return results;
    }

    public List<Student> getAllStudents()throws HibernateException{
        List<Student> studentList;
        try{
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            studentList = em.createQuery("from students order by firstname asc ").getResultList();
            return studentList;
        }catch (HibernateException e){
            e.printStackTrace();
            return null;
        } finally {
           em.close();
        }
    }

    public List<Student> getAllStudents(Boolean payFees)throws HibernateException {
        List<Student> studentList;
        try {
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            studentList = em.createQuery("from students S where S.paySchoolFees = ? order by firstname asc ")
                    .setParameter(0,payFees).getResultList();
            return studentList;
        } catch (HibernateException e) {
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
    public Boolean deleteStudent(Student student)throws HibernateException {
        try {
            em=HibernateUtil.getEntityManager();
            Student s = em.find(Student.class,student.getId());
            s.getStage().setNum_on_roll(s.getStage().getNum_on_roll()-1);

            em.getTransaction().begin();

            // check if the student will be removed permanently
            em.remove(s);

            HibernateUtil.commit();
            HibernateUtil.close();

            //remove the image from file
            StudentDetailsDao detailsDao =new StudentDetailsDao();
            ImageHandler.deleteOldImage(detailsDao.getImage(student));
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    /**
     *
     * @param student the student to remove from the database
     * @param isPermanent By default, student records will be turned off when deleted. unless isPermament flag is set to true
     * @return true if the delete operation was successful
     */
    public Boolean deleteStudent(Student student, Boolean isPermanent)throws HibernateException {
        try {

            if (isPermanent) {
                this.deleteStudent(student);
                return true;
            } else  {
                em=HibernateUtil.getEntityManager();
                Student s = em.find(Student.class,student.getId());
                s.getStage().setNum_on_roll(s.getStage().getNum_on_roll()-1);
                em.getTransaction().begin();
                s.setDeleted(true);
                HibernateUtil.commit();
                HibernateUtil.close();
                return true;
            }
        } catch (Exception e) {
            return  false;
        } finally {
            em.close();
        }
    }

    /**
     * delete and then their parent from the db
     * promote a particular student to the next class
     * @param student
     */
    public Boolean promoteStudent(Student student) throws HibernateException {
        try {
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            prepStdToUpdate(student, true, em); // set new values
            HibernateUtil.commit();
            HibernateUtil.close();
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        } finally {
            em.close();
        }
    }

    public Boolean updateStudentStage(List<Student> students, Stage stage) {
        int entityCount = students.size();
        int batchSize =25;
        em=HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        try {
            for (int i = 0; i < entityCount; i++) {
                // clear the cache memory
                if(i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
                Student student = students.get(i);
                Stage prevStage = student.getStage();
                if (student.getStage().getId() != stage.getId()) {
                    student.setStage(stage);

                    //update the number on row
                    prevStage.setNum_on_roll(prevStage.getNum_on_roll() -1);
                    stage.setNum_on_roll(stage.getNum_on_roll() + 1);
                    em.merge(student);
                    em.merge(stage);
                    em.merge(prevStage);
                }
            }
           HibernateUtil.commit();
        } catch (Exception e) {
            HibernateUtil.rollBack();
            return  false;
        } finally {
            em.close();
        }
        return false;
    }

    public Boolean updateStudentStage(List<Student> students, Boolean isPromotion) {
        StageDao stageDao = new StageDao();
        List<Stage> stages = stageDao.getGetAllStage();

        Boolean status = false;
        em = HibernateUtil.getEntityManager();
        int entityCount = students.size();
        int batchSize = 25;
        try {
            em.getTransaction().begin();
            for (int i = 0; i < entityCount; i++) {
                if (i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
                Student student = students.get(i);
                if (isPromotion) {
                    this.prepStdToUpdate(student, true, em); // set new values
                } else {
                    this.prepStdToUpdate(student, false, em); // set new values
                }
            }
            status = true;
        } catch (RuntimeException e) {
            if (HibernateUtil.getEntityManager().getTransaction().isActive()) {
                HibernateUtil.getEntityManager().getTransaction().rollback();
            }
            throw e;
        } catch (Exception e){
            e.printStackTrace();
            return status;
        }finally {
            em.close();
        }
        return  status;
    }

    public Boolean demoteStudent(Student student) {
        try {
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            prepStdToUpdate(student, false, em);
            HibernateUtil.commit();
            HibernateUtil.close();
            return  true;
        } catch (Exception e) {
            return  false;
        } finally {
            em.close();
        }
    }

    /**
     * promote all students in a particular class to the next class
     * @param stage
     */
    public void promoteStudent(Stage stage)throws HibernateException {
        em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        List<Student> students= em.createQuery("from students S where S.stage.classValue=?").setParameter(0,stage.getClassValue()).getResultList();
        for (Student s:students)
            promoteStudent(s);
        em.close();
    }

    /**
     * The method is used to prepare datails for promoting or demoting a student
     * @param student the student we want to promote or demote
     * @param isPromotion If true, we demote the student otherwise demote the student
     * @param em the connection string to the database provider.
     */
    private Boolean prepStdToUpdate(Student student, Boolean isPromotion, EntityManager em) {
        System.out.println("Preparing students to update");
        String hql= null;
        Query query=null;
        Stage newStage=null;
        StageDao stageDao = new StageDao();
        stageDao.syncNumberOnRow();
        Boolean didUpdate = false;

        // get the new class for the student using the class value of the current class
        if (isPromotion) {
            didUpdate = false;
            /**
             * find a student class for the student by finding the old class and add 1 to the class value
             */
            if(stageDao.existStage(stageDao.getGetAllStage(), (student.getStage().getClassValue() + 1))) {
                try {
                    System.out.println("we found a class ");
                    hql = "from Class S where S.classValue = ?1";
                    query = em.createQuery(hql);
                    System.out.println("Creating query");
                    query.setParameter(1, student.getStage().getClassValue() + 1);
                    newStage = (Stage) query.getSingleResult();
                    didUpdate = true;
                } catch (Exception e){
                    return false;
                }
            } else  {
                return false;
            }
        } else {
            didUpdate = false;
            hql ="from Class S where S.classValue = ?1";
            query = em.createQuery(hql);
            query.setParameter(1, student.getStage().getClassValue() - 1);
            newStage = (Stage) query.getSingleResult();
            didUpdate = true;
        }

        // check if the update was successful
        if (didUpdate) {
            try {
                // update number on row
                Stage preStage = em.find(Stage.class, student.getStage().getId());
                preStage.setNum_on_roll(preStage.getNum_on_roll() - 1);
                newStage.setNum_on_roll(newStage.getNum_on_roll() + 1);

                // make changes ready to be saved to the database
                em.merge(newStage);
                em.merge(preStage);

                // update student class id
                hql = "UPDATE students S set S.stage = ?1 where S.id = ?2";

                Query q = em.createQuery(hql);
                q.setParameter(1, newStage);
                q.setParameter(2, student.getId());
                q.executeUpdate();

                // update the school fees for the student
                if (student.getPaySchoolFees()) {
                    // Get the bill for that the new class and check if the student is not part of the bill
                    // then add the student to the bill

                    if(isPromotion) {
                        BillDao billDao = new BillDao();
                        billDao.addStudentToBill(newStage.getBill(), student);
                    }

                    // Disable update for Student fees account when promoting students
                    student.getAccount().setFeeToPay(newStage.getBill().getTotalBill());
                }
                // commit the records
                em.getTransaction().commit();
            } catch (Exception e) {
                HibernateUtil.rollBack();
            } finally {
                em.close();
            }
        }
        return true;
    }

    /**
     *  promotes every student in the school to the next class
     */
    public void promoteStudent()throws HibernateException {
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        List<Student> students = em.createQuery("from students ").getResultList();
        for(Student s:students)
            promoteStudent(s);
        HibernateUtil.close();
    }

    public Boolean updateAccount(StudentAccount account) {
        try {
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            StudentAccount studentAccount=em.find(StudentAccount.class,account.getId());
            studentAccount.setFeedingFeeCredit(account.getFeedingFeeCredit());
            studentAccount.setFeeToPay(account.getFeeToPay());
            studentAccount.setSchFeesPaid(account.getSchFeesPaid());
            HibernateUtil.commit();
            HibernateUtil.close();
            return true;
        } catch (Exception e){
            HibernateUtil.rollBack();
            return  false;
        } finally {
            if(em == null) {
                em.close();
            }
        }
    }

    public Boolean resetFeedingFee(Student student, Double amount) {
        em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        try {
            //we cannot reset the feeding fee for students who do not pay feeding fee unless the new amount is 0.00
            StudentAccount studentAccount = em.find(StudentAccount.class,student.getAccount().getId());
            studentAccount.setFeedingFeeCredit(amount);
            updateAccount(studentAccount);

            if(student.getFeedingStatus() == Student.FeedingStatus.SEMI_PERIODIC) {
                if(studentAccount.getFeedingFeeCredit() == 0) {
                    student.setFeedingStatus(Student.FeedingStatus.DAILY);
                    HibernateUtil.commit();
                }
            }
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // when the student is paying school fees
    public Boolean paySchoolFee(Student st, Double amount) {
        if (st == null || !st.getPaySchoolFees()){
            return false;
        }

        em=HibernateUtil.getEntityManager();
        StudentAccount acc = em.find(StudentAccount.class, st.getAccount().getId());

        // update the student's record with the total amount of fees paid.
        acc.setSchFeesPaid(acc.getSchFeesPaid() + amount);
        if(this.updateAccount(acc))
            return true;
        return false;
    }

    public Boolean payFeedingFee(Double amount, Student student) {
        if ( student == null || amount == 0 | amount < 0) {
            return false;
        }

        try {
            // accept the payment
            em= HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            student.updateFeedingAccount(amount);
            em.merge(student);
            HibernateUtil.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // when you want to set a new amount for the current fees
    public Boolean updateSchoolFee(Student st, Double amount) {
        if(amount.isNaN() || !st.getPaySchoolFees())
            return false;
        else {
            em = HibernateUtil.getEntityManager();
            StudentAccount account = em.find(StudentAccount.class, st.getAccount().getId());
            account.setFeeToPay( amount);
            if(this.updateAccount(account))
                return true;
            return false;
        }
    }

    // set the student school fees to it's original value
    public Boolean resetSchoolFees(Student st) {
        try {
            if(!st.getPaySchoolFees())
                return false;
            em=HibernateUtil.getEntityManager();
            StudentAccount account = em.find(StudentAccount.class, st.getAccount().getId());
            account.setFeeToPay((st.getStage().getBill().getTotalBill()));
            // reset the amount paid to 0
            account.setSchFeesPaid(0.0);
            if(this.updateAccount(account))
                return true;
            return false;
        } catch ( Exception e) {
            return false;
        } finally {
            em.close();
        }
    }
}
