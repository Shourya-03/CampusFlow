package com.sms.repository;

import com.sms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * StudentRepository — data access layer for the Student entity.
 * Provides CRUD, search, and filter operations.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    /** Searches students by name or roll number (case-insensitive partial match). */
    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> searchByKeyword(@Param("keyword") String keyword);

    /** Filters students by course. */
    List<Student> findByCourse(String course);

    /** Searches by keyword within a specific course. */
    @Query("SELECT s FROM Student s WHERE s.course = :course AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.rollNo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Student> searchByKeywordAndCourse(@Param("keyword") String keyword, @Param("course") String course);

    /** Finds student linked to a user account. */
    Optional<Student> findByUserId(Integer userId);

    /** Gets all distinct course names for the filter dropdown. */
    @Query("SELECT DISTINCT s.course FROM Student s ORDER BY s.course")
    List<String> findDistinctCourses();
}
