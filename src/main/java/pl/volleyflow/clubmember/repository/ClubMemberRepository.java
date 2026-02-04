package pl.volleyflow.clubmember.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.volleyflow.clubmember.model.ClubMember;
import pl.volleyflow.clubmember.model.MembershipStatus;

import java.util.Optional;

public interface ClubMemberRepository extends CrudRepository<ClubMember, Long> {

    @Query("""
                select cm
                from ClubMember cm
                where cm.clubId = :clubId
                  and cm.user.id = :userId
                  and cm.status = :status
            """)
    Optional<ClubMember> findByClubIdAndUserIdAndStatus(@Param("clubId") Long clubId,
                                                        @Param("userId") Long userId,
                                                        @Param("status") MembershipStatus status);

    @Query("""
                select case when count(cm) > 0 then true else false end
                from ClubMember cm
                where cm.clubId = :clubId
                  and cm.invitedEmail = :email
            """)
    boolean existsByClubIdAndInvitedEmail(@Param("clubId") Long clubId,
                                          @Param("email") String email);

    @Query("""
                select case when count(cm) > 0 then true else false end
                from ClubMember cm
                where cm.clubId = :clubId
                  and cm.user.id = :userId
                  and cm.status = 'ACTIVE'
            """)
    boolean isClubIdAndUserId(@Param("clubId") Long clubId,
                              @Param("userId") Long userId);
}
