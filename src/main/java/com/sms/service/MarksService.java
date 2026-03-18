package com.sms.service;

import com.sms.model.Marks;
import com.sms.repository.MarksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * MarksService — business logic for marks and grade computation.
 */
@Service
public class MarksService {

    @Autowired
    private MarksRepository marksRepository;

    public List<Marks> getAllMarks() {
        return marksRepository.findAll();
    }

    public Marks saveMarks(Marks marks) {
        return marksRepository.save(marks);
    }

    public List<Marks> getMarksByStudentId(Integer studentId) {
        return marksRepository.findByStudentIdOrderBySubject(studentId);
    }

    /** Average percentage for a student across all subjects. */
    public Double getAveragePercentage(Integer studentId) {
        Double avg = marksRepository.findAveragePercentageByStudentId(studentId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    /** Overall grade based on average percentage. */
    public String getOverallGrade(Integer studentId) {
        double avg = getAveragePercentage(studentId);
        if (avg >= 90) return "A";
        if (avg >= 80) return "B";
        if (avg >= 70) return "C";
        if (avg >= 60) return "D";
        return "F";
    }

    /** Average marks percentage per subject (for bar chart). */
    public Map<String, Double> getAverageBySubject() {
        Map<String, Double> result = new LinkedHashMap<>();
        List<Object[]> data = marksRepository.findAveragePercentageBySubject();
        for (Object[] row : data) {
            result.put((String) row[0], Math.round(((Number) row[1]).doubleValue() * 100.0) / 100.0);
        }
        return result;
    }

    /** All distinct subjects. */
    public List<String> getAllSubjects() {
        return marksRepository.findAllDistinctSubjects();
    }

    /** Subject-wise marks percentage for a specific student (for chart). */
    public Map<String, Double> getSubjectPercentagesForStudent(Integer studentId) {
        Map<String, Double> result = new LinkedHashMap<>();
        List<Marks> marks = marksRepository.findByStudentIdOrderBySubject(studentId);
        for (Marks m : marks) {
            result.put(m.getSubject(), Math.round(m.getPercentage() * 100.0) / 100.0);
        }
        return result;
    }
}
