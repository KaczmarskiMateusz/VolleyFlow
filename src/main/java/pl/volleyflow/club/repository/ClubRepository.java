package pl.volleyflow.club.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.volleyflow.club.model.Club;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClubRepository extends CrudRepository<Club, Long> {

    @Query(value = """
        select c.*
        from app.club c
        where c.external_id = :externalId
        limit 1
    """, nativeQuery = true)
    Optional<Club> findByExternalId(@Param("externalId") UUID externalId);

    @Query(value = """
        select c.*
        from app.club c
        where c.name = :name
        limit 1
    """, nativeQuery = true)
    Optional<Club> findByName(@Param("name") String name);

    @Query(value = """
        select exists(
            select 1
            from app.club c
            where lower(c.name) = lower(:name)
        )
    """, nativeQuery = true)
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query(value = """
        select c.*
        from app.club c
        order by c.created_at desc
    """, nativeQuery = true)
    List<Club> findAllClubs();

}
