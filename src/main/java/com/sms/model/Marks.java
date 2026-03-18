package com.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Marks entity — maps to the `marks` table.
 * Records exam marks per student per subject.
 */
@Entity
@Table(name = "marks")
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mark_id")
    private Integer markId;

    @NotNull(message = "Student is required")
    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @NotBlank(message = "Subject is required")
    @Column(name = "subject", nullable = false, length = 100)
    private String subject;

    @NotNull(message = "Marks obtained is required")
    @DecimalMin(value = "0.0", message = "Marks cannot be negative")
    @Column(name = "marks_obtained", nullable = false, precision = 5, scale = 2)
    private BigDecimal marksObtained;

    @NotNull(message = "Max marks is required")
    @DecimalMin(value = "1.0", message = "Max marks must be at least 1")
    @Column(name = "max_marks", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxMarks;

    @NotBlank(message = "Exam type is required")
    @Column(name = "exam_type", nullable = false, length = 50)
    private String examType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    // ------- Constructors -------
    public Marks() {}

    // ------- Helper Methods -------

    /** Returns percentage = (marksObtained / maxMarks) * 100 */
    public double getPercentage() {
        if (maxMarks == null || maxMarks.doubleValue() == 0) return 0;
        return marksObtained.doubleValue() / maxMarks.doubleValue() * 100;
    }

    /** Returns letter grade based on percentage */
    public String getGrade() {
        double pct = getPercentage();
        if (pct >= 90) return "A";
        if (pct >= 80) return "B";
        if (pct >= 70) return "C";
        if (pct >= 60) return "D";
        return "F";
    }

    // ------- Getters and Setters -------
    public Integer getMarkId() { return markId; }
    public void setMarkId(Integer markId) { this.markId = markId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public BigDecimal getMarksObtained() { return marksObtained; }
    public void setMarksObtained(BigDecimal marksObtained) { this.marksObtained = marksObtained; }

    public BigDecimal getMaxMarks() { return maxMarks; }
    public void setMaxMarks(BigDecimal maxMarks) { this.maxMarks = maxMarks; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}
