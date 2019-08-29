package com.fnmusic.user.management.api;

import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.base.utils.SystemUtils;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.exception.NotFoundException;
import com.fnmusic.user.management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("rest/v1/fn/music/user/management/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/suspend")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('SUSPEND_USER')")
    public void suspend(@RequestHeader("UserId") long userId, @RequestHeader("ExpiryDate") @Nullable Date expiryDate) {

        User currentUser = SystemUtils.getCurrentUser();
        if (userId == currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(userId);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        if (expiryDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, 7);
            expiryDate = calendar.getTime();
        }

        userService.suspendUser(userId,expiryDate);
    }


    @PostMapping(value = "/unsuspend")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('SUSPEND_USER')")
    public void unsuspend(@RequestHeader("UserId") long userId) {

        User currentUser = SystemUtils.getCurrentUser();
        if (userId == currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(userId);
        if (byId.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        userService.unsuspendUser(userId);
    }

    @GetMapping(value = "/suspended", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('SUSPEND_USER')")
    public Result<User> getAllSuspendedUsers(@RequestHeader("PageNumber") int pageNumber, @RequestHeader("PageSize") int pageSize) {

        Result<User> result = userService.retrieveAllSuspendedUsers(pageNumber,pageSize);
        return result;
    }

    @PostMapping(value = "unlock", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('UNLOCK_USER')")
    public void unlock(@RequestHeader("UserId") long userId) {

        User currentUser = SystemUtils.getCurrentUser();
        if (userId == currentUser.getId()) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> byId = userService.retrieveUserById(userId);
        if (byId.getData() == null) {
            throw new NotFoundException("Invalid Request");
        }

        userService.unlockUserById(userId);
    }

    @GetMapping(value = "/locked", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('UNLOCK_USER')")
    public Result<User> getAllLockedUsers(@RequestHeader("PageNumber") int pageNumber, @RequestHeader("PageSize") int pageSize) {

        Result<User> result = userService.retrieveAllLockedUsers(pageNumber,pageSize);
        return result;
    }

}
