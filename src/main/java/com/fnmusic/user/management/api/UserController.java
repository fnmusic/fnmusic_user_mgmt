package com.fnmusic.user.management.api;

import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.model.User;
import com.fnmusic.user.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "rest/v1/fn/music/user/management/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/findbyemail/{email}")
    @ResponseStatus(HttpStatus.OK)
    public User findByEmail(@PathVariable("email") String email) {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("email cannot be empty");

        User byEmail = userService.findUserByEmail(email);
        if (byEmail == null)
            throw new BadRequestException("User not Found");

        return byEmail;
    }

    @GetMapping(value = "/findbyusername/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public User findByUsername(@PathVariable("username") String username) {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException("username cannot be empty");

        User byUsername = userService.findUserByUsername(username);
        if (byUsername == null) {
            throw new BadRequestException("User not Found");
        }

        return byUsername;
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User user) {

        if (user == null)
            throw new IllegalArgumentException("Request is invalid");

        User byId = userService.findUserById(user.getId());


        return null;
    }


}
