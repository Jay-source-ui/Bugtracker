
package com.bugtrack.bugtracker.contoller;

import com.bugtrack.bugtracker.model.Attachment;
import com.bugtrack.bugtracker.model.Bug;
import com.bugtrack.bugtracker.model.User;
import com.bugtrack.bugtracker.service.AttachmentService;
import com.bugtrack.bugtracker.service.BugService;
import com.bugtrack.bugtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;

@Controller
public class AttachmentController {
    
    @Autowired
    private AttachmentService attachmentService;
    
    @Autowired
    private BugService bugService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/upload/{bugId}")
    public String uploadAttachment(@PathVariable Long bugId,
                                   @RequestParam("file") MultipartFile file,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        try {
            Bug bug = bugService.getBugById(bugId).orElse(null);
            User user = userService.findByUsername(userDetails.getUsername());
            
            if (bug != null && !file.isEmpty()) {
                attachmentService.saveAttachment(file, bug, user);
                redirectAttributes.addFlashAttribute("success", "File uploaded successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "File is empty or bug not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
        }
        
        return "redirect:/viewBug/" + bugId;
    }
    
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        try {
            Attachment attachment = attachmentService.getAttachment(id);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = attachmentService.getAttachmentPath(attachment);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(attachment.getFileType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> viewAttachment(@PathVariable Long id) {
        try {
            Attachment attachment = attachmentService.getAttachment(id);
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = attachmentService.getAttachmentPath(attachment);
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(attachment.getFileType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/deleteAttachment/{id}")
    public String deleteAttachment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Attachment attachment = attachmentService.getAttachment(id);
            Long bugId = attachment.getBug().getId();
            attachmentService.deleteAttachment(id);
            redirectAttributes.addFlashAttribute("success", "File deleted successfully!");
            return "redirect:/viewBug/" + bugId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete file!");
            return "redirect:/";
        }
    }
}