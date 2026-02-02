package pl.volleyflow.user.repository;

import org.springframework.data.repository.CrudRepository;
import pl.volleyflow.user.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmailAndExternalId(String email, UUID externalId);

    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserAccount> findByExternalId(UUID externalId);
}
