package com.sms.repository;

import com.sms.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * FeeRepository — data access layer for fee records.
 */
@Repository
public interface FeeRepository extends JpaRepository<Fee, Integer> {

    /** All fee records for a student. */
    List<Fee> findByStudentId(Integer studentId);

    /** Total amount across all fee records. */
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fee f")
    BigDecimal findTotalAmount();

    /** Total paid amount across all fee records. */
    @Query("SELECT COALESCE(SUM(f.paidAmount), 0) FROM Fee f")
    BigDecimal findTotalPaidAmount();
}
