package com.fnmusic.user.management.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
import com.fnmusic.user.management.messaging.AuditLogPublisher;
import com.fnmusic.user.management.messaging.MailPublisher;
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

    private String requestPath;
    private String email;
    private String username;
    private String newpassword;

    @Before
    public void init() throws NoSuchAlgorithmException {

        requestPath = "http://localhost:6000/rest/v1/fn/music/user/management/auth";
        email = "johndoe@yahoo.com";
        username = "JohnDoe";
        newpassword = "newpassword";

        given(hashService.encode("1234567890"))
                .willReturn("3247325873284234");
        given(hashService.encode(newpassword))
                .willReturn("3482948324792478");

        Signup signup = ModelUtils.signup();
        given(userService.retrieveUserByEmail(signup.getEmail()))
                .willReturn(new Result<>());
        given(userService.retrieveUserByUsername(signup.getUsername()))
                .willReturn(new Result<>());
        given(userService.create(any(User.class)))
                .willReturn(new Result<>(0,1L));

        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));
        given(userService.retrieveUserByEmail(username))
                .willReturn(new Result<>());

        given(userService.retrieveUserByUsername(email))
                .willReturn(new Result<>());
        given(userService.retrieveUserByUsername(username))
                .willReturn(new Result<>(0,user));

        AccessTokenWithUserDetails access = new AccessTokenWithUserDetails();
        access.setAccessToken("3682757574735j47y58y483y598y385934y5435839");
        access.setUser(user);
        access.setUsername(user.getEmail());
        access.setFeature(ModelUtils.feature());

        given(userService.login(any(User.class)))
                .willReturn(access);
        given(userService.unlockUserById(any(Long.class)))
                .willReturn(new Result<>(0));
        given(userService.getOldPasswords(any(Long.class)))
                .willReturn(new Result<>());

        given(authService.retrieveActivationToken(any(String.class)))
                .willReturn(new Result<>(0,ModelUtils.auth()));
        given(authService.retrieveForgotPasswordVerificationToken(email))
                .willReturn(new Result<>(0,ModelUtils.auth()));
        given(authService.retrievePasswordResetToken(email))
                .willReturn(new Result<>(0,ModelUtils.auth()));


    }

    @Test
    public void signUp() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signup")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(ModelUtils.signup()))
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void signInByEmail() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-UID",email);
        headers.add("X-AUTH-PASSWORD","1234567890");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signin")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void signInByUsername() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-UID",username);
        headers.add("X-AUTH-PASSWORD","1234567890");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/signin")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void sendConfirmationMail() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/confirm")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void activateAccount() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("Token", "1234567890");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/activate")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void forgotPassword() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/forgotpassword")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void forgotPasswordVerification() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("Token", "1234567890");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/forgotpassword/verify")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void PasswordResetWithVerificationToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("Token","1234567890");

        mvc.perform(MockMvcRequestBuilders
            .post(requestPath + "/passwordreset")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void PasswordResetWithoutVerificationToken() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Email",email);
        headers.add("Token","none");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/passwordreset")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void resetPassword() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-EMAIL",email);
        headers.add("X-AUTH-NEW-PASSWORD",newpassword);
        headers.add("X-AUTH-RESET-TOKEN","1234567890");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/resetpassword")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
        )
                .andExpect(status().is2xxSuccessful());
    }


}
