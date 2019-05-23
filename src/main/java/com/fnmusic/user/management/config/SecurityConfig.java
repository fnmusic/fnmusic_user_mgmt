package com.fnmusic.user.management.config;

import com.fnmusic.user.management.security.AuthenticationFilter;
import com.fnmusic.user.management.security.TokenAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http
                .authorizeRequests()
                .antMatchers("/rest/v1/fn/music/user/management/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        http
                .addFilterBefore(new AuthenticationFilter(authenticationManager()),BasicAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/rest/v1/fn/music/user/management/user/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());

        http
                .cors()
                .configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.addAllowedOrigin("*");
                    config.setAllowCredentials(true);
                    return config;
                });

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(tokenAuthenticationProvider());
    }


    @Bean
    public AuthenticationProvider tokenAuthenticationProvider(){ return new TokenAuthenticationProvider();}

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (httpServletRequest, httpServletResponse, e) -> httpServletResponse.sendError(httpServletResponse.SC_UNAUTHORIZED);
    }


}
