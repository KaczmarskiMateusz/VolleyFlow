package pl.volleyflow.club.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.volleyflow.authorization.model.UserPrincipal;
import pl.volleyflow.club.model.*;
import pl.volleyflow.club.service.ClubService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/app/clubs")
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

    @GetMapping("/my")
    public ResponseEntity<List<ClubListView>> getUserClubs(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(clubService.getUserClubs(principal.externalId()));
    }
    


}
