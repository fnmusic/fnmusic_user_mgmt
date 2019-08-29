package com.fnmusic.user.management.api;

import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.Role;
import com.fnmusic.base.models.User;
import com.fnmusic.base.utils.JsonMarshaller;
import com.fnmusic.base.utils.SystemUtils;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.messaging.MailPublisher;
import com.fnmusic.user.management.services.HashService;
import com.fnmusic.user.management.services.StorageService;
import com.fnmusic.user.management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import java.io.IOException;

@RestController
@RequestMapping(value = "rest/v1/fn/music/user/management/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private HashService hashService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private MailPublisher mailPublisher;

    @GetMapping( value = "/findbyemail/{Email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Result<User> findByEmail(@PathVariable("Email") @Email String email) {
        return userService.retrieveUserByEmail(email);
    }

    @GetMapping(value = "/findbyusername/{Username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Result<User> findByUsername(@PathVariable("Username") String username) {
        return userService.retrieveUserByUsername(username);
    }

    @PutMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('UPDATE_USER_DATA')")
    public void updateUser(@RequestPart("Request") String jsonObject, @Nullable @RequestPart("ProfilePhoto") MultipartFile profilePhoto, @Nullable @RequestPart("CoverPhoto") MultipartFile coverPhoto) throws IOException {

        User user = new JsonMarshaller<User>().unmarshall(jsonObject,User.class);
        if (user == null) {
            throw new BadRequestException("Invalid Request");
        }

        User currentUser = SystemUtils.getCurrentUser();
        if (!user.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        if (user.getUsername().isEmpty() || user.getUsername() == null) {
            throw new BadRequestException("Invalid Request");
        }

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String profilePhotoFileName = storageService.storeFile(profilePhoto,"profilephoto");
            user.setProfileImagePath(profilePhotoFileName);
        }

        if (coverPhoto != null && !coverPhoto.isEmpty()) {
            String coverPhotoFileName = storageService.storeFile(coverPhoto,"coverphoto");
            user.setCoverImagePath(coverPhotoFileName);
        }

        userService.update(user);
    }

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('FOLLOW_USER')")
    public void follow(@RequestHeader("UserId") long userId, @RequestHeader("FanId") long fanId) {

        User user = SystemUtils.getCurrentUser();
        if (fanId != user.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> userById = userService.retrieveUserById(userId);
        if (userById.getData() == null){
            throw new BadRequestException("Invalid Request");
        }

        Result<User> fanById = userService.retrieveUserById(fanId);
        if (fanById.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        if (userId == fanId) {
            throw new BadRequestException("Invalid Request");
        }

        userService.follow(userId,fanId);
    }

    @PostMapping("/unfollow")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('FOLLOW_USER')")
    public void unfollow(@RequestHeader("UserId") long userId, @RequestHeader("FanId") long fanId) {

        User user = SystemUtils.getCurrentUser();
        if (fanId != user.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> userById = userService.retrieveUserById(userId);
        if (userById.getData() == null){
            throw new BadRequestException("Invalid Request");
        }

        Result<User> fanById = userService.retrieveUserById(fanId);
        if (fanById.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        userService.unfollow(userId,fanId);
    }

    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> getFollowers(@RequestHeader("UserId") long id, @RequestHeader("PageNumber") int pageNumber, @RequestHeader("PageSize") int pageSize) {

        Result<User> byId = userService.retrieveUserById(id);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        return userService.getFollowers(id, pageNumber, pageSize);
    }

    @GetMapping( value = "/following", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> getFollowing(@RequestHeader("UserId") long id, @RequestHeader("PageNumber") int pageNumber, @RequestHeader("PageSize") int pageSize) {

        Result<User> byId = userService.retrieveUserById(id);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        return userService.getFollowing(id, pageNumber, pageSize);
    }

    @GetMapping( value = "/isfollower", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> isFollower(@RequestHeader("UserId") long userId, @RequestHeader("FanId") long fanId) {

        User currentUser = SystemUtils.getCurrentUser();
        if (userId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(userId);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        return userService.isFollower(userId,fanId);
    }

    @GetMapping(value = "/isfollowing", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> isFollowing(@RequestHeader("UserId") long userId, @RequestHeader("FanId") long fanId) {

        User currentUser = SystemUtils.getCurrentUser();
        if (fanId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(userId);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        return userService.isFollowing(userId,fanId);
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void logout() {

        String accessToken = SystemUtils.getAccessToken();
        userService.logout(accessToken);
    }


}
