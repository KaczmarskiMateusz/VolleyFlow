package pl.volleyflow.clubmember.model;

import jakarta.persistence.*;
import lombok.*;
import pl.volleyflow.club.model.Club;
import pl.volleyflow.club.model.ClubRole;
import pl.volleyflow.clubmember.model.memberprofile.MemberProfile;
import pl.volleyflow.clubmember.model.memberprofile.MembershipStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "club_member",
        uniqueConstraints = {
                // one person only once per club
                @UniqueConstraint(
                        name = "uk_club_member_club_profile",
                        columnNames = {"club_id", "member_profile_id"}
                ),
                // jersey number unique per club (optional)
                @UniqueConstraint(
                        name = "uk_club_member_club_jersey_number",
                        columnNames = {"club_id", "jersey_number"}
                )
        },
        indexes = {
                @Index(name = "ix_club_member_external_id", columnList = "external_id"),
                @Index(name = "ix_club_member_club_id", columnList = "club_id"),
                @Index(name = "ix_club_member_profile_id", columnList = "member_profile_id"),
                @Index(name = "ix_club_member_status", columnList = "status"),
                @Index(name = "ix_club_member_role", columnList = "role")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "club_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_club_member_club")
    )
    @ToString.Exclude
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_profile_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_club_member_profile")
    )
    @ToString.Exclude
    private MemberProfile memberProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ClubRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MembershipStatus status;

    // Who created the membership/invitation (optional). Stored as user externalId to avoid hard coupling.
    @Column(name = "created_by_user_external_id")
    private UUID createdByUserExternalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private VolleyballPosition position;

    // Jersey number inside the club. Beware of null uniqueness semantics depending on DB.
    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    // True if this membership represents a player profile (vs coach, physio, etc.).
    @Column(name = "is_player", nullable = false)
    private boolean player;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "left_at")
    private Instant leftAt;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    public static ClubMember createOwner(Club club, MemberProfile profile) {
        ClubMember m = new ClubMember();
        m.setClub(club);
        m.setMemberProfile(profile);
        m.setRole(ClubRole.OWNER);
        m.setStatus(MembershipStatus.ACTIVE);
        m.setPlayer(true);
        m.setJoinedAt(Instant.now());
        m.setCreatedByUserExternalId(null);
        return m;
    }

    public void activate() {
        this.status = MembershipStatus.ACTIVE;
        if (this.joinedAt == null) this.joinedAt = Instant.now();
        this.leftAt = null;
    }

    public void leave() {
        this.status = MembershipStatus.LEFT;
        this.leftAt = Instant.now();
    }

    public void remove() {
        this.status = MembershipStatus.REMOVED;
        this.leftAt = Instant.now();
    }

    @PrePersist
    void prePersist() {
        if (externalId == null) externalId = UUID.randomUUID();
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;

        // safe defaults
        if (role == null) role = ClubRole.MEMBER;
        if (status == null) status = MembershipStatus.ACTIVE;
        if (status == MembershipStatus.ACTIVE && joinedAt == null) joinedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}