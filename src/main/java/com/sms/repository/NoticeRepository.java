package com.sms.repository;

import com.sms.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * NoticeRepository — data access layer for notice board.
 */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    /** All notices ordered by newest first. */
    List<Notice> findAllByOrderByPostedAtDesc();

    /** Notices visible to a specific role (includes 'ALL'). */
    @Query(value = "SELECT * FROM notice WHERE visible_to = 'ALL' OR visible_to = :role ORDER BY posted_at DESC", nativeQuery = true)
    List<Notice> findVisibleToRole(@Param("role") String role);
}
