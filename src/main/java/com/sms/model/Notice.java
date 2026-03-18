package com.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Notice entity — maps to the `notice` table.
 * Represents announcements posted by admin, visible to specific roles.
 */
@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Integer noticeId;

    @NotBlank(message = "Title is required")
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Content is required")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "posted_by", nullable = false, length = 100)
    private String postedBy;

    @Column(name = "posted_at")
    private LocalDateTime postedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "visible_to")
    private Visibility visibleTo = Visibility.ALL;

    public enum Visibility {
        ALL, TEACHER, STUDENT
    }

    // ------- Constructors -------
    public Notice() {}

    // ------- Getters and Setters -------
    public Integer getNoticeId() { return noticeId; }
    public void setNoticeId(Integer noticeId) { this.noticeId = noticeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public LocalDateTime getPostedAt() { return postedAt; }
    public void setPostedAt(LocalDateTime postedAt) { this.postedAt = postedAt; }

    public Visibility getVisibleTo() { return visibleTo; }
    public void setVisibleTo(Visibility visibleTo) { this.visibleTo = visibleTo; }
}
