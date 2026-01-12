package pl.volleyflow.club;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
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
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @Setter
    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Setter
    @Column(name = "description", length = 2000)
    private String description;

    @Setter
    @Column(name = "city", length = 120)
    private String city;

    @Setter
    @Column(name = "logo_url", length = 2048)
    private String logoUrl;

    @Setter
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

    public boolean isActive() {
        return status == ClubStatus.ACTIVE;
    }

    public void disable() {
        this.status = ClubStatus.DISABLED;
    }

    public void archive() {
        this.status = ClubStatus.ARCHIVED;
    }

}