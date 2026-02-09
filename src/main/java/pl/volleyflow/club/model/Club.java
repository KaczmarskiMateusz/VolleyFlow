package pl.volleyflow.club.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "club",
        schema = "app",
        indexes = {
                @Index(name = "ix_club_external_id", columnList = "external_id"),
                @Index(name = "ix_club_name", columnList = "name"),
                @Index(name = "ix_club_status", columnList = "status")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @Column(name = "name", nullable = false, length = 160)
    @Setter
    private String name;

    @Column(name = "description", length = 2000)
    @Setter
    private String description;

    @Column(name = "city", length = 120)
    @Setter
    private String city;

    @Column(name = "logo_url", length = 2048)
    @Setter
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ClubStatus status = ClubStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Integer version;

    @PrePersist
    void prePersist() {
        if (externalId == null) externalId = UUID.randomUUID();

        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;

        if (status == null) status = ClubStatus.ACTIVE;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public void activate() {
        if (status == ClubStatus.ARCHIVED) {
            throw new IllegalStateException("Archived club cannot be activated");
        }
        this.status = ClubStatus.ACTIVE;
    }

    public void disable() {
        if (status == ClubStatus.ARCHIVED) {
            throw new IllegalStateException("Archived club cannot be disabled");
        }
        this.status = ClubStatus.DISABLED;
    }

    public void archive() {
        this.status = ClubStatus.ARCHIVED;
    }

    public void markToConfirm() {
        if (status == ClubStatus.ARCHIVED) {
            throw new IllegalStateException("Archived club cannot be set to confirm");
        }
        this.status = ClubStatus.TO_CONFIRM;
    }

    public void changeStatus(ClubStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus");

        if (this.status == newStatus) return;

        if (this.status == ClubStatus.ARCHIVED) {
            throw new IllegalStateException("Archived club status cannot be changed");
        }

        switch (newStatus) {
            case ACTIVE -> activate();
            case DISABLED -> disable();
            case TO_CONFIRM -> markToConfirm();
            case ARCHIVED -> archive();
        }
    }

}
