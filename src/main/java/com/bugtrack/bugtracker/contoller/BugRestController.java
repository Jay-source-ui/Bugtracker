
package com.bugtrack.bugtracker.contoller;

import com.bugtrack.bugtracker.model.Bug;
import com.bugtrack.bugtracker.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BugRestController {
    
    @Autowired
    private BugService bugService;
    
    @GetMapping("/bugs")
    public ResponseEntity<List<Bug>> getAllBugs() {
        return ResponseEntity.ok(bugService.getAllBugs());
    }
    
    @GetMapping("/bugs/{id}")
    public ResponseEntity<?> getBugById(@PathVariable Long id) {
        Optional<Bug> bug = bugService.getBugById(id);
        if (bug.isPresent()) {
            return ResponseEntity.ok(bug.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Bug not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @PostMapping("/bugs")
    public ResponseEntity<Bug> createBug(@RequestBody Bug bug) {
        bug.setCreatedDate(LocalDate.now());
        bug.setStatus("OPEN");
        bugService.saveBug(bug);
        return ResponseEntity.status(HttpStatus.CREATED).body(bug);
    }
    
    @DeleteMapping("/bugs/{id}")
    public ResponseEntity<?> deleteBug(@PathVariable Long id) {
        bugService.deleteBug(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Bug deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/bugs/{id}/resolve")
    public ResponseEntity<?> resolveBug(@PathVariable Long id) {
        bugService.resolveBug(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Bug resolved successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        List<Bug> bugs = bugService.getAllBugs();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBugs", bugs.size());
        stats.put("openBugs", bugs.stream().filter(b -> b.getStatus().equals("OPEN")).count());
        stats.put("resolvedBugs", bugs.stream().filter(b -> b.getStatus().equals("RESOLVED")).count());
        return ResponseEntity.ok(stats);
    }
}