package pl.volleyflow.clubmember.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.volleyflow.club.model.ClubListView;
import pl.volleyflow.clubmember.model.ClubMember;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends CrudRepository<ClubMember, Long> {

    @Query(value = """
                select cm.*
                from app.club_member cm
                where cm.club_id = :clubId
                  and cm.user_account_id = :userId
                  and cm.status = :status
                limit 1
            """, nativeQuery = true)
    Optional<ClubMember> findByClubIdAndUserIdAndStatus(@Param("clubId") Long clubId,
                                                        @Param("userId") Long userId,
                                                        @Param("status") String status);

    @Query(value = """
                select exists(
                    select 1
                    from app.club_member cm
                    where cm.club_id = :clubId
                      and cm.invited_email = :email
                )
            """, nativeQuery = true)
    boolean existsByClubIdAndInvitedEmail(@Param("clubId") Long clubId,
                                          @Param("email") String email);

    @Query(value = """
                select exists(
                    select 1
                    from app.club_member cm
                    where cm.club_id = :clubId
                      and cm.user_account_id = :userId
                      and cm.status = 'ACTIVE'
                )
            """, nativeQuery = true)
    boolean isClubIdAndUserId(@Param("clubId") Long clubId,
                              @Param("userId") Long userId);

    @Query(value = """
                select
                  c.name as name,
                  c.created_at as createdAt,
                  c.logo_url as logoUrl,
                  cm.role as role
                from app.club_member cm
                join app.club c on cm.club_id = c.id
                where cm.status = 'ACTIVE'
                  and cm.user_account_id = :userIntId
            """, nativeQuery = true)
    List<ClubListView> findUserClubs(@Param("userIntId") Long userIntId);

    @Query(value = """
            select 1
            from app.app.club_member
            where user_account_id = :userId
            and role = :role
            and club_id = :clubId;
            """, nativeQuery = true)
    boolean findUserRoleInClub(@Param("clubId") Long clubId,
                               @Param("userId") Long userId,
                               @Param("role") String role);


}
