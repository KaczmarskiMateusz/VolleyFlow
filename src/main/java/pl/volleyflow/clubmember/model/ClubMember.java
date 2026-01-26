package pl.volleyflow.clubmember.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pl.volleyflow.club.model.Club;
import pl.volleyflow.club.model.ClubRole;
import pl.volleyflow.user.model.UserAccount;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "club_member",
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_club_member_club"))
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id",
            foreignKey = @ForeignKey(name = "fk_club_member_user"))
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
    @JoinColumn(name = "invited_by_user_account_id",
            foreignKey = @ForeignKey(name = "fk_club_member_invited_by_user"))
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

    public static ClubMember createInvited(Club club,
                                           ClubRole role,
                                           String invitedEmailNormalized,
                                           String inviteToken,
                                           Instant inviteExpiresAt,
                                           UserAccount invitedBy) {
        ClubMember cm = ClubMember.builder()
                .externalId(UUID.randomUUID())
                .club(club)
                .user(null)
                .role(role)
                .status(MembershipStatus.INVITED)
                .invitedEmail(invitedEmailNormalized)
                .inviteToken(inviteToken)
                .inviteExpiresAt(inviteExpiresAt)
                .invitedBy(invitedBy)
                .build();

        cm.validateState();
        return cm;
    }

    @Column(name = "joined_at")
    private Instant joinedAt;

    public void resendInvite(String newInviteToken, Instant newExpiresAt) {
        if (this.status != MembershipStatus.INVITED || this.user != null) {
            throw new IllegalStateException("Cannot resend invite for non-invited member.");
        }
        this.inviteToken = newInviteToken;
        this.inviteExpiresAt = newExpiresAt;
        this.validateState();
    }

    public void acceptInvite(UserAccount user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null.");
        if (this.status != MembershipStatus.INVITED || this.user != null) {
            throw new IllegalStateException("Invite cannot be accepted - invalid state.");
        }
        if (this.inviteExpiresAt != null && this.inviteExpiresAt.isBefore(Instant.now())) {
            throw new IllegalStateException("Invite token expired.");
        }

        this.user = user;
        this.status = MembershipStatus.ACTIVE;
        this.inviteAcceptedAt = Instant.now();
        this.inviteToken = null;
        this.inviteExpiresAt = null;

        this.validateState();
    }

    public void suspend() {
        if (this.status != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE member can be suspended.");
        }
        this.status = MembershipStatus.SUSPENDED;
    }

    public void activate() {
        if (this.status != MembershipStatus.SUSPENDED) {
            throw new IllegalStateException("Only SUSPENDED member can be activated.");
        }
        this.status = MembershipStatus.ACTIVE;
    }

    public void leave() {
        if (this.status == MembershipStatus.LEFT) return;
        if (this.status == MembershipStatus.INVITED && this.user == null) {
            this.status = MembershipStatus.LEFT;
            this.inviteToken = null;
            this.inviteExpiresAt = null;
            return;
        }
        this.status = MembershipStatus.LEFT;
    }

    @PrePersist
    void prePersist() {
        if (externalId == null) externalId = UUID.randomUUID();
        normalizeInviteEmail();
        if (status == null) status = (user == null ? MembershipStatus.INVITED : MembershipStatus.ACTIVE);
        validateState();
    }

    @PreUpdate
    void preUpdate() {
        normalizeInviteEmail();
        validateState();
    }

    private void normalizeInviteEmail() {
        if (invitedEmail != null) {
            invitedEmail = invitedEmail.trim().toLowerCase();
            if (invitedEmail.isBlank()) invitedEmail = null;
        }
    }

    private void validateState() {
        if (status == MembershipStatus.INVITED) {
            if (user != null) throw new IllegalStateException("INVITED member cannot have user assigned.");
            if (invitedEmail == null) throw new IllegalStateException("INVITED member must have invitedEmail.");
            if (inviteToken == null) throw new IllegalStateException("INVITED member must have inviteToken.");
        }

        if (user != null) {
            if (inviteToken != null) throw new IllegalStateException("Member with user cannot have inviteToken.");
        }

        if (user == null && status != MembershipStatus.INVITED) {
            throw new IllegalStateException("Member without user must be in INVITED status.");
        }
    }
}
