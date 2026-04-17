
package com.bugtrack.bugtracker.repository;

import com.bugtrack.bugtracker.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByBugId(Long bugId);
    void deleteByBugId(Long bugId);
}
