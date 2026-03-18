package com.sms.repository;

import com.sms.model.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * MarksRepository — data access layer for marks/results records.
 */
@Repository
public interface MarksRepository extends JpaRepository<Marks, Integer> {

    /** All marks for a student. */
    List<Marks> findByStudentId(Integer studentId);

    /** All marks for a student ordered by subject. */
    List<Marks> findByStudentIdOrderBySubject(Integer studentId);

    /** Distinct subjects for a student's marks. */
    @Query("SELECT DISTINCT m.subject FROM Marks m WHERE m.studentId = :studentId")
    List<String> findDistinctSubjectsByStudentId(@Param("studentId") Integer studentId);

    /** All distinct subjects across all marks. */
    @Query("SELECT DISTINCT m.subject FROM Marks m")
    List<String> findAllDistinctSubjects();

    /** Average percentage per subject (for charts). */
    @Query("SELECT m.subject, AVG(m.marksObtained / m.maxMarks * 100) FROM Marks m GROUP BY m.subject")
    List<Object[]> findAveragePercentageBySubject();

    /** Average marks percentage for a student. */
    @Query("SELECT AVG(m.marksObtained / m.maxMarks * 100) FROM Marks m WHERE m.studentId = :studentId")
    Double findAveragePercentageByStudentId(@Param("studentId") Integer studentId);

    /** Top N students by average marks. */
    @Query("SELECT m.studentId, AVG(m.marksObtained / m.maxMarks * 100) as avgPct FROM Marks m GROUP BY m.studentId ORDER BY avgPct DESC")
    List<Object[]> findTopStudentsByAverage();
}
