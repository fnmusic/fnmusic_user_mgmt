package com.fnmusic.user.management.utils;

import com.fnmusic.base.models.Feature;
import com.fnmusic.base.models.Permission;
import com.fnmusic.base.models.Role;
import com.fnmusic.base.models.User;
import com.fnmusic.user.management.models.Auth;
import com.fnmusic.user.management.models.Signup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ModelUtils {

    public static Signup signup() {
        Signup signup = new Signup();
        signup.setEmail("johnsmith@yahoo.com");
        signup.setFirstname("John");
        signup.setLastname("Smith");
        signup.setUsername("JohnSmith");
        signup.setPassword("1234567890");
        signup.setDateCreated(new Date());

        return signup;
    }

    public static User user() {
        User user = new User();
        user.setId(1L);
        user.setEmail("johnsmith@yahoo.com");
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername("JohnSmith");
        user.setPasswordHash("3247325873284234");
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
        auth.setEmail("johndoe@yahoo.com");
        auth.setToken("1234567890");
        auth.setPasswordHash("3247325873284234");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2099,01,01);
        auth.setExpiryDate(calendar.getTime());

        return auth;
    }

}
