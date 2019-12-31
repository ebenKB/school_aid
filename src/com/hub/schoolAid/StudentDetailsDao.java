package com.hub.schoolAid;

import javax.persistence.EntityManager;

public class StudentDetailsDao {
    private EntityManager em;

    public String getImage(Student student){
        try{
            em=HibernateUtil.getEntityManager();
            return em.find(StudentDetails.class, student.getId()).getImage();

        }catch (Exception e){
            return  null;
        }
    }

    public Boolean addImage(Student student,String image){
        try{
              em = HibernateUtil.getEntityManager();
//              HibernateUtil.begin();
              Student std = em.find(Student.class,student.getId());

              StudentDetails studentDetails = new StudentDetails();
              studentDetails.setStudent(std);
              studentDetails.setImage(image);

//              HibernateUtil.save(StudentDetails.class,studentDetails);
            HibernateUtil.begin();
            em.persist(studentDetails);
            HibernateUtil.commit();
            HibernateUtil.close();

//            StudentDetails studentDetails =new StudentDetails();
//            studentDetails.setImage(image);
//            em=HibernateUtil.getEntityManager();
//            HibernateUtil.begin();
//            Student std =em.find(Student.class,student.getId());
//            studentDetails.setStudent(std);
//
//            HibernateUtil.commit();
//            HibernateUtil.save(StudentDetails.class,studentDetails);
//            HibernateUtil.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public Boolean updateDetails(Student student,StudentDetails details){
       try{
           em=HibernateUtil.getEntityManager();
           HibernateUtil.begin();
           StudentDetails newDetails = em.find(StudentDetails.class,student.getId());
           newDetails.setImage(details.getImage());
           HibernateUtil.commit();
           HibernateUtil.close();
           return true;
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }
    }

    public Boolean updateDetilsIfExist(Student student,StudentDetails details){
        System.out.println("in the update if exist function....");
        try {
            em=HibernateUtil.getEntityManager();
            HibernateUtil.begin();
            StudentDetails newDetails = em.find(StudentDetails.class,student.getId());

            if(newDetails ==null  || newDetails.equals("")){
                System.out.println("image is null");
                addImage(student,details.getImage());
            }else {
                System.out.println("this is the details we found"+ newDetails.getImage());
                updateDetails(student,details);
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
