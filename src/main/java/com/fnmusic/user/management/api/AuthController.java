package com.fnmusic.user.management.api;

import com.fnmusic.base.models.*;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
import com.fnmusic.base.utils.ConstantUtils;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.exception.InternalServerErrorException;
import com.fnmusic.user.management.exception.NotFoundException;
import com.fnmusic.user.management.messaging.AuditLogPublisher;
import com.fnmusic.user.management.messaging.MailPublisher;
import com.fnmusic.user.management.models.Auth;
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
import org.springframework.validation.annotation.Validated;
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
    private HashService hashService;
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

        Result<User> byEmail = userService.retrieveUserByEmail(signup.getEmail());
        if (byEmail != null) {
            if (byEmail.getData() != null) {
                if (signup.getEmail().equalsIgnoreCase(byEmail.getData().getEmail())) {
                    throw new BadRequestException("Email already exists");
                }
            }
        }

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
        user.setFirstName((signup.getFirstname() != null) ? signup.getUsername() : "");
        user.setLastName((signup.getLastname() != null) ? signup.getLastname() : "");
        user.setEmail(signup.getEmail());
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
        auditLog.setEvent("ACCOUNT REGISTRATION");
        auditLog.setDescription(user.getEmail() + " created an account");
        auditLog.setRole(Role.USER);
        auditLog.setAuditObject(user);
        auditLog.setTimeStamp(new Date().getTime());
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your account was successfully created");
        return response;
    }

    @PostMapping(value = "/signin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public AccessTokenWithUserDetails signin(@RequestHeader("X-AUTH-UID") String uid, @RequestHeader("X-AUTH-PASSWORD") String password ) throws NoSuchAlgorithmException {

        Result<User> result = userService.retrieveUserByEmail(uid);
        User user = result.getData();
        if (user == null) {
            result = userService.retrieveUserByUsername(uid);
            user = result.getData();
            if (user == null) {
                throw new BadRequestException("User does not exist");
            }
        }

        if (user.isLockOutEnabled()) {
            throw new BadRequestException("Your account is locked");
        }

        String loginPasswordHash = hashService.encode(password);
        if (!loginPasswordHash.equalsIgnoreCase(user.getPasswordHash())) {
            userService.increaseLoginAttempt(user.getEmail());
            throw new BadRequestException("Login failed, username or password incorrect");
        }

        AccessTokenWithUserDetails access = userService.login(user);
        if (access == null) {
            throw new InternalServerErrorException("Sorry, we were unable to log you in, please try again later");
        }

        //clear all failed signin attempts
        userService.unlockUserById(user.getId());

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(String.valueOf(user.getId()));
        auditLog.setEvent("LOGIN");
        auditLog.setDescription(user.getId() + " logged into their account");
        auditLog.setRole(user.getRole());
        auditLog.setAuditObject(access.hashCode());
        auditLog.setTimeStamp(new Date().getTime());
        auditLogPublisher.publish(auditLog);

        return access;
    }

    @PostMapping(value = "/signin/verification")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse LoginVerification(@RequestHeader("X-AUTH-EMAIL") String email) {

        Result<User> result = userService.retrieveUserByEmail(email);
        if (result.getData() == null) {
            throw new BadRequestException("Invalid Request");
        }

        String verificationToken = tokenService.generateToken(6);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,Calendar.MONTH,Calendar.DATE + 1);

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setToken(verificationToken);
        auth.setExpiryDate(calendar.getTime());
        authService.submitLoginVerificationToken(auth);
        //send verificationToken to user via sms


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

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Verified Successfully");

        return response;
    }

    @PostMapping("/confirm")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse confirmationMail(@RequestHeader("Email") String email) {

        Result<User> result = userService.retrieveUserByEmail(email);
        User user = result.getData();
        if (user == null) {
            throw new BadRequestException("Invalid Request");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 30);
        String token = tokenService.generateToken(20);

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setToken(token);
        auth.setExpiryDate(calendar.getTime());
        authService.submitActivationToken(auth);

        Mail mail = new Mail();
        mail.setUserId(String.valueOf(user.getId()));
        mail.setMailTo(new String[]{user.getEmail()});
        mail.setSubject("ACTIVATE YOUR ACCOUNT");
        mail.setText(
                "Thanks for using fnmusic! Please confirm your email address by clicking on the link below \n" +
                        "We may need to communicate with you via email on our exciting newsletters and services packages so it's important that we hava an up-to-date email address \n " +
                        "Thank you."
        );
        mail.setActionUrl(ConstantUtils.WEBURL + "/"+user.getEmail()+"/activate/" + token);
        mailPublisher.publish(mail);

        ServiceResponse response = new ServiceResponse();
        response.setCode(String.valueOf(HttpStatus.CREATED.value()));
        response.setDescription("Dear "+ user.getUsername() +", kindly check your email to activate your account");

        return response;
    }

    @PostMapping(value = "/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse activate(@RequestHeader("Email") @Email(message = "Invalid Email") @NotEmpty String email, @RequestHeader("Token") @NotEmpty String token) {

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
        auditLog.setUserId(String.valueOf(userService.retrieveUserByEmail(email).getData().getId()));
        auditLog.setEvent("ACCOUNT ACTIVATION");
        auditLog.setDescription(email + " activated their account");
        auditLog.setRole(Role.USER);
        auditLog.setAuditObject(auth);
        auditLog.setTimeStamp(new Date().getTime());
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your account was successfully activated");

        return response;
    }

    @PostMapping(value = "/forgotpassword")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse forgotPassword(@RequestHeader("Email") @Email String email) throws Exception {

        Result<User> byEmail = userService.retrieveUserByEmail(email);
        if (byEmail.getData() == null) {
            throw new NotFoundException("This email does not exist");
        }

        Date instant = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setExpiryDate(calendar.getTime());

        ServiceResponse response = new ServiceResponse();

        if (byEmail.getData().isPasswordResetProtection()) {
            String verificationToken = tokenService.generateToken(6);
            auth.setToken(verificationToken);
            authService.generateForgotPasswordVerificationToken(auth);

            Mail mail = new Mail();
            mail.setMailTo(new String[]{ email });
            mail.setUserId(String.valueOf(byEmail.getData().getId()));
            mail.setSubject("PASSWORD CHANGE VERIFICATION CODE");
            mail.setText("Your Verification code is " + verificationToken);
            mailPublisher.publish(mail);

            response.setCode("200");
            response.setDescription("Kindly check your email for your verification code");
        }
        else {
            response = generatePasswordResetToken(email,"none");
        }

        return response;
    }

    @PostMapping(value ="/forgotpassword/verify")
    @ResponseStatus(HttpStatus.OK)
    public ServiceResponse verifyForgotPasswordVerificationToken(@RequestHeader("Email") @Email(message = "Invalid Email") String email, @RequestHeader("Token") String token) {

        Result<User> byEmail = userService.retrieveUserByEmail(email);
        if (byEmail.getData() == null) {
            throw new NotFoundException("This email does not exist");
        }

        Result<Auth> authResult = authService.retrieveForgotPasswordVerificationToken(email);
        if (!token.equalsIgnoreCase(authResult.getData().getToken())) {
            throw new BadRequestException("Incorrect Verification Token");
        }

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your email was verified successfully");

        return response;
    }

    @PostMapping(value = "/passwordreset", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ServiceResponse generatePasswordResetToken(@RequestHeader("Email") @Email @NotEmpty String email, @RequestHeader("Token") String token) throws Exception {

        if (!token.equalsIgnoreCase("none")) {
            Result<Auth> tokenResult = authService.retrieveForgotPasswordVerificationToken(email);
            if (tokenResult == null) {
                throw new BadRequestException("Invalid Request");
            }

            if (!token.equalsIgnoreCase(tokenResult.getData().getToken())) {
                throw new BadRequestException("Invalid Request");
            }

            if (!email.equalsIgnoreCase(tokenResult.getData().getEmail())) {
                throw new BadRequestException(("Invalid Request"));
            }
        }

        Result<User> byEmail = userService.retrieveUserByEmail(email);
        if (byEmail.getData() == null) {
            throw new NotFoundException("This email does not exist");
        }

        String passwordResetToken = tokenService.generateToken(15);
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE) + 1);

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setToken(passwordResetToken);
        auth.setExpiryDate(calendar.getTime());
        authService.generatePasswordResetToken(auth);

        Mail mail = new Mail();
        mail.setUserId(String.valueOf(byEmail.getData().getId()));
        mail.setMailTo(new String[]{email});
        mail.setSubject("RESET YOUR PASSWORD");
        mail.setText(
                "Dear "+email+", \n" +
                "Kindly click this button below to activate your account. \n " +
                "Thank you."
        );
        mail.setActionUrl(ConstantUtils.WEBURL + "/"+email+"/passwordreset/" + passwordResetToken);
        mailPublisher.publish(mail);

        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(String.valueOf(byEmail.getData().getId()));
        auditLog.setEvent("FORGOT PASSWORD");
        auditLog.setDescription(email + " forgot their password");
        auditLog.setRole(Role.USER);
        auditLog.setAuditObject(auth);
        auditLog.setTimeStamp(new Date().getTime());
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
        auditLog.setEvent("PASSWORD RESET");
        auditLog.setDescription(user.getEmail() + " reset their password");
        auditLog.setRole(user.getRole());
        auditLog.setAuditObject(auth);
        auditLog.setTimeStamp(new Date().getTime());
        auditLogPublisher.publish(auditLog);

        ServiceResponse response = new ServiceResponse();
        response.setCode("200");
        response.setDescription("Your password was reset successfully");

        return response;
    }
}