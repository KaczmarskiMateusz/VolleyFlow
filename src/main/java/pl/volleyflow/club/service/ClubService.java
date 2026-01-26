package pl.volleyflow.club.service;


import pl.volleyflow.club.model.ClubDto;
import pl.volleyflow.club.model.CreateClubRequest;
import pl.volleyflow.club.model.UpdateClubRequest;

import java.util.List;
import java.util.UUID;

public interface ClubService {

    ClubDto createClub(CreateClubRequest request, UUID userExternalId);

    ClubDto getByExternalId(UUID clubId, UUID userExternalId);

    ClubDto updateClub(UUID clubId, UpdateClubRequest request, UUID userExternalId);

    void deleteClub(UUID clubId, UUID userExternalId);

    List<ClubDto> getAllClubs();
}

