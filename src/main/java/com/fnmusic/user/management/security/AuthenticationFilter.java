package com.fnmusic.user.management.security;

import com.fnmusic.user.management.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;
    private final String TOKEN_SESSION_KEY = "token";
    private final String USER_SESSION_KEY = "user";

    private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = httpServletRequest(request);
        HttpServletResponse httpServletResponse = httpServletResponse(response);

        try {
            String token = httpServletRequest.getHeader("X-AUTH-TOKEN");
            if (token != null) {
                processTokenAuthentication(token);
            }

            addSessionContextToLogging();
            chain.doFilter(httpServletRequest, httpServletResponse);

        } catch (MaxUploadSizeExceededException e) {
            logger.error(e.getMessage());
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,"Unable to Authorize Request");
        } catch (InternalAuthenticationServiceException e) {
            logger.error(e.getMessage());
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid Request");
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            logger.error(e.getMessage());
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }

    private void processTokenAuthentication(String token) {
        PreAuthenticatedAuthenticationToken preAuthToken = new PreAuthenticatedAuthenticationToken(token,null);
        Authentication authentication = authenticatePreAuthToken(preAuthToken);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private Authentication authenticatePreAuthToken(PreAuthenticatedAuthenticationToken preAuthToken) {
        Authentication authentication = authenticationManager.authenticate(preAuthToken);
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to Authenticate with provided credentials");
        }

        return authentication;
    }

    private void addSessionContextToLogging() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            String tokenValue = null;
            if (authentication != null && !AppUtils.isNullOrEmpty(authentication.getDetails().toString())) {
                tokenValue = authentication.getDetails().toString();
            }
            MDC.put(TOKEN_SESSION_KEY, tokenValue);

            String userValue = null;
            if (authentication != null && !AppUtils.isNullOrEmpty(authentication.getPrincipal().toString())) {
                userValue = authentication.getPrincipal().toString();
            }
            MDC.put(USER_SESSION_KEY, userValue);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private HttpServletRequest httpServletRequest(ServletRequest request) {
        return (HttpServletRequest) request;
    }
    private HttpServletResponse httpServletResponse(ServletResponse response) {
        return (HttpServletResponse) response;
    }
}
