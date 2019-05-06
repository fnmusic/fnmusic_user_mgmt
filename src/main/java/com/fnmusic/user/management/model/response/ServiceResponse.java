package com.fnmusic.user.management.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fnmusic.user.management.model.Error;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse {

    private String code;
    private String description;
    private List<Error> errors;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
