package pl.volleyflow.clubmember.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.volleyflow.authorization.model.UserPrincipal;
import pl.volleyflow.clubmember.model.AddMemberRequest;
import pl.volleyflow.clubmember.model.MemberResponse;
import pl.volleyflow.clubmember.service.ClubMemberService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/app/clubs/{clubExternalId}/members")
@RequiredArgsConstructor
public class ClubMemberController {

    private final ClubMemberService clubMemberService;

    @PostMapping
    public ResponseEntity<MemberResponse> addMember(@PathVariable UUID clubExternalId,
                                                    @RequestBody @Valid AddMemberRequest req,
                                                    @AuthenticationPrincipal UserPrincipal principal) {
        MemberResponse created = clubMemberService.addOrInviteMember(clubExternalId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{memberExternalId}/resend-invitation")
    public ResponseEntity<Void> resendInvitation(@PathVariable UUID clubExternalId,
                                                 @PathVariable UUID memberExternalId,
                                                 @AuthenticationPrincipal UserPrincipal principal) {
        clubMemberService.resendInvitation(clubExternalId, memberExternalId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{memberExternalId}")
    public ResponseEntity<Void> removeMember(@PathVariable UUID clubExternalId,
                                             @PathVariable UUID memberExternalId,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        clubMemberService.removeMember(clubExternalId, memberExternalId);
        return ResponseEntity.noContent().build();
    }

}

