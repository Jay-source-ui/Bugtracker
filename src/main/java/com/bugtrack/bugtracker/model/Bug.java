package com.bugtrack.bugtracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bugs")
public class Bug {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private String status;
    private String priority;
    private String assignedTo;
    private LocalDate createdDate;
    private LocalDate resolvedDate;
    
    // New fields for assignment tracking
    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    private User assignedBy;
    
    private LocalDate assignedDate;
    
    @ManyToOne
    @JoinColumn(name = "reported_by_id")
    private User reportedBy;
    
    // Constructors
    public Bug() {
        this.createdDate = LocalDate.now();
        this.status = "OPEN";
    }
    
    // Getters and Setters for existing fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    
    public LocalDate getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(LocalDate resolvedDate) { this.resolvedDate = resolvedDate; }
    
    // Getters and Setters for new fields
    public User getAssignedBy() { return assignedBy; }
    public void setAssignedBy(User assignedBy) { this.assignedBy = assignedBy; }
    
    public LocalDate getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDate assignedDate) { this.assignedDate = assignedDate; }
    
    public User getReportedBy() { return reportedBy; }
    public void setReportedBy(User reportedBy) { this.reportedBy = reportedBy; }
}