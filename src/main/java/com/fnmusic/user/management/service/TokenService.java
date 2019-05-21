package com.fnmusic.user.management.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnmusic.base.models.User;
import com.fnmusic.user.management.redis.RedisCacheRepository;
import com.fnmusic.user.management.utils.Utils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class TokenService {

    @Autowired
    private ObjectMapper om;
    @Autowired
    private RedisCacheRepository redisCacheRepository;
    @Value("${app.token.secretKey}")
    private String secretKey;
    @Value("${app.token.issuer}")
    private String issuer;
    @Value("${app.token.audience}")
    private String audience;

    private Object tokenCache;
    private static Logger logger = LoggerFactory.getLogger(TokenService.class);

    @PostConstruct
    public void init() {
        this.tokenCache = redisCacheRepository.createCache(Utils.APPNAME,"tokenCache",7200000);
    }

    public TokenService() {
        logger.info("creating TokenCache");
    }

    public String generateUserSessionToken(User user){

        Map<String,Object> claims = new HashMap();
        claims.put("id",user.getId());
        claims.put("username",user.getUsername());
        claims.put("firstname",user.getFirstName());
        claims.put("lastname",user.getLastName());
        claims.put("email",user.getEmail());
        claims.put("role",user.getRole());
        claims.put("accountverificationstatus",user.isVerified());

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

    public String generateUserAccessToken(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(UUID.randomUUID().toString());
        }

        return sb.toString();
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


    public void store(String token, Object data) {
        redisCacheRepository.put(tokenCache, token, data);
    }

    public boolean contains(String token) {
        return redisCacheRepository.isPresent(tokenCache,token);
    }

    public Authentication retrieve(String token) {
        return (Authentication) redisCacheRepository.get(tokenCache,token);
    }

    public boolean remove(String key) {
        return redisCacheRepository.remove(tokenCache,key);
    }

}
