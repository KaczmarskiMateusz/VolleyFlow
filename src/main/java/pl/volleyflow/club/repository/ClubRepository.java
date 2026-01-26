package pl.volleyflow.club.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.volleyflow.club.model.Club;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {

    Optional<Club> findByExternalId(UUID externalId);

    Optional<Club> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

}
