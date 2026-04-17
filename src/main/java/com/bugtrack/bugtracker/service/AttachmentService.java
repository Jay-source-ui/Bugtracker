
package com.bugtrack.bugtracker.service;

import com.bugtrack.bugtracker.model.Attachment;
import com.bugtrack.bugtracker.model.Bug;
import com.bugtrack.bugtracker.model.User;
import com.bugtrack.bugtracker.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {
    
    @Autowired
    private AttachmentRepository attachmentRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public String saveAttachment(MultipartFile file, Bug bug, User user) throws IOException {
        // Create upload directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());
        
        // Save attachment info to database
        Attachment attachment = new Attachment();
        attachment.setFileName(originalFileName);
        attachment.setFileType(file.getContentType());
        attachment.setFilePath(uniqueFileName);
        attachment.setFileSize(file.getSize());
        attachment.setBug(bug);
        attachment.setUploadedBy(user);
        
        attachmentRepository.save(attachment);
        
        return uniqueFileName;
    }
    
    public List<Attachment> getAttachmentsByBugId(Long bugId) {
        return attachmentRepository.findByBugId(bugId);
    }
    
    public Attachment getAttachment(Long id) {
        return attachmentRepository.findById(id).orElse(null);
    }
    
    public Path getAttachmentPath(Attachment attachment) {
        return Paths.get(uploadDir).resolve(attachment.getFilePath());
    }
    
    public void deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id).orElse(null);
        if (attachment != null) {
            // Delete file from disk
            try {
                Path filePath = Paths.get(uploadDir).resolve(attachment.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Delete from database
            attachmentRepository.deleteById(id);
        }
    }
    
    public void deleteAllAttachmentsByBugId(Long bugId) {
        List<Attachment> attachments = attachmentRepository.findByBugId(bugId);
        for (Attachment attachment : attachments) {
            deleteAttachment(attachment.getId());
        }
    }
}