package pl.volleyflow.club.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.volleyflow.club.model.*;
import pl.volleyflow.club.model.exceptions.ClubNotFoundException;
import pl.volleyflow.club.model.exceptions.ClubPermissionException;
import pl.volleyflow.club.repository.ClubRepository;
import pl.volleyflow.clubmember.model.ClubMember;
import pl.volleyflow.clubmember.repository.ClubMemberRepository;
import pl.volleyflow.user.model.UserAccount;
import pl.volleyflow.user.repository.UserAccountRepository;
import pl.volleyflow.user.service.exceptions.UserNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final UserAccountRepository userAccountRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Transactional
    public ClubDto createClub(CreateClubRequest request, UUID userExternalId) {
        UserAccount user = getUserAccountOrThrow(userExternalId);
        Club club = ClubMapper.fromCreateRequest(request);

        clubRepository.save(club);

        ClubMember ownerMembership = ClubMember.createOwner(club.getId(), user);

        clubMemberRepository.save(ownerMembership);
        return ClubMapper.toDto(club);
    }

    @Override
    @Transactional(readOnly = true)
    public ClubDto getByExternalId(UUID clubExternalId, UUID userExternalId) {
        UserAccount user = getUserAccountOrThrow(userExternalId);

        Club club = getClubOrThrow(clubExternalId);

        boolean isMember = clubMemberRepository.isClubIdAndUserId(club.getId(), user.getId());

        log.info("User externalId={} is {} of club externalId={}",
                userExternalId, isMember ? "member" : "not member", clubExternalId);

        return isMember
                ? ClubMapper.toDto(club)
                : ClubMapper.toDtoWithoutMembers(club);
    }

    @Transactional
    @Override
    public ClubDto updateClub(UUID clubId, UpdateClubRequest request, UUID userExternalId) {
        UserAccount user = getUserAccountOrThrow(userExternalId);

        Club club = getClubOrThrow(clubId);

        if (!clubMemberRepository.findUserRoleInClub(club.getId(), user.getId(), ClubRole.OWNER.name())) {
            throw new ClubPermissionException("No permissions to update club");
        }

        ClubMapper.applyUpdate(club, request);

        Club saved = clubRepository.save(club);
        log.info("Updated club externalId={}, name={}", saved.getExternalId(), saved.getName());
        return ClubMapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deleteClub(UUID clubId, UUID userExternalId) {
        UserAccount user = getUserAccountOrThrow(userExternalId);
        Club club = getClubOrThrow(clubId);
        if (!clubMemberRepository.findUserRoleInClub(club.getId(), user.getId(), ClubRole.OWNER.name())) {
            throw new ClubPermissionException("No permissions to delete club");
        }
        clubRepository.delete(club);
        log.info("Deleted club externalId={}, name={}", club.getExternalId(), club.getName());
    }


    @Override
    @Transactional(readOnly = true)
    public List<ClubDto> getAllClubs() {
        return clubRepository.findAllClubs().stream()
                .map(ClubMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClubListView> getUserClubs(UUID uuid) {
        UserAccount user = getUserAccountOrThrow(uuid);
        log.info("Getting clubs for user externalId={}", uuid);
        return clubMemberRepository.findUserClubs(user.getId());
    }

    private UserAccount getUserAccountOrThrow(UUID userExternalId) {
        UserAccount user = userAccountRepository.findByExternalId(userExternalId)
                .orElseThrow(() -> new UserNotFoundException("User with externalID:" + userExternalId + " not found"));
        return user;
    }

    private Club getClubOrThrow(UUID clubExternalId) {
        return clubRepository.findByExternalId(clubExternalId)
                .orElseThrow(() -> new ClubNotFoundException("Club not found"));
    }

}
