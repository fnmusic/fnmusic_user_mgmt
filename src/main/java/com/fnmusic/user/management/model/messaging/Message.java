package com.fnmusic.user.management.model.messaging;

import java.util.Date;

public class Message {

    public String event;
    public String description;
    public String email;
    public String role;
    public Date date;

    public void setEvent(String event) {
        this.event = event;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "event='" + event + '\'' +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", date=" + date +
                '}';
    }
}
