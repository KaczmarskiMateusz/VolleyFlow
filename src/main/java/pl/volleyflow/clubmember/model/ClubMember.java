package pl.volleyflow.clubmember.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.volleyflow.club.model.ClubRole;
import pl.volleyflow.user.model.UserAccount;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "club_member",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_club_member_club_invited_email",
                        columnNames = {"club_id", "invited_email"}
                )
        },
        indexes = {
                @Index(name = "idx_club_member_club_id", columnList = "club_id"),
                @Index(name = "idx_club_member_user_id", columnList = "user_account_id"),
                @Index(name = "idx_club_member_invited_email", columnList = "invited_email"),
                @Index(name = "idx_club_member_status", columnList = "status")
        }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ClubMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "external_id", nullable = false, unique = true, updatable = false)
    private UUID externalId;

    @Column(name = "club_id", nullable = false)
    private Long clubId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id")
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ClubRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MembershipStatus status;

    @Column(name = "invited_email", length = 320)
    private String invitedEmail;

    @Column(name = "invite_token", length = 128, unique = true)
    private String inviteToken;

    @Column(name = "invite_expires_at")
    private Instant inviteExpiresAt;

    @Column(name = "invite_accepted_at")
    private Instant inviteAcceptedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by_user_account_id")
    private UserAccount invitedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @PrePersist
    void prePersist() {
        if (externalId == null) externalId = UUID.randomUUID();
    }

    public static ClubMember invite(Long clubId,
                                    ClubRole role,
                                    String invitedEmailNormalized,
                                    String inviteToken,
                                    Instant inviteExpiresAt,
                                    UserAccount invitedBy,
                                    UserAccount userOrNull) {

        return ClubMember.builder()
                .clubId(clubId)
                .role(role)
                .status(MembershipStatus.INVITED)
                .invitedEmail(invitedEmailNormalized)
                .inviteToken(inviteToken)
                .inviteExpiresAt(inviteExpiresAt)
                .invitedBy(invitedBy)
                .user(userOrNull)
                .build();
    }

    public void accept(UserAccount user) {
        this.user = user;
        this.status = MembershipStatus.ACTIVE;
        this.inviteAcceptedAt = Instant.now();
        this.joinedAt = Instant.now();
        this.inviteToken = null;
        this.inviteExpiresAt = null;
    }

    public static ClubMember createOwner(Long clubId, UserAccount user) {
        return ClubMember.builder()
                .clubId(clubId)
                .user(user)
                .role(ClubRole.OWNER)
                .status(MembershipStatus.ACTIVE)
                .joinedAt(Instant.now())
                .build();
    }


}
