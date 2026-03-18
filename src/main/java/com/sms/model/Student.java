package com.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Student entity — maps to the `student` table.
 * Represents a student record with personal details and optional link to a User account.
 */
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Roll number is required")
    @Size(max = 20)
    @Column(name = "roll_no", unique = true, nullable = false, length = 20)
    private String rollNo;

    @NotBlank(message = "Course is required")
    @Size(max = 50)
    @Column(name = "course", nullable = false, length = 50)
    private String course;

    @Column(name = "dob")
    private LocalDate dob;

    @Size(max = 15)
    @Column(name = "contact", length = 15)
    private String contact;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Size(max = 255)
    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "user_id")
    private Integer userId;

    // ------- Constructors -------

    public Student() {}

    public Student(String name, String rollNo, String course, LocalDate dob, String contact) {
        this.name = name;
        this.rollNo = rollNo;
        this.course = course;
        this.dob = dob;
        this.contact = contact;
    }

    // ------- Getters and Setters -------

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    @Override
    public String toString() {
        return "Student{studentId=" + studentId + ", name='" + name +
               "', rollNo='" + rollNo + "', course='" + course + "'}";
    }
}
