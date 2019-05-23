package com.fnmusic.user.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnmusic.user.management.models.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AnonymousUserFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;
    private static final String TOKEN_SESSION_KEY = "token";
    private static final String USER_SESSION_KEY = "user";

    private static Logger logger = LoggerFactory.getLogger(AnonymousUserFilter.class);

    public AnonymousUserFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private HttpServletRequest httpServletRequest(ServletRequest request) {return (HttpServletRequest) request; }
    private HttpServletResponse httpServletResponse(ServletResponse response) { return (HttpServletResponse) response; }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = httpServletRequest(request);
        HttpServletResponse httpServletResponse = httpServletResponse(response);

        try {
            String contentType = httpServletRequest.getContentType();
            if (contentType != MediaType.APPLICATION_JSON_VALUE) {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,"Invalid Request");
            }

            chain.doFilter(request,response);
        } catch (MaxUploadSizeExceededException e) {
            httpServletResponse.getWriter().println("Unable to authorize your request");
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (InternalAuthenticationServiceException e) {
            SecurityContextHolder.clearContext();
            logger.error("Internal authentication service exception");
            httpServletResponse.sendError(httpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setCode("1008");
            serviceResponse.setDescription(e.getMessage());
            String jsonResponse = new ObjectMapper().writeValueAsString(serviceResponse);
            httpServletResponse.setHeader("Content-Type","application/json");
            httpServletResponse.getWriter().println(jsonResponse);
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } finally {
            MDC.remove(TOKEN_SESSION_KEY);
            MDC.remove(USER_SESSION_KEY);
        }
    }

}
