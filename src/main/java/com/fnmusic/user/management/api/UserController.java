package com.fnmusic.user.management.api;

import com.fnmusic.base.models.AuditLog;
import com.fnmusic.base.models.Notification;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.base.utils.AuditLogType;
import com.fnmusic.base.utils.JsonMarshaller;
import com.fnmusic.base.utils.NotificationType;
import com.fnmusic.base.utils.SystemUtils;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.messaging.AuditLogPublisher;
import com.fnmusic.user.management.messaging.MailPublisher;
import com.fnmusic.user.management.messaging.NotificationPublisher;
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
import java.util.ArrayList;
import java.util.List;

/**
 * The User Controller, basically handles all operations that invlove the use of the USER Object
 */
@RestController
@RequestMapping(value = "rest/v1/fn/music/user/management/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    HashService hashService;
    @Autowired
    StorageService storageService;
    @Autowired
    MailPublisher mailPublisher;
    @Autowired
    AuditLogPublisher auditLogPublisher;
    @Autowired
    NotificationPublisher notificationPublisher;

    /**
     * The method retrieves user information with the use of the user id
     * @param id The id of the user
     * @return Result<User> object containing the user data
     */
    @GetMapping(value = "/findbyid/{Id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Result<User> findById(@PathVariable("Id") long id) {
        User currentUser = SystemUtils.isAuthenticated() ? SystemUtils.getCurrentUser() : null;
        Result<User> userResult = userService.retrieveUserById(id);

        AuditLog auditLog = new AuditLog();
        if (currentUser != null) {
            auditLog.setUserId(currentUser.getId().toString());
            auditLog.setAuditLogType(AuditLogType.USER);
            auditLog.setRole(currentUser.getRole());
        }
        else {
            auditLog.setAuditLogType(AuditLogType.SYSTEM);
        }
        auditLog.setEvent("RETRIEVE USER DATA BY ID");
        auditLog.setDescription("User data was retrieved using valid ID");
        auditLog.setAuditLogObject(userResult);
        auditLogPublisher.publish(auditLog);

        return userResult;
    }

    /**
     * The method retrieves user information with the use of an email
     * @param email The email of the user
     * @return Result<User> object containing the user data
     */
    @GetMapping(value = "/findbyemail/{Email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Result<User> findByEmail(@PathVariable("Email") @Email String email) {
        User currentUser = SystemUtils.isAuthenticated() ? SystemUtils.getCurrentUser() : null;
        Result<User> userResult = userService.retrieveUserByEmail(email);

        AuditLog auditLog = new AuditLog();
        if (currentUser != null) {
            auditLog.setUserId(currentUser.getId().toString());
            auditLog.setAuditLogType(AuditLogType.USER);
            auditLog.setRole(currentUser.getRole());
        }
        else {
            auditLog.setAuditLogType(AuditLogType.SYSTEM);
        }
        auditLog.setEvent("RETRIEVE USER DATA BY EMAIL");
        auditLog.setDescription("User data was retrieved using valid email address");
        auditLog.setAuditLogObject(userResult);
        auditLogPublisher.publish(auditLog);

        return userResult;
    }

    /**
     * The method retrieves user information with the use of a username
     * @param username The username of the user
     * @return Result<User> object containing the user data
     */
    @GetMapping(value = "/findbyusername/{Username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Result<User> findByUsername(@PathVariable("Username") String username) {
        User currentUser = SystemUtils.isAuthenticated() ? SystemUtils.getCurrentUser() : null;
        Result<User> userResult = userService.retrieveUserByUsername(username);

        AuditLog auditLog = new AuditLog();
        if (currentUser != null) {
            auditLog.setUserId(currentUser.getId().toString());
            auditLog.setAuditLogType(AuditLogType.USER);
            auditLog.setRole(currentUser.getRole());
        }
        else {
            auditLog.setAuditLogType(AuditLogType.SYSTEM);
        }
        auditLog.setEvent("RETRIEVE USER DATA BY USERNAME");
        auditLog.setDescription("User data was retrieved using valid username");
        auditLog.setAuditLogObject(userResult);
        auditLogPublisher.publish(auditLog);

        return userResult;
    }

    /**
     * The method retrieves user information with the use of a phone
     * @param phone The phone of the user
     * @return Result<User> object containing the user data
     */
    @GetMapping(value = "/findbyphone/{Phone}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Result<User> findByPhone(@PathVariable("Phone") String phone) {
        User currentUser = SystemUtils.isAuthenticated() ? SystemUtils.getCurrentUser() : null;
        Result userResult = userService.retrieveUserByPhone(phone);

        AuditLog auditLog = new AuditLog();
        if (currentUser != null) {
            auditLog.setUserId(currentUser.getId().toString());
            auditLog.setAuditLogType(AuditLogType.USER);
            auditLog.setRole(currentUser.getRole());
        }
        else {
            auditLog.setAuditLogType(AuditLogType.SYSTEM);
        }
        auditLog.setEvent("RETRIEVE USER DATA BY USERNAME");
        auditLog.setDescription("User data was retrieved using valid username");
        auditLog.setAuditLogObject(userResult);
        auditLogPublisher.publish(auditLog);

        return userResult;
    }

    /**
     * This method updates the user profile data
     * @param jsonObject A json object representing user data to be updated
     * @param profilePhoto Profile Photo file of the user
     * @param coverPhoto Cover Photo file of the user
     * @throws IOException
     */
    @PutMapping(value = "/update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('UPDATE_USER_DATA')")
    public void updateUser(
            @RequestPart("Request") String jsonObject,
            @Nullable @RequestPart("ProfilePhoto") MultipartFile profilePhoto,
            @Nullable @RequestPart("CoverPhoto") MultipartFile coverPhoto) throws IOException {

        User user = new JsonMarshaller<User>().unmarshall(jsonObject,User.class);
        if (user == null) {
            throw new BadRequestException("Invalid Request");
        }

        User currentUser = SystemUtils.getCurrentUser();
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

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setRole(currentUser.getRole());
        auditLog.setEvent("UPDATE USER PROFILE");
        auditLog.setDescription("User profile was updated using a valid email address");
        auditLog.setAuditLogObject(user);
        auditLogPublisher.publish(auditLog);
    }

    /**
     * This method enables a user to follow a user account
     * @param userId The id of the user account to follow
     * @param followerId The id of the user account requesting to follow
     */
    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('FOLLOW_USER')")
    public void followUser(@RequestHeader("UserId") long userId, @RequestHeader("FollowerId") long followerId) {

        User currentUser = SystemUtils.getCurrentUser();
        if (followerId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> userById = userService.retrieveUserById(userId);
        if (userById.getData() == null){
            throw new BadRequestException("Invalid Request");
        }

        Result<User> fanById = userService.retrieveUserById(followerId);
        if (fanById.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        if (userId == followerId) {
            throw new BadRequestException("Invalid Request");
        }

        userService.follow(userId,followerId);

        List<User> followers = new ArrayList<>();
        followers.add(currentUser);

        Notification notification = new Notification();
        notification.setUserId(currentUser.getId().toString());
        notification.setUserId(String.valueOf(userId));
        notification.setNotificationType(NotificationType.FOLLOW);
        notification.setNotificationObject(followers);
        notificationPublisher.publish(notification);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("FOLLOW USER");
        auditLog.setDescription(currentUser.getEmail() + " followed " + userById.getData().getEmail());
        auditLog.setRole(currentUser.getRole());
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param userId
     * @param followerId
     */
    @PostMapping("/unfollow")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('FOLLOW_USER')")
    public void unfollowUser(@RequestHeader("UserId") long userId, @RequestHeader("FollowerId") long followerId) {

        User user = SystemUtils.getCurrentUser();
        if (followerId != user.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> userById = userService.retrieveUserById(userId);
        if (userById.getData() == null){
            throw new BadRequestException("Invalid Request");
        }

        Result<User> fanById = userService.retrieveUserById(followerId);
        if (fanById.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        userService.unfollow(userId,followerId);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(fanById.getData().getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("UNFOLLOW USER");
        auditLog.setDescription(fanById.getData().getEmail() + " stopped following " + userById.getData().getEmail());
        auditLog.setRole(fanById.getData().getRole());
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param userId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> getFollowers(@RequestHeader("UserId") long userId, @RequestHeader("PageNumber") int pageNumber, @RequestHeader("PageSize") int pageSize) {
        User currentUser = SystemUtils.getCurrentUser();
        if (userId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(userId);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> followers = userService.getFollowers(userId, pageNumber, pageSize);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("RETRIEVE FOLLOWERS");
        auditLog.setDescription(currentUser.getEmail() + " requested for their followers list");
        auditLog.setAuditLogObject(followers);
        auditLogPublisher.publish(auditLog);

        return followers;
    }

    /**
     *
     * @param userId
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping( value = "/following", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> getFollowing(@RequestHeader("UserId") long userId, @RequestHeader("PageNumber") int pageNumber, @RequestHeader("PageSize") int pageSize) {
        User currentUser = SystemUtils.getCurrentUser();
        if (userId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(userId);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> following = userService.getFollowing(userId, pageNumber, pageSize);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("RETRIEVE FOLLOWING");
        auditLog.setDescription(byId.getData().getId() + " retrieved following list");
        auditLog.setRole(byId.getData().getRole());
        auditLog.setAuditLogObject(following);
        auditLogPublisher.publish(auditLog);

        return following;
    }

    /**
     * To check if the profile account is a follower of the current user account
     * @param userId The Id of the current user account
     * @param followerId The Id of the profile user account
     * @return
     */
    @GetMapping( value = "/isfollower", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> isFollower(@RequestHeader("UserId") long userId, @RequestHeader("FollowerId") long followerId) {
        User currentUser = SystemUtils.getCurrentUser();
        if (userId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        if (userId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byFanId = userService.retrieveUserById(followerId);
        if (byFanId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> isfollowerResult = userService.isFollower(userId,followerId);
        if (!isfollowerResult.getData().getId().equals(followerId)) {
            throw new BadRequestException("Invalid Request");
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("FOLLOWER CHECK");
        auditLog.setDescription(currentUser.getEmail() + " requested to check if " + byFanId.getData().getEmail() + " is a follower");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(isfollowerResult);
        auditLogPublisher.publish(auditLog);

        return isfollowerResult;
    }

    /**
     * To check if the current user account is a follower of the profile account
     * @param userId The id of the profile user account
     * @param followerId The id of the current user
     * @return the profile account data
     */
    @GetMapping(value = "/isfollowing", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> isFollowing(@RequestHeader("UserId") long userId, @RequestHeader("FollowerId") long followerId) {
        User currentUser = SystemUtils.getCurrentUser();
        if (followerId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byUserId = userService.retrieveUserById(userId);
        if (byUserId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> isfollowingResult = userService.isFollowing(userId,followerId);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("FOLLOWING CHECK");
        auditLog.setDescription(currentUser.getEmail() + " requested to check if they are following " + byUserId.getData().getEmail());
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(isfollowingResult);
        auditLogPublisher.publish(auditLog);

        return isfollowingResult;
    }

    /**
     *
     */
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void logout() {
        User currentUser = SystemUtils.getCurrentUser();
        String accessToken = SystemUtils.getAccessToken();
        userService.logout(accessToken);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("LOG OUT");
        auditLog.setDescription(currentUser.getEmail() + " has successfully logged out");
        auditLog.setRole(currentUser.getRole());
        auditLogPublisher.publish(auditLog);
    }


}
