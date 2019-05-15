package com.fnmusic.user.management.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnmusic.user.management.model.response.ServiceResponse;
import com.fnmusic.user.management.service.HashService;
import com.fnmusic.user.management.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class AuthenticationFilter extends GenericFilterBean {

    @Autowired
    private HashService hashService;

    private AuthenticationManager authenticationManager;
    private static final String TOKEN_SESSION_KEY = "token";
    private static final String USER_SESSION_KEY = "user";

    private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private HttpServletRequest httpServletRequest(ServletRequest request) {return (HttpServletRequest) request; }
    private HttpServletResponse httpServletResponse(ServletResponse response) { return (HttpServletResponse) response; }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = httpServletRequest(request);
        HttpServletResponse httpServletResponse = httpServletResponse(response);

        String token = httpServletRequest.getHeader("X-AUTH-TOKEN");
        try {

            if (token != null || !token.isEmpty()) {
                processTokenAuthentication(token);
            }
            addSessionContextToLogging();
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
        } catch (Exception e) {
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Authentication token not found");
        } finally {
            MDC.remove(TOKEN_SESSION_KEY);
            MDC.remove(USER_SESSION_KEY);
        }
    }

    private void addSessionContextToLogging() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            String tokenValue = null;
            if (authentication != null && !Utils.isNullOrEmpty(authentication.getDetails().toString())) {
                tokenValue = hashService.encode(authentication.getDetails().toString());
            }
            MDC.put(TOKEN_SESSION_KEY, tokenValue);

            String userValue = null;
            if (authentication != null && !Utils.isNullOrEmpty(authentication.getPrincipal().toString())) {
                userValue = hashService.encode(authentication.getDetails().toString());
            }
            MDC.put(USER_SESSION_KEY, userValue);
        }
        catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm error for adding session context to logging");

        }
    }

    private void processTokenAuthentication(String token) {
        PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken = new PreAuthenticatedAuthenticationToken(token,null);
        Authentication authentication = tryToAuthenticate(preAuthenticatedAuthenticationToken);
    }

    private Authentication tryToAuthenticate(PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken) {
        Authentication authentication = authenticationManager.authenticate(preAuthenticatedAuthenticationToken);
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to authenticate user with provided credentials");
        }
        logger.debug("User successfully authenticated");
        return authentication;
    }


}
