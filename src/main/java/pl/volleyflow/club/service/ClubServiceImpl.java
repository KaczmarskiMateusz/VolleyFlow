package pl.volleyflow.club.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.volleyflow.club.model.*;
import pl.volleyflow.club.repository.ClubRepository;
import pl.volleyflow.clubmember.model.ClubMember;
import pl.volleyflow.clubmember.model.exceptions.ClubMemberForbiddenException;
import pl.volleyflow.clubmember.model.MembershipStatus;
import pl.volleyflow.clubmember.repository.ClubMemberRepository;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.repository.UserAccountRepository;
import pl.volleyflow.user.service.exceptions.UserNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final UserAccountRepository userAccountRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Transactional
    public ClubDto createClub(CreateClubRequest request, UUID userExternalId) {

        UserAccount user = getUserAccount(userExternalId);

        Club club = ClubMapper.fromCreateRequest(request);

        clubRepository.save(club);

        ClubMember ownerMembership = ClubMember.builder()
                .club(club)
                .user(user)
                .role(ClubRole.OWNER)
                .status(MembershipStatus.ACTIVE) // TODO SEND EMAIL WITH TOKEN TO ACTIVE ACCOUNT
                .build();

        clubMemberRepository.save(ownerMembership);

        return ClubMapper.toDto(club);
    }

    @Override
    @Transactional()
    public ClubDto getByExternalId(UUID clubId, UUID userExternalId) {
        UserAccount user = getUserAccount(userExternalId);

        ClubMember member = clubMemberRepository
                .findByClubExternalIdAndUserExternalId(clubId, user.getExternalId())
                .orElseThrow(() -> new ClubMemberForbiddenException("User is not a member of this club"));

        if (member.getStatus() != MembershipStatus.ACTIVE) {
            throw new ClubMemberForbiddenException("Membership is not active");
        }

        Club club = member.getClub();
        ClubDto dto = ClubMapper.toDto(club);

        return dto;
    }

    @Override
    public ClubDto updateClub(UUID clubId, UpdateClubRequest request, UUID userExternalId) {
        return null;
    }

    @Override
    public void deleteClub(UUID clubId, UUID userExternalId) {

    }

    @Override
    public List<ClubDto> getAllClubs() {
        return List.of();
    }

    private UserAccount getUserAccount(UUID userExternalId) {
        UserAccount user = userAccountRepository.findByExternalId(userExternalId)
                .orElseThrow(() -> new UserNotFoundException("User with externalID:" + userExternalId + " not found"));
        return user;
    }

}
