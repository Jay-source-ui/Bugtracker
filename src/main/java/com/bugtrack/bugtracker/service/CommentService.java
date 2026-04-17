package com.bugtrack.bugtracker.service;

import com.bugtrack.bugtracker.model.Comment;
import com.bugtrack.bugtracker.model.Bug;
import com.bugtrack.bugtracker.model.User;
import com.bugtrack.bugtracker.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    public void addComment(Bug bug, User user, String content) {
        Comment comment = new Comment();
        comment.setBug(bug);
        comment.setUser(user);
        comment.setContent(content);
        commentRepository.save(comment);
    }
    
    public List<Comment> getCommentsByBugId(Long bugId) {
        return commentRepository.findByBugIdOrderByCreatedDateDesc(bugId);
    }
    
    public void deleteCommentsByBugId(Long bugId) {
        List<Comment> comments = commentRepository.findByBugIdOrderByCreatedDateDesc(bugId);
        commentRepository.deleteAll(comments);
    }
}