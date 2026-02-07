package pl.volleyflow.club.model;

import java.time.Instant;

public interface ClubListView {
    String getName();
    Instant getCreatedAt();
    String getLogoUrl();
    String getRole();
}
