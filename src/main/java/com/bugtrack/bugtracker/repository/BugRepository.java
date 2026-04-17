package com.bugtrack.bugtracker.repository;

import com.bugtrack.bugtracker.model.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
    
    // Bug-related methods only
    List<Bug> findByStatus(String status);
    List<Bug> findByPriority(String priority);
    List<Bug> findByAssignedTo(String assignedTo);
    List<Bug> findByTitleContainingIgnoreCase(String title);
    List<Bug> findByStatusAndPriority(String status, String priority);
}