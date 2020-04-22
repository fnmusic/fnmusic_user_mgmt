package com.fnmusic.user.management.utils;

import com.fnmusic.base.models.*;
import com.fnmusic.base.security.AuthenticationWithToken;
import com.fnmusic.user.management.models.Auth;
import com.fnmusic.user.management.models.AuthKey;
import com.fnmusic.user.management.models.Signup;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModelUtils {

    public static Signup signup() {
        Signup signup = new Signup();
        signup.setEmail("johnsmith@yahoo.com");
        signup.setUsername("JohnSmith");
        signup.setPhone("2348168850433");
        signup.setPassword("1234567890");
        signup.setDateCreated(new Date());
        signup.setAuthKey(AuthKey.Email);

        return signup;
    }

    public static User user() {
        User user = new User();
        user.setId(1L);
        user.setEmail("johnsmith@yahoo.com");
        user.setName("John Smith");
        user.setUsername("JohnSmith");
        user.setPasswordHash("3247325873284234");
        user.setPhone("2348168850433");
        user.setRole(Role.USER);
        user.setDateCreated(new Date());

        return user;
    }

    public static User secondUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("janedoe@yahoo.com");
        user.setName("Jane Doe");
        user.setUsername("JaneDoe");
        user.setPasswordHash("32434i454fedf");
        user.setPhone("2348168850434");
        user.setRole(Role.USER);
        user.setDateCreated(new Date());

        return user;
    }

    public static Feature feature() {
        Feature feature = new Feature();
        feature.setId(1);
        feature.setName("USER_FEATURE");
        feature.setPermissions(permisssions());
        feature.setDescription("User Feature");
        feature.setRole(Role.USER);

        return feature;
    }

    public static List<Permission> permisssions() {
        List<Permission> permissions = new ArrayList<>();
        Permission permission1 = new Permission(1,"READ_USER_DATA","");
        Permission permission2 = new Permission(2,"UPDATE_USER_DATA","");
        Permission permission3 = new Permission(3,"ADMIN","");

        permissions.add(permission1);
        permissions.add(permission2);
        permissions.add(permission3);

        return permissions;
    }

    public static Auth auth() {
        Auth auth = new Auth();
        auth.setId(1);
        auth.setEmail("johnsmith@yahoo.com");
        auth.setToken("1234567890");
        auth.setPasswordHash("3247325873284234");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2099,01,01);
        auth.setExpiryDate(calendar.getTime());

        return auth;
    }

    public static Authentication authentication() {
        UserPrincipal userPrincipal = new UserPrincipal(user(),feature().getPermissions());
        Authentication authentication = new AuthenticationWithToken(userPrincipal,userPrincipal.hashCode(),userPrincipal.getAuthorities());
        return authentication;
    }

}
