package com.bugtrack.bugtracker.contoller;

import com.bugtrack.bugtracker.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private BugService bugService;
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        var allBugs = bugService.getAllBugs();
        
        long totalBugs = allBugs.size();
        long openBugs = allBugs.stream()
            .filter(b -> b.getStatus().equals("OPEN"))
            .count();
        long inProgressBugs = allBugs.stream()
            .filter(b -> b.getStatus().equals("IN_PROGRESS"))
            .count();
        long resolvedBugs = allBugs.stream()
            .filter(b -> b.getStatus().equals("RESOLVED"))
            .count();
        long highPriorityBugs = allBugs.stream()
            .filter(b -> b.getPriority().equals("HIGH"))
            .count();
        long mediumPriorityBugs = allBugs.stream()
            .filter(b -> b.getPriority().equals("MEDIUM"))
            .count();
        long lowPriorityBugs = allBugs.stream()
            .filter(b -> b.getPriority().equals("LOW"))
            .count();
        
        model.addAttribute("totalBugs", totalBugs);
        model.addAttribute("openBugs", openBugs);
        model.addAttribute("inProgressBugs", inProgressBugs);
        model.addAttribute("resolvedBugs", resolvedBugs);
        model.addAttribute("highPriorityBugs", highPriorityBugs);
        model.addAttribute("mediumPriorityBugs", mediumPriorityBugs);
        model.addAttribute("lowPriorityBugs", lowPriorityBugs);
        
        return "dashboard";
    }
}