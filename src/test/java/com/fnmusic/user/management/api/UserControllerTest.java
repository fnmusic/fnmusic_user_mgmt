package com.fnmusic.user.management.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnmusic.base.models.Feature;
import com.fnmusic.base.models.Result;
import com.fnmusic.base.models.User;
import com.fnmusic.base.models.UserPrincipal;
import com.fnmusic.base.security.AccessTokenWithUserDetails;
import com.fnmusic.base.security.AuthenticationWithToken;
import com.fnmusic.user.management.messaging.AuditLogPublisher;
import com.fnmusic.user.management.messaging.MailPublisher;
import com.fnmusic.user.management.messaging.NotificationPublisher;
import com.fnmusic.user.management.services.HashService;
import com.fnmusic.user.management.services.StorageService;
import com.fnmusic.user.management.services.TokenService;
import com.fnmusic.user.management.services.UserService;
import com.fnmusic.user.management.utils.ModelUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import java.net.URI;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Stephen Enunwah
 */
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @MockBean
    UserService userService;
    @MockBean
    HashService hashService;
    @MockBean
    StorageService storageService;
    @MockBean
    MailPublisher mailPublisher;
    @MockBean
    AuditLogPublisher auditLogPublisher;
    @MockBean
    NotificationPublisher notificationPublisher;
    @MockBean
    TokenService tokenService;

    String requestPath, accessToken, email, username, phone;
    Long id;


    /**
     * Init Method to set up uninitialized variables
     */
    @Before
    public void init() {
        requestPath = "http://localhost:6000/rest/v1/fn/music/user/management/user";
        accessToken = "1234567890987654321234567890";
        id = 1L;
        email = "johnsmith@yahoo.com";
        username = "JohnSmith";
        phone = "2348168850433";
    }

    /**
     * This tests the findUserById endpoint when the Security Context is not set
     * @throws Exception
     */
    @Test
    public void findUserById_SecurityContextNotSet() throws Exception {
        User user = ModelUtils.user();
        given(userService.retrieveUserById(id))
                .willReturn(new Result<>(0,user));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get(requestPath + "/findbyid/"+id+"")
        )
                .andExpect(status().is2xxSuccessful());


    }

    /**
     * This tests the findUserById endpoint when the Security Context is set
     * @throws Exception
     */
    @Test
    public void findUserById_SecurityContextSet() throws Exception {
        User user = ModelUtils.user();
        Authentication authentication = ModelUtils.authentication();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN",accessToken);

        given(tokenService.contains(accessToken))
                .willReturn(true);
        given(tokenService.retrieve(accessToken))
                .willReturn(authentication);
        given(userService.retrieveUserById(id))
                .willReturn(new Result<>(0,user));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get(requestPath + "/findbyid/"+id+"")
                .headers(httpHeaders)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This tests the findUserByEmail endpoint when the Security Context is not set
     * @throws Exception
     */
    @Test
    public void findUserByEmail_SecurityContextNotSet() throws Exception {
        User user = ModelUtils.user();
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get(requestPath + "/findbyemail/"+email+"")
        )
                .andExpect(status().is2xxSuccessful());


    }

    /**
     * This tests the findUserByEmail endpoint when the Security Context is set
     * @throws Exception
     */
    @Test
    public void findUserByEmail_SecurityContextSet() throws Exception {
        User user = ModelUtils.user();
        Authentication authentication = ModelUtils.authentication();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN",accessToken);

        given(tokenService.contains(accessToken))
                .willReturn(true);
        given(tokenService.retrieve(accessToken))
                .willReturn(authentication);
        given(userService.retrieveUserByEmail(email))
                .willReturn(new Result<>(0,user));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get(requestPath + "/findbyemail/"+email+"")
                .headers(httpHeaders)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This tests the findUserByUsername endpoint when the Security Context is not set
     * @throws Exception
     */
    @Test
    public void findUserByUsername_SecurityContextNotSet() throws Exception {
        User user = ModelUtils.user();
        given(userService.retrieveUserByUsername(username))
                .willReturn(new Result<>(0,user));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get(requestPath + "/findbyusername/"+username+"")
        )
                .andExpect(status().is2xxSuccessful());


    }

    /**
     * This tests the findUserByUsername endpoint when the Security Context is set
     * @throws Exception
     */
    @Test
    public void findUserByUsername_SecurityContextSet() throws Exception {

        User user = ModelUtils.user();
        Authentication authentication = ModelUtils.authentication();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN",accessToken);

        given(tokenService.contains(accessToken))
                .willReturn(true);
        given(tokenService.retrieve(accessToken))
                .willReturn(authentication);
        given(userService.retrieveUserByUsername(username))
                .willReturn(new Result<>(0,user));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get(requestPath + "/findbyusername/"+username+"")
                .headers(httpHeaders)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This tests the findUserByPhone endpoint when the Security Context is not set
     * @throws Exception
     */
    @Test
    public void findUserByPhone_SecurityContextNotSet() throws Exception {
        User user = ModelUtils.user();
        given(userService.retrieveUserByPhone(phone))
                .willReturn(new Result<>(0,user));

        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get(requestPath + "/findbyphone/"+phone+"")
        )
                .andExpect(status().is2xxSuccessful());


    }

    /**
     * This tests the findUserByPhone endpoint when the Security Context is set
     * @throws Exception
     */
    @Test
    public void findUserByPhone_SecurityContextSet() throws Exception {
        User user = ModelUtils.user();
        Authentication authentication = ModelUtils.authentication();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN",accessToken);

        given(tokenService.contains(accessToken))
                .willReturn(true);
        given(tokenService.retrieve(accessToken))
                .willReturn(authentication);
        given(userService.retrieveUserByPhone(phone))
                .willReturn(new Result<>(0,user));

        mvc.perform(MockMvcRequestBuilders
                .get(requestPath + "/findbyphone/"+phone+"")
                .headers(httpHeaders)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This tests the update user endpoint of the User Controller
     * @throws Exception
     */
    @Test
    public void UpdateUser() throws Exception {
        User user = ModelUtils.user();
        String jsonObject = om.writeValueAsString(user);

        given(tokenService.contains(accessToken))
                .willReturn(true);
        given(tokenService.retrieve(accessToken))
                .willReturn(ModelUtils.authentication());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN",accessToken);

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(requestPath + "/update");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        MockMultipartFile request = new MockMultipartFile("Request", "", "application/json",jsonObject.getBytes());
        mvc.perform(builder
                .file(request)
                .headers(httpHeaders)
        )
                .andExpect(status().is2xxSuccessful());

    }

    /**
     * This tests the followUser  endpoint of the User Controller
     * @throws Exception
     */
    @Test
    public void followUser() throws Exception {

        given(tokenService.contains(accessToken))
                .willReturn(true);
        given(tokenService.retrieve(accessToken))
                .willReturn(ModelUtils.authentication());
        given(userService.retrieveUserById(1L))
                .willReturn(new Result<>(0,ModelUtils.user()));
        given(userService.retrieveUserById(2L))
                .willReturn(new Result<>(0,ModelUtils.secondUser()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN",accessToken);
        httpHeaders.add("UserId","2");
        httpHeaders.add("FollowerId","1");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/follow")
                .headers(httpHeaders)
        )
                .andExpect(status().is2xxSuccessful());
    }

    /**
     * This tests the unfollowUser endpoint of the User Controller
     * @throws Exception
     */
    @Test
    public void unfollowUser() throws Exception {

        given(tokenService.contains(accessToken))
                .willReturn(true);
        given(tokenService.retrieve(accessToken))
                .willReturn(ModelUtils.authentication());
        given(userService.retrieveUserById(1L))
                .willReturn(new Result<>(0,ModelUtils.user()));
        given(userService.retrieveUserById(2L))
                .willReturn(new Result<>(0, ModelUtils.secondUser()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-AUTH-TOKEN",accessToken);
        httpHeaders.add("UserId","2");
        httpHeaders.add("FollowerId","1");

        mvc.perform(MockMvcRequestBuilders
                .post(requestPath + "/unfollow")
                .headers(httpHeaders)
        )
                .andExpect(status().is2xxSuccessful());
    }


    @Test
    public void getFollowers() {

    }

    @Test
    public void getFollowing() {

    }

    @Test
    public void isFollower() {

    }

    @Test
    public void isFollowing() {

    }


}
