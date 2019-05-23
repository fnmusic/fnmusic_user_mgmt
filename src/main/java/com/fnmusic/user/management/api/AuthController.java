package com.fnmusic.user.management.api;

import com.fnmusic.base.Utils.MailType;
import com.fnmusic.base.models.Mail;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.Role;
import com.fnmusic.base.models.User;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.exception.InternalServerErrorException;
import com.fnmusic.user.management.messaging.Publisher.impl.AuditLogPublisher;
import com.fnmusic.user.management.messaging.Publisher.impl.MailPublisher;
import com.fnmusic.user.management.models.*;
import com.fnmusic.user.management.service.AuthService;
import com.fnmusic.user.management.service.HashService;
import com.fnmusic.user.management.service.TokenService;
import com.fnmusic.user.management.service.UserService;
import com.fnmusic.user.management.utils.AppUtils;
import com.fnmusic.user.management.utils.AuditLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

@RestController
@RequestMapping(value = "rest/v1/fn/music/user/management/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    @Autowired
    private HashService passwordHasher;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private MailPublisher mailPublisher;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuditLogPublisher auditLogPublisher;

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse signup(@RequestBody @Validated Signup signup) throws NoSuchAlgorithmException {

        Result<User> byEmail = userService.findUserByEmail(signup.getEmail());
        if (byEmail != null) {
            if (byEmail.getData() != null) {
                if (signup.getEmail().equalsIgnoreCase(byEmail.getData().getEmail()))
                    throw new BadRequestException("Email already exists");
            }
        }

        Result<User> byUsername = userService.findUserByUsername(signup.getUsername());
        if (byUsername != null) {
            if (byUsername.getData() != null) {
                if (signup.getUsername().equalsIgnoreCase(byUsername.getData().getUsername()))
                    throw new BadRequestException("Username already exists");
            }
        }

        User newUser = new User();
        newUser.setUsername(signup.getUsername());
        newUser.setFirstName(signup.getFirstname());
        newUser.setLastName(signup.getLastname());
        newUser.setDateOfBirth(signup.getDateOfBirth());
        newUser.setGender(signup.getGender());
        newUser.setEmail(signup.getEmail());
        newUser.setPasswordHash(passwordHasher.encode(signup.getPassword()));
        newUser.setRole(Role.USER);
        newUser.setDateCreated(new Date());
        Result<User> result = userService.createUser(newUser);
        if (result.getData() == null)
            throw new IllegalStateException("Unable to create createdUser");

        User createdUser = result.getData();
        Result<UserAuth> authResult = authService.generateActivationToken(createdUser.getEmail());
        String activationToken = authResult.getData().getToken();

        Mail mail = AppUtils.getMailInstance(MailType.accountActivation, createdUser,activationToken);
        if (mail == null) {
            throw new IllegalStateException("Mail Object for signup cannot be null");
        }
        mailPublisher.publishMessage(mail);

        AuditLog auditLog = AppUtils.getAuditLogInstance(AuditLogType.register, createdUser);
        if (auditLog == null) {
            throw new IllegalStateException("AuditLog Object for signup cannot be null");
        }
        auditLogPublisher.publishMessage(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode(String.valueOf(HttpStatus.CREATED.value()));
        response.setDescription("Dear "+createdUser.getUsername()+", kindly check your email to activate your account");

        return response;
    }

//    @PostMapping("/activate/{email}/{activationtoken}")
//    @ResponseStatus(HttpStatus.OK)
//    @Validated
//    public ServiceResponse activateAccount(@PathVariable("email") @Email String email, @PathVariable("activationtoken") @NotEmpty String token) {
//
//        if (email.isEmpty()) {
//            throw new BadRequestException("Invalid Request");
//        }
//
//        if (token.isEmpty()) {
//            throw new BadRequestException("Invalid Request");
//        }
//
//        String resetToken = authService.retrieveActivationToken(email);
//        if (resetToken == null) {
//            throw new InternalServerErrorException("Something went wrong, try again later");
//        }
//
//        if (token != resetToken) {
//            throw new InternalServerErrorException("Sorry, we couldn't activate your account, resend activation link");
//        }
//
//        ServiceResponse response = new ServiceResponse();
//        response.setCode("200");
//        response.setDescription("Your account was successfully activated");
//        return response;
//    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccessTokenWithUserDetails login(@RequestBody @Validated Login login) throws NoSuchAlgorithmException {

        Result<User> result = userService.findUserByEmail(login.getEmail());
        User user = result.getData();
        if (user == null) {
           throw new BadRequestException("User does not exist");
        }

        if (user.isLockOutEnabled())
            throw new BadRequestException("Your account is locked");

        String loginPasswordHash = passwordHasher.encode(login.getPassword());
        if (!loginPasswordHash.equalsIgnoreCase(user.getPasswordHash())) {
            authService.increaseLoginAttempt(login.getEmail());
            throw new BadRequestException("Login failed, username or password incorrect");
        }

        AccessTokenWithUserDetails access = userService.loginUser(user);
        if (access == null) {
            throw new InternalServerErrorException("Sorry, we were unable to log you in, please try again later");
        }

        AuditLog auditLog = AppUtils.getAuditLogInstance(AuditLogType.login,user);
        if (auditLog == null) {
            throw new IllegalStateException("AuditLog object for 'login' cannot be null");
        }
        auditLogPublisher.publishMessage(auditLog);

        return access;
    }


//    @PostMapping(value = "/forgotpassword/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public ServiceResponse forgotPassword(@Validated @NotEmpty @NotNull @PathVariable("email") String email) {
//
//        if (email.isEmpty() || email == null) {
//            throw new BadRequestException("Email cannot be empty");
//        }
//
//        Result<User> byEmail = userService.findUserByEmail(email);
//        if (byEmail.getData() == null) {
//            throw new NotFoundException(400,"This email does not exist");
//        }
//
//        String passwordResetToken = tokenService.generateLinkToken(15);
//        authService.submitPasswordResetToken(email,passwordResetToken);
//
//        Mail mail = AppUtils.getMailInstance(MailType.passwordReset,byEmail.getData().getEmail(),passwordResetToken);
//        if (mail == null) {
//            throw new IllegalStateException("Mail Object cannot be null");
//        }
//        mailPublisher.publishMessage(mail);
//
//        AuditLog auditLog = AppUtils.getAuditLogInstance(AuditLogType.forgotPassword,byEmail);
//        if (auditLog == null) {
//            throw new IllegalStateException("AuditLog cannot be null");
//        }
//        auditLogPublisher.publishMessage(auditLog);
//
//        ServiceResponse response = new ServiceResponse();
//        response.setCode("202");
//        response.setDescription("Dear " + email + ", kindly check your email for the password reset link");
//
//        return response;
//    }
//
//    @PutMapping(value = "/passwordreset", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.OK)
//    public ServiceResponse passwordReset(@RequestBody PasswordReset reset, @RequestHeader String resetToken) throws NoSuchAlgorithmException {
//
//        if (reset.getPassword().isEmpty()) {
//            throw new BadRequestException("password cannot be empty");
//        }
//
//        if (reset.getEmail().isEmpty()) {
//            throw new BadRequestException("email cannot be empty");
//        }
//
//        String token = authService.retrievePasswordResetToken(reset.getEmail());
//        if (resetToken != token) {
//            throw new InternalServerErrorException("Something went wrong, please try again later");
//        }
//
//        reset.setPassword(passwordHasher.encode(reset.getPassword()));
//        authService.resetPassword(reset);
//
//        AuditLog auditLog = AppUtils.getAuditLogInstance(AuditLogType.passwordReset,reset);
//        if (auditLog == null) {
//            throw new IllegalStateException("AuditLog cannot be null");
//        }
//        auditLogPublisher.publishMessage(auditLog);
//
//        ServiceResponse response = new ServiceResponse();
//        response.setCode("200");
//        response.setDescription("Your password was reset successfully");
//
//        return response;
//    }
}
