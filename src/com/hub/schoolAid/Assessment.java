package com.hub.schoolAid;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.persistence.*;


@Entity
public class Assessment {

    public Assessment(){

    }

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "assessment_sequence", sequenceName = "assessment_sequence", allocationSize = 1)
    @GeneratedValue(generator = "assessment_sequence")
    private Long id;
    private Double classScore;
    private Double examScore;

    @OneToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToOne
    @JoinColumn(name = "grade")
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "student_asmnt_id")
    private Student student;

//    @JoinColumn(name = "student_asmnt_id", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Student student;

    @OneToOne
    @JoinColumn(name = "term")
    private Term term;

    private Boolean isDeleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getClassScore() {
        return classScore;
    }

    public void setClassScore(Double classScore) {
        this.classScore = classScore;
    }

    public Double getExamScore() {
        return examScore;
    }

    public void setExamScore(Double examScore) {
        this.examScore = examScore;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public void  createReport(Assessment assessment){

    }

    @Override
    public String toString() {
        String msg = this.classScore+"\n"+this.getExamScore()+"\n"+this.getGrade()+"\n"+this.getCourse();
        return msg;
    }
}
