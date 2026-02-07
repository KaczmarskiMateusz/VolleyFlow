package pl.volleyflow.club.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.volleyflow.club.model.*;
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

        return isMember
                ? ClubMapper.toDto(club)
                : ClubMapper.toDtoWithoutMembers(club);
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

    @Override
    public List<ClubListView> getUserClubs(UUID uuid) {
        UserAccount user = getUserAccountOrThrow(uuid);
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
