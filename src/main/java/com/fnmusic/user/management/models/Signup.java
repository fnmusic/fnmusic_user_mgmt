package com.fnmusic.user.management.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class Signup {

    @NotNull
    @NotEmpty
    @JsonProperty("Username")
    private String username;

    @JsonProperty("FirstName")
    private String firstname;

    @JsonProperty("LastName")
    private String lastname;

    @NotNull
    @NotEmpty
    @Email
    @JsonProperty("Email")
    private String email;

    @NotNull
    @NotEmpty
    @JsonProperty("Password")
    private String password;

    @JsonProperty("DateCreated")
    private Date dateCreated;

    @PostConstruct
    public void init() {
        this.dateCreated = new Date();
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
