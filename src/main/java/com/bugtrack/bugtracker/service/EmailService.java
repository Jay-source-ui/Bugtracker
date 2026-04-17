package com.bugtrack.bugtracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.bugtrack.bugtracker.model.Bug;
import com.bugtrack.bugtracker.model.User;

@Service
public class EmailService {

    @Autowired(required = false)  // This makes it optional (won't crash if email not configured)
    private JavaMailSender mailSender;

    public void sendBugAssignedEmail(Bug bug, User assignedTo) {
        if (mailSender == null) {
            System.out.println("Email not configured. Skipping email to: " + assignedTo.getEmail());
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(assignedTo.getEmail());
            message.setSubject("Bug Assigned: #" + bug.getId() + " - " + bug.getTitle());
            message.setText(
                "Hello " + assignedTo.getFullName() + ",\n\n" +
                "A bug has been assigned to you:\n\n" +
                "Bug ID: " + bug.getId() + "\n" +
                "Title: " + bug.getTitle() + "\n" +
                "Priority: " + bug.getPriority() + "\n" +
                "Status: " + bug.getStatus() + "\n\n" +
                "Please login to the Bug Tracker system for more details.\n\n" +
                "Thank you,\n" +
                "Bug Tracker Team"
            );
            mailSender.send(message);
            System.out.println("Email sent to: " + assignedTo.getEmail());
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendBugResolvedEmail(Bug bug, User reportedBy) {
        if (mailSender == null) {
            System.out.println("Email not configured. Skipping email to: " + reportedBy.getEmail());
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(reportedBy.getEmail());
            message.setSubject("Bug Resolved: #" + bug.getId() + " - " + bug.getTitle());
            message.setText(
                "Hello " + reportedBy.getFullName() + ",\n\n" +
                "The bug you reported has been resolved:\n\n" +
                "Bug ID: " + bug.getId() + "\n" +
                "Title: " + bug.getTitle() + "\n" +
                "Resolved Date: " + bug.getResolvedDate() + "\n\n" +
                "Please verify the fix.\n\n" +
                "Thank you,\n" +
                "Bug Tracker Team"
            );
            mailSender.send(message);
            System.out.println("Resolution email sent to: " + reportedBy.getEmail());
        } catch (Exception e) {
            System.out.println("Failed to send resolution email: " + e.getMessage());
        }
    }

    public void sendBugReportEmail(Bug bug) {
        if (mailSender == null) {
            System.out.println("Email not configured. Skipping report email");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("admin@bugtracker.com");
            message.setSubject("New Bug Reported: #" + bug.getId());
            message.setText(
                "A new bug has been reported:\n\n" +
                "Bug ID: " + bug.getId() + "\n" +
                "Title: " + bug.getTitle() + "\n" +
                "Description: " + bug.getDescription() + "\n" +
                "Priority: " + bug.getPriority() + "\n" +
                "Reported By: " + (bug.getReportedBy() != null ? bug.getReportedBy().getUsername() : "Unknown") + "\n\n" +
                "Please review and assign appropriately."
            );
            mailSender.send(message);
            System.out.println("Report email sent to admin");
        } catch (Exception e) {
            System.out.println("Failed to send report email: " + e.getMessage());
        }
    }

    // Complete the missing method
    public void sendBugAssignedByEmail(Bug bug, User assignedTo, User assignedBy) {
        if (mailSender == null) {
            System.out.println("Email not configured. Skipping assignment email to: " + assignedTo.getEmail());
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(assignedTo.getEmail());
            message.setSubject("Bug Assigned to You: #" + bug.getId() + " - " + bug.getTitle());
            
            String roleMessage = "";
            if (assignedBy.getRole().equals("DEVELOPER")) {
                roleMessage = "A Developer has assigned this bug to you for testing.";
            } else if (assignedBy.getRole().equals("TESTER")) {
                roleMessage = "A Tester has assigned this bug to you for fixing.";
            } else {
                roleMessage = "An Administrator has assigned this bug to you.";
            }
            
            message.setText(
                "Hello " + assignedTo.getFullName() + ",\n\n" +
                roleMessage + "\n\n" +
                "Bug Details:\n" +
                "ID: " + bug.getId() + "\n" +
                "Title: " + bug.getTitle() + "\n" +
                "Description: " + bug.getDescription() + "\n" +
                "Priority: " + bug.getPriority() + "\n" +
                "Status: " + bug.getStatus() + "\n\n" +
                "Assigned by: " + assignedBy.getFullName() + " (" + assignedBy.getRole() + ")\n" +
                "Assigned on: " + bug.getAssignedDate() + "\n\n" +
                "Please login to the Bug Tracker system to view and work on this bug.\n\n" +
                "Thank you,\n" +
                "Bug Tracker Team"
            );
            mailSender.send(message);
            System.out.println("Assignment email sent to: " + assignedTo.getEmail());
        } catch (Exception e) {
            System.out.println("Failed to send assignment email: " + e.getMessage());
        }
    }
}