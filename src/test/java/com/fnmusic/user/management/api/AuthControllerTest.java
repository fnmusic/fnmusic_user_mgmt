package com.fnmusic.user.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
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
import com.fnmusic.user.management.utils.ModelUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.NoSuchAlgorithmException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Stephen Enunwah
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private AuthService authService;
    @MockBean
    private UserService userService;
    @MockBean
    private HashService hashService;
    @MockBean
    private MailPublisher mailPublisher;
    @MockBean
    private AuditLogPublisher auditLogPublisher;
    @MockBean
    private SMSPublisher smsPublisher;

    private String requestPath;
    private String email;
    private String username;
    private String phone;
    private String new_password;

    /**
     * This method is the used to initialize constants that will be used within this test class
     * @throws NoSuchAlgorithmException
     */
    @Before
    public void init() throws NoSuchAlgorithmException {

        requestPath = "http://localhost:6000/rest/v1/fn/music/user/management/auth";
        email = "johnsmith@yahoo.com";
        username = "JohnSmith";
        phone = "2348168850433";
        new_password = "new_password";

        /*
            This mock is for sign in tests
            BEGIN
         */
        User user = ModelUtils.user();
        AccessTokenWithUserDetails access = new AccessTokenWithUserDetails();
        access.setAccessToken("3682757574735j47y58y483y598y385934y5435839");
        access.setUser(user);
        access.setUsername(user.getEmail());
        access.setFeature(ModelUtils.feature());
        given(userService.login(any(User.class)))
                .willReturn(access);
        /*
            END
         */

    }

    /**
     * This test is for user signup via email
     * @throws Exception
     */
    @Test
    public void signUpByEmail() throws Exception {

        Signup signup = ModelUtils.signup();
        signup.setAuthKey(AuthKey.Email);
        given(hashService.encode("1234567890"))
                .willReturn("3247325873284234");
        given(userService.retrieveUserByEmail(signup.getEmail()))
                .willReturn(new Result<>());
        given(userService.create(any(User.class)))
                .willReturn(new Result<>(0,1L));

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signup")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(ModelUtils.signup()))
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is for user signup via phone
     * @throws Exception
     */
    @Test
    public void signUpByPhone() throws Exception {

        Signup signup = ModelUtils.signup();
        signup.setAuthKey(AuthKey.Phone);
        given(hashService.encode("1234567890"))
                .willReturn("3247325873284234");
        given(userService.retrieveUserByPhone(signup.getPhone()))
                .willReturn(new Result<>());
        given(userService.create(any(User.class)))
                .willReturn(new Result<>(0,1L));

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signup")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(ModelUtils.signup()))
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is for user signin via email
     * @throws Exception
     */
    @Test
    public void signInByEmail() throws Exception {

        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));
        given(hashService.encode("1234567890"))
                .willReturn("3247325873284234");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-UID",email);
        headers.add("X-AUTH-PASSWORD","1234567890");
        headers.add("X-AUTH-KEY","Email");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signin")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is for user signin via username
     * @throws Exception
     */
    @Test
    public void signInByUsername() throws Exception {

        User user = ModelUtils.user();
        given(userService.retrieveUserByUsername(username))
                .willReturn(new Result<>(0,user));
        given(hashService.encode("1234567890"))
                .willReturn("3247325873284234");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-UID",username);
        headers.add("X-AUTH-PASSWORD","1234567890");
        headers.add("X-AUTH-KEY","Username");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signin")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is for user signin via phone
     * @throws Exception
     */
    @Test
    public void signInByPhone() throws Exception {

        User user = ModelUtils.user();
            given(userService.retrieveUserByPhone(phone))
                .willReturn(new Result<>(0,user));
        given(hashService.encode("1234567890"))
                .willReturn("3247325873284234");

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-UID",phone);
        headers.add("X-AUTH-PASSWORD","1234567890");
        headers.add("X-AUTH-KEY","Phone");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signin")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is to send login verification Token
     * @throws Exception
     */
    @Test
    public void sendLoginVerificationToken() throws Exception {
        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-EMAIL",email);

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signin/verification")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        ).andExpect(status().is2xxSuccessful());

    }

    /**
     * This test is to verify login verification token
     * @throws Exception
     */
    @Test
    public void verifyLoginVerificationToken() throws Exception {
        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<User>(0,user));

        Auth auth = ModelUtils.auth();
        given(authService.retrieveLoginVerificationToken(email))
                .willReturn(new Result<Auth>(0,auth));

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-EMAIL",email);
        headers.add("X-AUTH-TOKEN","1234567890");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signin/verification/token")
                .accept(MediaType.APPLICATION_JSON)
                .headers(headers)
        ).andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is to send account confirmation mail to the user after creating a user account
     * @throws Exception
     */
    @Test
    public void sendConfirmationMail() throws Exception {
        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<User>(0,user));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("ActivationLink","/activate");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/confirm")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        ).andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is to activate an account
     * @throws Exception
     */
    @Test
    public void activateAccount() throws Exception {
        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<User>(0,user));

        Auth auth = ModelUtils.auth();
        given(authService.retrieveActivationToken(email))
                .willReturn(new Result<>(0,auth));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("Token", "1234567890");
        headers.add("ResetLink","/reset/password");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/activate")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is to send forgot password verification token
     * @throws Exception
     */
    @Test
    public void sendForgotPasswordVerificationToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);

        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<User>(0,ModelUtils.user()));

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/forgotpassword/verification")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is to verify forgot password verification token
     * @throws Exception
     */
    @Test
    public void verifyForgotPasswordVerificationToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("Token", "1234567890");

        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));

        Auth auth = ModelUtils.auth();
        given(authService.retrieveForgotPasswordVerificationToken(email))
                .willReturn(new Result<>(0,auth));

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/forgotpassword/verification/token")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test to generate password reset link with verification token
     * @throws Exception
     */
    @Test
    public void PasswordResetWithVerificationToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("Token","1234567890");
        headers.add("ResetLink","/reset/password");

        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));

        mvc.perform(MockMvcRequestBuilders
            .post(requestPath + "/passwordreset")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is to generate password reset link without verification token
     * @throws Exception
     */
    @Test
    public void PasswordResetWithoutVerificationToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("Token","1234567890");
        headers.add("ResetLink","/reset/password");

        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/passwordreset")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This test is to reset password
     * @throws Exception
     */
    @Test
    public void resetPassword() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-EMAIL",email);
        headers.add("X-AUTH-NEW-PASSWORD", new_password);
        headers.add("X-AUTH-RESET-TOKEN","1234567890");

        Auth auth = ModelUtils.auth();
        given(authService.retrievePasswordResetToken(email))
                .willReturn(new Result<>(0,auth));

        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));

        given(hashService.encode(new_password))
                .willReturn("0000000000000");
        given(userService.getOldPasswords(user.getId()))
                .willReturn(new Result<>(0,null));

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/resetpassword")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }


}
