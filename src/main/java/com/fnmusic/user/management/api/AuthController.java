package com.fnmusic.user.management.api;

import com.fnmusic.base.Utils.MailType;
import com.fnmusic.base.models.Mail;
import com.fnmusic.base.models.Result;
import com.fnmusic.user.management.exception.InternalServerErrorException;
import com.fnmusic.user.management.exception.NotFoundException;
import com.fnmusic.user.management.model.*;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.messaging.Publisher.impl.AuditLogPublisher;
import com.fnmusic.user.management.messaging.Publisher.impl.MailPublisher;
import com.fnmusic.user.management.model.response.*;
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

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    public SignupResponse signup(@RequestBody Signup signup) throws NoSuchAlgorithmException {

        User byEmail = userService.findUserByEmail(signup.getEmail());
        if (byEmail != null) {
            if (signup.getEmail().equalsIgnoreCase(byEmail.getEmail()))
                throw new BadRequestException("Email already exists");
        }

        User byUsername = userService.findUserByUsername(signup.getUsername());
        if (byUsername != null) {
            if (signup.getUsername().equalsIgnoreCase(byUsername.getUsername()))
                throw new BadRequestException("Username already exists");
        }

        signup.setPassword(passwordHasher.encode(signup.getPassword()));
        signup.setDateCreated(new Date());
        Result<User> result = authService.create(signup);
        if (result.getData() == null)
            throw new IllegalStateException("Unable to create user");

        String accountActivationToken = tokenService.generateLinkToken(20);
        User user = result.getData();
        authService.submitAccountActivationToken(user.getEmail(),accountActivationToken);

        Mail mail = AppUtils.getMailInstance(MailType.accountActivation,user,accountActivationToken);
        if (mail == null) {
            throw new IllegalStateException("Mail Object for signup cannot be null");
        }
        mailPublisher.publishMessage(mail);

        AuditLog auditLog = AppUtils.getAuditLogInstance(AuditLogType.register,user);
        if (auditLog == null) {
            throw new IllegalStateException("AuditLog Object for signup cannot be null");
        }
        auditLogPublisher.publishMessage(auditLog);

        SignupResponse response = new SignupResponse();
        response.setAccessToken(tokenService.generateUserSessionToken(user));
        response.setCode(String.valueOf(HttpStatus.CREATED.value()));
        response.setDescription("Dear "+signup.getUsername()+", kindly check your email to activate your account");

        return response;
    }

    @PostMapping("/activate/{email}/{activationtoken}")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse activateAccount(@PathVariable("email") @Validated @Email String email, @PathVariable("activationtoken") @Validated @NotEmpty String token) {

        if (email.isEmpty()) {
            throw new BadRequestException("Invalid Request");
        }

        if (token.isEmpty()) {
            throw new BadRequestException("Invalid Request");
        }

        String userToken = authService.getAccountActivationToken(email);
        if (token == null) {
            throw new InternalServerErrorException("Something went wrong, try again later");
        }

        if (token != userToken) {
            throw new InternalServerErrorException("Sorry, we couldn't activate your account, resend activation link");
        }

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your account was successfully activated");
        return response;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody @Validated Login login) throws NoSuchAlgorithmException {

        User user = userService.findUserByEmail(login.getEmail());
        if (user == null) {
           throw new BadRequestException("User does not exist");
        }

        if (user.isLockOutEnabled())
            throw new BadRequestException("Your account is locked");

        String loginPasswordHash = passwordHasher.encode(login.getPassword());
        if (!loginPasswordHash.equalsIgnoreCase(user.getPasswordHash())) {
            authService.increaseAccessFailedCount(login.getEmail());
            throw new BadRequestException("Login failed, username or password incorrect");
        }

        AuditLog auditLog = AppUtils.getAuditLogInstance(AuditLogType.login,user);
        if (auditLog == null) {
            throw new IllegalStateException("AuditLog object for 'login' cannot be null");
        }
        auditLogPublisher.publishMessage(auditLog);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(tokenService.generateUserSessionToken(user));
        response.setCode("200");
        response.setDescription("You have successfully logged in");

        return response;
    }



    @PostMapping(value = "/forgotpassword/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ServiceResponse forgotPassword(@Validated @NotEmpty @NotNull @PathVariable("email") String email) {

        if (email.isEmpty() || email == null) {
            throw new BadRequestException("Email cannot be empty");
        }

        User byEmail = userService.findUserByEmail(email);
        if (byEmail == null) {
            throw new NotFoundException(400,"This email does not exist");
        }

        String passwordResetToken = tokenService.generateLinkToken(15);
        authService.submitPasswordResetToken(email,passwordResetToken);

        Mail mail = AppUtils.getMailInstance(MailType.passwordReset,byEmail.getEmail(),passwordResetToken);
        if (mail == null) {
            throw new IllegalStateException("Mail Object cannot be null");
        }
        mailPublisher.publishMessage(mail);

        AuditLog auditLog = AppUtils.getAuditLogInstance(AuditLogType.forgotPassword,byEmail);
        if (auditLog == null) {
            throw new IllegalStateException("AuditLog cannot be null");
        }
        auditLogPublisher.publishMessage(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("202");
        response.setDescription("Dear " + email + ", kindly check your email for the password reset link");

        return response;
    }

    @PutMapping(value = "/passwordreset", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse passwordReset(@RequestBody PasswordReset reset) throws NoSuchAlgorithmException {

        if (reset.getPassword().isEmpty()) {
            throw new BadRequestException("password cannot be empty");
        }

        if (reset.getEmail().isEmpty()) {
            throw new BadRequestException("email cannot be empty");
        }

        String resetToken = authService.retrievePasswordResetToken(reset.getEmail());

        reset.setPassword(passwordHasher.encode(reset.getPassword()));
        authService.resetPassword(reset);

        AuditLog auditLog = AppUtils.getAuditLogInstance(AuditLogType.passwordReset,reset);
        if (auditLog == null) {
            throw new IllegalStateException("AuditLog cannot be null");
        }
        auditLogPublisher.publishMessage(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your password was reset successfully");

        return response;
    }
}
