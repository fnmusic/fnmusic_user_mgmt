package com.fnmusic.user.management.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    @Value("${app.passwordhash.salt}")
    private String salt;

    public String encode(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte[] bytes = md.digest(salt.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte byt : bytes){
            sb.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}
