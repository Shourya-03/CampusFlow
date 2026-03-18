package com.sms.repository;

import com.sms.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * AttendanceRepository — data access layer for attendance records.
 * Provides query methods for attendance tracking and reporting.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    /** All attendance records for a specific student, ordered by date desc. */
    List<Attendance> findByStudentIdOrderByDateDesc(Integer studentId);

    /** Last N attendance records for a student. */
    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId ORDER BY a.date DESC")
    List<Attendance> findRecentByStudentId(@Param("studentId") Integer studentId);

    /** Count of present records for a student in a specific subject. */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.subject = :subject AND a.status = 'PRESENT'")
    long countPresentByStudentAndSubject(@Param("studentId") Integer studentId, @Param("subject") String subject);

    /** Total count for a student in a specific subject. */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.subject = :subject")
    long countTotalByStudentAndSubject(@Param("studentId") Integer studentId, @Param("subject") String subject);

    /** Distinct subjects attended by a student. */
    @Query("SELECT DISTINCT a.subject FROM Attendance a WHERE a.studentId = :studentId")
    List<String> findDistinctSubjectsByStudentId(@Param("studentId") Integer studentId);

    /** All distinct subjects across all students. */
    @Query("SELECT DISTINCT a.subject FROM Attendance a")
    List<String> findAllDistinctSubjects();

    /** Count of total attendance records for a student. */
    long countByStudentId(Integer studentId);

    /** Count of PRESENT records for a student. */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.status = 'PRESENT'")
    long countPresentByStudentId(@Param("studentId") Integer studentId);
}
