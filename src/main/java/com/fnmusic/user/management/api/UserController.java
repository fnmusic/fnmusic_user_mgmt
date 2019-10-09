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

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setRole(currentUser.getRole());
        auditLog.setEvent("UPDATE USER PROFILE");
        auditLog.setDescription("User profile was updated using a valid email address");
        auditLog.setAuditLogObject(user);
        auditLogPublisher.publish(auditLog);
    }

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('FOLLOW_USER')")
    public void follow(@RequestHeader("UserId") long userId, @RequestHeader("FanId") long fanId) {

        User currentUser = SystemUtils.getCurrentUser();
        if (fanId != currentUser.getId()) {
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

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(fanById.getData().getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("UNFOLLOW USER");
        auditLog.setDescription(fanById.getData().getEmail() + " stopped following " + userById.getData().getEmail());
        auditLog.setRole(fanById.getData().getRole());
        auditLogPublisher.publish(auditLog);
    }

    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> getFollowers(@RequestHeader("UserId") long id, @RequestHeader("PageNumber") int pageNumber, @RequestHeader("PageSize") int pageSize) {
        User currentUser = SystemUtils.getCurrentUser();
        if (id != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(id);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> followers = userService.getFollowers(id, pageNumber, pageSize);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("RETRIEVE FOLLOWERS");
        auditLog.setDescription(currentUser.getEmail() + " requested for their followers list");
        auditLog.setAuditLogObject(followers);
        auditLogPublisher.publish(auditLog);

        return followers;
    }

    @GetMapping( value = "/following", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> getFollowing(@RequestHeader("UserId") long id, @RequestHeader("PageNumber") int pageNumber, @RequestHeader("PageSize") int pageSize) {
        User currentUser = SystemUtils.getCurrentUser();
        Result<User> following = userService.getFollowing(id, pageNumber, pageSize);

        AuditLog auditLog = new AuditLog();

        Result<User> byId = userService.retrieveUserById(id);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("RETRIEVE FOLLOWING");
        auditLog.setDescription(byId.getData().getId() + " retrieved following list");
        auditLog.setRole(byId.getData().getRole());
        auditLog.setAuditLogObject(following);
        auditLogPublisher.publish(auditLog);

        return following;
    }

    @GetMapping( value = "/isfollower", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('READ_FAN_DATA')")
    public Result<User> isFollower(@RequestHeader("UserId") long userId, @RequestHeader("FanId") long fanId) {
        User currentUser = SystemUtils.getCurrentUser();
        if (fanId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        if (userId != currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(userId);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("FOLLOWER CHECK");
        auditLog.setDescription(currentUser.getEmail() + " requested to check if " + byId.getData().getEmail() + " is a follower");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(byId);
        auditLogPublisher.publish(auditLog);

        return byId;
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

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("FOLLOWING CHECK");
        auditLog.setDescription(currentUser.getEmail() + " requested to check if they are following " + byId.getData().getEmail());
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(byId);
        auditLogPublisher.publish(auditLog);

        return userService.isFollowing(userId,fanId);
    }

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
