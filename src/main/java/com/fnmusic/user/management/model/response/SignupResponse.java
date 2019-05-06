package com.fnmusic.user.management.model.response;

public class SignupResponse extends ServiceResponse {

    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
