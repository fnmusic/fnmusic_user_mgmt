package com.fnmusic.user.management.api;

import com.fnmusic.base.models.*;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
import com.fnmusic.base.utils.AuditLogType;
import com.fnmusic.base.utils.CharacterType;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.base.utils.RandomGeneratorUtils;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.exception.InternalServerErrorException;
import com.fnmusic.user.management.exception.NotFoundException;
import com.fnmusic.user.management.messaging.AuditLogPublisher;
import com.fnmusic.user.management.messaging.MailPublisher;
import com.fnmusic.user.management.messaging.SMSPublisher;
import com.fnmusic.user.management.models.Auth;
import com.fnmusic.user.management.models.AuthKey;
import com.fnmusic.user.management.models.Signup;
import com.fnmusic.user.management.services.AuthService;
import com.fnmusic.user.management.services.HashService;
import com.fnmusic.user.management.services.TokenService;
import com.fnmusic.user.management.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "rest/v1/fn/music/user/management/auth")
public class AuthController {

    @Autowired
    HashService hashService;
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;
    @Autowired
    MailPublisher mailPublisher;
    @Autowired
    TokenService tokenService;
    @Autowired
    AuditLogPublisher auditLogPublisher;
    @Autowired
    SMSPublisher smsPublisher;

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * This method creates a new user account via email or phone
     * @param signup The signup object gotten from the request body of the http request
     * @return A Service Response Object stating the status of the request
     * @throws NoSuchAlgorithmException
     */
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse signup(@RequestBody Signup signup) throws NoSuchAlgorithmException {

        //if phone or email is missing, then the request is invalid
        if (signup.getPhone() == null && signup.getEmail() == null) {
            throw new BadRequestException("Email or Phone must be present");
        }

        switch (signup.getAuthKey()) {
            case Email:
                //verify if email already exists
                Result<User> byEmail = userService.retrieveUserByEmail(signup.getEmail());
                if (byEmail != null) {
                    if (byEmail.getData() != null) {
                        if (signup.getEmail().equalsIgnoreCase(byEmail.getData().getEmail())) {
                            throw new BadRequestException("Email already exists");
                        }
                    }
                }
                break;

            case Phone:
                //verify if phone already exists
                Result<User> byPhone = userService.retrieveUserByPhone(signup.getPhone());
                if (byPhone != null) {
                    if (byPhone.getData() != null) {
                        if (signup.getPhone().equalsIgnoreCase(byPhone.getData().getPhone())) {
                            throw new BadRequestException("Phone already exists");
                        }
                    }
                }
                break;
        }

        //verify if username already exists
        Result<User> byUsername = userService.retrieveUserByUsername(signup.getUsername());
        if (byUsername != null) {
            if (byUsername.getData() != null) {
                if (signup.getUsername().equalsIgnoreCase(byUsername.getData().getUsername())) {
                    throw new BadRequestException("Username already exists");
                }
            }
        }

        User user = new User();
        user.setUsername(signup.getUsername());
        user.setEmail(signup.getEmail());
        user.setPhone(signup.getPhone());
        user.setPasswordHash(hashService.encode(signup.getPassword()));
        user.setRole(Role.USER);
        user.setDateCreated(signup.getDateCreated() != null ? signup.getDateCreated() : new Date());

        Result<User> result = userService.create(user);
        if (result.getIdentityValue() <= 0L) {
            throw new InternalServerErrorException("Something went wrong, sorry we couldn't create your account at this time");
        }

        user.setId(result.getIdentityValue());

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(String.valueOf(user.getId()));
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("ACCOUNT REGISTRATION");
        auditLog.setDescription(user.getEmail() + " created an account");
        auditLog.setRole(Role.USER);
        auditLog.setAuditLogObject(user);
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your account was successfully created");
        return response;
    }

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccessTokenWithUserDetails signin(
            @RequestHeader("X-AUTH-UID") String uid,
            @RequestHeader("X-AUTH-PASSWORD") String password,
            @RequestHeader("X-AUTH-KEY") AuthKey authKey) throws NoSuchAlgorithmException {

        Result<User> result = null;
        switch (authKey) {
            case Email:
                result = userService.retrieveUserByEmail(uid);
                break;
            case Phone:
                result = userService.retrieveUserByPhone(uid);
                break;
            case Username:
                result = userService.retrieveUserByUsername(uid);
                break;
        }

        User user = result.getData();
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        
        //if account is locked and lockoutEndDate is not yet reached, login fails
        if (user.isLockOutEnabled()) {
            Date currentDate = new Date();
            if (!currentDate.after(user.getLockOutEndDateUtc())) {
                throw new BadRequestException("your account is locked");
            }
        }

        //if account is suspended, then login fails
        if (user.isSuspended()) {
            throw new BadRequestException("your account has been suspended");
        }

        //if password is incorrect, failed login attempts value increments by 1
        String loginPasswordHash = hashService.encode(password);
        if (!loginPasswordHash.equalsIgnoreCase(user.getPasswordHash())) {
            userService.increaseLoginAttempt(user.getEmail());
            throw new BadRequestException("username or password incorrect");
        }

        AccessTokenWithUserDetails access = userService.login(user);
        if (access == null) {
            throw new InternalServerErrorException("we were unable to log you in, please try again later");
        }

        //clear all failed signin attempts
        userService.unlockUserById(user.getId());

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(String.valueOf(user.getId()));
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("LOGIN");
        auditLog.setDescription(user.getId() + " successfully logged into their account");
        auditLog.setRole(user.getRole());
        auditLog.setAuditLogObject(access.hashCode());
        auditLogPublisher.publish(auditLog);

        return access;
    }

    @PostMapping(value = "/signin/verification")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse sendLoginVerificationToken(@RequestHeader("X-AUTH-EMAIL") String email) {

        Result<User> result = userService.retrieveUserByEmail(email);
        if (result.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        String verificationToken = RandomGeneratorUtils.generateCode(CharacterType.ALPHABETIC,6);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setToken(verificationToken);
        auth.setExpiryDate(calendar.getTime());
        authService.submitLoginVerificationToken(auth);

        //send verificationToken to user via sms
        SMS sms = new SMS();
        sms.setUserId(result.getData().getId().toString());
        sms.setRecipient(result.getData().getPhone());
        sms.setMessage("Your " + ConstantUtils.APPNAME + " login verification code is " + verificationToken);
        smsPublisher.publish(sms);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(result.getData().getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("LOGIN VERIFICATION TOKEN REQUEST");
        auditLog.setDescription(auth.getEmail() + " requested for a login verification token");
        auditLog.setRole(result.getData().getRole());
        auditLog.setAuditLogObject(auth);
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Verification code was successfully sent your mobile phone");

        return response;
    }

    @PostMapping(value = "/signin/verification/token")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse verifyLoginVerificationToken(@RequestHeader("X-AUTH-EMAIL") String email, @RequestHeader("X-AUTH-TOKEN") String token) {

        Result<User> result = userService.retrieveUserByEmail(email);
        if (result.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        Result<Auth> authResult = authService.retrieveLoginVerificationToken(email);
        if (authResult.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        Auth auth = authResult.getData();
        if (!token.equalsIgnoreCase(auth.getToken())) {
            throw new BadRequestException("incorrect verification token");
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(result.getData().getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("LOGIN TOKEN VERIFICATION");
        auditLog.setDescription(auth.getEmail() + " successfully verified their login verification token");
        auditLog.setRole(result.getData().getRole());
        auditLog.setAuditLogObject(auth);
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Verified Successfully");

        return response;
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse sendEmailConfirmationMessage(@RequestHeader("Email") String email, @RequestHeader("ActivationLink") String link) {

        Result<User> result = userService.retrieveUserByEmail(email);
        User user = result.getData();
        if (user == null) {
            throw new BadRequestException("Invalid Request");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 30);
        String token = RandomGeneratorUtils.generateCode(CharacterType.ALPHANUMERIC,20).toLowerCase();

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setToken(token);
        auth.setExpiryDate(calendar.getTime());
        authService.submitActivationToken(auth);

        Mail mail = new Mail();
        mail.setUserId(user.getId());
        mail.setTo(new String[]{user.getEmail()});
        mail.setSubject("ACTIVATE YOUR ACCOUNT");
        mail.setText(
                "Thanks for using fnmusic! Please confirm your email address by clicking on the link below \n" +
                        "We may need to communicate with you via email on our exciting newsletters and services packages so it's important that we hava an up-to-date email address \n " +
                        "Thank you."
        );
        mail.setActionUrl(link + token);
        mailPublisher.publish(mail);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(user.getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("EMAIL CONFIRMATION MESSAGE REQUEST");
        auditLog.setDescription(user.getEmail() + " requested for an email confirmation message");
        auditLog.setRole(user.getRole());
        auditLog.setAuditLogObject(auth);
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode(String.valueOf(HttpStatus.CREATED.value()));
        response.setDescription("Dear "+ user.getUsername() +", kindly check your email to activate your account");

        return response;
    }

    @PostMapping(value = "/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse activate(@RequestHeader("Email") @Email @NotEmpty String email, @RequestHeader("Token") @NotEmpty String token) {

        Result<User> result = userService.retrieveUserByEmail(email);
        if (!email.equalsIgnoreCase(result.getData().getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        Result<Auth> tokenResult = authService.retrieveActivationToken(email);
        Auth auth = tokenResult.getData();
        if (auth == null) {
            throw new BadRequestException("Invalid Request");
        }

        if (!email.equalsIgnoreCase(auth.getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        String activationToken = auth.getToken();
        if (activationToken.isEmpty()) {
            throw new BadRequestException("Invalid Request");
        }

        if (!token.equalsIgnoreCase(activationToken)) {
            throw new BadRequestException("Invalid Request");
        }

        authService.activateAccount(auth);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(String.valueOf(result.getData().getId()));
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("ACCOUNT ACTIVATION");
        auditLog.setDescription(email + " successfully activated their account");
        auditLog.setRole(Role.USER);
        auditLog.setAuditLogObject(auth);
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your account was successfully activated");

        return response;
    }

    @PostMapping(value = "/forgotpassword/verification")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse sendForgotPasswordVerificationToken(@RequestHeader("Email") @Email String email) {

        Result<User> byEmail = userService.retrieveUserByEmail(email);
        if (byEmail.getData() == null) {
            throw new NotFoundException("This email does not exist");
        }

        Date instant = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        String verificationToken = RandomGeneratorUtils.generateCode(CharacterType.NUMERIC,6);
        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setExpiryDate(calendar.getTime());
        auth.setToken(verificationToken);
        authService.submitForgotPasswordVerificationToken(auth);

        Mail mail = new Mail();
        mail.setTo(new String[]{ email });
        mail.setUserId(byEmail.getData().getId());
        mail.setSubject("PASSWORD CHANGE VERIFICATION CODE");
        mail.setText("Your Verification code is " + verificationToken);
        mailPublisher.publish(mail);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(byEmail.getData().getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("FORGOT PASSWORD VERIFICATION TOKEN REQUEST");
        auditLog.setDescription(byEmail.getData().getEmail() + " requested for a forgot-password verification token");
        auditLog.setRole(byEmail.getData().getRole());
        auditLog.setAuditLogObject(auth);
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Kindly check your email for your verification code");

        return response;
    }

    @PostMapping(value ="/forgotpassword/verification/token")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse verifyForgotPasswordVerificationToken(@RequestHeader("Email") @Email String email, @RequestHeader("Token") String token) {

        Result<User> byEmail = userService.retrieveUserByEmail(email);
        if (byEmail.getData() == null) {
            throw new NotFoundException("This email does not exist");
        }

        Result<Auth> authResult = authService.retrieveForgotPasswordVerificationToken(email);
        if (!token.equalsIgnoreCase(authResult.getData().getToken())) {
            throw new BadRequestException("Incorrect Verification Token");
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(byEmail.getData().getId().toString());
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("FORGOT PASSWORD TOKEN VERIFICATION");
        auditLog.setDescription(byEmail.getData().getEmail() + " successfully verified their forgot-password verification token");
        auditLog.setRole(byEmail.getData().getRole());
        auditLog.setAuditLogObject(authResult.getData());
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your email was verified successfully");

        return response;
    }

    @PostMapping(value = "/passwordreset", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ServiceResponse sendPasswordResetLink(@RequestHeader("Email") @Email @NotEmpty String email, @RequestHeader("ResetLink") String link) {

        Result<User> byEmail = userService.retrieveUserByEmail(email);
        if (byEmail.getData() == null) {
            throw new NotFoundException("This email does not exist");
        }

        String passwordResetToken = RandomGeneratorUtils.generateCode(CharacterType.ALPHABETIC,6);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setToken(passwordResetToken);
        auth.setExpiryDate(calendar.getTime());
        authService.generatePasswordResetToken(auth);

        Mail mail = new Mail();
        mail.setUserId(byEmail.getData().getId());
        mail.setTo(new String[]{email});
        mail.setSubject("RESET YOUR PASSWORD");
        mail.setText(
                "Dear "+email+", \n" +
                "Kindly click this button below to activate your account. \n " +
                "Thank you."
        );
        mail.setActionUrl(link + passwordResetToken);
        mailPublisher.publish(mail);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(String.valueOf(byEmail.getData().getId()));
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("PASSWORD RESET TOKEN REQUEST");
        auditLog.setDescription(email + " requested for a password-reset token");
        auditLog.setRole(Role.USER);
        auditLog.setAuditLogObject(auth);
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("202");
        response.setDescription("Dear " + email + ", kindly check your email for the password reset link");

        return response;
    }

    @PostMapping(value = "/resetpassword", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse resetPassword(
            @RequestHeader("X-AUTH-EMAIL") @Email(message = "Invalid Email") String email,
            @RequestHeader("X-AUTH-NEW-PASSWORD") @NotNull @NotEmpty String password,
            @RequestHeader("X-AUTH-RESET-TOKEN") @NotNull @NotEmpty String resetToken) throws NoSuchAlgorithmException {

        Result<Auth> result = authService.retrievePasswordResetToken(email);
        Auth auth = result.getData();
        auth.setPasswordHash(hashService.encode(password));
        if (!resetToken.equalsIgnoreCase(auth.getToken())) {
            throw new BadRequestException("Invalid Request");
        }

        if (!email.equalsIgnoreCase(auth.getEmail())) {
            throw new BadRequestException("Invalid Request");
        }

        Result<User> userResult = userService.retrieveUserByEmail(email);
        User user = userResult.getData();
        if (auth.getPasswordHash().equalsIgnoreCase(user.getPasswordHash())) {
            throw new BadRequestException("You cannot use an old password");
        }

        Result<User> oldPasswordsResult = userService.getOldPasswords(user.getId());
        if (oldPasswordsResult.getList() != null) {
            List<User> oldPasswordsList = oldPasswordsResult.getList();
            if (!oldPasswordsList.isEmpty()) {
                for (User oldPassword : oldPasswordsList) {
                    if (auth.getPasswordHash().equalsIgnoreCase(oldPassword.getPasswordHash())) {
                        throw new BadRequestException("You cannot use an old password");
                    }
                }
            }
        }

        authService.resetPassword(auth);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(String.valueOf(user.getId()));
        auditLog.setAuditLogType(AuditLogType.USER);
        auditLog.setEvent("RESET PASSWORD");
        auditLog.setDescription(user.getEmail() + " successfully reset their password");
        auditLog.setRole(user.getRole());
        auditLog.setAuditLogObject(auth);
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your password was reset successfully");

        return response;
    }
}