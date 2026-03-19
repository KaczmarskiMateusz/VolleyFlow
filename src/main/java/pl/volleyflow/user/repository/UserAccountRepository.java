package pl.volleyflow.user.repository;

import org.springframework.data.repository.CrudRepository;
import pl.volleyflow.user.model.UserAccount;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends CrudRepository<UserAccount, Long> {

    Optional<UserAccount> findByLoginEmailAndExternalId(String loginEmail, UUID externalId);

    Optional<UserAccount> findByLoginEmail(String loginEmail);

    Optional<UserAccount> findByLoginEmailAndDeletedFalse(String loginEmail);

    boolean existsByLoginEmail(String loginEmail);

    boolean existsByLoginEmailAndDeletedFalse(String loginEmail);

    boolean existsByExternalId(UUID externalId);

    Optional<UserAccount> findByExternalId(UUID externalId);

    Optional<UserAccount> findByExternalIdAndDeletedFalse(UUID externalId);
}
