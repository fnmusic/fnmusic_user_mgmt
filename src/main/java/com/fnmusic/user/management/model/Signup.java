package com.fnmusic.user.management.model;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Created by Stephen.Enunwah
 */

public class Signup extends Login {

    @NotNull
    private String username;
    @NotNull
    private String firstname;
    @NotNull
    private String lastname;
    @NotNull
    private Date dateOfBirth;
    @NotNull
    private String gender;
    @Nullable
    private Date dateCreated;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Nullable
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(@Nullable Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
