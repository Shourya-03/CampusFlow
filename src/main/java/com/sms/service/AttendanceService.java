package com.sms.service;

import com.sms.model.Attendance;
import com.sms.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * AttendanceService — business logic for attendance tracking and summary.
 */
@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAttendanceByStudentId(Integer studentId) {
        return attendanceRepository.findByStudentIdOrderByDateDesc(studentId);
    }

    /** Returns recent attendance records for a student (last N). */
    public List<Attendance> getRecentAttendance(Integer studentId, int limit) {
        List<Attendance> all = attendanceRepository.findByStudentIdOrderByDateDesc(studentId);
        return all.size() > limit ? all.subList(0, limit) : all;
    }

    /**
     * Builds a subject-wise attendance summary for a student.
     * Returns a list of maps with keys: subject, total, present, absent, percentage
     */
    public List<Map<String, Object>> getSubjectWiseSummary(Integer studentId) {
        List<String> subjects = attendanceRepository.findDistinctSubjectsByStudentId(studentId);
        List<Map<String, Object>> summary = new ArrayList<>();

        for (String subject : subjects) {
            long total = attendanceRepository.countTotalByStudentAndSubject(studentId, subject);
            long present = attendanceRepository.countPresentByStudentAndSubject(studentId, subject);
            long absent = total - present;
            double percentage = total > 0 ? (double) present / total * 100 : 0;

            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("subject", subject);
            entry.put("total", total);
            entry.put("present", present);
            entry.put("absent", absent);
            entry.put("percentage", Math.round(percentage * 100.0) / 100.0);
            summary.add(entry);
        }
        return summary;
    }

    /** Overall attendance percentage for a student. */
    public double getOverallPercentage(Integer studentId) {
        long total = attendanceRepository.countByStudentId(studentId);
        if (total == 0) return 0;
        long present = attendanceRepository.countPresentByStudentId(studentId);
        return Math.round((double) present / total * 10000.0) / 100.0;
    }

    /** Distinct subjects across all records. */
    public List<String> getAllSubjects() {
        return attendanceRepository.findAllDistinctSubjects();
    }
}
