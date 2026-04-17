
package com.bugtrack.bugtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 2000, nullable = false)
    private String content;
    
    private LocalDateTime createdDate;
    
    @ManyToOne
    @JoinColumn(name = "bug_id")
    private Bug bug;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    public Comment() {
        this.createdDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public Bug getBug() {
        return bug;
    }
    
    public void setBug(Bug bug) {
        this.bug = bug;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}