package com.bugtrack.bugtracker.contoller;

import com.bugtrack.bugtracker.model.Bug;
import com.bugtrack.bugtracker.model.User;
import com.bugtrack.bugtracker.service.BugService;
import com.bugtrack.bugtracker.service.UserService;
import com.bugtrack.bugtracker.service.EmailService;
import com.bugtrack.bugtracker.service.AttachmentService;
import com.bugtrack.bugtracker.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class BugController {
    
    @Autowired
    private BugService bugService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Autowired
    private CommentService commentService;
    
    @GetMapping("/")
    public String viewHomePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("listBugs", bugService.getAllBugs());
        
        if (userDetails != null) {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("currentUserRole", currentUser.getRole());
            model.addAttribute("currentUserId", currentUser.getId());
            model.addAttribute("currentUsername", currentUser.getUsername());
        }
        
        return "index";
    }
    
    @GetMapping("/showNewBugForm")
    public String showNewBugForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Bug bug = new Bug();
        model.addAttribute("bug", bug);
        
        // Get current user
        User currentUser = userService.findByUsername(userDetails.getUsername());
        
        // Show users from opposite role based on current user's role
        List<User> assignableUsers;
        if (currentUser.getRole().equals("DEVELOPER")) {
            // Developer can assign to Testers
            assignableUsers = userService.getAllTesters();
        } else if (currentUser.getRole().equals("TESTER")) {
            // Tester can assign to Developers
            assignableUsers = userService.getAllDevelopers();
        } else {
            // ADMIN can assign to both Developers and Testers
            assignableUsers = userService.findAllDevelopersAndTesters();
        }
        
        model.addAttribute("assignableUsers", assignableUsers);
        model.addAttribute("currentUserRole", currentUser.getRole());
        
        return "new_bug";
    }
    
    @PostMapping("/saveBug")
    public String saveBug(@ModelAttribute("bug") Bug bug, 
                          @RequestParam(value = "assignedToId", required = false) Long assignedToId,
                          @RequestParam(value = "file", required = false) MultipartFile file,
                          @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.findByUsername(userDetails.getUsername());
        
        // Set reported by
        bug.setReportedBy(currentUser);
        bug.setCreatedDate(LocalDate.now());
        bug.setStatus("OPEN");
        
        // Set assigned to if selected
        if (assignedToId != null) {
            User assignedTo = userService.findById(assignedToId);
            bug.setAssignedTo(assignedTo.getUsername());
            bug.setAssignedBy(currentUser);
            bug.setAssignedDate(LocalDate.now());
            
            // Send email notification
            try {
                emailService.sendBugAssignedByEmail(bug, assignedTo, currentUser);
            } catch (Exception e) {
                System.out.println("Assignment email failed: " + e.getMessage());
            }
        }
        
        // Save bug first
        bugService.saveBug(bug);
        
        // Upload file if present
        if (file != null && !file.isEmpty()) {
            try {
                attachmentService.saveAttachment(file, bug, currentUser);
            } catch (IOException e) {
                System.out.println("File upload failed: " + e.getMessage());
            }
        }
        
        return "redirect:/";
    }
    
    @GetMapping("/showEditBugForm/{id}")
    public String showEditBugForm(@PathVariable(value = "id") long id, 
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Bug bug = bugService.getBugById(id).orElse(null);
        model.addAttribute("bug", bug);
        
        User currentUser = userService.findByUsername(userDetails.getUsername());
        
        // Get assignable users based on role for reassignment
        List<User> assignableUsers;
        if (currentUser.getRole().equals("DEVELOPER")) {
            assignableUsers = userService.getAllTesters();
        } else if (currentUser.getRole().equals("TESTER")) {
            assignableUsers = userService.getAllDevelopers();
        } else {
            assignableUsers = userService.findAllDevelopersAndTesters();
        }
        
        model.addAttribute("assignableUsers", assignableUsers);
        model.addAttribute("currentUserRole", currentUser.getRole());
        
        return "edit_bug";
    }
    
    @PostMapping("/updateBug/{id}")
    public String updateBug(@PathVariable Long id, 
                            @ModelAttribute("bug") Bug bug,
                            @RequestParam(value = "assignedToId", required = false) Long assignedToId,
                            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.findByUsername(userDetails.getUsername());
        
        // Update assignment if changed
        if (assignedToId != null) {
            User assignedTo = userService.findById(assignedToId);
            bug.setAssignedTo(assignedTo.getUsername());
            bug.setAssignedBy(currentUser);
            bug.setAssignedDate(LocalDate.now());
            
            // Send email notification for reassignment
            try {
                emailService.sendBugAssignedByEmail(bug, assignedTo, currentUser);
            } catch (Exception e) {
                System.out.println("Reassignment email failed: " + e.getMessage());
            }
        }
        
        bug.setId(id);
        bugService.updateBug(bug);
        return "redirect:/";
    }
    
    @GetMapping("/deleteBug/{id}")
    public String deleteBug(@PathVariable(value = "id") long id) {
        // Delete all attachments first
        attachmentService.deleteAllAttachmentsByBugId(id);
        // Then delete the bug
        bugService.deleteBug(id);
        return "redirect:/";
    }
    
    @GetMapping("/resolveBug/{id}")
    public String resolveBug(@PathVariable(value = "id") long id) {
        bugService.resolveBug(id);
        return "redirect:/";
    }
    
    @GetMapping("/filter/{status}")
    public String filterByStatus(@PathVariable String status, Model model) {
        model.addAttribute("listBugs", bugService.getBugsByStatus(status));
        return "index";
    }
    
    @GetMapping("/search")
    public String searchBugs(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword == null || keyword.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("listBugs", bugService.searchBugs(keyword));
        return "index";
    }
    
    // My Bugs - Show only bugs assigned to current user
    @GetMapping("/mybugs")
    public String showMyBugs(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        List<Bug> myBugs = bugService.getBugsByAssignedTo(currentUser.getUsername());
        model.addAttribute("listBugs", myBugs);
        model.addAttribute("currentUserRole", currentUser.getRole());
        return "index";
    }
    
    // Reported by me - Show bugs reported by current user
    @GetMapping("/reportedByMe")
    public String showReportedByMe(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        List<Bug> myReportedBugs = bugService.getBugsByReportedBy(currentUser);
        model.addAttribute("listBugs", myReportedBugs);
        model.addAttribute("currentUserRole", currentUser.getRole());
        return "index";
    }
    
    // View Bug Details with attachments and comments
    @GetMapping("/viewBug/{id}")
    public String viewBugDetails(@PathVariable Long id, Model model, 
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Bug bug = bugService.getBugById(id).orElse(null);
        model.addAttribute("bug", bug);
        model.addAttribute("comments", commentService.getCommentsByBugId(id));
        model.addAttribute("attachments", attachmentService.getAttachmentsByBugId(id));
        
        if (userDetails != null) {
            User currentUser = userService.findByUsername(userDetails.getUsername());
            model.addAttribute("currentUserRole", currentUser.getRole());
        }
        
        return "bug_details";
    }
    
    // Add comment to bug
    @PostMapping("/addComment/{id}")
    public String addComment(@PathVariable Long id, 
                             @RequestParam String content,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Bug bug = bugService.getBugById(id).orElse(null);
        User user = userService.findByUsername(userDetails.getUsername());
        commentService.addComment(bug, user, content);
        return "redirect:/viewBug/" + id;
    }
     
    @GetMapping("/api_docs")
    public String showApiDocs() {
        return "api_docs";
    }
}