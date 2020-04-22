package com.fnmusic.user.management.api.settings;

import com.fnmusic.base.models.*;
import com.fnmusic.base.utils.AuditLogType;
import com.fnmusic.base.utils.CharacterType;
import com.fnmusic.base.utils.RandomGeneratorUtils;
import com.fnmusic.base.utils.SystemUtils;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.messaging.AuditLogPublisher;
import com.fnmusic.user.management.messaging.MailPublisher;
import com.fnmusic.user.management.messaging.SMSPublisher;
import com.fnmusic.user.management.models.Auth;
import com.fnmusic.user.management.services.AuthService;
import com.fnmusic.user.management.services.HashService;
import com.fnmusic.user.management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("rest/v1/fn/music/user/management/settings/account")
public class AccountController {

    @Autowired
    AuthService authService;
    @Autowired
    UserService userService;
    @Autowired
    HashService hashService;
    @Autowired
    SMSPublisher smsPublisher;
    @Autowired
    MailPublisher mailPublisher;
    @Autowired
    AuditLogPublisher auditLogPublisher;

    /*
        Login & Security Settings
     */

    /**
     *
     * @param username
     */
    @PutMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    public void UpdateUsername(@RequestHeader("Username") @NotEmpty String username) {
        User currentUser = SystemUtils.getCurrentUser();

        if (username.isEmpty() || username == null) {
            throw new BadRequestException("Invalid Request");
        }

        if (username.equalsIgnoreCase(currentUser.getUsername())) {
            throw new BadRequestException("Invalid Request");
        }

        userService.updateUsername(currentUser.getId(), username);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("USERNAME UPDATE");
        auditLog.setDescription("User account with ID: " + currentUser.getId() + " updated their username");
        auditLog.setAuditLogObject(username);
        auditLog.setRole(currentUser.getRole());
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param phone
     */
    @PutMapping("/phone")
    @ResponseStatus(HttpStatus.OK)
    public void UpdatePhone(@RequestHeader("Phone") @NotEmpty String phone) {
        User currentUser = SystemUtils.getCurrentUser();
        if (phone.equalsIgnoreCase(currentUser.getPhone())) {
            throw new BadRequestException("Invalid Request");
        }

        userService.updatePhone(currentUser.getId(),phone);
        userService.updatePhoneConfirmed(currentUser.getId(),false);
        sendPhoneVerificationToken(phone);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("PHONE UPDATE");
        auditLog.setDescription("User account with ID: " + currentUser.getId() + "updated their phone");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(phone);
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param phone
     */
    @PostMapping("/phone/token")
    @ResponseStatus(HttpStatus.OK)
    public void sendPhoneVerificationToken(@RequestHeader("Phone") String phone) {
        User currentUser = SystemUtils.getCurrentUser();
        if (!phone.equalsIgnoreCase(currentUser.getPhone())) {
            throw new BadRequestException("Invalid Request");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        //Generate Phone Verification Token
        Auth auth = new Auth();
        auth.setPhone(phone);
        auth.setToken(RandomGeneratorUtils.generateCode(CharacterType.NUMERIC,6));
        auth.setExpiryDate(calendar.getTime());
        authService.submitPhoneVerificationToken(auth);

        SMS sms = new SMS();
        sms.setUserId(currentUser.getId().toString());
        sms.setMessage("Your verification Code is " + auth.getToken());
        sms.setRecipient(phone);
        smsPublisher.publish(sms);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("PHONE UPDATE VERIFICATION TOKEN REQUEST");
        auditLog.setDescription("A Phone Verification Token was sent to " + phone);
        auditLog.setAuditLogObject(auth);
        auditLog.setRole(currentUser.getRole());
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param phone
     * @param token
     */
    @PutMapping("/phone/verify")
    @ResponseStatus(HttpStatus.OK)
    public void UpdatePhoneVerification(@RequestHeader("Phone") @NotEmpty String phone, @RequestHeader("Token") @NotEmpty String token) {
        User currentUser = SystemUtils.getCurrentUser();
        if (!phone.equalsIgnoreCase(currentUser.getPhone())) {
            throw new BadRequestException("Invalid Request");
        }

        Result<Auth> result = authService.retrievePhoneVerificationToken(phone);
        Auth auth = result.getData();
        if (!token.equalsIgnoreCase(auth.getToken())) {
            throw new BadRequestException("Incorrect Token");
        }

        userService.updatePhoneConfirmed(currentUser.getId(),true);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("PHONE UPDATE CONFIRMATION");
        auditLog.setDescription("User account with ID: " + currentUser.getId() + "confirmed their phone");
        auditLog.setAuditLogObject(auth);
        auditLog.setRole(currentUser.getRole());
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param email
     */
    @PutMapping("/email")
    @ResponseStatus(HttpStatus.OK)
    public void UpdateEmail(@RequestHeader("Email") @Email @NotEmpty String email) {
        User currentUser = SystemUtils.getCurrentUser();
        if (email.equalsIgnoreCase(currentUser.getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        userService.updateEmail(currentUser.getId(),email);
        userService.updateEmailConfirmed(currentUser.getId(),false);
        sendEmailVerificationToken(email);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("EMAIL UPDATE");
        auditLog.setDescription("User account with ID: " + currentUser.getId() + "updated their email");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(email);
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param email
     */
    @PostMapping("/email/token")
    @ResponseStatus(HttpStatus.OK)
    public void sendEmailVerificationToken(@RequestHeader("Email") String email) {
        User currentUser = SystemUtils.getCurrentUser();
        if (!email.equalsIgnoreCase(currentUser.getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setToken(RandomGeneratorUtils.generateCode(CharacterType.NUMERIC,6));
        auth.setExpiryDate(calendar.getTime());
        authService.submitEmailVerificationToken(auth);

        Mail mail = new Mail();
        mail.setUserId(currentUser.getId());
        mail.setTo(new String[]{currentUser.getEmail()});
        mail.setSubject("EMAIL VERIFICATION");
        mail.setText("Your verification code is " + auth.getToken());
        mailPublisher.publish(mail);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("EMAIL VERIFICATION TOKEN REQUEST");
        auditLog.setDescription(currentUser.getEmail() + " requested for an email verification token");
        auditLog.setAuditLogObject(auth);
        auditLog.setRole(currentUser.getRole());
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param email
     * @param token
     */
    @PutMapping("/email/verify")
    @ResponseStatus(HttpStatus.OK)
    public void UpdateEmailVerification(@RequestHeader("Email") @Email String email,
                                        @RequestHeader("Token") @NotEmpty String token) {
        User currentUser = SystemUtils.getCurrentUser();
        if (!email.equalsIgnoreCase(currentUser.getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        Result<Auth> result = authService.retrieveEmailVerificationToken(email);
        Auth auth = result.getData();
        if (!token.equalsIgnoreCase(auth.getToken())) {
            throw new BadRequestException("Incorrect Token");
        }

        userService.updateEmailConfirmed(currentUser.getId(),true);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("EMAIL UPDATE CONFIRMATION");
        auditLog.setDescription("User account with ID: " + currentUser.getId() + "confirmed their email");
        auditLog.setAuditLogObject(auth);
        auditLog.setRole(currentUser.getRole());
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param phone
     */
    @PostMapping("/twofactor/token")
    @ResponseStatus(HttpStatus.OK)
    public void sendTwoFactorVerificationToken(@RequestHeader("Phone") String phone) {
        User currentUser = SystemUtils.getCurrentUser();
        if (!phone.equalsIgnoreCase(currentUser.getPhone())) {
            throw new BadRequestException("Invalid Request");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        Auth auth = new Auth();
        auth.setPhone(phone);
        auth.setToken(RandomGeneratorUtils.generateCode(CharacterType.NUMERIC,6));
        auth.setExpiryDate(calendar.getTime());

        authService.submitTwoFactorVerificationToken(auth);

        SMS sms = new SMS();
        sms.setUserId(currentUser.getId().toString());
        sms.setMessage("Your verification Code is " + auth.getToken());
        sms.setRecipient(currentUser.getPhone());
        smsPublisher.publish(sms);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("TWO-FACTOR VERFICATION TOKEN REQUEST");
        auditLog.setDescription("User account with ID: " + currentUser.getId() + "requested for a Two-factor verification token");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(auth);
        auditLogPublisher.publish(auditLog);
    }

    /**
     *
     * @param phone
     * @param token
     * @param status
     */
    @PutMapping("/twofactor")
    @ResponseStatus(HttpStatus.OK)
    public void UpdateTwoFactor(@RequestHeader("Phone") String phone,
                                @RequestHeader("Token") String token,
                                @RequestHeader("Status") boolean status) {
        User currentUser = SystemUtils.getCurrentUser();
        if (!phone.equalsIgnoreCase(currentUser.getPhone())) {
            throw new BadRequestException("Invalid Request");
        }

        Result<Auth> result = authService.retrieveTwoFactorVerificationToken(currentUser.getPhone());
        Auth auth = result.getData();
        if (!token.equalsIgnoreCase(auth.getToken())) {
            throw new BadRequestException("Invalid Token");
        }

        userService.updateTwoFactor(currentUser.getId(),status);

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("UPDATE TWO-FACTOR");
        auditLog.setDescription("User account with ID: " + currentUser.getId() + " updated their Two-factor enabled status");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(status);
        auditLogPublisher.publish(auditLog);
    }



    @PutMapping("/password")
    @ResponseStatus(HttpStatus.OK)
    public void UpdatePassword(@RequestHeader("X-AUTH-OLD-PASSWORD") String currentPassword,
                               @RequestHeader("X-AUTH-NEW-PASSWORD") String newPassword) throws NoSuchAlgorithmException {
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

        userService.updatePassword(currentUser.getId(),newPasswordHash);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("UPDATE PASSWORD");
        auditLog.setDescription(currentUser.getEmail() + " updated their password");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(newPasswordHash);
        auditLogPublisher.publish(auditLog);
    }

    @PostMapping("/password/verify")
    @ResponseStatus(HttpStatus.OK)
    public void VerifyPassword(@RequestHeader("X-AUTH-PASSWORD") String password) throws NoSuchAlgorithmException {
        User currentUser = SystemUtils.getCurrentUser();
        String hashedPassword = hashService.encode(password);
        if (!hashedPassword.equalsIgnoreCase(currentUser.getPasswordHash())) {
            throw new BadRequestException("Invalid Request");
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("PASSWORD VERIFICATION");
        auditLog.setDescription("User account with ID: " + currentUser.getId() + " verified their password");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(hashedPassword);
        auditLogPublisher.publish(auditLog);
    }

    @PutMapping("/passwordresetprotection")
    @ResponseStatus(HttpStatus.OK)
    public void UpdatePasswordResetProtection(@RequestHeader("Status") boolean status) {
        User currentUser = SystemUtils.getCurrentUser();
        userService.updatePasswordResetProtection(currentUser.getId(),status);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("UPDATE PASSWORD RESET PROTECTION");
        auditLog.setDescription(currentUser.getEmail() + " updated their Password Reset Protection Status");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(status);
        auditLogPublisher.publish(auditLog);
    }

    /*
        Account Settings
        Data & Permissions
     */

    @PutMapping("/country")
    @ResponseStatus(HttpStatus.OK)
    public void UpdateCountry(@RequestHeader("Country") @NotEmpty String country) {
        User currentUser = SystemUtils.getCurrentUser();
        userService.updateNationality(currentUser.getId(),country);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("UPDATE NATIONALITY");
        auditLog.setDescription(currentUser.getEmail() + " updated their Nationality");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(country);
        auditLogPublisher.publish(auditLog);
    }

    @PutMapping("/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public void accountDeactivation(@RequestHeader("Status") boolean status) {
        User currentUser = SystemUtils.getCurrentUser();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 14);
        userService.updateActivationStatus(currentUser.getId(),status,status ? calendar.getTime() : null);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(currentUser.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("DEACTIVATE ACCOUNT");
        auditLog.setDescription(currentUser.getEmail() + " deactivated their account");
        auditLog.setRole(currentUser.getRole());
        auditLog.setAuditLogObject(status);
        auditLogPublisher.publish(auditLog);
    }

}
