package com.fnmusic.user.management.security;

import com.fnmusic.user.management.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getPrincipal().toString();
        if (token == null || token.isEmpty()) {
            throw new AccessDeniedException("Invalid Token");
        }

        if (!tokenService.contains(token)) {
            throw new AccessDeniedException("Invalid or Expired Token");
        }

        return tokenService.retrieve(token);
    }

    @Override
    public boolean supports(Class<?> authentication) {
       return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
