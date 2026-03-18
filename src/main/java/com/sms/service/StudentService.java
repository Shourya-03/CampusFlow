package com.sms.service;

import com.sms.model.Student;
import com.sms.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * StudentService — business logic for student CRUD and search operations.
 */
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Integer id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByUserId(Integer userId) {
        return studentRepository.findByUserId(userId);
    }

    /** Search and optionally filter by course. */
    public List<Student> searchStudents(String keyword, String course) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasCourse = course != null && !course.trim().isEmpty();

        if (hasKeyword && hasCourse) {
            return studentRepository.searchByKeywordAndCourse(keyword.trim(), course.trim());
        } else if (hasKeyword) {
            return studentRepository.searchByKeyword(keyword.trim());
        } else if (hasCourse) {
            return studentRepository.findByCourse(course.trim());
        }
        return studentRepository.findAll();
    }

    public List<String> getAllCourses() {
        return studentRepository.findDistinctCourses();
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Integer id) {
        studentRepository.deleteById(id);
    }

    public long getStudentCount() {
        return studentRepository.count();
    }
}
