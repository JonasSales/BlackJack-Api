package com.example.demo.auth.filter;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


import com.example.demo.auth.model.User;
import com.example.demo.auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationService authenticationService;

    // Construtor com a injeção de dependência do AuthenticationService
    public LoginFilter(String url, AuthenticationManager authManager, AuthenticationService authenticationService) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
        this.authenticationService = authenticationService;  // Armazenar a instância do AuthenticationService
    }

    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {

        User user = new ObjectMapper()
                .readValue(req.getInputStream(), User.class);

        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                user.getEmail(), user.getPassword(), Collections.emptyList()));
    }

    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth)
            throws IOException, ServletException {
        // Agora utilizando a instância injetada de authenticationService
        authenticationService.generateToken(auth.getName(), res);
    }
}
