package pl.volleyflow.clubmember.model;

import jakarta.persistence.*;
import lombok.*;
import pl.volleyflow.user.model.UserAccount;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "member_profile",
        indexes = {
                @Index(name = "ix_member_profile_external_id", columnList = "external_id"),
                @Index(name = "ix_member_profile_contact_email", columnList = "contact_email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "weight_kg")
    private Integer weightKg;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @OneToOne(mappedBy = "memberProfile", fetch = FetchType.LAZY)
    @ToString.Exclude
    private UserAccount userAccount;

    @OneToMany(mappedBy = "memberProfile", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<ClubMember> memberships = new ArrayList<>();

    @Column(name = "display_name")
    private String displayName;

    @PrePersist
    void prePersist() {
        if (externalId == null) externalId = UUID.randomUUID();

        if (displayName == null) {
            String fn = firstName == null ? "" : firstName.trim();
            String ln = lastName == null ? "" : lastName.trim();
            String combined = (fn + " " + ln).trim();
            displayName = combined.isEmpty() ? null : combined;
        }

        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

}
