package pl.volleyflow.club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.volleyflow.club.model.ClubDto;
import pl.volleyflow.club.service.ClubService;

import java.util.List;

@RestController
@RequestMapping("/admin/clubs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminClubController {

    private final ClubService clubService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClubDto>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

}
