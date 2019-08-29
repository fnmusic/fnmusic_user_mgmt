package com.fnmusic.user.management.api.settings;

import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.base.utils.SystemUtils;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.services.HashService;
import com.fnmusic.user.management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("rest/v1/fn/music/user/management/settings/account")
public class AccountController {

    @Autowired
    private UserService userService;
    @Autowired
    private HashService hashService;

    /*
        Login & Security settings
     */

    @PutMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('')")
    public void UpdateUsername(@RequestHeader("Username") @NotEmpty String username) {

        User currentUser = SystemUtils.getCurrentUser();
        if (username.equalsIgnoreCase(currentUser.getUsername())) {
            throw new BadRequestException("Invalid Request");
        }

        userService.updateUsername(currentUser.getEmail(), username);
    }

    @PutMapping("/phone")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('')")
    public void UpdatePhone(@RequestHeader("Phone") @NotEmpty String phone) {

        User currentUser = SystemUtils.getCurrentUser();
        if (phone.equalsIgnoreCase(currentUser.getPhoneNumber())) {
            throw new BadRequestException("Invalid Request");
        }

        userService.updatePhone(currentUser.getEmail(),phone);
    }

    @PutMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('')")
    public void UpdateEmail(@RequestHeader("Email") @Email @NotEmpty String email) {

        User currentUser = SystemUtils.getCurrentUser();
        if (email.equalsIgnoreCase(currentUser.getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        userService.updateEmail(currentUser.getId(),email);
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('')")
    public void UpdatePassword(@RequestHeader("X-AUTH-OLD-PASSWORD") String currentPassword, @RequestHeader("X-AUTH-NEW-PASSWORD") String newPassword) throws NoSuchAlgorithmException {

        User currentUser = SystemUtils.getCurrentUser();
        String currentPasswordHash = hashService.encode(currentPassword);
        if (!currentPasswordHash.equalsIgnoreCase(currentUser.getPasswordHash())) {
            throw new BadRequestException("Invalid Request");
        }

        String newPasswordHash = hashService.encode(newPassword);
        if (newPasswordHash.equalsIgnoreCase(currentUser.getPasswordHash())) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> result = userService.getOldPasswords(SystemUtils.getCurrentUser().getId());
        List<User> oldPasswords = result.getList();
        if (!oldPasswords.isEmpty()) {
            for (User user : oldPasswords) {
                if (newPasswordHash.equalsIgnoreCase(user.getPasswordHash())) {
                    throw new BadRequestException("This password has been used before");
                }
            }
        }

        userService.updatePassword(currentUser.getEmail(),newPasswordHash);
    }

    @PutMapping("/twofactor")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('')")
    public void UpdateLoginVerificationStatus(@RequestHeader("Status") boolean status) {

        User currentUser = SystemUtils.getCurrentUser();
        userService.updateTwoFactor(currentUser.getEmail(),status);
    }

    @PutMapping("/passwordresetprotection")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('')")
    public void UpdatePasswordResetProtection(@RequestHeader("Status") boolean status) {

        User currentUser = SystemUtils.getCurrentUser();
        userService.updatePasswordResetProtection(currentUser.getEmail(),status);
    }

    /*
        Account settings
        Data & Permissions
     */

    @PutMapping("/country")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('')")
    public void UpdateCountry(@RequestHeader("Country") @NotEmpty String country) {

        User currentUser = SystemUtils.getCurrentUser();
        userService.updateNationality(currentUser.getEmail(),country);
    }

    @PostMapping("/deactivate")
    @ResponseStatus(HttpStatus.OK)
    //@PreAuthorize("hasAuthority('')")
    public void accountDeactivation(@RequestHeader("Status") boolean status) {

        User currentUser = SystemUtils.getCurrentUser();
        userService.updateActivationStatus(currentUser.getEmail(),status);
    }

}
