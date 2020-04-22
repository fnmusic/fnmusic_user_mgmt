package com.fnmusic.user.management.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class Signup {

    @NotNull
    @NotEmpty
    @JsonProperty("Username")
    private String username;

    @Nullable
    @Email
    @JsonProperty("Email")
    private String email;

    @Nullable
    @Pattern( regexp = "^\\+(?:[0-9]?){6,14}[0-9]$", message = "Invalid Phone")
    @JsonProperty("Phone")
    private String phone;

    @NotNull
    @NotEmpty
    @JsonProperty("Password")
    private String password;

    @JsonProperty("AuthKey")
    private AuthKey authKey;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthKey getAuthKey() {
        return authKey;
    }

    public void setAuthKey(AuthKey authKey) {
        this.authKey = authKey;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


}
