package pl.volleyflow.authorization.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

public record UserPrincipal(
        UUID externalId,
        String email,
        Collection<? extends GrantedAuthority> authorities
) {}
