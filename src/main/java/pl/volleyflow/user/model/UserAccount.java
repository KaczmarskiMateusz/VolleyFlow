package pl.volleyflow.user.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "user_account",
        schema = "app",
        indexes = {
                @Index(name = "ix_user_account_external_id", columnList = "external_id"),
                @Index(name = "ix_user_account_email", columnList = "email")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"passwordHash", "globalRoles"})
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @Setter
    @Column(name = "first_name", length = 80)
    private String firstName;

    @Setter
    @Column(name = "last_name", length = 80)
    private String lastName;

    @Setter
    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Setter
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Setter
    @Column(name = "phone_number", unique = true, length = 32)
    private String phoneNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Setter
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Setter
    @Column(name = "display_name", length = 120)
    private String displayName;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "global_role", nullable = false, length = 40)
    private GlobalRole globalRole = GlobalRole.USER;

    @Setter
    @Column(name = "avatar_url", length = 2048)
    private String avatarUrl;


    @PrePersist
    void prePersist() {
        if (externalId == null) externalId = UUID.randomUUID();

        normalizeEmail();

        final Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;

        if (status == null) status = AccountStatus.ACTIVE;
    }

    @PreUpdate
    void preUpdate() {
        normalizeEmail();
        updatedAt = Instant.now();
    }

    private void normalizeEmail() {
        if (email != null) email = email.trim().toLowerCase();
    }

}
