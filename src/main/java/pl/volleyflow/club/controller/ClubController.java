package pl.volleyflow.club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.volleyflow.authorization.model.UserPrincipal;
import pl.volleyflow.club.model.ClubDto;
import pl.volleyflow.club.model.ClubListDto;
import pl.volleyflow.club.model.CreateClubRequest;
import pl.volleyflow.club.model.UpdateClubRequest;
import pl.volleyflow.club.service.ClubService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping
    public ResponseEntity<ClubDto> createClub(@Valid @RequestBody CreateClubRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(clubService.createClub(request, principal.externalId()));
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<ClubDto> updateClub(@PathVariable UUID externalId,
                                              @Valid @RequestBody UpdateClubRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(clubService.updateClub(externalId, request, principal.externalId()));
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<ClubDto> getClub(@PathVariable UUID externalId,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(clubService.getByExternalId(externalId, principal.externalId()));
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> deleteClub(@PathVariable UUID externalId,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        clubService.deleteClub(externalId, principal.externalId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClubDto>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

}
