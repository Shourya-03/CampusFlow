package com.sms.service;

import com.sms.model.Notice;
import com.sms.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * NoticeService — business logic for the notice board.
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByPostedAtDesc();
    }

    public Notice saveNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    public void deleteNotice(Integer id) {
        noticeRepository.deleteById(id);
    }

    /** Notices visible to a specific user role. */
    public List<Notice> getNoticesForRole(String role) {
        // role comes as "ADMIN", "TEACHER", or "STUDENT"
        return noticeRepository.findVisibleToRole(role);
    }

    /** Latest N notices for a role (for dashboard preview). */
    public List<Notice> getLatestNoticesForRole(String role, int limit) {
        List<Notice> notices = noticeRepository.findVisibleToRole(role);
        return notices.size() > limit ? notices.subList(0, limit) : notices;
    }

    public long getNoticeCount() {
        return noticeRepository.count();
    }
}
