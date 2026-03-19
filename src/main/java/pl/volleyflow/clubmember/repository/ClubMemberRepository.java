package pl.volleyflow.clubmember.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pl.volleyflow.club.model.ClubListView;
import pl.volleyflow.clubmember.model.ClubMember;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends CrudRepository<ClubMember, Long> {

    @Query(value = """
                select cm.*
                from app.club_member cm
                join app.member_profile mp on mp.id = cm.member_profile_id
                join app.user_account ua on ua.member_profile_id = mp.id
                where cm.club_id = :clubId
                  and ua.id = :userId
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
                    join app.member_profile mp on mp.id = cm.member_profile_id
                    where cm.club_id = :clubId
                      and lower(mp.contact_email) = lower(:email)
                )
            """, nativeQuery = true)
    boolean existsByClubIdAndContactEmail(@Param("clubId") Long clubId,
                                          @Param("email") String email);

    @Query(value = """
                select exists(
                    select 1
                    from app.club_member cm
                    join app.member_profile mp on mp.id = cm.member_profile_id
                    join app.user_account ua on ua.member_profile_id = mp.id
                    where cm.club_id = :clubId
                      and ua.id = :userId
                      and cm.status = 'ACTIVE'
                )
            """, nativeQuery = true)
    boolean isUserInRole(@Param("clubId") Long clubId,
                         @Param("userId") Long userId);

    @Query(value = """
                select
                  c.external_id as externalId,
                  c.name as name,
                  c.created_at as createdAt,
                  c.logo_url as logoUrl,
                  cm.role as role
                from app.club_member cm
                join app.member_profile mp on mp.id = cm.member_profile_id
                join app.user_account ua on ua.member_profile_id = mp.id
                join app.club c on cm.club_id = c.id
                where cm.status = 'ACTIVE'
                  and ua.id = :userIntId
            """, nativeQuery = true)
    List<ClubListView> findUserClubs(@Param("userIntId") Long userIntId);

    @Query(value = """
            select exists(
                select 1
                from app.club_member cm
                join app.member_profile mp on mp.id = cm.member_profile_id
                join app.user_account ua on ua.member_profile_id = mp.id
                where ua.id = :userId
                  and cm.role = :role
                  and cm.club_id = :clubId
                  and cm.status = 'ACTIVE'
            )
            """, nativeQuery = true)
    boolean findUserRoleInClub(@Param("clubId") Long clubId,
                               @Param("userId") Long userId,
                               @Param("role") String role);

    @Query(value = """
            select exists(
                select 1
                from app.club_member cm
                where cm.club_id = :clubId
                  and cm.member_profile_id = :profileId
            )
            """, nativeQuery = true)
    boolean existsByClubIdAndProfileId(@Param("clubId") Long clubId,
                                       @Param("profileId") Long profileId);

}
