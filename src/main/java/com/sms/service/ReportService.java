package com.sms.service;

import com.sms.model.Student;
import com.sms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

/**
 * ReportService — aggregates data for the admin reports dashboard.
 * All calculations use repository queries, nothing computed in controllers.
 */
@Service
public class ReportService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private MarksRepository marksRepository;
    @Autowired private FeeRepository feeRepository;
    @Autowired private NoticeRepository noticeRepository;

    public long getTotalStudents() {
        return studentRepository.count();
    }

    public long getTotalNotices() {
        return noticeRepository.count();
    }

    public BigDecimal getTotalFeeCollected() {
        return feeRepository.findTotalPaidAmount();
    }

    public BigDecimal getTotalFeePending() {
        BigDecimal total = feeRepository.findTotalAmount();
        BigDecimal paid = feeRepository.findTotalPaidAmount();
        return total.subtract(paid);
    }

    /** Count of students whose overall attendance is below 75%. */
    public long getStudentsBelowAttendanceThreshold() {
        List<Student> students = studentRepository.findAll();
        long count = 0;
        for (Student s : students) {
            long total = attendanceRepository.countByStudentId(s.getStudentId());
            if (total > 0) {
                long present = attendanceRepository.countPresentByStudentId(s.getStudentId());
                double pct = (double) present / total * 100;
                if (pct < 75) count++;
            }
        }
        return count;
    }

    /** Count of students above 75% attendance. */
    public long getStudentsAboveAttendanceThreshold() {
        List<Student> students = studentRepository.findAll();
        long above = 0;
        for (Student s : students) {
            long total = attendanceRepository.countByStudentId(s.getStudentId());
            if (total > 0) {
                long present = attendanceRepository.countPresentByStudentId(s.getStudentId());
                double pct = (double) present / total * 100;
                if (pct >= 75) above++;
            }
        }
        return above;
    }

    /** Top 5 students by average marks. Returns list of {studentName, avgPercentage}. */
    public List<Map<String, Object>> getTopStudentsByMarks(int limit) {
        List<Object[]> data = marksRepository.findTopStudentsByAverage();
        List<Map<String, Object>> result = new ArrayList<>();
        int count = 0;
        for (Object[] row : data) {
            if (count >= limit) break;
            Integer studentId = ((Number) row[0]).intValue();
            double avgPct = Math.round(((Number) row[1]).doubleValue() * 100.0) / 100.0;
            Optional<Student> student = studentRepository.findById(studentId);
            if (student.isPresent()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("studentName", student.get().getName());
                entry.put("rollNo", student.get().getRollNo());
                entry.put("avgPercentage", avgPct);
                result.add(entry);
                count++;
            }
        }
        return result;
    }

    /** Bottom 5 students by attendance. Returns list of {studentName, attendancePercentage}. */
    public List<Map<String, Object>> getBottomStudentsByAttendance(int limit) {
        List<Student> students = studentRepository.findAll();
        List<Map<String, Object>> allStudents = new ArrayList<>();

        for (Student s : students) {
            long total = attendanceRepository.countByStudentId(s.getStudentId());
            if (total > 0) {
                long present = attendanceRepository.countPresentByStudentId(s.getStudentId());
                double pct = Math.round((double) present / total * 10000.0) / 100.0;
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("studentName", s.getName());
                entry.put("rollNo", s.getRollNo());
                entry.put("attendancePercentage", pct);
                allStudents.add(entry);
            }
        }

        // Sort by attendance ascending (lowest first)
        allStudents.sort(Comparator.comparingDouble(m -> (Double) m.get("attendancePercentage")));
        return allStudents.size() > limit ? allStudents.subList(0, limit) : allStudents;
    }
}
