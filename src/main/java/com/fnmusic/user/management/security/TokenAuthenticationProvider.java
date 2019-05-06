package com.fnmusic.user.management.security;

import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.service.SessionTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SessionTokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getPrincipal().toString();
        if (token == null || token.isEmpty()) {
            throw new BadRequestException("Invalid Token");
        }

        if (!tokenService.contains(token)) {
            throw new BadRequestException("Invalid or Expired Token");
        }

        return tokenService.retrieve(token);
    }

    @Override
    public boolean supports(Class<?> authentication) {
       return authentication.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
