package com.sms.service;

import com.sms.model.Timetable;
import com.sms.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * TimetableService — business logic for timetable management.
 */
@Service
public class TimetableService {

    @Autowired
    private TimetableRepository timetableRepository;

    public List<Timetable> getAllEntries() {
        return timetableRepository.findAllByOrderByDayOfWeekAscStartTimeAsc();
    }

    public List<Timetable> getEntriesByCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            return getAllEntries();
        }
        return timetableRepository.findByCourseOrderByDayOfWeekAscStartTimeAsc(course.trim());
    }

    public Timetable saveEntry(Timetable entry) {
        return timetableRepository.save(entry);
    }

    public void deleteEntry(Integer id) {
        timetableRepository.deleteById(id);
    }

    public List<String> getDistinctCourses() {
        return timetableRepository.findDistinctCourses();
    }
}
