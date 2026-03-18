package com.sms;

import com.sms.model.*;
import com.sms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DataInitializer — seeds the database with sample data on first run.
 * Uses INSERT-if-not-exists logic so re-running won't create duplicates.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            StudentRepository studentRepository,
            AttendanceRepository attendanceRepository,
            MarksRepository marksRepository,
            FeeRepository feeRepository,
            NoticeRepository noticeRepository,
            TimetableRepository timetableRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // ===== USERS =====
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", passwordEncoder.encode("admin123"), User.Role.ADMIN);
                userRepository.save(admin);
            }
            if (userRepository.findByUsername("teacher1").isEmpty()) {
                User teacher = new User("teacher1", passwordEncoder.encode("teacher123"), User.Role.TEACHER);
                userRepository.save(teacher);
            }
            if (userRepository.findByUsername("student1").isEmpty()) {
                User student = new User("student1", passwordEncoder.encode("student123"), User.Role.STUDENT);
                userRepository.save(student);
            }
            if (userRepository.findByUsername("student2").isEmpty()) {
                User student2 = new User("student2", passwordEncoder.encode("student123"), User.Role.STUDENT);
                userRepository.save(student2);
            }

            // ===== STUDENTS (link student1 to user) =====
            if (studentRepository.findAll().isEmpty()) {
                Integer student1UserId = userRepository.findByUsername("student1").map(User::getUserId).orElse(null);
                Integer student2UserId = userRepository.findByUsername("student2").map(User::getUserId).orElse(null);

                Student s1 = new Student("Arjun Sharma", "CS2024001", "B.Sc Computer Science", LocalDate.of(2003, 6, 15), "9876543210");
                s1.setEmail("arjun.sharma@email.com");
                s1.setAddress("123 Main St, Delhi");
                s1.setBio("Passionate about coding and AI.");
                s1.setUserId(student1UserId);
                studentRepository.save(s1);

                Student s2 = new Student("Priya Patel", "CS2024002", "B.Sc Computer Science", LocalDate.of(2003, 9, 22), "9812345678");
                s2.setEmail("priya.patel@email.com");
                s2.setAddress("456 Oak Ave, Mumbai");
                s2.setBio("Loves mathematics and data science.");
                s2.setUserId(student2UserId);
                studentRepository.save(s2);

                Student s3 = new Student("Rahul Verma", "IT2024001", "B.Tech Information Technology", LocalDate.of(2002, 11, 8), "9823456789");
                s3.setEmail("rahul.verma@email.com");
                studentRepository.save(s3);

                Student s4 = new Student("Sneha Gupta", "BCA2024001", "Bachelor of Computer Applications", LocalDate.of(2004, 1, 30), "9834567890");
                s4.setEmail("sneha.gupta@email.com");
                studentRepository.save(s4);

                Student s5 = new Student("Mohammed Iqbal", "DS2024001", "B.Sc Data Science", LocalDate.of(2003, 4, 17), "9845678901");
                s5.setEmail("mohammed.iqbal@email.com");
                studentRepository.save(s5);

                // ===== ATTENDANCE =====
                String[] subjects = {"Mathematics", "Data Structures", "English", "Physics"};
                Student[] students = {s1, s2, s3, s4, s5};
                for (Student s : students) {
                    for (String subject : subjects) {
                        for (int day = 1; day <= 20; day++) {
                            Attendance a = new Attendance();
                            a.setStudentId(s.getStudentId());
                            a.setSubject(subject);
                            a.setDate(LocalDate.of(2025, 2, Math.min(day, 28)));
                            // ~80% present for s1, ~60% for s4, ~75% for others
                            boolean present;
                            if (s == s1) present = day % 5 != 0;
                            else if (s == s4) present = day % 5 < 3;
                            else present = day % 4 != 0;
                            a.setStatus(present ? Attendance.Status.PRESENT : Attendance.Status.ABSENT);
                            attendanceRepository.save(a);
                        }
                    }
                }

                // ===== MARKS =====
                for (Student s : students) {
                    int base = s == s1 ? 85 : s == s2 ? 78 : s == s3 ? 65 : s == s4 ? 55 : 72;
                    for (String subject : subjects) {
                        Marks m = new Marks();
                        m.setStudentId(s.getStudentId());
                        m.setSubject(subject);
                        m.setMarksObtained(BigDecimal.valueOf(base + (subject.hashCode() % 10)));
                        m.setMaxMarks(BigDecimal.valueOf(100));
                        m.setExamType("Midterm");
                        marksRepository.save(m);
                    }
                }

                // ===== FEES =====
                for (Student s : students) {
                    Fee f = new Fee();
                    f.setStudentId(s.getStudentId());
                    f.setAmount(BigDecimal.valueOf(50000));
                    f.setPaidAmount(s == s1 ? BigDecimal.valueOf(50000) : s == s4 ? BigDecimal.ZERO : BigDecimal.valueOf(25000));
                    f.setDueDate(LocalDate.of(2025, 6, 30));
                    f.setDescription("Tuition Fee - Semester 1");
                    f.calculateStatus();
                    feeRepository.save(f);
                }

                // ===== NOTICES =====
                Notice n1 = new Notice();
                n1.setTitle("Welcome to New Academic Session 2025");
                n1.setContent("We are pleased to welcome all students to the new academic session. Classes begin on March 1st, 2025. Please check your timetables and make sure to attend orientation.");
                n1.setPostedBy("admin");
                n1.setVisibleTo(Notice.Visibility.ALL);
                noticeRepository.save(n1);

                Notice n2 = new Notice();
                n2.setTitle("Midterm Examination Schedule Released");
                n2.setContent("The midterm examination schedule has been published. Exams will be held from April 15th to April 25th. Students are advised to prepare accordingly.");
                n2.setPostedBy("admin");
                n2.setVisibleTo(Notice.Visibility.STUDENT);
                noticeRepository.save(n2);

                Notice n3 = new Notice();
                n3.setTitle("Faculty Meeting on March 10th");
                n3.setContent("All faculty members are requested to attend the meeting on March 10th at 10:00 AM in the conference hall. Agenda: Semester planning and assessment methods.");
                n3.setPostedBy("admin");
                n3.setVisibleTo(Notice.Visibility.TEACHER);
                noticeRepository.save(n3);

                Notice n4 = new Notice();
                n4.setTitle("Library Hours Extended");
                n4.setContent("The library will now be open until 9:00 PM on weekdays. Take advantage of the extended hours for your exam preparations.");
                n4.setPostedBy("admin");
                n4.setVisibleTo(Notice.Visibility.ALL);
                noticeRepository.save(n4);

                // ===== TIMETABLE =====
                String course1 = "B.Sc Computer Science";
                timetableRepository.save(createTT(course1, "Mathematics", Timetable.DayOfWeek.MONDAY, "09:00", "10:00", "101", "Dr. Kumar"));
                timetableRepository.save(createTT(course1, "Data Structures", Timetable.DayOfWeek.MONDAY, "10:00", "11:00", "102", "Prof. Singh"));
                timetableRepository.save(createTT(course1, "English", Timetable.DayOfWeek.TUESDAY, "09:00", "10:00", "103", "Ms. Sharma"));
                timetableRepository.save(createTT(course1, "Physics", Timetable.DayOfWeek.TUESDAY, "10:00", "11:00", "104", "Dr. Patel"));
                timetableRepository.save(createTT(course1, "Mathematics", Timetable.DayOfWeek.WEDNESDAY, "09:00", "10:00", "101", "Dr. Kumar"));
                timetableRepository.save(createTT(course1, "Data Structures", Timetable.DayOfWeek.THURSDAY, "09:00", "10:00", "102", "Prof. Singh"));
                timetableRepository.save(createTT(course1, "Physics", Timetable.DayOfWeek.FRIDAY, "09:00", "10:00", "104", "Dr. Patel"));

                String course2 = "B.Tech Information Technology";
                timetableRepository.save(createTT(course2, "Mathematics", Timetable.DayOfWeek.MONDAY, "11:00", "12:00", "201", "Dr. Kumar"));
                timetableRepository.save(createTT(course2, "Data Structures", Timetable.DayOfWeek.TUESDAY, "11:00", "12:00", "202", "Prof. Singh"));
                timetableRepository.save(createTT(course2, "English", Timetable.DayOfWeek.WEDNESDAY, "11:00", "12:00", "203", "Ms. Sharma"));
            }
        };
    }

    private Timetable createTT(String course, String subject, Timetable.DayOfWeek day,
                               String start, String end, String room, String teacher) {
        Timetable tt = new Timetable();
        tt.setCourse(course);
        tt.setSubject(subject);
        tt.setDayOfWeek(day);
        tt.setStartTime(LocalTime.parse(start));
        tt.setEndTime(LocalTime.parse(end));
        tt.setRoomNumber(room);
        tt.setTeacherName(teacher);
        return tt;
    }
}