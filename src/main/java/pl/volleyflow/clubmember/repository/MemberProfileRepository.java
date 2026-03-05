package pl.volleyflow.clubmember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.volleyflow.clubmember.model.MemberProfile;

import java.util.Optional;
import java.util.UUID;

public interface MemberProfileRepository extends JpaRepository<MemberProfile, Long> {

    Optional<MemberProfile> findByExternalId(UUID externalId);

    Optional<MemberProfile> findByContactEmailIgnoreCase(String contactEmail);

}
