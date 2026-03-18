package com.sms.repository;

import com.sms.model.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * TimetableRepository — data access layer for timetable entries.
 */
@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Integer> {

    /** All entries for a specific course, ordered by day and start time. */
    List<Timetable> findByCourseOrderByDayOfWeekAscStartTimeAsc(String course);

    /** All entries ordered by day and start time. */
    List<Timetable> findAllByOrderByDayOfWeekAscStartTimeAsc();

    /** Distinct courses from timetable entries. */
    @Query("SELECT DISTINCT t.course FROM Timetable t ORDER BY t.course")
    List<String> findDistinctCourses();
}
