package com.hub.schoolAid;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.HibernateException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class AttendanceTemporaryDao {

    private EntityManager em;
    private ObservableList<AttendanceTemporary> attendanceList= FXCollections.observableArrayList();

    /**
     * This method saves students to a temporary attendance table
     * the data is then moved to the master table after the close of the day
     * @param student
     * @return
     * @throws HibernateException
     */
    public Boolean createAttendanceSheet(Student student, LocalDate date) throws HibernateException {
        try {
            HibernateUtil.save(AttendanceTemporary.class,setAttendanceData(student, date));
            // update the system date with the new date
            TermDao termDao = new TermDao();
            termDao.updateCurrentDate(date);
            return true;
        } catch (Exception e) {
            HibernateUtil.rollBack();
            return false;
        } finally {
            HibernateUtil.close();
        }
    }

    public Boolean createAttendanceSheet(List<Student>students, LocalDate date) throws HibernateException {
        try {
            if (students.size() < 1)
                return false;

            int batchSize = 25;
            int entityCount = students.size();
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            for(int i=0; i< entityCount; i++){
                Student student = students.get(i);
                AttendanceTemporary at = setAttendanceData(student, date);

                // check if we have reached the batch size and merge the records
                if(i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
                em.merge(at);
            }
            HibernateUtil.commit();

            // update the system date with the new date
            TermDao termDao = new TermDao();
            termDao.updateCurrentDate(date);
            return true;
        } catch (Exception e) {
            HibernateUtil.rollBack();
            return  false;
        } finally {
            em.close();
        }
    }

    /**
     * prepare records for creating a new attendance
     * @param student the student that we are creating the attendance for
     * @param date the date that the attendace is created for
     * @return attendance
     */
    private AttendanceTemporary setAttendanceData(Student student, LocalDate date) {
        AttendanceTemporary attendance = new AttendanceTemporary();
        attendance.setStudent(student);
        attendance.setDate(date);
        attendance.setPresent(Boolean.FALSE);
        if(student.getPayFeeding())
            attendance.setFeedingFee(student.getAccount().getFeedingFeeToPay());
        else attendance.setFeedingFee(0.0);

        return attendance;
    }
    public void markPresent(AttendanceTemporary attendanceTemporary) {
        // check if the student pays feeding fee
        if (attendanceTemporary.getStudent().getPayFeeding()) {
            // check if the student is paying now
            if (attendanceTemporary.hasPaidNow()) {
                // the student is paying now - check if the amount the student is paying is equal to what they have to pay
                if(attendanceTemporary.getFeedingFee() == attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()) {
                    // the student is paying what they have to pay -- MARK THE STUDENT PRESENT
                    // check if the student can be set to DAILY
                    if(attendanceTemporary.getStudent().getAccount().getFeedingFeeCredit() == 0 ) {
                        // change the status of the student to DAILY
                        attendanceTemporary.getStudent().setFeedingStatus(Student.FeedingStatus.DAILY);
                    }

                } else {
                    // the student is not paying what they have to pay for the day.
                    Double diff = attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay();
                    attendanceTemporary.getStudent().updateFeedingAccount(diff);

                    // check if we have to change the student to periodic
                    if (attendanceTemporary.getStudent().getAccount().getFeedingFeeCredit() != 0) {
                        // check if the student is not already PERIODIC
                        if (attendanceTemporary.getStudent().getFeedingStatus() == Student.FeedingStatus.DAILY) {
                            // change the status of the student to PERIODIC
                            attendanceTemporary.getStudent().setFeedingStatus(Student.FeedingStatus.PERIODIC);
                        }
                    }
                }

            } else {
                // the student is not paying now - DEBIT the student with the amount supposed to be paid
//                attendanceTemporary.getStudent().debitFeedingAccount(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
                // if the student is not paying now, then we must make sure the amount to debit is always the amount that the student is supposed to pay
                attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
                attendanceTemporary.getStudent().debitFeedingAccount(attendanceTemporary.getFeedingFee());

                // check if the student has status DAILY and change to PERIODIC
                if (attendanceTemporary.getStudent().getAccount().getFeedingFeeCredit() != 0) {
                    attendanceTemporary.getStudent().setFeedingStatus(Student.FeedingStatus.PERIODIC);
                }
            }
        } else {
            // the student does not pay feeding fee - MARK THE STUDENT PRESENT AND DO NOTHING
            attendanceTemporary.setFeedingFee(0.00);
        }
        try {
            // COMMIT all changes
            StudentDao studentDao = new StudentDao();
            studentDao.updateStudentRecord(attendanceTemporary.getStudent());
            studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            attendanceTemporary.setPresent(true);
            em.merge(attendanceTemporary);
            HibernateUtil.commit();
        } catch (Exception e) {
            HibernateUtil.rollBack();
        } finally {
//            em.close();
            HibernateUtil.close();
        }
    }

    public Boolean prepAttendanceRecords(AttendanceTemporary attendanceTemporary, Boolean isCheckIn) {
        // set the fields for the attendance
        if(isCheckIn) {
            attendanceTemporary.setPresent(true);
        } else {
            attendanceTemporary.setPresent(false);
        }

        if (attendanceTemporary.getStudent().getPayFeeding()) {
//            attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());

            if(isCheckIn) {
                // debit the student with the amount supposed to be paid
                attendanceTemporary.getStudent().debitFeedingAccount(attendanceTemporary.getFeedingFee());
            } else {
                attendanceTemporary.getStudent().updateFeedingAccount(attendanceTemporary.getFeedingFee());
            }

            // check the new balance of the student
            if((attendanceTemporary.getStudent().getAccount().getFeedingFeeCredit() > 0) ||(attendanceTemporary.getStudent().getAccount().getFeedingFeeCredit() < 0)) {
                attendanceTemporary.getStudent().setFeedingStatus(Student.FeedingStatus.PERIODIC);
            } else if(attendanceTemporary.getStudent().getAccount().getFeedingFeeCredit() == 0) {
                attendanceTemporary.getStudent().setFeedingStatus(Student.FeedingStatus.DAILY);
            }
        }
            return false;
    }
    public Boolean checkInWithCoupon(AttendanceTemporary attendanceTemporary) {
        try {
            em= HibernateUtil.getEntityManager();

            if(attendanceTemporary.isPresent()) {
                return false;
            }
                prepAttendanceRecords(attendanceTemporary, true);

                // persist the changes to the
                HibernateUtil.begin();
                em.merge(attendanceTemporary.getStudent());
                em.merge(attendanceTemporary);
                HibernateUtil.commit();
                return  true;

        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    public Boolean checkInWithCoupon(List<AttendanceTemporary>attendanceTemporaryList) {
        try {
            int batchSize = 25;
            int totalRecords = attendanceTemporaryList.size();
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();

            // iterate the list
            for(int i=0; i< totalRecords; i++) {
                if(i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
                AttendanceTemporary at = attendanceTemporaryList.get(i);
                prepAttendanceRecords(at, true);

                // save the updates to the database
                em.merge(at);
                em.merge(at.getStudent());
            }
            HibernateUtil.commit();
            return true;
        } catch (Exception e) {
            HibernateUtil.rollBack();
            return false;
        } finally {
            em.close();
        }
    }

    public Boolean checkOutWithCoupon(List<AttendanceTemporary> attendanceTemporaryList) {
        try {
            int batchSize = 25;
            int totalRecords = attendanceTemporaryList.size();
            em = HibernateUtil.getEntityManager();
            em.getTransaction().begin();

            // iterate the list
            for (int i=0; i < totalRecords; i++) {
                if (i > 0 && i % batchSize == 0) {
                    em.flush();
                    em.clear();
                }
                AttendanceTemporary at = attendanceTemporaryList.get(i);
                prepAttendanceRecords(at, false);

                em.merge(at);
                em.merge(at.getStudent());
            }
            HibernateUtil.commit();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }

    public Boolean checkOutWithCoupon() {
        return false;
    }


    public void checkOut() {
        System.out.println("We want to check out the student");
    }

//    public  void markPresent(AttendanceTemporary attendanceTemporary) {
//        try {
//            attendanceTemporary.setPresent(Boolean.TRUE);
//            //check if the student pays feeding fee
//            if(attendanceTemporary.getStudent().getPayFeeding()){
//                // check if the student pays daily
//                if(attendanceTemporary.getStudent().getFeedingStatus() != Student.FeedingStatus.DAILY) {
//                    // check if the student is paying more than they have to pay for a day
//                    if(attendanceTemporary.getFeedingFee() > attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()) {
//                        //the student is paying in excess so save the balance and set the feeding fee to the amount
//                        attendanceTemporary.getStudent().addNewFeedingFeeCredit((attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()));
//                        // attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
//                        attendanceTemporary.setFeedingFee(attendanceTemporary.getFeedingFee());
//                    } else {
//                        // Check if the student is paying now or later. if a periodic or semi periodic student pays for only a day even though they may be owing for previous days, just check them in for today.
//                        if(attendanceTemporary.hasPaidNow() && attendanceTemporary.getFeedingFee() == attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
//                            attendanceTemporary.setFeedingFee(attendanceTemporary.getFeedingFee());
//                        } else if (attendanceTemporary.hasPaidNow() && attendanceTemporary.getFeedingFee() < attendanceTemporary.getStudent().getAccount().getFeeToPay()) {
//                            System.out.println(" PERIODIC: The student is paying less that they have to pay ");
//                            Double dif = attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay();
//                            attendanceTemporary.getStudent().debitFeedingAccount(dif);
//
//                        }
//                        // the student is present but does not have feeding fee for today
//                        else attendanceTemporary.getStudent().debitFeedingAccount(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
//                    }
//                } else {
//                    if( !attendanceTemporary.hasPaidNow()) {
//                        //mark the student present, set the feeding fee to zero and then debit the student
//                        attendanceTemporary.getStudent().debitFeedingAccount(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
//                    }else if(attendanceTemporary.hasPaidNow()){
//                        //check if the student is paying in excess of what they should pay for a day
//                        if(attendanceTemporary.getFeedingFee() > attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
//                            attendanceTemporary.getStudent().addNewFeedingFeeCredit((attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()));
////                            attendanceTemporary.setFeedingFee(attendanceTemporary.getFeedingFee());
//
//                            // update the student account details
//                            StudentDao studentDao = new StudentDao();
//                            studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
//
//                            //consider changing the status of the student to periodic
//                            if(attendanceTemporary.getStudent().getAccount().getFeedingFeeCredit() > 0){
//                                attendanceTemporary.getStudent().setFeedingStatus(Student.FeedingStatus.SEMI_PERIODIC);
//                                studentDao.updateStudentRecord(attendanceTemporary.getStudent());
//                            }
//                        } else {
//                            System.out.println("DAILY. Paying less than my daily amount");
//                            double diff = attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay();
//                            attendanceTemporary.getStudent().debitFeedingAccount(diff);
//                            // get the account and debit the account
//                        }
//                    }
//                }
//                StudentDao studentDao = new StudentDao();
//                studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
//            } else attendanceTemporary.setFeedingFee(0.0); // the student does not pay feeding fee
//
//            em=HibernateUtil.getEntityManager();
//            HibernateUtil.begin();
//            em.merge(attendanceTemporary);
//            HibernateUtil.commit();
//        } catch (Exception e) {
//            em.clear();
//            HibernateUtil.close();
//        } finally {
//            em.clear();
//            HibernateUtil.close();
//            System.out.println("We have closed the connection"+ em);
//        }
//    }
    public  Boolean markAbsent(AttendanceTemporary attendanceTemporary) {
        try {
            attendanceTemporary.setPresent(Boolean.FALSE);

            if(attendanceTemporary.getStudent().getFeedingStatus() != Student.FeedingStatus.DAILY) {
                // check if the student paid in excess and then get the balance after subtracting feeding fee. make the balance negative and use the result to update the student account balance.
                // The negative value cancels out the amount we added earlier to the student account
                if(attendanceTemporary.getFeedingFee() > attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
                    attendanceTemporary.getStudent().addNewFeedingFeeCredit(
                            (attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()) * -1);
                } else {
                    // revert the feeding debit and add the amount we subtracted earlier to the feeding fee
                    if(attendanceTemporary.getFeedingFee() != attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){   //--add
                        attendanceTemporary.getStudent().addNewFeedingFeeCredit(attendanceTemporary.getFeedingFee());
                    } //--add

                    // revert the feeding fee and tag the student as NOT PAID for today
                    else if((attendanceTemporary.getFeedingFee() == attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()) && !attendanceTemporary.hasPaidNow()){
                        attendanceTemporary.getStudent().addNewFeedingFeeCredit(attendanceTemporary.getFeedingFee());
                    } //-end add
                }
                attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
                StudentDao studentDao = new StudentDao();
                studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
            } else if((!attendanceTemporary.hasPaidNow())){
                attendanceTemporary.getStudent().addNewFeedingFeeCredit(attendanceTemporary.getFeedingFee());
                StudentDao studentDao = new StudentDao();
                studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
            } else if(attendanceTemporary.hasPaidNow() && attendanceTemporary.getFeedingFee() > attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()){
                attendanceTemporary.getStudent().addNewFeedingFeeCredit( (attendanceTemporary.getFeedingFee() - attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay()) * -1);
                attendanceTemporary.setFeedingFee(attendanceTemporary.getStudent().getAccount().getFeedingFeeToPay());
                StudentDao studentDao = new StudentDao();
                studentDao.updateAccount(attendanceTemporary.getStudent().getAccount());
            }
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            em.merge(attendanceTemporary);
            HibernateUtil.commit();
            return true;
        }catch (Exception e) {
            return false;
        } finally {
            em.clear();
            HibernateUtil.close();
        }
    }

    public List <AttendanceTemporary> getTempAttendance() {
        try {
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            List<AttendanceTemporary>results =  em.createQuery(" from AttendanceTemporary A order by A.student.firstname asc ").getResultList();
            return results;
        } catch (Exception e) {
            return null;
        } finally {
            if(em == null)
            {
                em.close();
            }
        }
    }

    public List<AttendanceTemporary> getTempAttendance(Stage stage) {
        try {
            em = HibernateUtil.getEntityManager();
           em.getTransaction().begin();
            String hql = "from AttendanceTemporary  A where A.student.id = ?1 order by A.student.firstname asc";
            Query query = em.createQuery(hql);
            query.setParameter(1, stage.getId());
            List<AttendanceTemporary> results = query.getResultList();
            return results;
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Boolean checkIfExisForToday(){
        try{
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            String hql = "from AttendanceTemporary at ";
            Query query= em.createQuery(hql);
            query.setFirstResult(1);
            query.setMaxResults(1);
            AttendanceTemporary at =(AttendanceTemporary) query.getSingleResult();
            if(at.getDate().equals(LocalDate.now()))
                return true;
            return false;
        }catch (Exception e){
            return false;
        }
    }
    public List<AttendanceTemporary> getAbsentees(){
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        return em.createQuery(" from AttendanceTemporary A where A.present=false ").getResultList();
    }

    public Boolean deleteAllTempAttendance() {
        try {
            em=HibernateUtil.getEntityManager();
            em.getTransaction().begin();
            List<AttendanceTemporary> list =getTempAttendance();
            for(AttendanceTemporary a:list){
                em.remove(a);
            }
            HibernateUtil.commit();
            return  true;
        } catch (PersistenceException p) {
            p.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public AttendanceTemporary getAttendance(int id) {
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            AttendanceTemporary at = (AttendanceTemporary) em.createQuery("from AttendanceTemporary where  +id+ like '"+id+"' ").getSingleResult();
            return at;
        } catch (PersistenceException p){
            p.printStackTrace();
            return null;
        } finally {
           em.close();
        }
    }

    public  List<AttendanceTemporary> getStudentAttendance(String name){
        try{
            em=HibernateUtil.getEntityManager();
            List<AttendanceTemporary> results =  em.createQuery("from AttendanceTemporary  at where LOWER(at.student.firstname) like '%"+name.toLowerCase()+"' or lower( at.student.firstname) like '+"+name.toLowerCase()+"%' " +
                    "or lower(at.student.lastname) like '%"+name.toLowerCase()+"' or lower(at.student.othername) like '%"+name+"' " +
                    "or lower(student.othername) like  '"+name+"' or lower(student.stage.name)  like  '"+name.toLowerCase()+"%'  " +
                    "or lower(student.stage.name)  like '%"+name.toLowerCase()+"' ").getResultList();
            return results;
        }catch (Exception e) {
            return null;
        } finally {
           em.close();
           HibernateUtil.close();
        }
    }

    public void removeStudent(Student student) {
        em=HibernateUtil.getEntityManager();
        HibernateUtil.begin();
        em.createQuery("delete from AttendanceTemporary  where '+student_id+' = '+student.id+' ").executeUpdate();
    }

    public int checkAttendanceInterval() {
        try {
            int diff = 0;
            LocalDate date = TermDao.getCurrentDate(true);

            if(date == LocalDate.now()) {
                return diff;
            } else  {
//                Use a singleton to return the current date
                Period interval = Period.between(date, LocalDate.now());
                diff = interval.getDays();
                if ( (diff == 0) ||((diff > 0) && (diff < 15 && interval.getMonths() < 1 && interval.getYears() < 1)) ) {
                    return diff;
                } else {
                    return -1;
                }
            }
        } catch (Exception e) {
            return -2;
        }
    }

    public int checkAttendanceInterval(LocalDate date) {
        // check if the date is not later than the already existing dates
        LocalDate termDate = TermDao.getCurrentDate(true);
        if(date.compareTo(termDate) < 1) {
            return 0;
        } else return checkAttendanceInterval();
    }


    public Boolean canCreateAttendance(LocalDate date) {
        // check if the date provided is ahead of today
        if(date.compareTo(LocalDate.now()) > 0) {
            return false;
        }
        if ( (this.checkAttendanceInterval(date) == 1)|| ((this.checkAttendanceInterval(date) > 0) && (this.checkAttendanceInterval(date) < 15))) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean setPaidNow(AttendanceTemporary at) {
        try {
            em = HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            at.setPaidNow(true);
            HibernateUtil.commit();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }
//    private Boolean canCreateNewAttendance() {
//        if(termDao.getCurrentDate().equals(LocalDate.now())) {
//            System.out.println("We can create a date for the attendance");
//            return true;
//        } else {
//            Period interval = Period.between(termDao.getCurrentDate(), LocalDate.now());
//            int diff= interval.getDays();
//            System.out.println("This is the difference between the days"+ diff);
//            if(diff > 0 && diff < 6 && interval.getMonths() < 1 && interval.getYears() < 1) {
//                System.out.println("The difference is less than 6");
//            }
//        }
//        return false;
//    }
}
