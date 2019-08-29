package com.fnmusic.user.management.api;

import com.fnmusic.base.utils.SystemUtils;
import com.fnmusic.user.management.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("rest/v1/fn/music/user/management/session")
public class SessionController {

    @Autowired
    private TokenService tokenService;

    @GetMapping(value = "/exists", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "status Up";
    }
}
