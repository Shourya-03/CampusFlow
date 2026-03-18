package com.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fee entity — maps to the `fee` table.
 * Tracks fee amount, payment, due date, and status per student.
 */
@Entity
@Table(name = "fee")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Integer feeId;

    @NotNull(message = "Student is required")
    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FeeStatus status = FeeStatus.UNPAID;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    public enum FeeStatus {
        PAID, PARTIALLY_PAID, UNPAID
    }

    // ------- Constructors -------
    public Fee() {}

    // ------- Helper -------

    /** Returns balance = amount - paidAmount */
    public BigDecimal getBalance() {
        if (paidAmount == null) return amount;
        return amount.subtract(paidAmount);
    }

    /** Auto-calculates status based on paid vs amount */
    public void calculateStatus() {
        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = FeeStatus.UNPAID;
        } else if (paidAmount.compareTo(amount) >= 0) {
            this.status = FeeStatus.PAID;
        } else {
            this.status = FeeStatus.PARTIALLY_PAID;
        }
    }

    // ------- Getters and Setters -------
    public Integer getFeeId() { return feeId; }
    public void setFeeId(Integer feeId) { this.feeId = feeId; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public FeeStatus getStatus() { return status; }
    public void setStatus(FeeStatus status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}
