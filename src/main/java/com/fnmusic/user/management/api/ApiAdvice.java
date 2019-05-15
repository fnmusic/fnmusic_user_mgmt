package com.fnmusic.user.management.api;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fnmusic.user.management.exception.BadRequestException;
import com.fnmusic.user.management.exception.InternalServerErrorException;
import com.fnmusic.user.management.model.Error;
import com.fnmusic.user.management.model.response.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice(annotations = RestController.class)
public class ApiAdvice {

    private static Logger logger = LoggerFactory.getLogger(ApiAdvice.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceResponse handleIllegalArgumentException(IllegalArgumentException e) {
        ServiceResponse response = new ServiceResponse();
        response.setCode("500");
        response.setDescription(e.getMessage());

        return response;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceResponse handleIllegalStateException(IllegalStateException e) {
        ServiceResponse response = new ServiceResponse();
        response.setCode("500");
        response.setDescription(e.getMessage());

        return response;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServiceResponse handleConstraintViolationException(ConstraintViolationException e) {
        ServiceResponse response = new ServiceResponse();
        response.setCode("400");
        response.setDescription(e.getMessage());
        List<Error> errors = new ArrayList<>();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            errors.add(new Error(violation.getPropertyPath().toString(),violation.getMessage()));
        }
        response.setErrors(errors);
        return response;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServiceResponse badRequestException(BadRequestException e) {
        ServiceResponse response = new ServiceResponse();
        response.setCode("400");
        response.setDescription(e.getMessage());

        return response;
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServiceResponse handleInternalServerErrorException(InternalServerErrorException e) {
        ServiceResponse response = new ServiceResponse();
        response.setCode("500");
        response.setDescription(e.getMessage());

        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServiceResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ServiceResponse response = new ServiceResponse();
        response.setCode("400");
        response.setDescription("Bad Request");

        BindingResult result = e.getBindingResult();
        List<FieldError> errorList = result.getFieldErrors();
        List<Error> errors = new ArrayList<>();
        for(FieldError fieldError : errorList) {
            errors.add(new Error(fieldError.getField(),fieldError.getDefaultMessage()));
        }

        response.setErrors(errors);

        return response;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServiceResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ServiceResponse response = new ServiceResponse();
        response.setCode("400");
        response.setDescription(e.getLocalizedMessage());
        if (e.getCause() != null) {
            String message = e.getCause().getMessage();
            if (e.getCause() instanceof JsonMappingException) {
                String[] arr = message.split("\\(");
                if (arr.length > 0) {
                    String temp = arr[0];
                    String[] arr2 = message.split("\\[");
                    if (arr2.length > 1) {
                        message = temp + " (field: [" + arr2[1];
                    } else {
                        message = temp;
                    }
                }
            }

            if (e.getCause() instanceof JsonParseException) {
                String[] arr = message.split("at");
                if (arr.length > 0) {
                    String temp = arr[0];
                    JsonParseException jpe = (JsonParseException) e.getCause();
                    message = temp + " [line: " + jpe.getLocation().getLineNr() + ", column: " + jpe.getLocation().getColumnNr() + "]";
                }
            }
            response = new ServiceResponse();
            response.setCode("400");
            response.setDescription(message);

        }
        return response;
    }

}
