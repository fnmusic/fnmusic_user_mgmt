package com.fnmusic.user.management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnmusic.user.management.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TokenService {

    @Autowired
    private ObjectMapper om;

    @Value("${app.token.secretKey}")
    private String secretKey;
    @Value("${app.token.issuer}")
    private String issuer;
    @Value("${app.token.audience}")
    private String audience;

    public String generateUserAccessToken(User user){

        Map<String,Object> claims = new HashMap();
        claims.put("id",user.getId());
        claims.put("username",user.getUsername());
        claims.put("firstname",user.getFirstname());
        claims.put("lastname",user.getLastname());
        claims.put("email",user.getEmail());
        claims.put("gender",user.getGender());
        claims.put("role",user.getRole());
        claims.put("accountverificationstatus",user.getAccountVerificationStatus());

        //Generate Json Web Token
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(user.getEmail())
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setAudience(audience)
                .setExpiration(new Date(4))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public String generateLinkToken(int tokenLength) {
        final String CONSTVALUES = "abcdefghijklmnopqrstuvwxyz1234567890";
        char[] charArray = CONSTVALUES.toCharArray();

        String token = "";
        Random rnd = new Random();
        for (int i = 0; i < tokenLength; i++) {
            int index = rnd.nextInt(charArray.length - 1);
            token += charArray[index];
        }

        return token;
    }

}
