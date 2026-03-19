package pl.volleyflow.club.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.volleyflow.club.model.ClubAuditLog;

@Repository
public interface ClubAuditLogRepository extends JpaRepository<ClubAuditLog, Long> {
}
