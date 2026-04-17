package com.bugtrack.bugtracker.service;

import com.bugtrack.bugtracker.model.Bug;
import com.bugtrack.bugtracker.model.User;
import com.bugtrack.bugtracker.repository.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BugService {
    
    @Autowired
    private BugRepository bugRepository;
    
    public List<Bug> getAllBugs() {
        return bugRepository.findAll();
    }
    
    public Optional<Bug> getBugById(Long id) {
        return bugRepository.findById(id);
    }
    
    public void saveBug(Bug bug) {
        bug.setCreatedDate(LocalDate.now());
        bugRepository.save(bug);
    }
    
    public void updateBug(Bug bug) {
        bugRepository.save(bug);
    }
    
    public void deleteBug(Long id) {
        bugRepository.deleteById(id);
    }
    
    public void resolveBug(Long id) {
        Bug bug = bugRepository.findById(id).orElse(null);
        if (bug != null) {
            bug.setStatus("RESOLVED");
            bug.setResolvedDate(LocalDate.now());
            bugRepository.save(bug);
        }
    }
    
    public List<Bug> getBugsByStatus(String status) {
        return bugRepository.findByStatus(status);
    }
    
    public List<Bug> searchBugs(String keyword) {
        return bugRepository.findByTitleContainingIgnoreCase(keyword);
    }

	public List<Bug> getBugsByReportedBy(User currentUser) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Bug> getBugsByAssignedTo(String username) {
		// TODO Auto-generated method stub
		return null;
	}
}