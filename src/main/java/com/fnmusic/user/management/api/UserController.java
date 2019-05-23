package com.fnmusic.user.management.api;

import com.fnmusic.base.Utils.SystemUtils;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.exception.NotFoundException;
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
    public Result<User> findByEmail(@PathVariable("email") String email) {

        User currentUser = SystemUtils.getCurrentUser();
        if (!email.equalsIgnoreCase(currentUser.getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("email cannot be empty");

        Result<User> byEmail = userService.findUserByEmail(email);
        if (byEmail.getData() == null)
            throw new NotFoundException("User not Found");

        return byEmail;
    }

    @GetMapping(value = "/findbyusername/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Result<User> findByUsername(@PathVariable("username") String username) {

        User currentUser = SystemUtils.getCurrentUser();

        Result<User> byUsername = userService.findUserByUsername(username);
        if (byUsername.getData() == null) {
            throw new BadRequestException("User not Found");
        }

        return byUsername;
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody User user) {

        if (user == null)
            throw new IllegalArgumentException("Request is invalid");

        Result<User> byId = userService.findUserById(user.getId());
        if (byId.getData() == null) {
            throw new BadRequestException("User not found");
        }

    }


}
