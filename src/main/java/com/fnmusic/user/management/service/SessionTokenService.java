package com.fnmusic.user.management.service;

import org.springframework.security.core.Authentication;

public class SessionTokenService {

    public boolean contains(String token) {
        return false;
    }

    public Authentication retrieve(String token) {
        return null;
    }
}
